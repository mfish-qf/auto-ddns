package cn.com.mfish.ddns.main;

import cn.com.mfish.ddns.common.Constants;
import cn.com.mfish.ddns.common.Utils;
import cn.com.mfish.ddns.config.AliConfig;
import cn.com.mfish.ddns.config.ConfigLoad;
import cn.com.mfish.ddns.config.DomainConfig;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author ：qiufeng
 * @description：自动刷新DDNS
 * @date ：2022/1/5 10:34
 */
@Slf4j
public class AutoDDNS {

    private static final ThreadLocal<IAcsClient> threadClient = new ThreadLocal<>();
    private static final String defaultDomain = "mfish.com.cn";
    private static final String[] defaultPrefix = {"app"};

    public static void run(String prop) {
        log.info("-------------------------------开始获取IP-------------------------------");
        String hostIp = Utils.getCurrentHostIP(Constants.HUASHENG_IP);
        File file = new File(Constants.MY_IP);
        String localIp = "";
        if (file.isFile() && file.exists()) {
            try (InputStream is = new FileInputStream(file)) {
                int iAvail = is.available();
                byte[] bytes = new byte[iAvail];
                is.read(bytes);
                localIp = new String(bytes);
            } catch (Exception ex) {
                log.error("读取IP文件异常");
            }
        }
        if (hostIp.equals(localIp)) {
            log.info("--------------------------IP未发生改变-------------------------------");
            return;
        }
        log.info("-------------------------------当前主机公网IP为：" + hostIp + "-------------------------------");
        if (StringUtils.isEmpty(prop)) {
            log.info("-----------------未读取文件路径，请检查参数-------------------");
            return;
        }
        List<DomainConfig> list = ConfigLoad.getDomainConfigs(prop);
        if (list == null || list.size() == 0) {
            log.info("--------------未读取到配置，执行默认域名-----------");
            updateDomainIP(prop, defaultDomain, hostIp, defaultPrefix);
        }else{
            log.info("--------------读取到配置，执行域名绑定-----------");
            list.stream().forEach((di) -> {
                updateDomainIP(prop, di.getDomain(), hostIp, di.getPrefix());
            });
        }
        threadClient.remove();
        try (BufferedWriter out = new BufferedWriter(new FileWriter(Constants.MY_IP))) {
            out.write(hostIp);
        } catch (IOException e) {
            log.error("写入文件异常，IP：" + hostIp);
        }
    }

    private static void initAcsClient(String prop) {

    }

    /**
     * 更新域名绑定的动态IP
     *
     * @param prop          文件路径
     * @param domainName    域名名称
     * @param currentHostIP 当前IP
     * @param prefix        前缀
     */
    private static void updateDomainIP(String prop, String domainName, String currentHostIP, String[] prefix) {
        AliConfig aliConfig = ConfigLoad.getAliConfig(prop);
        if (aliConfig == null) {
            return;
        }
        // 设置鉴权参数，初始化客户端
        DefaultProfile profile = DefaultProfile.getProfile(aliConfig.getRegion_id(), aliConfig.getAccess_key_id(), aliConfig.getAccess_key_secret());
        IAcsClient client = new DefaultAcsClient(profile);
        threadClient.set(client);
        List<DescribeDomainRecordsResponse.Record> domainRecords = getLastDomainRecords(aliConfig, domainName);
        // 最新的一条解析记录
        if (ObjectUtils.isEmpty(domainRecords)) {
            return;
        }
        // 获取当前主机公网IP
        List<String> prefixes = null;
        if (!ArrayUtils.isEmpty(prefix)) {
            prefixes = Arrays.asList(prefix);
        }
        for (DescribeDomainRecordsResponse.Record record : domainRecords) {
            //如果传入的二级域名前缀不为空，同时传入的二级域名前缀中不包含当前记录的RR值，则跳过本次循环
            if (!ObjectUtils.isEmpty(prefixes.toArray()) && !prefixes.contains(record.getRR())) {
                continue;
            }
            updateDomainRecord(aliConfig, currentHostIP, record);
        }
    }

    /**
     * 获取最新的解析记录
     *
     * @param aliConfig
     * @param domainName
     * @return
     */
    private static List<DescribeDomainRecordsResponse.Record> getLastDomainRecords(AliConfig aliConfig, String domainName) {
        // 查询指定二级域名的最新解析记录
        DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
        // 主域名
        describeDomainRecordsRequest.setDomainName(domainName);
        // 解析记录类型
        describeDomainRecordsRequest.setType(aliConfig.getType());
        describeDomainRecordsRequest.setPageSize(Long.parseLong(aliConfig.getPage_size()));
        try {
            // 调用SDK发送请求
            return threadClient.get().getAcsResponse(describeDomainRecordsRequest).getDomainRecords();
        } catch (ClientException e) {
            log.error("错误:获取域名解析记录异常", e);
            throw new RuntimeException();
        }
    }

    /**
     * 修改解析记录
     *
     * @param aliConfig
     * @param currentHostIP
     * @param record
     * @return
     */
    private static UpdateDomainRecordResponse updateDomainRecord(AliConfig aliConfig, String currentHostIP, DescribeDomainRecordsResponse.Record record) {
        try {
            // 记录ID
            String recordId = record.getRecordId();
            // 记录值
            String recordsValue = record.getValue();
            //当前主机公网IP与阿里云上映射的IP不相等，则更新IP
            if (currentHostIP.equals(recordsValue)) {
                log.info("IP未发生变化: {}", currentHostIP);
                return null;
            }
            // 修改解析记录
            UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest();
            // 主机记录
            updateDomainRecordRequest.setRR(record.getRR());
            // 记录ID
            updateDomainRecordRequest.setRecordId(recordId);
            // 将主机记录值改为当前主机IP
            updateDomainRecordRequest.setValue(currentHostIP);
            // 解析记录类型
            updateDomainRecordRequest.setType(aliConfig.getType());

            // 调用SDK发送请求
            UpdateDomainRecordResponse updateDomainRecordResponse = threadClient.get().getAcsResponse(updateDomainRecordRequest);
            log.info("updateDomainRecord: {}", updateDomainRecordResponse);
            log.info("子域名:" + record.getRR() + "IP修改为" + currentHostIP);
            return updateDomainRecordResponse;
        } catch (ClientException e) {
            log.error("错误:修改解析记录异常", e);
            throw new RuntimeException();
        }
    }
}
