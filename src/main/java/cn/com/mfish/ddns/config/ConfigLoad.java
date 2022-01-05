package cn.com.mfish.ddns.config;

import cn.com.mfish.ddns.common.Constants;
import cn.com.mfish.ddns.common.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author ：qiufeng
 * @description：配置加载
 * @date ：2022/1/5 14:59
 */
@Slf4j
public class ConfigLoad {
    private ConfigLoad() {
    }

    private static volatile AliConfig aliConfig;
    private static volatile List<DomainConfig> domainConfigs;

    private static String formatPropPath(String propPath) {
        if (propPath.endsWith("/")) {
            return propPath;
        }
        return propPath + "/";
    }

    /**
     * 获取阿里域名相关配置
     *
     * @param propPath 配置文件目录
     * @return
     */
    public static AliConfig getAliConfig(String propPath) {
        if (aliConfig == null) {
            synchronized (ConfigLoad.class) {
                if (aliConfig == null) {
                    try (InputStream in = new BufferedInputStream(new FileInputStream(formatPropPath(propPath) + Constants.ALI_CONFIG_NAME))) {
                        Properties properties = new Properties();
                        properties.load(in);
                        aliConfig = Utils.getInstanceByProp(AliConfig.class, properties);
                    } catch (IOException ex) {
                        log.error("错误:IO异常", ex);
                    }
                }
            }
        }
        return aliConfig;
    }



    /**
     * 获取域名相关配置
     *
     * @return
     */
    public static List<DomainConfig> getDomainConfigs(String propPath) {
        if (domainConfigs == null) {
            synchronized (ConfigLoad.class) {
                if (domainConfigs == null) {
                    try (InputStream in = new BufferedInputStream(new FileInputStream(formatPropPath(propPath) + Constants.DOMAIN_CONFIG_NAME))) {
                        Properties properties = new Properties();
                        properties.load(in);
                        domainConfigs = new ArrayList<>();
                        for (Object key : properties.keySet()) {
                            DomainConfig domainInfo = new DomainConfig();
                            domainInfo.setDomain(key.toString());
                            String value = properties.getProperty(key.toString(),"");
                            String[] values = value.split(Constants.SPLIT_REGEX);
                            domainInfo.setPrefix(values);
                            domainConfigs.add(domainInfo);
                        }
                    } catch (IOException ex) {
                        log.error("错误:IO异常", ex);
                    }
                }
            }
        }

        return domainConfigs;
    }

}
