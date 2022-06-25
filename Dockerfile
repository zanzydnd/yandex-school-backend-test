FROM openjdk:17
ADD backend-school.jar backend-school.jar
ENTRYPOINT ["java", "-jar" ,"backend-school.jar"]