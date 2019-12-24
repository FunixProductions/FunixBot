const Twitch = require('../modules/twitch');
const Logs = require('../modules/logs');

let giveaway = false;
let subOnly = false;
let word = '';
let userList = [];

class Giveaway {
    static init(client, user, args, target) {
        if (Twitch.isMod(user) || Twitch.isStreamer(user)) {
            if (args.length === 0) {
                client.whisper(user['display-name'], "Giveaway : Commande incorrecte -> !giveaway [start|roll|stop] [mot gagnant] (sub) l'option sub n'est pas obligatoire.");
                return;
            }
            let status = args[0].toLowerCase();
            if (status === 'start') {
                if (giveaway === true) {
                    client.say(target, "Un giveaway est déjà en cours. (!giveaway stop)");
                    return;
                }
                if (args.length < 2) {
                    client.whisper(user['display-name'], "Giveaway : Commande incorrecte -> !giveaway [start|roll|stop] [mot gagnant] (sub) l'option sub n'est pas obligatoire.");
                    return;
                }
                let wordWin = args[1];
                userList = [];
                subOnly = false;
                word = wordWin.toLowerCase();
                giveaway = true;
                if (args.length >= 3 && args[2].toLowerCase() === 'sub') {
                    subOnly = true;
                }
                client.say(target, "HolidayPresent Giveaway lancé ! Mot clé -> " + word + (subOnly ? " (réservé aux abonnés sub twitch)" : ''));
                console.log("[Giveaway] Lancé par : " + user['display-name'] + ". Mot clé -> " + word + (subOnly ? " pour les subs." : ''));
                Logs.logSystem("[Giveaway] Lancé par : " + user['display-name'] + ". Mot clé -> " + word + (subOnly ? " pour les subs." : ''));
            } else if (status === 'roll') {
                if (giveaway === false) {
                    client.say(target, "Aucun giveaway en cours.");
                    return;
                }
                let winner = getWinner();
                if (winner === null) {
                    client.say(target, "HolidayPresent Aucun participant pour le giveaway. Fin du concours.");
                    Logs.logSystem("[Giveaway] Aucun participant pour le giveaway. Fin du concours.");
                    giveaway = false;
                } else {
                    client.say(target, "HolidayPresent Le gagnant est : " + winner);
                    client.whisper(user['display-name'], "Gagnant du concours avec le mot : " + word + " est -> " + winner);
                    console.log("[Giveaway] Giveaway" + (subOnly ? " sub" : '') + " [" + word + "], gagnant : " + winner);
                    Logs.logSystem("[Giveaway] Giveaway" + (subOnly ? " sub" : '') + " [" + word + "], gagnant : " + winner);
                    removeWinner(winner);
                }
            } else if (status === 'stop') {
                if (giveaway === false) return;
                giveaway = false;
                userList = [];
                client.say(target, "HolidayPresent Giveaway terminé !");
                console.log("[Giveaway] - Giveaway terminé");
                Logs.logSystem("[Giveaway] - Giveaway terminé");
            } else {
                client.whisper(user['display-name'], "Giveaway : Commande incorrecte -> !giveaway [start|roll|stop] [mot gagnant] (sub) l'option sub n'est pas obligatoire.");
            }
        }
    }

    static participant(client, user, message) {
        if (giveaway === true) {
            message = message.toLowerCase();
            if (userInList(user['display-name'])) {
                return;
            }
            if (message === word) {
                if (subOnly && !Twitch.isSub(user)) {
                    client.whisper(user['display-name'], "Le giveaway est réservé aux abonnés (sub) twitch");
                    return;
                }
                userList.push(user['display-name']);
                console.log("[Giveaway] - Ajout de " + user['display-name'] + " à la liste");
                client.whisper(user['display-name'], "Participation au giveaway prise en compte !");
            }
        }
    }
}

function removeWinner(user) {
    let index = userList.indexOf(user);
    if (index > -1) {
        userList.splice(index, 1);
    }
}

function userInList(user) {
    for (let i = 0; i < userList.length; ++i) {
        if (userList[i] === user) {
            return true;
        }
    }
    return false;
}

function getWinner() {
    const nbrUsers = userList.length;
    if (nbrUsers === 0) {
        return null;
    }
    if (nbrUsers === 1) {
        return userList[0];
    }
    const winner = Math.floor(Math.random() * nbrUsers);
    return userList[winner];
}

module.exports = Giveaway;