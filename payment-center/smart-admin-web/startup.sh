#!/bin/bash

if [ -z "$1" ]; then
    echo "请输入环境变量 test、prod"
    exit 0
fi

env="$1"

case "$1" in
    test)
      build_env="pre"
      ;;
    *)
      build_env="$1"
      ;;
esac



if [ "$env" != "test" -a "$env" != "prod" ]; then
  echo '请输入环境变量 test、prod'
  exit 0
fi

timestamp=$(date +%Y%m%d%H%M%S)
tag="$env"-"$timestamp"

case "$env" in
    prod)
        webport=1880
        ;;
    *)
	      webport=1881
        ;;
esac


echo '构建前端服务'
docker build --memory 1024m --memory-swap 4g --no-cache --rm -t payment-center-web:"$tag" --build-arg DEPLOY="$build_env" .
echo '前端服务构建完成'

echo '上传文件到对象存储'

docker create --name temp-container-"$tag" payment-center-web:"$tag"
docker cp temp-container-"$tag":/usr/share/nginx/html /tmp/container-fs-"$tag"

case "$env" in
    prod)
        coscli cp /tmp/container-fs-"$tag"/ cos://payment-center/payment-center-web/static/ -r
        ;;
    *)
        coscli cp /tmp/container-fs-"$tag"/ cos://payment-center/payment-center-web-"$env"/static/ -r
        ;;
esac
docker rm temp-container-"$tag"
rm -rf /tmp/container-fs-"$tag"

echo '上传完成'

export TAG="$tag"
export ENV="$env"
export WEB_PORT="$webport"

echo '准备启动服务'
docker compose -p smart-admin-"$env" -f docker-compose.yaml up -d
echo '服务启动完成'

