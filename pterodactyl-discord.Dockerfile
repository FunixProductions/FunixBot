FROM maven:3-openjdk-17 AS MAVEN

MAINTAINER Antoine PRONNIER, <antoine.pronnier@gmail.com>

WORKDIR /container/funixbot/

COPY pom.xml .

COPY Core/pom.xml ./core/
COPY Core/src ./core/src

COPY funixbot-discord/pom.xml ./funixbot-discord/
COPY funixbot-discord/src ./funixbot-discord/src

COPY funixbot-twitch/pom.xml ./funixbot-twitch/
COPY funixbot-twitch/src ./funixbot-twitch/src

RUN mvn clean package -B
RUN rm funixbot-discord/target/funix-bot-discord-*-javadoc.jar
RUN rm funixbot-discord/target/funix-bot-discord-*-sources.jar
RUN rm funixbot-twitch/target/funix-bot-twitch-*-sources.jar
RUN rm funixbot-twitch/target/funix-bot-twitch-*-sources.jar

FROM openjdk:17 AS FINAL_PTEROQ

MAINTAINER Antoine PRONNIER, <antoine.pronnier@gmail.com>

USER container
ENV USER=container HOME=/home/container
WORKDIR /home/container

COPY --from=MAVEN /container/funixbot/funixbot-discord/target/funix-bot-discord-*.jar /home/java/funixbot.jar

COPY ./entrypointPteroq.sh /entrypoint.sh

CMD ["/bin/bash", "/entrypoint.sh"]
