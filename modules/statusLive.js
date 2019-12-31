let isStreaming = false;
let startStream = 0;
let game = '';
let title = '';
let nbrMessages = 0;

class StatusLive {
    static checkStatus(Twitch, config, client, target) {
        Twitch.callApi(config, client, function (data) {
            if (data.isStreaming && !isStreaming) {
                isStreaming = true;
                startStream = Date.now();
                game = data.game;
                title = data.title;
                nbrMessages = 0;
                client.say(target, "imGlitch Le live commence ! COUCOU ! funixgLuv");
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
}

module.exports = StatusLive;