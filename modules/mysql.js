const mysql = require('mysql');

const configSql = {
    tables: {
        chat_commands: {
            table: "bot_twitch_commands",
            columns: ["id", "command", "message"]
        }
    }
};

class Mysql {
    constructor(mysqlInfos) {
        let host = mysqlInfos.host;
        let databaseName = mysqlInfos.database;
        console.log("[MYSQL] - Connection à la base : " + host);
        let database = mysql.createConnection({
            host: host,
            user: mysqlInfos.user,
            password: mysqlInfos.password,
            database: databaseName
        });
        database.connect(function (err) {
            if (err) throw err;
            console.log("[MYSQL] - Base SQL : " + host + "/" + databaseName + " connectée.");

            const createCmdTable = "CREATE TABLE IF NOT EXISTS " + configSql.tables.chat_commands.table +
                " (" + configSql.tables.chat_commands.columns[0] + " INT AUTO_INCREMENT PRIMARY KEY, " +
                configSql.tables.chat_commands.columns[1] + " VARCHAR(255), " +
                configSql.tables.chat_commands.columns[2] + " VARCHAR(255))";
            database.query(createCmdTable, function (err, result) {
                if (err) throw err;
                if (result.warningCount === 0) {
                    const insertCommands = "INSERT INTO " + configSql.tables.chat_commands.table + " (" +
                        configSql.tables.chat_commands.columns[1] + ", " +
                        configSql.tables.chat_commands.columns[2] + ") VALUES " +
                        "('prog', 'La programmation des lives : https://funixgaming.fr/prog')," +
                        "('extension', \"Tu veux être au courant des prochains lives ? Tu peux télécharger l'extension chrome ! https://goo.gl/KiaU8K\")," +
                        "('instantgaming', 'Payez vos jeux moins chers ! https://goo.gl/x12DKs Partenaire Instant Gaming <3')," +
                        "('twitter', 'Mon twitter : https://twitter.com/funixgaming')," +
                        "('instagram', 'Mon instagram : https://instagram.com/funixgaming_')," +
                        "('youtube', 'Ma chaine YouTube : https://youtube.com/c/funixgaming')," +
                        "('don', \"La donnation est un moyen d'aider et de me soutenir : https://streamlabs.com/funixgaming\")," +
                        "('sub', \"LE SUB C'EST VRAIMENT SUPER ! : https://www.twitch.tv/products/funixgaming <3\")," +
                        "('battlenet', \"Pour m'ajouter en ami sur BattleNet : FunixGaming#2154\")";
                    database.query(insertCommands, function (err) {
                        if (err) throw err;
                    });
                    console.log("[MYSQL] - Table : " + configSql.tables.chat_commands.table + " crée");
                }
            });

        });
        this.database = database;
    }

    getCommandList(cb) {
        const requestCommand = "SELECT " + configSql.tables.chat_commands.columns[1] + " FROM " + configSql.tables.chat_commands.table;
        this.database.query(requestCommand, function (err, result) {
            if (err) throw err;
            let commandList = [];
            for (let i = 0; i < result.length; ++i) {
                commandList.push(result[i].command);
            }
            cb(commandList);
        });
    }

    getChatCommand(cmd, cb) {
        const requestCommand = "SELECT * FROM " + configSql.tables.chat_commands.table + " WHERE " + configSql.tables.chat_commands.columns[1] + "='" + cmd + "'";
        this.database.query(requestCommand, function (err, result) {
            if (err) throw err;
            if (result.length > 0) {
                cb(result[0].message);
            } else {
                cb(null);
            }
        });
    }

}

module.exports = Mysql;
