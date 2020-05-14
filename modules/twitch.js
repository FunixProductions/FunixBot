const tmi = require('tmi.js');
const Logs = require('./logs');
const dataDirPath = './data/';
const bearerTokenFilePath = dataDirPath + 'bearerToken.json';
const fs = require('fs');
const fetch = require('node-fetch');

class Twitch {
    constructor(config) {
        const username = config.channels[0];
        console.log("[TWITCH] - Connection au chat : " + username);
        const client = new tmi.client(config);
        this.client = client;
        client.on('connected', function () {
            client.color("Blue");
            console.log("\x1b[42m[TWITCH] - Bot connecté sur le chat de " + username + "\x1b[0m");
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
    if (user.badges === null)
        return false;
    return user.badges.broadcaster === '1';
}

function getChatters(channel, client, cb) {
    let options = {
        url: "https://tmi.twitch.tv/group/user/" + channel.substr(1) + "/chatters",
        method: "GET"
    };
    client.api(options, function (err, res, body) {
        if (err) {
            Logs.logError(err);
            throw err;
        }
        cb(body.chatters);
    });
}

function getBearerToken(config) {
    return function (callback) {
        if (!fs.existsSync(dataDirPath))
            fs.mkdirSync(dataDirPath);
        if (!fs.existsSync(bearerTokenFilePath))
            fs.appendFileSync(bearerTokenFilePath, '{"notGenerated": true}');
        const bearerStored = JSON.parse(fs.readFileSync(bearerTokenFilePath));
        if (bearerStored.notGenerated || (new Date().getTime() - bearerStored.generated_timestamp >= bearerStored.expires_in - 60)) {
            let url = 'https://id.twitch.tv/oauth2/token?client_id=' + config.apiKey +
                '&client_secret=' + config.clientSecret +
                '&grant_type=client_credentials' +
                '&scope=bits:read channel:edit:commercial channel:read:subscriptions channel:moderate chat:edit chat:read whispers:read whispers:edit clips:edit user:edit user:edit:broadcast user:edit:follows';
            let options = {
                method: "POST"
            };
            fetch(url, options)
                .then(res => res.json())
                .then((body) => {
                    if (!body.access_token || !body.expires_in) {
                        console.log("\x1b[31mERROR - FETCH BEARER TOKEN (More info in logs)\x1b[0m");
                        Logs.logError("Error while get new bearer token: " + JSON.stringify(body));
                        return;
                    }
                    const toFile = {
                        "generated_timestamp": new Date().getTime(),
                        "access_token": body.access_token,
                        "expires_in": body.expires_in
                    }
                    fs.writeFileSync(bearerTokenFilePath, JSON.stringify(toFile), 'utf8');
                    callback(body.access_token);
                });
        } else {
            callback(bearerStored.access_token);
        }
    }
}

function callTwitchApi(config, client, cb) {
    getBearerToken(config)((bearerToken) => {
        let options = {
            url: 'https://api.twitch.tv/helix/streams?user_login=' + config.channel,
            method: "GET",
            headers: {
                'Client-ID': config.apiKey,
                'Authorization' : 'Bearer ' + bearerToken
            }
        };
        client.api(options, function (err, res, body) {
            let dataApi = {
                isStreaming: "null",
                title: "null",
                game_id: 0,
                game: "null",
                viewers: 0
            };
            if (err) {
                Logs.logError(err);
                throw err;
            }
            if (!body.data) {
                console.log("\x1b[31mERROR - FETCH TWITCH STREAM DATA (More info in logs)\x1b[0m");
                Logs.logError("Error when fetching twitch streams api: " + JSON.stringify(body));
                return;
            }
            if (body.data.length === 0) {
                dataApi.isStreaming = false;
                cb(dataApi);
                return;
            }
            let live = body.data[0];
            dataApi.isStreaming = true;
            dataApi.title = live.title;
            dataApi.viewers = live.viewer_count;
            dataApi.game_id = live.game_id;
            options.url = 'https://api.twitch.tv/helix/games?id=' + live.game_id;
            getGameName(client, options)((gameName) => {
                dataApi.game = gameName;
                cb(dataApi);
            });
        });
    });
}

function getGameName(client, options) {
    return function (callback) {
        client.api(options, function (err, res, body) {
            if (err) {
                Logs.logError(err);
                throw err;
            }
            if (!body.data) {
                console.log("\x1b[31mERROR - FETCH TWITCH STREAM DATA (More info in logs)\x1b[0m");
                Logs.logError("Error when fetching twitch streams api: " + JSON.stringify(body));
                return;
            }
            if (body.data.length > 0)
                callback(body.data[0].name);
        });
    };
}

module.exports = Twitch;
module.exports.isMod = isMod;
module.exports.isSub = isSub;
module.exports.isStreamer = isStreamer;
module.exports.callApi = callTwitchApi;
module.exports.getChatters = getChatters;
module.exports.getBearerToken = getBearerToken;
