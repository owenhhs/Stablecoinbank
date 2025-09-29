FROM maven:3.6.3 AS builder
ARG PKG=sa-admin
ARG ENV

COPY ./ /apps/
WORKDIR /apps/
RUN sh ./build.sh

# 生成项目部署的镜像
FROM openjdk:8
WORKDIR /opt/apps/

COPY --from=builder /apps/app.jar /opt/apps/app.jar
COPY --from=builder /apps/start.sh /opt/apps/start.sh

# 指定镜像启动时候的操作，直接运行程序的启动脚本
CMD ["sh", "/opt/apps/start.sh"]
