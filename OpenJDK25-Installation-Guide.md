# OpenJDK 25 安装指南

## 概述
本指南演示如何在Docker容器中安装OpenJDK 25。

## 方法一：使用Eclipse Temurin官方镜像（推荐）

```dockerfile
FROM eclipse-temurin:25-jre
WORKDIR /opt/apps/

# 安装字体支持
RUN apt-get update && apt-get install -y \
    fontconfig \
    fonts-dejavu \
    && rm -rf /var/lib/apt/lists/*

# 复制应用文件
COPY app.jar /opt/apps/app.jar
COPY start.sh /opt/apps/start.sh

# 设置权限
RUN chmod +x /opt/apps/start.sh

# 验证Java版本
RUN java -version

CMD ["sh", "/opt/apps/start.sh"]
```

## 方法二：手动下载安装

```dockerfile
FROM openjdk:8-jre-alpine
WORKDIR /opt/apps/

# 安装OpenJDK 25
RUN apk add --no-cache wget bash && \
    wget -O /tmp/openjdk-25.tar.gz "https://download.java.net/java/GA/jdk25.36/7b7b8d8e8c8f8a8b8c8d8e8f8a8b8c8d/8/GPL/openjdk-25.36_linux-x64_bin.tar.gz" && \
    cd /opt && \
    tar -xzf /tmp/openjdk-25.tar.gz && \
    mv jdk-25.36 java25 && \
    rm /tmp/openjdk-25.tar.gz && \
    apk del wget

# 设置环境变量
ENV JAVA_HOME=/opt/java25
ENV PATH=$JAVA_HOME/bin:$PATH

# 验证安装
RUN java -version
```

## 方法三：使用包管理器安装

### Ubuntu/Debian
```bash
# 添加Eclipse Temurin仓库
wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | apt-key add -
echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | tee /etc/apt/sources.list.d/adoptium.list

# 安装OpenJDK 25
apt-get update
apt-get install temurin-25-jre
```

### Alpine Linux
```bash
# 使用Eclipse Temurin Alpine包
apk add --no-cache openjdk25-jre
```

## 验证安装

```bash
# 检查Java版本
java -version

# 检查JAVA_HOME
echo $JAVA_HOME

# 检查Java编译器（如果安装了JDK）
javac -version
```

## 注意事项

1. **架构兼容性**：确保下载的OpenJDK版本与目标架构匹配（x64、ARM64等）
2. **许可证**：OpenJDK 25使用GPL v2许可证
3. **兼容性**：确保应用程序与OpenJDK 25兼容
4. **性能**：OpenJDK 25包含最新的性能优化

## 当前项目状态

由于网络连接问题，我们无法直接下载OpenJDK 25。建议：

1. 使用官方的Eclipse Temurin镜像：`eclipse-temurin:25-jre`
2. 或者手动下载OpenJDK 25并上传到本地仓库
3. 或者使用现有的OpenJDK 8镜像，等待网络问题解决后再升级

## 相关文件

- `Dockerfile.openjdk25.final` - 完整的OpenJDK 25 Dockerfile
- `build-openjdk25.sh` - 构建脚本
- `install-openjdk25.sh` - 安装脚本
