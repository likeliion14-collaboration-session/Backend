#!/bin/bash
PID=$(pgrep -f 'app.jar')
if [ -n "$PID" ]; then
  echo "기존 프로세스 종료: $PID"
  kill -9 $PID
fi

nohup java -jar -Dspring.profiles.active=prod app.jar > app.log 2>&1 &
echo "서버 시작됨. 로그: app.log"

# 실행 권한 부여가 필요한 경우: chmod +x deploy/run.sh
