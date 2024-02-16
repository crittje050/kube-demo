FROM maven:3.9.6-eclipse-temurin-21

RUN mkdir -p /app

COPY . /app

WORKDIR app

RUN mvn -e clean package -DskipTests --no-transfer-progress

EXPOSE 8080

CMD java -jar ./target/kube-demo.jar