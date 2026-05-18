package org.Mastercord.mincratchattodiscord;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatToDiscord extends JavaPlugin {

    private DiscordWebhook discordWebhook;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        discordWebhook = new DiscordWebhook(this);

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        getCommand("ctd").setExecutor(new CommandHandler(this));

        getLogger().info("ChatToDiscord enabled! Forwarding chat to Discord.");
    }

    @Override
    public void onDisable() {
        getLogger().info("ChatToDiscord disabled.");
    }

    public DiscordWebhook getDiscordWebhook() {
        return discordWebhook;
    }
}
