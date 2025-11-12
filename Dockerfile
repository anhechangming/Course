
FROM eclipse-temurin:25-jre
WORKDIR /app

# 从当前目录（虚拟机中存放JAR的目录）复制JAR包到镜像中
# 注意：此时JAR包已通过步骤2传到虚拟机的当前目录，而非主机的target
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} app.jar

# 非root用户运行（完全复用老师的安全配置）
RUN useradd -ms /bin/bash appuser && \
    chown appuser:appuser app.jar
USER appuser

# 暴露应用端口（与老师一致）
EXPOSE 8080

# JVM参数配置（沿用老师的容器优化参数）
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

# 启动命令（完全复用老师的ENTRYPOINT格式）
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]