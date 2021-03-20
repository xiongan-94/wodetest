#!/bin/bash

if [ $# -ne 1 ]
then
        echo "args numbers is error!!!"
        exit
fi
 

case $1 in
"start")
        ssh hadoop102 sudo systemctl start gmetad
        ssh hadoop102 sudo systemctl start gmond
        ssh hadoop103 sudo systemctl start gmond
        ssh hadoop104 sudo systemctl start gmond

        ;;
"restart")
        ssh hadoop102 sudo systemctl restart gmetad
        ssh hadoop102 sudo systemctl restart gmond
        ssh hadoop103 sudo systemctl restart gmond
        ssh hadoop104 sudo systemctl restart gmond
        ;;
"stop")
        ssh hadoop102 sudo systemctl stop gmetad
        ssh hadoop102 sudo systemctl stop gmond
        ssh hadoop103 sudo systemctl stop gmond
        ssh hadoop104 sudo systemctl stop gmond
        ;;
*)
        echo "args is error!!!"
        ;;
esac
