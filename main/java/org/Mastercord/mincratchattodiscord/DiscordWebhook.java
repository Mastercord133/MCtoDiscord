package org.Mastercord.mincratchattodiscord;

import org.bukkit.entity.Player;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhook {

    private final ChatToDiscord plugin;

    public DiscordWebhook(ChatToDiscord plugin) {
        this.plugin = plugin;
    }

    public void sendMessage(Player player, String message) {
        String webhookUrl = plugin.getConfig().getString("webhook-url", "");
        if (webhookUrl.isEmpty() || webhookUrl.equals("YOUR_WEBHOOK_URL_HERE")) {
            plugin.getLogger().warning("Webhook URL is not configured! Set it in config.yml.");
            return;
        }

        String serverName = plugin.getConfig().getString("server-name", "Minecraft");
        String avatarUrl = plugin.getConfig().getString("avatar-url", "");
        boolean usePlayerAvatar = plugin.getConfig().getBoolean("use-player-avatar", true);
        String format = plugin.getConfig().getString("message-format", "**{player}**: {message}");

        String formattedMessage = format
                .replace("{player}", escapeJson(player.getName()))
                .replace("{message}", escapeJson(message))
                .replace("{server}", escapeJson(serverName));

        String playerAvatarUrl = usePlayerAvatar
                ? "https://mc-heads.net/avatar/" + player.getName() + "/64"
                : (avatarUrl.isEmpty() ? "" : avatarUrl);

        String username = plugin.getConfig().getString("webhook-username", serverName);

        String jsonPayload = buildPayload(username, playerAvatarUrl, formattedMessage);

        // Send async so we don't block the main thread
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = URI.create(webhookUrl).toURL();
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "ChatToDiscord-Plugin");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == 204 || responseCode == 200) {
                    if (plugin.getConfig().getBoolean("debug", false)) {
                        plugin.getLogger().info("Message sent to Discord successfully.");
                    }
                } else {
                    plugin.getLogger().warning("Failed to send message to Discord. HTTP " + responseCode);
                }

                connection.disconnect();

            } catch (Exception e) {
                plugin.getLogger().severe("Error sending message to Discord: " + e.getMessage());
            }
        });
    }

    public void sendEmbed(String title, String description, int color) {
        String webhookUrl = plugin.getConfig().getString("webhook-url", "");
        if (webhookUrl.isEmpty() || webhookUrl.equals("YOUR_WEBHOOK_URL_HERE")) return;

        String serverName = plugin.getConfig().getString("server-name", "Minecraft");
        String username = plugin.getConfig().getString("webhook-username", serverName);

        String jsonPayload = "{"
                + "\"username\":\"" + escapeJson(username) + "\","
                + "\"embeds\":[{"
                + "\"title\":\"" + escapeJson(title) + "\","
                + "\"description\":\"" + escapeJson(description) + "\","
                + "\"color\":" + color
                + "}]}";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = URI.create(webhookUrl).toURL();
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "ChatToDiscord-Plugin");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                }

                connection.getResponseCode();
                connection.disconnect();
            } catch (Exception e) {
                plugin.getLogger().severe("Error sending embed to Discord: " + e.getMessage());
            }
        });
    }

    private String buildPayload(String username, String avatarUrl, String content) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"username\":\"").append(escapeJson(username)).append("\"");
        if (!avatarUrl.isEmpty()) {
            sb.append(",\"avatar_url\":\"").append(escapeJson(avatarUrl)).append("\"");
        }
        sb.append(",\"content\":\"").append(escapeJson(content)).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}