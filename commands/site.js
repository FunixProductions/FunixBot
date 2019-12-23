const pacifista = "https://pacifista.fr";
const funixgaming = "https://funixgaming.fr";

class Site {
    static command(client, channel) {
        client.say(channel, "Site de FunixGaming : " + funixgaming);
        client.say(channel, "Site de Pacifista : " + pacifista);
    }
}

module.exports = Site;