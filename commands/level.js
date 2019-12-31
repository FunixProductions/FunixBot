class Level {
    static command(client, user, target, database) {
        database.getUserExp(user['user-id'], function (data) {
            if (data !== null) {
                client.say(target, user['display-name'] + " Niveau " + data.level + " (" + data.xp + "/" + data.xp_next_level + ")");
            } else {
                client.say(target, user['display-name'] + " n'a pas encore de niveau sur ce stream.")
            }
        });
    }
}

module.exports = Level;