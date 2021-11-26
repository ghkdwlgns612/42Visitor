#!/bin/bash

REPOSITORY=/home/jaehchoi/visitor_server
PROJECT_NAME=visitor

CURRENT_PID=$(pgrep -f ${PROJECT_NAME}.*.jar)

echo "현재 구동 중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
        echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
        echo "> kill -15 $CURRENT_PID"
        kill -15 $CURRENT_PID
        sleep 10
fi


cd $REPOSITORY

echo "> Git Pull"

git pull

echo "> 프로젝트 Build 시작"

./gradlew clean bootjar

cp $REPOSITORY/build/libs/*.jar /home/jaehchoi/visitor_jar/

cd /home/jaehchoi/visitor_jar

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls -tr . | grep jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

nohup java -jar -Dspring.profiles.active=dev $JAR_NAME > /dev/null 2>&1 &
