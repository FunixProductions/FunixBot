const Twitch = require('../modules/twitch');
const Logs = require('../modules/logs');
const fs = require('fs');

const dirDataPath = './data/';
const usersGiveawayPath = dirDataPath + 'giveawayUsers.json';
const configGiveawayPath = dirDataPath + 'giveawayConfig.json';

class Giveaway {
    static init(client, user, args, target) {
        if (Twitch.isMod(user) || Twitch.isStreamer(user)) {
            if (args.length === 0) {
                client.whisper(user['display-name'], "Giveaway : Commande incorrecte -> !giveaway [start|roll|stop] [mot gagnant] (sub) l'option sub n'est pas obligatoire.");
                return;
            }
            let status = args[0].toLowerCase();
            checkGiveawayFile();
            let configGiveaway = JSON.parse(fs.readFileSync(configGiveawayPath));
            if (status === 'start') {
                if (configGiveaway.giveaway === true) {
                    client.say(target, "Un giveaway est déjà en cours. (!giveaway stop)");
                    return;
                }
                if (args.length < 2) {
                    client.whisper(user['display-name'], "Giveaway : Commande incorrecte -> !giveaway [start|roll|stop] [mot gagnant] (sub) l'option sub n'est pas obligatoire.");
                    return;
                }
                emptyGiveawayFile();
                let wordWin = args[1];
                configGiveaway.subOnly = false;
                configGiveaway.word = wordWin.toLowerCase();
                configGiveaway.giveaway = true;
                if (args.length >= 3 && args[2].toLowerCase() === 'sub') {
                    configGiveaway.subOnly = true;
                }
                client.say(target, "HolidayPresent Giveaway lancé ! Mot clé -> " + configGiveaway.word + (configGiveaway.subOnly ? " (réservé aux abonnés sub twitch)" : ''));
                console.log("[Giveaway] Lancé par : " + user['display-name'] + ". Mot clé -> " + configGiveaway.word + (configGiveaway.subOnly ? " pour les subs." : ''));
                Logs.logSystem("[Giveaway] Lancé par : " + user['display-name'] + ". Mot clé -> " + configGiveaway.word + (configGiveaway.subOnly ? " pour les subs." : ''));
                fs.writeFileSync(configGiveawayPath, JSON.stringify(configGiveaway));
            } else if (status === 'roll') {
                if (configGiveaway.giveaway === false) {
                    client.say(target, "Aucun giveaway en cours.");
                    return;
                }
                let userList = getParticipants();
                let winner = getWinner(userList);
                if (winner === null) {
                    client.say(target, "HolidayPresent Aucun participant pour le giveaway. Fin du concours.");
                    Logs.logSystem("[Giveaway] Aucun participant pour le giveaway. Fin du concours.");
                    configGiveaway.giveaway = false;
                    emptyGiveawayFile();
                } else {
                    client.say(target, "HolidayPresent Le gagnant est : " + winner);
                    client.whisper(user['display-name'], "Gagnant du concours avec le mot : " + configGiveaway.word + " est -> " + winner);
                    console.log("[Giveaway] Giveaway" + (configGiveaway.subOnly ? " sub" : '') + " [" + configGiveaway.word + "], gagnant : " + winner);
                    Logs.logSystem("[Giveaway] Giveaway" + (configGiveaway.subOnly ? " sub" : '') + " [" + configGiveaway.word + "], gagnant : " + winner);
                    removeWinner(winner);
                }
                fs.writeFileSync(configGiveawayPath, JSON.stringify(configGiveaway));
            } else if (status === 'stop') {
                if (configGiveaway.giveaway === false) return;
                configGiveaway.giveaway = false;
                emptyGiveawayFile();
                client.say(target, "HolidayPresent Giveaway terminé !");
                console.log("[Giveaway] - Giveaway terminé");
                Logs.logSystem("[Giveaway] - Giveaway terminé");
                fs.writeFileSync(configGiveawayPath, JSON.stringify(configGiveaway));
            } else {
                client.whisper(user['display-name'], "Giveaway : Commande incorrecte -> !giveaway [start|roll|stop] [mot gagnant] (sub) l'option sub n'est pas obligatoire.");
            }
        }
    }

    static participant(client, user, message) {
        checkGiveawayFile();
        let configGiveaway = JSON.parse(fs.readFileSync(configGiveawayPath));
        if (configGiveaway.giveaway === true) {
            message = message.toLowerCase();
            if (isParticipant(user['display-name'])) {
                return;
            }
            if (message === configGiveaway.word) {
                if (configGiveaway.subOnly && !Twitch.isSub(user)) {
                    client.whisper(user['display-name'], "Le giveaway est réservé aux abonnés (sub) twitch");
                    return;
                }
                if (!isParticipant(user['display-name'])) {
                    addNewuser(user['display-name']);
                }
                console.log("[Giveaway] - Ajout de " + user['display-name'] + " à la liste");
                client.whisper(user['display-name'], "Participation au giveaway prise en compte !");
            }
        }
    }
}

function emptyGiveawayFile() {
    checkGiveawayFile();
    fs.writeFileSync(usersGiveawayPath, JSON.stringify([]));
}

function checkGiveawayFile() {
    if (!fs.existsSync(dirDataPath)) {
        fs.mkdirSync(dirDataPath);
    }
    if (!fs.existsSync(usersGiveawayPath)) {
        fs.appendFileSync(usersGiveawayPath, []);
    }
    if (!fs.existsSync(configGiveawayPath)) {
        fs.appendFileSync(configGiveawayPath, JSON.stringify({
            'giveaway': false,
            'subOnly': false,
            'word': ''
        }))
    }
}

function isParticipant(userName) {
    checkGiveawayFile();
    let users = JSON.parse(fs.readFileSync(usersGiveawayPath));
    for (let i = 0; i < users.length; ++i) {
        if (users[i] === userName) {
            return true;
        }
    }
    return false;
}

function addNewuser(userName) {
    checkGiveawayFile();
    let users = JSON.parse(fs.readFileSync(usersGiveawayPath));
    users = JSON.stringify(users.concat([userName]));
    fs.writeFileSync(usersGiveawayPath, users);
}

function getParticipants() {
    checkGiveawayFile();
    return JSON.parse(fs.readFileSync(usersGiveawayPath));
}

function removeWinner(user) {
    checkGiveawayFile();
    let userList = JSON.parse(fs.readFileSync(usersGiveawayPath));
    let index = userList.indexOf(user);
    if (index > -1) {
        userList.splice(index, 1);
    }
    userList = JSON.stringify(userList);
    fs.writeFileSync(usersGiveawayPath, userList);
}

function getWinner(userList) {
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