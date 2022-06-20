FROM openjdk:17
ADD target/backend-school.jar backend-school.jar
ENTRYPOINT ["java", "-jar" ,"backend-school.jar"]