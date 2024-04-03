# Используем базовый образ Ubuntu
FROM ubuntu:latest

# Установка необходимых пакетов
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    maven \
    git

# Копирование исходного кода приложения в контейнер
COPY . /app

# Устанавливаем рабочую директорию
WORKDIR /app

# Собираем приложение с помощью Maven
RUN mvn package -DskipTests

# Запускаем приложение
CMD ["java", "-jar", "target/quarkus-app/quarkus-run.jar"]
