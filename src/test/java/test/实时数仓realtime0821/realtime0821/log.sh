#!/bin/bash
nginx_home=/opt/module/nginx
log_home=/home/atguigu/realtime0821
# 启动和停止我们的日志服务器

case $1 in
"start")
    # 先启动 nginx
    if [[ -z "$(ps -ef | grep nginx | grep -v grep)" ]]; then
        echo "在hadoop162上启动nginx"
        $nginx_home/sbin/nginx
    else
        echo "nginx 已经启动, 不用重复启动"
    fi

    # 分别在三台设备启动日志服务器
    for host in hadoop162 hadoop163 hadoop164; do
        echo "在 $host 上启动日志服务器"
        ssh $host "nohup java -jar $log_home/gmall-logger-0.0.1-SNAPSHOT.jar --server.port=8081 1>/dev/null 2>&1 &"
    done

   ;;
"stop")
    # 先停日志服务器
    for host in hadoop162 hadoop163 hadoop164 ; do
        echo "在 $host 上停止日志服务器"
        ssh $host "jps | awk '/gmall-logger/ {print \$1}' | xargs kill -9"
    done
    # 再停止nginx
    echo "在hadoop162上停止nginx"
    $nginx_home/sbin/nginx -s stop
;;
*)
    echo "你的使用姿势不对: "
    echo " log.sh start 启动日志服务器集群"
    echo " log.sh stop  停止日志服务器集群"
;;
esac



