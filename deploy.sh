#!/bin/bash

# Pet Hospital Deployment Script

echo "=========================================="
echo "   Pet Hospital System Deployment script"
echo "=========================================="

echo "[1/3] Building Backend..."
cd pet-hospital-backend
if [ -z "$JAVA_HOME" ]; then
    export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home"
fi
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "Backend build failed!"
    exit 1
fi
echo "Backend built successfully."

echo "[2/3] Building Frontend..."
cd ../pet-hospital-frontend
npm install
npm run build
if [ $? -ne 0 ]; then
    echo "Frontend build failed!"
    exit 1
fi
echo "Frontend built successfully."

echo "[3/3] Deployment Ready!"
echo "Backend JAR: pet-hospital-backend/target/pet-hospital-backend-0.0.1-SNAPSHOT.jar"
echo "Frontend Dist: pet-hospital-frontend/dist"
echo ""
echo "To run backend: java -jar pet-hospital-backend/target/pet-hospital-backend-0.0.1-SNAPSHOT.jar"
echo "To serve frontend: Use Nginx or 'npm run preview'"
