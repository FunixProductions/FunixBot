const ip = "play.pacifista.fr";
const version = "1.16.3";

class Ip {
    static command(client, channel) {
        client.say(channel, "Serveur minecraft Pacifista IP : " + ip + " en " + version);
    }
}

module.exports = Ip;