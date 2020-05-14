const Logs = require('../modules/logs');

class FollowCheck {
    static command(client, user, target, Twitch, config) {
        const userId = user['user-id'];
        if (parseInt(userId) === config.settings.streamerId) {
            return;
        }
        callUserApi(config, client, userId, function (followDate) {
            if (followDate === null) {
                client.say(target, user['display-name'] + " ne suit pas la chaine.");
                return;
            }
            const date = new Date(followDate);
            const formattedDate = formatDateFR(date);
            const followTime = getFollowTime(date);
            client.say(target, user['display-name'] + " suit la chaine depuis le : " + formattedDate + " (" + followTime + ")");
        });
    }
}

function callUserApi(config, client, userId, cb) {
    Twitch.getBearerToken(config.api.twitch)((bearerToken) => {
        let options = {
            url: 'https://api.twitch.tv/helix/users/follows?to_id=' + config.settings.streamerId + '&from_id=' + userId,
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
            if (!body.data) {
                console.log("\x1b[31mERROR - FETCH TWITCH FOLLOWS DATA (More info in logs)\x1b[0m");
                Logs.logError("Error when fetching twitch follow api: " + JSON.stringify(body));
                return;
            }
            if (body.data.length > 0)
                cb(body.data[0].followed_at);
            else
                cb(null);
        })
    });
}

function getFollowTime(date) {
    const now = Date.now();
    const diffDates = now - (new Date(date));
    const nbrYears = parseInt(diffDates / 31536000000);
    const nbrMonths = parseInt(diffDates / 2628000000);
    const nbrDays = parseInt(diffDates / 86400000);
    let followTime = '';
    if (nbrYears > 0) {
        followTime += nbrYears + (nbrYears > 1 ? ' ans ' : ' an ');
    }
    if (nbrMonths > 0) {
        followTime += nbrMonths + ' mois ';
    }
    if (nbrDays > 0) {
        followTime += nbrDays + (nbrDays > 1 ? ' jours' : ' jour');
    }
    return followTime;
}

function formatDateFR(date) {
    const months = ['janvier', 'février', 'mars', 'avril', 'mai', 'juin', 'juillet', 'aout', 'septembre', 'octobre', 'novembre', 'décembre'];
    return (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + " " + months[date.getMonth()] + " " + date.getFullYear();
}

module.exports = FollowCheck;
