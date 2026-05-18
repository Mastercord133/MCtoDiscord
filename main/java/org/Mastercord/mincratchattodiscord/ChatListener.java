package org.Mastercord.mincratchattodiscord;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatListener implements Listener {

    private final ChatToDiscord plugin;

    public ChatListener(ChatToDiscord plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        if (!plugin.getConfig().getBoolean("forward-chat", true)) return;

        String plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        plugin.getDiscordWebhook().sendMessage(event.getPlayer(), plainMessage);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!plugin.getConfig().getBoolean("forward-join-quit", true)) return;

        String playerName = event.getPlayer().getName();
        String msg = plugin.getConfig().getString("join-message", "**{player}** joined the server!")
                .replace("{player}", playerName);

        // Green color (decimal)
        plugin.getDiscordWebhook().sendEmbed("", msg, 5763719);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!plugin.getConfig().getBoolean("forward-join-quit", true)) return;

        String playerName = event.getPlayer().getName();
        String msg = plugin.getConfig().getString("quit-message", "**{player}** left the server.")
                .replace("{player}", playerName);

        // Red color (decimal)
        plugin.getDiscordWebhook().sendEmbed("", msg, 15548997);
    }
}