#!/bin/bash

echo "Installing OpenJDK 25 into existing Docker container..."

# Get the OpenJDK 25 download URL
JDK25_URL="https://download.java.net/java/GA/jdk25.36/7b7b8d8e8c8f8a8b8c8d8e8f8a8b8c8d/8/GPL/openjdk-25.36_linux-x64_bin.tar.gz"

# Create a temporary container from the existing image
CONTAINER_ID=$(docker run -d openjdk:8-jre-alpine sleep 3600)

echo "Created temporary container: $CONTAINER_ID"

# Copy the installation script into the container
cat > /tmp/install_jdk25.sh << 'EOF'
#!/bin/sh
set -e

# Install wget
apk add --no-cache wget

# Download OpenJDK 25
wget -O /tmp/openjdk-25.tar.gz "https://download.java.net/java/GA/jdk25.36/7b7b8d8e8c8f8a8b8c8d8e8f8a8b8c8d/8/GPL/openjdk-25.36_linux-x64_bin.tar.gz"

# Extract and install
cd /opt
tar -xzf /tmp/openjdk-25.tar.gz
mv jdk-25.36 java25

# Clean up
rm /tmp/openjdk-25.tar.gz
apk del wget

echo "OpenJDK 25 installed successfully"
java -version
EOF

# Copy the script to the container
docker cp /tmp/install_jdk25.sh $CONTAINER_ID:/tmp/install_jdk25.sh

# Make it executable and run it
docker exec $CONTAINER_ID chmod +x /tmp/install_jdk25.sh
docker exec $CONTAINER_ID /tmp/install_jdk25.sh

# Commit the container as a new image
docker commit $CONTAINER_ID zfx-smart-admin-api-openjdk25

# Clean up
docker stop $CONTAINER_ID
docker rm $CONTAINER_ID

echo "OpenJDK 25 installation completed!"
echo "New image created: zfx-smart-admin-api-openjdk25"
