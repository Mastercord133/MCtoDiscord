package org.Mastercord.mincratchattodiscord;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final ChatToDiscord plugin;

    public CommandHandler(ChatToDiscord plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§6ChatToDiscord §7v" + plugin.getDescription().getVersion());
            sender.sendMessage("§7Use §e/ctd reload §7to reload the config.");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("chattodiscord.reload")) {
                sender.sendMessage("§cYou don't have permission to do that.");
                return true;
            }
            plugin.reloadConfig();
            plugin.getDiscordWebhook();
            sender.sendMessage("§aChatToDiscord config reloaded!");
            return true;
        }

        sender.sendMessage("§cUnknown subcommand. Use §e/ctd reload§c.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload");
        }
        return List.of();
    }
}