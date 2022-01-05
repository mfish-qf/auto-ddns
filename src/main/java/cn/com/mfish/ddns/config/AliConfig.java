package cn.com.mfish.ddns.config;

import lombok.Data;

/**
 * @author ：qiufeng
 * @description：阿里接口配置信息
 * @date ：2022/1/5 14:56
 */
@Data
public class AliConfig {
    /**
     * 阿里云ACCESS_KEY_ID
     */
    private String access_key_id = "*****************";
    /**
     * 阿里云ACCESS_KEY_SECRET
     */
    private String access_key_secret = "*********************";
    /**
     * alidns
     */
    private String dns_domain = "alidns.aliyuncs.com";

    /**
     * 地域ID
     */
    private String region_id = "cn-hangzhou";

    /**
     * 类型
     */
    private String type = "A";

    /**
     * 每页的记录数
     */
    private String page_size = "100";

}
