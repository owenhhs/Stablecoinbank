#!/bin/bash

if [ -z "$1" ]; then
    echo "请输入环境变量 test、prod"
    exit 0
fi

env="$1"

if [ "$env" != "test" -a "$env" != "prod" ]; then
  echo '请输入环境变量 test、prod'
  exit 0
fi

timestamp=$(date +%Y%m%d%H%M%S)
tag="$env"-"$timestamp"

case "$env" in
    prod)
        serviceport=1024
        ;;
    *)
	      serviceport=1028
        ;;
esac


echo '构建后端服务'
docker build --memory 1024m --memory-swap 4g --no-cache --rm -t x-smart-admin-service:"$tag" --build-arg ENV="$env" .
echo '后端服务构建完成'

export ENV="$env"
export TAG="$tag"
export SERVICE_PORT="$serviceport"

echo '准备启动服务'
docker compose -p smart-admin-"$env" -f docker-compose.yaml up -d
echo '服务启动完成'

