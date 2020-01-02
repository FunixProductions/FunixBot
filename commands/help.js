let commandsIncluded = [
    "discord",
    "help",
    "prime",
    "site",
    "ip",
    "level",
    "fc"
];

class Help {
    static command(client, database, channel) {
        database.getCommandList(function (commandList) {
            let message = "Voici la liste des commandes (précédées d'un !) -> ";
            for (let i = 0; i < commandList.length; ++i) {
                message += commandList[i] + ' ';
            }
            for (let i = 0; i < commandsIncluded.length; ++i) {
                message += commandsIncluded[i] + ' ';
            }
            client.say(channel, message);
        });
    }
}

module.exports = Help;