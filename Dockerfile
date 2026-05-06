# Dockerfile
FROM tomcat:10.1-jdk11

# Supprime les apps par défaut de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copie le WAR déjà compilé (ou compile avec Maven)
COPY target/projet-backend-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose le port
EXPOSE 8080

CMD ["catalina.sh", "run"]