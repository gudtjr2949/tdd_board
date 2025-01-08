# base 이미지 설정
FROM openjdk:17.0.1-jdk-slim

# jar 파일 위치를 변수로 설정
ARG JAR_FILE=build/libs/board-0.0.1-SNAPSHOT.jar

ENV DB_URL=localhost

CMD ["./gradlew", "clean", "build"]

# jar 파일을 컨테이너 내부에 복사
COPY ${JAR_FILE} board-app.jar

# 실행 명령어
CMD ["java", "-jar", "board-app.jar"]