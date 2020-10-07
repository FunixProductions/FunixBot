const config = require('../.env.json');
const WebSocketClient = require('websocket').client;

function connect() {
    let heartbeatInterval = 1000 * 60; //ms between PING's
    let heartbeatHandle;

    const ws = new WebSocketClient();
    ws.connect('wss://pubsub-edge.twitch.tv/', null, null, null, null);

    ws.on('connect', function (connection) {
        console.log("[PubSub] - Connected !");

        connection.on('error', function (err) {
            console.error("[PubSub ERROR] - " + err.toString());
        });

        connection.on('close', function () {
            console.log("[PubSub] - Colsed");
            clearInterval(heartbeatHandle);
            ws.abort();
            connect();
        });

        connection.on('message', function (message) {
            let serverMsg = JSON.parse(message.utf8Data);
            if (serverMsg.type === 'RECONNECT') {
                clearInterval(heartbeatHandle);
                ws.abort();
                connect();
                console.log("[PubSub] - Reconnecting...");
            } else if (serverMsg.type === 'RESPONSE') {
                if (serverMsg.error.length > 0)
                    console.log("response: " + serverMsg.error);
            } else if (serverMsg.type === 'MESSAGE') {
                if (serverMsg.data.topic === 'channel-points-channel-v1.' + config.settings.streamerId) {
                    let redemption = serverMsg.data.message;
                    console.log("Channel point !");//TODO Make more work on it
                }
            }
        });

        function heartbeat() {
            let message = {
                type: 'PING'
            };
            connection.sendUTF(JSON.stringify(message));
        }

        function listen(topic) {
            let message = {
                type: 'LISTEN',
                nonce: nonce(15),
                data: {
                    topics: topic,
                    auth_token: 'k8qm0y4b9b2ks148jds88c2x2reo4t'
                }
            };
            connection.sendUTF(JSON.stringify(message));
        }

        heartbeat();
        listen(['channel-points-channel-v1.' + config.settings.streamerId]);
        heartbeatHandle = setInterval(heartbeat, heartbeatInterval);
    });

    ws.on('connectFailed', function () {
        console.error("[PubSub] - Connection failed !");
        clearInterval(heartbeatHandle);
        ws.abort();
        connect();
    })
}

// Source: https://www.thepolyglotdeveloper.com/2015/03/create-a-random-nonce-string-using-javascript/
function nonce(length) {
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    for (var i = 0; i < length; i++) {
        text += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    return text;
}

connect();
