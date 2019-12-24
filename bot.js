process.stdin.resume();
process.stdin.setEncoding('utf8');
console.log("\x1b[33mDémarrage du bot...\x1b[0m");
const Logs = require('./modules/logs');
Logs.logSystem("Démarrage du bot...");
const Mysql = require('./modules/mysql');
const Twitch = require('./modules/twitch');
const AutoMessagesClass = require('./modules/autoMessages');
const config = require('./.env.json');

let database = new Mysql(config.mysql);
let FunixBot = new Twitch(config.funixbot).client;
let AutoMessages = new AutoMessagesClass(config.settings.autoMessages);

let commands = {
    prime: require('./commands/prime'),
    discord: require('./commands/discord'),
    site: require('./commands/site'),
    help: require('./commands/help'),
    ip: require('./commands/ip')
};

const prefix = '!';

FunixBot.on('message', function (target, user, msg, self) {
    if (self) { return; }
    AutoMessages.newMessage(FunixBot, target);
    msg = msg.replace(/\s\s+/g, ' ');
    msg = msg.toLowerCase();
    Logs.log(user, msg);
    const args = msg.split(' ');
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
            default:
                database.getChatCommand(cmd, function (message) {
                    if (message !== null) {
                        FunixBot.say(target, message);
                    } else {
                        FunixBot.whisper(user['display-name'], "Commande non reconnue : " + cmd + ". Tapez !help pour avoir la liste des commandes.");
                    }
                });
        }
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
            Logs.logSystem("Arrêt du bot.");
            process.exit(0);
            break;
        case 'say':
            let message = '';
            for (let i = 0; i < args.length; ++i) {
                message += args[i] + ' ';
            }
            FunixBot.say(target, message);
            break;
        default:
            console.log("Commande non reconnue : " + cmd);
    }
});
