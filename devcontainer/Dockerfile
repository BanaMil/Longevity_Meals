# Dockerfile for Java 21.0.6 + Flutter + Gradle 8.13 + MongoDB server
FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

# 기본 패키지 설치
RUN apt-get update && apt-get install -y \
    curl unzip git bash build-essential \
    libglu1-mesa wget xz-utils zip \
    ca-certificates gnupg lsb-release software-properties-common \
    && apt-get clean

# Flutter Linux 데스크탑 빌드를 위한 필수 라이브러리 추가
RUN apt-get update && apt-get install -y \
    cmake \
    ninja-build \
    clang \
    libgtk-3-dev \
    pkg-config \
    && apt-get clean

# Oracle Java 21.0.6 다운로드 및 설치
RUN mkdir -p /usr/lib/jvm && \
    wget -O /tmp/jdk.tar.gz https://download.oracle.com/java/21/archive/jdk-21.0.6_linux-x64_bin.tar.gz && \
    tar -xzf /tmp/jdk.tar.gz -C /usr/lib/jvm && \
    mv /usr/lib/jvm/jdk-21.0.6 /usr/lib/jvm/java-21-openjdk-amd64

# JAVA_HOME 설정
ENV JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
ENV PATH="$JAVA_HOME/bin:$PATH"

# Gradle 8.13 설치
RUN wget https://services.gradle.org/distributions/gradle-8.13-bin.zip -P /tmp && \
    unzip -d /opt/gradle /tmp/gradle-8.13-bin.zip && \
    ln -s /opt/gradle/gradle-8.13/bin/gradle /usr/bin/gradle

# Flutter 설치
# Dart SDK는 Flutter에 포함되어 있으므로 따로 설치할 필요 없음
# $path에 flutter와 dart를 등록해서 명령어 사용 가능하게 설정
RUN git clone https://github.com/flutter/flutter.git /opt/flutter
ENV PATH="/opt/flutter/bin:/opt/flutter/bin/cache/dart-sdk/bin:$PATH"

# MongoDB 서버 설치
RUN curl -fsSL https://pgp.mongodb.com/server-6.0.asc | gpg --dearmor -o /usr/share/keyrings/mongodb-server-6.0.gpg && \
    echo "deb [ signed-by=/usr/share/keyrings/mongodb-server-6.0.gpg ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/6.0 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-6.0.list && \
    apt-get update && \
    apt-get install -y mongodb-org && \
    apt-get clean

# Tesseract OCR 설치 (한글 모델 포함)
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-kor \
    libtesseract-dev \
    && apt-get clean

# Flutter doctor 실행 (캐시 생성용)
# 오류가 발생해도 설치 단계가 중단되지 않도록 하기 위함
RUN flutter doctor || true

# 로그인 셸 환경변수 설정 (.bashrc)
RUN echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> /root/.bashrc && \
    echo 'export PATH=$JAVA_HOME/bin:/opt/flutter/bin:/opt/flutter/bin/cache/dart-sdk/bin:$PATH' >> /root/.bashrc

# SSH 설치 및 설정
RUN apt-get update && apt-get install -y openssh-server && \
    mkdir /var/run/sshd && \
    echo 'root:csd25-1qwe' | chpasswd && \
    sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config && \
    echo "PasswordAuthentication yes" >> /etc/ssh/sshd_config

# 컨테이너 시작 시 sshd 실행
CMD ["/usr/sbin/sshd", "-D"]

# 작업 디렉토리 설정
WORKDIR /app