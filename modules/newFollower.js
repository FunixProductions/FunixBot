const Logs = require('./logs');
const dataDirPath = './data/';
const followersFilePath = dataDirPath + 'followers.json';
const fs = require('fs');
const Twitch = require('../modules/twitch');

class NewFollower {
    constructor(client, config) {
        getLastFollower(client, config, function (data) {
            hasAlreadyFollowed(data.userId);
        });
        this.client = client;
        this.config = config;
    }

    checkNewFollower() {
        const client = this.client;
        const config = this.config;
        getLastFollower(this.client, this.config, function (data) {
            if (!hasAlreadyFollowed(data.userId)) {
                client.say(config.funixbot.channels[0], "VoHiYo Nouveau follow : " + data.userName + " (" + data.nbrFollows + " followers)");
            }
        });
    }
}

function hasAlreadyFollowed(userId) {
    checkFollowersFile();
    let followers = JSON.parse(fs.readFileSync(followersFilePath));
    for (let i = 0; i < followers.length; ++i) {
        if (followers[i] === userId) {
            return true;
        }
    }
    followers = JSON.stringify(followers.concat([userId]));
    fs.writeFileSync(followersFilePath, followers);
    return false;
}

function getLastFollower(client, config, cb) {
    Twitch.getBearerToken(config.api.twitch)((bearerToken) => {
        const options = {
            url: 'https://api.twitch.tv/helix/users/follows?to_id=' + config.settings.streamerId + '&first=1',
            method: "GET",
            headers: {
                'Client-ID': config.api.twitch.apiKey,
                'Authorization' : 'Bearer ' + bearerToken
            }
        };
        client.api(options, function (err, res, body) {
            if (err) {
                Logs.logError(err);
                throw err;
            }
            if (!body.data || body.data.length < 1) {
                console.log("\x1b[31mERROR - FETCH TWITCH STREAM LAST FOLLOWER (More info in logs)\x1b[0m");
                Logs.logError("Error when fetching twitch last follow api: " + JSON.stringify(body));
                return;
            }
            const data = {
                userName: body.data[0].from_name,
                userId: parseInt(body.data[0].from_id),
                nbrFollows: body.total
            };
            cb(data);
        });
    })
}

function checkFollowersFile() {
    if (!fs.existsSync(dataDirPath)) {
        fs.mkdirSync(dataDirPath);
    }
    if (!fs.existsSync(followersFilePath)) {
        fs.appendFileSync(followersFilePath, '[]');
    }
}

module.exports = NewFollower;