process.stdin.resume();
process.stdin.setEncoding('utf8');
console.log("\x1b[33mDémarrage du bot...\x1b[0m");

const fs = require('fs');

const Logs = require('./modules/logs');
const Mysql = require('./modules/mysql');
const Twitch = require('./modules/twitch');
const NewFollowerClass = require('./modules/newFollower');
const AutoMessagesClass = require('./modules/autoMessages');
const StatusLive = require('./modules/statusLive');
const WebServerClass = require('./modules/webserver');
const config = require('./.env.json');

let database = new Mysql(config.mysql);
let FunixBot = new Twitch(config.funixbot).client;
let AutoMessages = new AutoMessagesClass(config.settings.autoMessages);
let NewFollower = new NewFollowerClass(FunixBot, config);
let WebServer = new WebServerClass(config, database);

let commands = {
    prime: require('./commands/prime'),
    discord: require('./commands/discord'),
    site: require('./commands/site'),
    help: require('./commands/help'),
    ip: require('./commands/ip'),
    giveaway: require('./commands/giveaway'),
    level: require('./commands/level'),
    followCheck: require('./commands/followCheck')
};

const prefix = '!';

FunixBot.on('message', function (target, user, msg, self) {
    if (self) { return; }
    msg = msg.replace(/\s\s+/g, ' ');
    msg = msg.toLowerCase();
    let args = msg.split(' ');
    commands.giveaway.participant(FunixBot, user, args[0]);
    database.messageUserXP(user, FunixBot, target, Twitch, config);
    if (args[0].charAt(0) === prefix) {
        let cmd = args[0].substr(1);
        args.shift();
        switch (cmd) {
            case 'prime':
                commands.prime.command(FunixBot, user['display-name']);
                break;
            case 'discord':
                commands.discord.command(FunixBot, target);
                break;
            case 'site':
                commands.site.command(FunixBot, target);
                break;
            case 'help':
                commands.help.command(FunixBot, database, target);
                break;
            case 'ip':
                commands.ip.command(FunixBot, target);
                break;
            case 'giveaway':
                commands.giveaway.init(FunixBot, user, args, target);
                break;
            case 'level':
                commands.level.command(FunixBot, user, target, database);
                break;
            case 'fc':
                commands.followCheck.command(FunixBot, user, target, Twitch, config);
                break;
            case 'uptime':
                StatusLive.uptime(config.api.twitch, FunixBot, target);
                break;
            default:
                database.getChatCommand(cmd, function (message) {
                    if (message !== null) {
                        FunixBot.say(target, message);
                    } else {
                        FunixBot.whisper(user['display-name'], "Commande non reconnue : " + cmd + ". Tapez !help pour avoir la liste des commandes.");
                    }
                });
        }
    } else {
        AutoMessages.newMessage(FunixBot, target);
        Logs.log(user, msg);
        StatusLive.incrementMessages();
    }
});

process.stdin.on('data', function (msg) {
    msg = msg.replace(/(\r\n|\n|\r)/gm, "");
    msg = msg.replace(/\s\s+/g, ' ');
    const args = msg.split(' ');
    const cmd = args[0];
    const target = config.funixbot.channels[0];
    args.shift();
    switch (cmd) {
        case 'stop':
            console.log("\x1b[33mArrêt du bot.\x1b[0m");
            process.exit(0);
            break;
        case 'say':
            let message = '';
            for (let i = 0; i < args.length; ++i) {
                message += args[i] + ' ';
            }
            FunixBot.say(target, message);
            break;
        case 'purgeLogs':
            const now = Date.now();
            let logsDeleted = 0;
            fs.readdir(Logs.logDir, function (err, files) {
                for (let i = 0; i < files.length; ++i) {
                    let file = files[i];
                    if (file !== '.gitkeep') {
                        file = file.replace(/\.[^/.]+$/, "");
                        let date = Date.parse(file);
                        if (now - date >= 5259600000) {
                            fs.unlink("./" + Logs.logDir + file + '.log', function (err) {
                                if (err) {
                                    Logs.logError(err);
                                    throw err;
                                }
                            });
                            logsDeleted++;
                        }
                    }
                }
                Logs.logSystem(logsDeleted + " logs ont été supprimés");
                console.log(logsDeleted + " logs ont été supprimés");
            });
            break;
        default:
            console.log("Commande non reconnue : " + cmd);
    }
});

setInterval(function () {
    Twitch.getChatters(config.funixbot.channels[0], FunixBot, function (users) {
        database.timeUsersXP(users, FunixBot, config.funixbot.channels[0], Twitch, config);
    });
}, 300000);

setInterval(function () {
    StatusLive.checkStatus(config.api.twitch, FunixBot, config.funixbot.channels[0]);
    WebServer.updateTopUsers();
}, 10000);

setInterval(function () {
    NewFollower.checkNewFollower();
}, 5000);