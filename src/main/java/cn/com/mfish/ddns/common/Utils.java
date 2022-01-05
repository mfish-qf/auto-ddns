package cn.com.mfish.ddns.common;

import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * @author ：qiufeng
 * @description：公共方法
 * @date ：2022/1/5 11:21
 */
@Slf4j
public class Utils {
    /**
     * 获取当前IP
     *
     * @return
     */
    public static String getCurrentHostIP(String strUrl) {
        HttpURLConnection urlConnection;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(Constants.HTTP_GET);
            urlConnection.connect();
        } catch (IOException e) {
            log.error("错误:创建连接失败", e);
            return null;
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("错误:获取当前IP失败", e);
            return null;
        }
    }

    /**
     * 反射创建配置对象
     *
     * @param c
     * @param properties
     * @param <T>
     * @return
     */
    public static <T> T getInstanceByProp(Class<T> c, Properties properties) {
        try {
            T object = c.newInstance();
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (properties.containsKey(field.getName())) {
                    field.set(object, properties.get(field.getName()));
                }
            }
            return object;
        } catch (IllegalAccessException | InstantiationException e) {
            log.error("错误:反射创建配置对象异常", e);
        }
        return null;
    }


}
