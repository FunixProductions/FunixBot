let ws;

// Source: https://www.thepolyglotdeveloper.com/2015/03/create-a-random-nonce-string-using-javascript/
function nonce(length) {
    let text = "";
    let possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    for (let i = 0; i < length; i++) {
        text += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    return text;
}

function heartbeat() {
    const message = {
        type: 'PING'
    };
    ws.send(JSON.stringify(message));
}

function listen(topic) {
    const message = {
        type: 'LISTEN',
        nonce: nonce(15),
        data: {
            topics: topic,
            auth_token: 'k8qm0y4b9b2ks148jds88c2x2reo4t'
        }
    };
    ws.send(JSON.stringify(message));
}

function connectTwitchPubSub() {
    const heartbeatInterval = 1000 * 60; //ms between PING's
    const reconnectInterval = 1000 * 3; //ms to wait before reconnect
    let heartbeatHandle;

    ws = new WebSocket("wss://pubsub-edge.twitch.tv/");

    ws.onopen = function (event) {
        console.log("Connected to twitch pubsub !")
        heartbeat();
        heartbeatHandle = setInterval(heartbeat, heartbeatInterval);
        listen(['channel-points-channel-v1.' + config]);
    }

    ws.onerror = function (error) {
        console.error("Error when connecting to twitch pubsub.");
    }

    ws.onmessage = function (event) {
        let message = JSON.parse(event.data);
        if (message.type === 'RECONNECT') {
            console.log("Reconnecting to twitch pubsub...")
            setTimeout(connectTwitchPubSub, reconnectInterval);
        } else if (message.type === 'RESPONSE') {
            if (message.error.length > 0)
                console.log("response: " + message.error);
        } else if (message.type === 'MESSAGE') {
            if (message.data.topic === 'channel-points-channel-v1.' + config) {
                let redemption = JSON.parse(message.data.message);
                let rewardID = redemption.data.redemption.reward.id;
                if (rewardID === '1b48b6e2-83f3-4af6-bf4b-25843910176a') {
                    playSound('funixSounds/robloxOOF');
                } else if (rewardID === 'ee099003-c44a-4128-bb94-0fbb1a346a6e') {
                    playSound('funixSounds/tcon');
                } else if (rewardID === '063ae6ea-ed45-4ca5-90ff-6d33fe2c1ae4') {
                    playSound('funixSounds/rene-balek');
                }
            }
        }
    }

    ws.onclose = function (event) {
        clearInterval(heartbeatHandle);
        setTimeout(connectTwitchPubSub, reconnectInterval)
    }

}

document.onload = connectTwitchPubSub();

function playSound(soundName) {
    new Audio("/sounds/" + soundName + '.mp3').play();
}