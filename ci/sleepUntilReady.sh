#!/bin/bash

url=$1
maxTries=$2
delay=1s # also valid: 1m 1.5s etc.
for i in $(seq 1 $maxTries); do
    response=$(curl -LI  $url -H 'OCS-APIRequest: true' -o /dev/null -w '%{http_code}\n' -s)
    echo "Check instance being ready: Attempt $i of $maxTries…"
    if [ $response -eq '200'  ] || [ $response -eq '204' ]
    then
        exit 0
    fi
    sleep $delay
done

exit 1