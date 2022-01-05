package cn.com.mfish.ddns.main;

/**
 * @author ：qiufeng
 * @description：启动类
 * @date ：2022/1/5 14:49
 */
public class Application {
    public static void main(String[] args) {
        //必须传入配置文件所在目录参数
        if (args == null || args.length == 0) {
            return;
        }
        AutoDDNS.run(args[0]);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
