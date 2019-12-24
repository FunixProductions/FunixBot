const tmi = require('tmi.js');
const Logs = require('./logs');

class Twitch {
    constructor(config) {
        const username = config.channels[0];
        console.log("[TWITCH] - Connection au chat : " + username);
        const client = new tmi.client(config);
        this.client = client;
        client.on('connected', function () {
            client.color("Blue");
            console.log("\x1b[42m[TWITCH] - Bot connecté sur le chat de " + username + "\x1b[0m");
            Logs.logSystem("Bot connecté sur le chat de " + username);
        });

        client.on("hosted", function (channel, username, viewers, autohost) {
            if (viewers > 1) {
                client.action(channel, username + " Est en train de nous host avec: " + viewers + " viewers ! funixgWut funixgWut funixgWut");
            }
        });

        client.on("raided", function (channel, username, viewers) {
            if (viewers > 1) {
                client.action(channel, username + " Est en train de nous raid avec: " + viewers + " viewers ! funixgWut funixgWut funixgWut");
            }
        });

        client.on('hosting', function (channel, target, viewers) {
            client.action(channel, "FunixGaming host la chaine : " + target + ", pour un total de " + viewers + " viewers ! funixgPoce");
        });
        client.connect();
    }
}

function isMod(user) {
    return user.mod;
}

function isSub(user) {
    return user.subscriber;
}

function isStreamer(user) {
    return user.badges.broadcaster === '1';
}

function callTwitchApi(config, client, cb) {
    let options = {
        url: 'https://api.twitch.tv/helix/streams?user_login=' + config.channel,
        method: "GET",
        headers: {
            'Client-ID': config.apiKey
        }
    };
    client.api(options, function (err, res, body) {
        let dataApi = {
            isStreaming: "null",
            title: "null",
            game: "null",
            viewers: 0
        };
        if (err) throw err;
        if (body.data.length === 0) {
            dataApi.isStreaming = false;
            cb(dataApi);
            return;
        }
        let live = body.data[0];
        dataApi.isStreaming = true;
        dataApi.title = live.title;
        dataApi.viewers = live.viewer_count;
        options.url = 'https://api.twitch.tv/helix/games?id=' + live.game_id;
        getGameName(client, options)((gameName) => {
            dataApi.game = gameName;
            cb(dataApi);
        });
    });
}

function getGameName(client, options) {
    return function (callback) {
        client.api(options, function (err, res, body) {
            if (err) throw err;
            callback(body.data[0].name);
        });
    };
}

module.exports = Twitch;
module.exports.isMod = isMod;
module.exports.isSub = isSub;
module.exports.isStreamer = isStreamer;
module.exports.callApi = callTwitchApi;
