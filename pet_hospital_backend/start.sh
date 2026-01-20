#!/bin/bash

# 宠物医院后端服务启动脚本
# 功能：检查8080端口，如果被占用则杀死进程，然后启动服务

echo "================================"
echo "  宠物医院后端服务启动脚本"
echo "================================"

# 设置 JAVA_HOME 为明确的 JDK 路径
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home"
export PATH=$JAVA_HOME/bin:$PATH

echo "使用 JAVA_HOME: $JAVA_HOME"
java -version
javac -version
mvn -version

# 定义端口
PORT=8080

# 检查8080端口是否被占用
echo "正在检查端口 $PORT 是否被占用..."
PID=$(lsof -ti:$PORT)

if [ -n "$PID" ]; then
    echo "发现端口 $PORT 被进程 $PID 占用"
    echo "正在终止进程 $PID..."
    kill -9 $PID
    
    # 等待进程完全终止
    sleep 2
    
    # 再次检查是否成功终止
    if lsof -ti:$PORT > /dev/null; then
        echo "错误：无法终止占用端口 $PORT 的进程"
        exit 1
    else
        echo "进程已成功终止"
    fi
else
    echo "端口 $PORT 未被占用"
fi

# 检查 logs 目录是否存在
if [ ! -d "logs" ]; then
    mkdir -p logs
fi

# 检查是否已编译
# 检查 target 目录下是否有 jar 文件
JAR_FILE=$(find target -name "*.jar" -not -name "*-sources.jar" 2>/dev/null | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo ""
    echo "target 目录下未找到 JAR 文件，正在编译项目..."
    mvn clean package -DskipTests
    if [ $? -ne 0 ]; then
        echo "编译失败，请检查错误信息"
        exit 1
    fi
    # 重新查找
    JAR_FILE=$(find target -name "*.jar" -not -name "*-sources.jar" 2>/dev/null | head -n 1)
    if [ -z "$JAR_FILE" ]; then
        echo "错误：编译后仍未找到 JAR 文件"
        exit 1
    fi
fi

echo ""
echo "找到 JAR 文件: $JAR_FILE"
echo "正在启动服务..."
echo "================================"
echo ""

# 使用 nohup 在后台启动服务
nohup java -jar "$JAR_FILE" > logs/application.log 2>&1 &

# 获取启动的进程ID
NEW_PID=$!
echo "服务已启动，进程ID: $NEW_PID"
echo "日志文件: logs/application.log"

# 等待几秒钟检查服务是否正常启动
sleep 5

if ps -p $NEW_PID > /dev/null; then
    echo ""
    echo "✅ 服务启动成功！"
    echo "访问地址: http://localhost:$PORT"
    echo ""
    echo "查看日志: tail -f logs/application.log"
    echo "停止服务: kill $NEW_PID"
else
    echo ""
    echo "❌ 服务启动失败，请查看日志文件"
    echo "查看日志: cat logs/application.log"
    exit 1
fi
