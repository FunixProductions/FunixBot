class AutoMessages {
    constructor(config) {
        this.messagesLimit = config.messagesLimit;
        this.messagesList = config.messagesList;
        this.messagesCount = 0;
        this.messageSelected = 0;
    }

    newMessage(client, chat) {
        this.messagesCount++;
        if (this.messagesCount > this.messagesLimit) {
            this.messagesCount = 0;
            client.say(chat, this.messagesList[this.messageSelected]);
            this.messageSelected++;
            if (this.messageSelected >= this.messagesList.length) {
                this.messageSelected = 0;
            }
        }
    }
}

module.exports = AutoMessages;