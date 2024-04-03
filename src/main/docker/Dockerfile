# Используем базовый образ Ubuntu
FROM ubuntu:latest

# Устанавливаем необходимые пакеты
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    default-jdk

# Скачиваем и устанавливаем GraalVM
RUN wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-21.2.0/graalvm-ce-java11-linux-amd64-21.2.0.tar.gz
RUN tar -xvf graalvm-ce-java11-linux-amd64-21.2.0.tar.gz
ENV GRAALVM_HOME=/graalvm-ce-java11-21.2.0
ENV PATH=$GRAALVM_HOME/bin:$PATH

# Копируем приложение Quarkus в контейнер
COPY /home/arseniy/IdeaProjects/conbd/target/quarkus-app/quarkus-run.jar /app/
WORKDIR /app

# Указываем порт, который будет открыт в контейнере
EXPOSE 8080

# Запускаем приложение Quarkus
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
