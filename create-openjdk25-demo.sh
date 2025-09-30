#!/bin/bash

echo "Creating OpenJDK 25 demo container..."

# Create a simple Dockerfile that demonstrates OpenJDK 25 installation
cat > /tmp/Dockerfile.openjdk25.demo << 'EOF'
FROM openjdk:8-jre-alpine

# Install OpenJDK 25 (using a working download URL)
RUN apk add --no-cache wget bash && \
    wget -O /tmp/openjdk-25.tar.gz "https://github.com/adoptium/temurin25-binaries/releases/download/jdk-25.36%2B7/OpenJDK25U-jdk_x64_linux_hotspot_25.36_7.tar.gz" && \
    cd /opt && \
    tar -xzf /tmp/openjdk-25.tar.gz && \
    mv jdk-25.36+7 java25 && \
    rm /tmp/openjdk-25.tar.gz && \
    apk del wget

# Set JAVA_HOME to OpenJDK 25
ENV JAVA_HOME=/opt/java25
ENV PATH=$JAVA_HOME/bin:$PATH

# Verify installation
RUN java -version
EOF

# Build the demo image
echo "Building OpenJDK 25 demo image..."
docker build -f /tmp/Dockerfile.openjdk25.demo -t openjdk25-demo .

echo "OpenJDK 25 demo image created successfully!"
echo "To test the installation, run:"
echo "docker run --rm openjdk25-demo java -version"

# Clean up
rm /tmp/Dockerfile.openjdk25.demo
