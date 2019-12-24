const fs = require('fs');
const logDir = 'logs/';

function getDateData() {
    const now = new Date();
    return {
        time: (now.getHours() < 10 ? now.getHours().toString() + '0' : now.getHours().toString()) + ':' +
            (now.getMinutes() < 10 ? now.getMinutes().toString() + '0' : now.getMinutes().toString()) + ':' +
            (now.getSeconds() < 10 ? now.getSeconds().toString() + '0' : now.getSeconds()),
        todayDate: (now.getDate() < 10 ? now.getDate() + '0' : now.getDate()) + '-' +
        (now.getMonth() < 10 ? now.getMonth() + '0' : now.getMonth()) + '-' +
        now.getFullYear()
    };
}

function checkLogDir() {
    if (!fs.existsSync(logDir)) {
        fs.mkdirSync(logDir);
    }
}

class Logs {
    static async log(user, msg) {
        checkLogDir();
        const dataDate = getDateData();
        let messageLog = "INFO [" + dataDate.time + "] <" + user['display-name'] + "> " + msg + '\n';
        fs.appendFile(logDir + dataDate.todayDate + '.log', messageLog, function (err) {
            if (err) throw err;
        });
    }

    static async logSystem(msg) {
        checkLogDir();
        const dataDate = getDateData();
        let messageLog = "SYSTEM [" + dataDate.time + "] " + msg + '\n';
        fs.appendFile(logDir + dataDate.todayDate + '.log', messageLog, function (err) {
            if (err) throw err;
        });
    }
}

module.exports = Logs;