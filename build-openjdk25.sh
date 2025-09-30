#!/bin/bash

# Build script for OpenJDK 25 Docker image

echo "Building SmartAdmin API with OpenJDK 25..."

# Navigate to the API directory
cd payment-center/smart-admin-api

# Build the Docker image using the OpenJDK 25 Dockerfile
docker build -f Dockerfile.openjdk25.final -t zfx-smart-admin-api-openjdk25 .

echo "Build completed. Image tagged as: zfx-smart-admin-api-openjdk25"

# Show the image details
echo "Docker image details:"
docker images | grep zfx-smart-admin-api-openjdk25

echo "To run the container with OpenJDK 25:"
echo "docker run -d --name smart-admin-api-openjdk25 -p 1681:1024 zfx-smart-admin-api-openjdk25"
