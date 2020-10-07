const express = require('express');
const es6Renderer = require('express-es6-template-engine');
const fs = require('fs');
const Logs = require('../logs');
const config = require('../../.env.json');

const app = express();
app.engine('html', es6Renderer);
app.set('views', './modules/webserver/views/');
app.set('view engine', 'html');

let database;

class WebServer {
    constructor(db) {
        const port = config.settings.webserverPort;
        database = db;
        app.listen(port, function () {
            console.log("Webserver running on port: " + port);
        });
    }
}

app.get('/twitch/sounds', function (req, res) {
    res.render('twitch/sounds', {locals: {config: JSON.stringify(config)}});
});

app.get('/api/topusers', function (req, res) {
    const apiKeyHeader = req.header('api-key');
    checkApiKey(apiKeyHeader, req, res, function (status) {
        if (status) {
            database.getUserClassment(function (data) {
                res.type('json');
                res.json(data);
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
                res.json(data);
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