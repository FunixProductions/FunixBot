const privateMessage = "Hey, tu veux t'abonner tous les mois sans pour autant débourser 5€ par mois? " +
    "J'ai la solution! Le TWITCH PRIME: https://twitch.amazon.com/prime C'est quoi? C'est un abonnement " +
    "(GRATUIT LE PREMIER MOIS) que tu payes 25€ par an (pour les moins de 25 ans) et qui te permet te t'abonner " +
    "tous les mois à 1 chaîne Twitch de ton choix. Tu as aussi des jeux et plein de bonus gratuits tous les mois! :)";

class Prime {
    static command(client, user) {
        client.whisper(user, privateMessage);
    }
}

module.exports = Prime;
