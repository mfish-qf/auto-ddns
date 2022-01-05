package cn.com.mfish.ddns.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ：qiufeng
 * @description：域名配置
 * @date ：2022/1/5 15:06
 */
@Data
@Accessors(chain = true)
public class DomainConfig {
    //域名
    private String domain;
    //前缀
    private String[] prefix;
}
