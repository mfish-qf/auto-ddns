package cn.com.mfish.ddns.common;

/**
 * @author ：qiufeng
 * @description：常量类
 * @date ：2022/1/5 11:24
 */
public class Constants {
    /**
     * 服务器当前IP
     */
    public static final String MY_IP = "myip";
    /**
     * 花生壳获取IP地址
     */
    public static final String HUASHENG_IP = "http://www.3322.org/dyndns/getip";
    /**
     * GET请求
     */
    public static final String HTTP_GET = "GET";
    /**
     * 查分二级域名的分隔符
     */
    public static final String SPLIT_REGEX = ",";

    /**
     * 域名配置文件名称
     */
    public static final String DOMAIN_CONFIG_NAME = "domains.properties";

    /**
     * 阿里配置文件名称
     */
    public static final String ALI_CONFIG_NAME = "ali.properties";
}
