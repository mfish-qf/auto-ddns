# auto-ddns
## 项目介绍
阿里域名IP动态绑定<br/>
该项目帮助家用服务器无固定IP的同学，通过固定域名可以访问到自己家的服务器</br>
项目执行后会在本地存储一个IP文件，下次执行时会比对当前IP跟历史IP是否一致，如果IP发生改变则自动将域名绑定设置未新的IP</br>
生效时间受阿里云域名解析影响，可能存在一定延迟
## 前提
执行绑定的二级域名必须先在阿里云中配置，如未配置不会主动新增二级域名

## 项目打包运行
mvn package进行打包</br>

## 项目部署
### 创建目录
mkdir /root/auto-ddns
### 拷贝文件
将lib目录和auto-ddns.jar文件拷贝到/root/auto-ddns目录下<br>
将ali.properties,domains.properties,startup.sh文件也放置auto-ddns目录下<br>
修改ali.properties 将access_key_id，access_key_secret更改为自己阿里云的密钥对<br>
修改domains.properties将域名和前缀修改为自己的域名、前缀
chmod +X startup.sh 给startup.sh文件赋权<br>
### 单次执行
cd /root/auto-ddns 进入目录</br>
./startup.sh 运行即可
### 定时执行(centos7)
* 配置定时器</br>
  * crontab -e 
  * 增加 */5 * * * * /root/auto-ddns/startup.sh
* 配置完成后系统每5分钟调度执行一次auto-ddns

