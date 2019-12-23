const urlFunix = "https://discord.gg/4v8XAVK";
const urlPacifista = "https://discord.gg/cUPbDxB";

class Discord {
    static command(client, channel) {
        client.say(channel, "Discord de FunixGaming : " + urlFunix);
        client.say(channel, "Discord de Pacifista : " + urlPacifista);
    }
}

module.exports = Discord;