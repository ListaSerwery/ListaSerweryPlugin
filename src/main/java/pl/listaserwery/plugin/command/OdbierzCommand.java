package pl.listaserwery.plugin.command;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.listaserwery.plugin.ListaSerweryPlugin;
import pl.listaserwery.plugin.util.Constants;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class OdbierzCommand extends Command {

    private final ListaSerweryPlugin plugin;
    private final Map<UUID, Long> cooldownMap = new HashMap<>();

    public OdbierzCommand(ListaSerweryPlugin plugin, String name, List<String> aliases) {
        super(name);
        this.setAliases(aliases);
        this.plugin = plugin;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return java.util.Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Ta komenda jest dostepna tylko dla graczy.");
            return true;
        }

        Player player = (Player) sender;
        FileConfiguration config = plugin.getConfig();

        // Cooldown check
        int cooldownSeconds = config.getInt(Constants.CONFIG_COOLDOWN, 60);
        if (cooldownMap.containsKey(player.getUniqueId())) {
            long lastUsed = cooldownMap.get(player.getUniqueId());
            long remaining = ((lastUsed + (cooldownSeconds * 1000L)) - System.currentTimeMillis()) / 1000;
            if (remaining > 0) {
                sendMessage(player, config.getString(Constants.MSG_COOLDOWN, Constants.DEFAULT_COOLDOWN)
                        .replace("%seconds%", String.valueOf(remaining)));
                return true;
            }
        }
        cooldownMap.put(player.getUniqueId(), System.currentTimeMillis());

        String apiKey = config.getString(Constants.CONFIG_API_KEY);
        String serverId = config.getString(Constants.CONFIG_SERVER_ID);

        if (apiKey == null || apiKey.equals("ZMIEN_MNIE") || serverId == null || serverId.equals("ZMIEN_MNIE")) {
            sendMessage(player, config.getString(Constants.MSG_INVALID_SETUP, Constants.DEFAULT_INVALID_SETUP));
            return true;
        }

        sendMessage(player, config.getString(Constants.MSG_CHECKING, Constants.DEFAULT_CHECKING));

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> checkReward(player, apiKey, serverId, config));

        return true;
    }

    /**
     * Attempts to claim a reward for the player. If no reward is available, it
     * checks the player's vote status.
     */
    private void checkReward(Player player, String apiKey, String serverId, FileConfiguration config) {
        HttpURLConnection conn = null;
        try {
            String urlString = String.format("%s?server_id=%s", Constants.API_URL_CLAIM, serverId);
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-API-Key", apiKey);
            conn.setDoOutput(true);

            String jsonInputString = "{\"nickname\": \"" + player.getName() + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == 429) {
                sendMessage(player, config.getString(Constants.MSG_RATE_LIMIT, Constants.DEFAULT_RATE_LIMIT));
                return;
            }

            if (responseCode == 401 || responseCode == 403 || responseCode == 400) {
                plugin.getLogger().warning("Blad API (" + responseCode + "): Sprawdz klucz API lub Server ID.");
                sendMessage(player, config.getString(Constants.MSG_INVALID_SETUP, Constants.DEFAULT_INVALID_SETUP));
                return;
            }

            if (responseCode == 200) {
                try (InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                    JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();

                    if (jsonResponse.has("success") && jsonResponse.get("success").getAsBoolean()) {
                        JsonObject data = jsonResponse.getAsJsonObject("data");

                        if (data.has("claimed") && data.get("claimed").getAsBoolean()) {

                            Bukkit.getScheduler().runTask(plugin, () -> {
                                sendMessage(player, config.getString(Constants.MSG_SUCCESS, Constants.DEFAULT_SUCCESS));
                                executeRewards(player, config);
                            });
                        } else {

                            checkVoteStatus(player, apiKey, serverId, config);
                        }
                    } else {
                        sendMessage(player, config.getString(Constants.MSG_API_ERROR, Constants.DEFAULT_API_ERROR));
                    }
                }
            } else {
                sendMessage(player, config.getString(Constants.MSG_API_ERROR, Constants.DEFAULT_API_ERROR));
                plugin.getLogger().log(Level.WARNING,
                        "Błąd HTTP " + responseCode + " przy sprawdzaniu nagrody dla " + player.getName());
            }

        } catch (Exception e) {
            sendMessage(player, config.getString(Constants.MSG_API_ERROR, Constants.DEFAULT_API_ERROR));
            plugin.getLogger().log(Level.SEVERE, "Wyjatek podczas sprawdzania nagrody dla gracza " + player.getName(),
                    e);
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    /**
     * Checks the player's voting status and sends them an appropriate message.
     */
    private void checkVoteStatus(Player player, String apiKey, String serverId, FileConfiguration config) {
        HttpURLConnection conn = null;
        try {
            String urlString = String.format("%s?server_id=%s&nickname=%s", Constants.API_URL_CHECK, serverId,
                    player.getName());
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-API-Key", apiKey);

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                try (InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                    JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();

                    if (jsonResponse.has("success") && jsonResponse.get("success").getAsBoolean()) {
                        JsonObject data = jsonResponse.getAsJsonObject("data");
                        if (data.has("has_voted") && data.get("has_voted").getAsBoolean()) {
                            if (data.has("is_reward_claimed") && data.get("is_reward_claimed").getAsBoolean()) {
                                String nextVoteAt = data.get("next_vote_at").getAsString();
                                OffsetDateTime nextVoteTime = OffsetDateTime.parse(nextVoteAt);
                                Duration duration = Duration.between(OffsetDateTime.now(), nextVoteTime);
                                long hours = duration.toHours();
                                long minutes = duration.toMinutes() % 60;
                                String timeLeft = String.format("%d godzin i %d minut", hours, minutes);
                                sendMessage(player, config
                                        .getString(Constants.MSG_ALREADY_CLAIMED, Constants.DEFAULT_ALREADY_CLAIMED)
                                        .replace("%timeLeft%", timeLeft));
                            } else {
                                sendMessage(player, "&cWystąpił nieoczekiwany błąd. Spróbuj ponownie.");
                            }
                        } else {
                            sendVoteMessage(player, serverId, config);
                        }
                    }
                }
            } else {
                sendVoteMessage(player, serverId, config);
            }
        } catch (Exception e) {
            sendVoteMessage(player, serverId, config);
            plugin.getLogger().log(Level.SEVERE,
                    "Wyjatek podczas sprawdzania statusu głosu dla gracza " + player.getName(), e);
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    /**
     * Executes the reward commands for the player.
     */
    private void executeRewards(Player player, FileConfiguration config) {
        List<String> rewardCommands = config.getStringList(Constants.CONFIG_REWARDS_COMMANDS);
        for (String cmd : rewardCommands) {
            String formattedCmd = cmd.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCmd);
        }
    }

    /**
     * Sends a formatted message to the player.
     */
    private void sendMessage(Player player, String message) {
        String prefix = plugin.getConfig().getString(Constants.MSG_PREFIX, Constants.DEFAULT_PREFIX);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    /**
     * Sends a clickable message to the player, prompting them to vote.
     */
    private void sendVoteMessage(Player player, String serverId, FileConfiguration config) {
        String prefix = config.getString(Constants.MSG_PREFIX, Constants.DEFAULT_PREFIX);
        String message = config.getString(Constants.MSG_NO_VOTE, Constants.DEFAULT_NO_VOTE);
        String voteMessage = config.getString(Constants.MSG_VOTE_LINK, Constants.DEFAULT_VOTE_LINK);
        String voteUrl = "https://listaserwery.pl/serwer/" + serverId;

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

        TextComponent component = new TextComponent(
                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', prefix + voteMessage)));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, voteUrl));
        player.spigot().sendMessage(component);
    }
}
