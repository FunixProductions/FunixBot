const express = require('express');
const fs = require('fs');
const Logs = require('../modules/logs');

const app = express();
let database;

let url = '';
let topUsers = [];

class WebServer {
    constructor(config, db) {
        const port = config.settings.webserverPort;
        const debug = config.funixbot.options.debug;
        database = db;
        app.listen(port, function () {
            if (debug) {
                url = 'http://localhost:' + port;
                console.log("Webserver running on " + url);
            } else {
                url = 'http://funixgaming.fr:' + port;
                console.log("Webserver running on " + url);
            }
            database.getUserClassment(function (data) {
                topUsers = data;
            });
        });
    }
}

app.get('/api/topusers', function (req, res) {
    const apiKeyHeader = req.header('api-key');
    checkApiKey(apiKeyHeader, req, res, function (status) {
        if (status) {
            database.getUserClassment(function (data) {
                res.send(data);
            });
        }
    });
});

app.get('/api/commandlist', function (req, res) {
    const apiKeyHeader = req.header('api-key');
    checkApiKey(apiKeyHeader, req, res, function (status) {
        if (status) {
            database.getCommandList(function (data) {
                res.type('json');
                res.send(data);
            });
        }
    });
});

function checkApiKey(apiKeyHeader, req, res, cb) {
    const dataFolderPath = './data/';
    const apiKeyPath = dataFolderPath + 'apikey.txt';
    let data = {success: false};
    res.type('json');
    if (!fs.existsSync(dataFolderPath)) {
        fs.mkdirSync(dataFolderPath);
    }
    if (!fs.existsSync(apiKeyPath)) {
        data.error = "Pas de clé api générée sur le bot.";
        res.send(data);
        cb(false);
        return;
    }
    if (!fs.existsSync(dataFolderPath)) {
        fs.mkdirSync(dataFolderPath);
    }
    fs.readFile('./data/apikey.txt', function (err, key) {
        if (err) {
            Logs.logError(err);
            throw err;
        }
        const apiKey = key.toString('utf-8');
        if (apiKey !== apiKeyHeader) {
            data.error = "Clé API incorrecte.";
            res.send(data);
            cb(false);
        } else {
            cb(true);
        }
    });
}

module.exports = WebServer;