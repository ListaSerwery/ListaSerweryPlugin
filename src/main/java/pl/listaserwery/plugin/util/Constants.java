package pl.listaserwery.plugin.util;

/**
 * A utility class to hold constant values used throughout the plugin.
 * This helps avoid "magic strings" and makes maintenance easier.
 */
public final class Constants {

    private Constants() {
        // Private constructor to prevent instantiation
    }

    // API URLs
    public static final String API_URL_CLAIM = "https://listaserwery.pl/api/vote/claim";
    public static final String API_URL_CHECK = "https://listaserwery.pl/api/vote/check";

    // Configuration Keys
    public static final String CONFIG_API_KEY = "api-key";
    public static final String CONFIG_SERVER_ID = "server-id";
    public static final String CONFIG_REWARDS_COMMANDS = "rewards.commands";
    public static final String CONFIG_COOLDOWN = "command.cooldown-seconds";

    // Message Configuration Keys
    public static final String MSG_PREFIX = "messages.prefix";
    public static final String MSG_INVALID_SETUP = "messages.invalid-setup";
    public static final String MSG_CHECKING = "messages.checking";
    public static final String MSG_SUCCESS = "messages.success";
    public static final String MSG_RATE_LIMIT = "messages.rate-limit";
    public static final String MSG_API_ERROR = "messages.api-error";
    public static final String MSG_ALREADY_CLAIMED = "messages.already-claimed";
    public static final String MSG_NO_VOTE = "messages.no-vote";
    public static final String MSG_VOTE_LINK = "messages.vote-link";
    public static final String MSG_COOLDOWN = "messages.cooldown";

    // Default Messages
    public static final String DEFAULT_PREFIX = "&8[&bListaSerwery&8] ";
    public static final String DEFAULT_INVALID_SETUP = "&cPlugin nie jest poprawnie skonfigurowany. Skontaktuj się z administracją.";
    public static final String DEFAULT_CHECKING = "&7Sprawdzam dostępność nagrody...";
    public static final String DEFAULT_SUCCESS = "&aPomyślnie odebrano nagrodę za głos! Dziękujemy.";
    public static final String DEFAULT_RATE_LIMIT = "&cZbyt wiele zapytań. Odczekaj chwilę przed ponowną próbą.";
    public static final String DEFAULT_API_ERROR = "&cWystąpił błąd podczas łączenia z API...";
    public static final String DEFAULT_ALREADY_CLAIMED = "&cOdebrałeś już nagrodę za ostatni głos! Następny głos możliwy za: &e%timeLeft%";
    public static final String DEFAULT_NO_VOTE = "&cNie masz żadnych nieodebranych nagród...";
    public static final String DEFAULT_VOTE_LINK = "&aKliknij tutaj, aby zaglosowac!";
    public static final String DEFAULT_COOLDOWN = "&cMusisz odczekać jeszcze &e%seconds% &csekund przed ponownym użyciem komendy.";
}
