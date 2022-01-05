#!/bin/bash
cd /root/auto-ddns
nohup /usr/java/bin/java -jar /root/auto-ddns/auto-ddns.jar /root/auto-ddns/ >/root/auto-ddns/catalina.out 2>&1 &
