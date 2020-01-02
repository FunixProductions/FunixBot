const Logs = require('../modules/logs');

class FollowCheck {
    static command(client, user, target, Twitch, config) {
        const userId = user['user-id'];
        if (parseInt(userId) === config.settings.streamerId) {
            return;
        }
        callUserApi(config, client, userId, function (followDate) {
            const date = new Date(followDate);
            const formattedDate = formatDateFR(date);
            const followTime = getFollowTime(date);
            client.say(target, user['display-name'] + " dernier follow le : " + formattedDate + " (" + followTime + ")");
        });
    }
}

function callUserApi(config, client, userId, cb) {
    let options = {
        url: 'https://api.twitch.tv/helix/users/follows?to_id=' + config.settings.streamerId + '&from_id=' + userId,
        method: "GET",
        headers: {
            'Client-ID': config.api.twitch.apiKey
        }
    };
    client.api(options, function (err, res, body) {
        if (err) {
            Logs.logError(err);
            throw err;
        }
        cb(body.data[0].followed_at);
    })
}

function getFollowTime(date) {
    const now = Date.now();
    const diffDates = now - (new Date(date));
    const nbrYears = parseInt(diffDates / 31536000000);
    const nbrMonths = parseInt(diffDates / 2628000000);
    const nbrDays = parseInt(diffDates / 86400000);
    return (nbrYears > 0 ? (nbrYears + (nbrYears > 1 ? ' ans ' : ' an ')) : '') +
        nbrMonths > 0 ? nbrMonths + ' mois ' : '' +
        nbrDays > 0 ? (nbrDays + (nbrDays > 1 ? ' jours' : ' jour')) : '';
}

function formatDateFR(date) {
    const months = ['janvier', 'février', 'mars', 'avril', 'mai', 'juin', 'juillet', 'aout', 'septembre', 'octobre', 'novembre', 'décembre'];
    return (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + " " + months[date.getMonth()] + " " + date.getFullYear();
}

module.exports = FollowCheck;
