# Используем базовый образ Ubuntu
FROM ubuntu:latest

# Установка необходимых пакетов
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    maven \
    git

# Устанавливаем рабочую директорию

WORKDIR /app
# Копирование исходного кода приложения в контейнер

COPY . /app

# Собираем приложение с помощью Maven

CMD ./mvnw clean package -DuberJar=true -DskipTests -X

# Запускаем приложение
CMD ["java", "-jar", "target/quarkus-app/quarkus-run.jar"]
