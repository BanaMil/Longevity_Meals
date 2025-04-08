FROM ubuntu:22.04

# 기본 패키지 설치
RUN apt-get update && apt-get install -y \
    curl unzip git bash build-essential openjdk-21-jdk gradle \
    libglu1-mesa wget xz-utils zip \
    && apt-get clean

# Flutter 설치
RUN git clone https://github.com/flutter/flutter.git /opt/flutter
ENV PATH="/opt/flutter/bin:/opt/flutter/bin/cache/dart-sdk/bin:${PATH}"


# JAVA_HOME 설정
ENV JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"