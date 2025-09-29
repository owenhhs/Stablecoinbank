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
        serviceport=1680
        ;;
    *)
	      serviceport=1681
        ;;
esac


echo '构建后端服务'
docker build --memory 1024m --memory-swap 4g --no-cache --rm -t payment-center-service:"$tag" --build-arg ENV="$env" .
echo '后端服务构建完成'

export ENV="$env"
export TAG="$tag"
export SERVICE_PORT="$serviceport"

# 设置必需的环境变量
export MYSQL_URL="jdbc:mysql://localhost:3306/smart_admin_v3?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
export MYSQL_USERNAME="root"
export MYSQL_PASSWORD="123456"
export REDIS_DATABASE="0"
export REDIS_HOST="localhost"
export REDIS_PORT="6379"
export REDIS_PASSWORD=""
export MYBATIS_ENCRYPT_ENABLE="false"
export MYBATIS_ENCRYPT_SECRETKEY="your-secret-key-here"
export RSA_PRIVATE_KEY="your-private-key-here"
export RSA_PUBLIC_KEY="your-public-key-here"
export STORAGE_REGION="us-east-1"
export STORAGE_ENDPOINT="s3.amazonaws.com"
export STORAGE_BACKETNAME="your-bucket-name"
export STORAGE_ACCESSKEY="your-access-key"
export STORAGE_SECRETKEY="your-secret-key"
export SERVER_DOMAIN="http://localhost:$serviceport"
export WEB_DOMAIN="http://localhost:1881"
export REFUSE_XJ_CHANNEL_ID=""
export LOCK_USER_CHANNEL=""
export SMS_PHONE=""

echo '准备启动服务'
docker compose -p smart-admin-"$env" -f docker-compose.yaml up -d
echo '服务启动完成'

