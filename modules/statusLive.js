const Twitch = require('../modules/twitch');

let isStreaming = false;
let startStream = 0;
let game = '';
let title = '';
let nbrMessages = 0;

class StatusLive {
    static checkStatus(config, client, target) {
        Twitch.callApi(config, client, function (data) {
            if (data.isStreaming && !isStreaming) {
                isStreaming = true;
                startStream = Date.now();
                game = data.game;
                title = data.title;
                nbrMessages = 0;
                client.say(target, "imGlitch Le live commence ! Jeu : " + game);
                return;
            }
            if (!data.isStreaming && isStreaming) {
                isStreaming = false;
                startStream = 0;
                game = '';
                title = '';
                client.say(target, "imGlitch Le live est terminé ! Merci d'avoir suivi le live ! Suivez FunixGaming sur Twitter et checkez la prog pour les prochains lives. !prog & !twitter");
                client.say(target, "Nombre de messages sur ce live : " + nbrMessages);
                nbrMessages = 0;
                return;
            }
            if (data.isStreaming && isStreaming) {
                if (game !== data.game) {
                    game = data.game;
                    client.say(target, "imGlitch Nouveau jeu -> " + game);
                }
                if (title !== data.title) {
                    title = data.title;
                    client.say(target, "imGlitch Nouveau statut -> " + title);
                }
            }
        });
    }
    static incrementMessages() {
        ++nbrMessages;
    }
    static uptime(config, client, target) {
        Twitch.callApi(config, client, function (data) {
            if (data.isStreaming) {
                let uptime = Date.now() - startStream;
                let diff = {};
                uptime = Math.floor(uptime / 1000);
                diff.sec = uptime % 60;
                uptime = Math.floor((uptime - diff.sec) / 60);
                diff.min = uptime % 60;
                uptime = Math.floor((uptime - diff.min) / 60);
                diff.hour = uptime % 24;
                uptime = Math.floor((uptime - diff.hour) / 24);
                diff.day = uptime;
                let strUptime = "imGlitch Live en cours depuis : ";
                if (diff.day > 0) {
                    strUptime += diff.day + "J ";
                }
                if (diff.hour > 0) {
                    strUptime += diff.hour + "h ";
                }
                if (diff.min > 0) {
                    strUptime += diff.min + "m ";
                }
                if (diff.sec > 0) {
                    strUptime += diff.sec + "s";
                }
                client.say(target, strUptime);
            } else {
                client.say(target, "imGlitch Pas de live en cours ! (!prog ou !twitter)");
            }
        });
    }
}

module.exports = StatusLive;