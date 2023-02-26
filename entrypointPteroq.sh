#!/bin/bash
cd /home/container

echo "Get latest funixbot.jar"
rm funixbot.jar
cp /home/java/funixbot.jar funixbot.jar

echo "Java version"
java --version

# Replace Startup Variables
MODIFIED_STARTUP=`eval echo $(echo ${STARTUP} | sed -e 's/{{/${/g' -e 's/}}/}/g')`
echo ":/home/container$ ${MODIFIED_STARTUP}"

# Run the Server
${MODIFIED_STARTUP}