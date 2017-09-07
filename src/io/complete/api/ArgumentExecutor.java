package io.complete.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.google.common.collect.ImmutableList;

public abstract class ArgumentExecutor
implements CommandExecutor,
TabCompleter {
    protected final List<CommandArguement> arguments = new ArrayList<CommandArguement>();
    protected final String label;

    public ArgumentExecutor(String label) {
        this.label = label;
    }

    public boolean containsArgument(CommandArguement argument) {
        return this.arguments.contains(argument);
    }

    public void addArgument(CommandArguement argument) {
        this.arguments.add(argument);
    }

    public void removeArgument(CommandArguement argument) {
        this.arguments.remove(argument);
    }

    public CommandArguement getArgument(String id) {
        for (CommandArguement argument : this.arguments) {
            String name = argument.getName();
            if (!name.equalsIgnoreCase(id) && !Arrays.asList(argument.getAliases()).contains(id.toLowerCase())) continue;
            return argument;
        }
        return null;
    }

    public String getLabel() {
        return this.label;
    }

    public List<CommandArguement> getArguments() {
        return ImmutableList.copyOf(this.arguments);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String permission2;
        if (args.length < 1) {
            sender.sendMessage(ChatColor.AQUA.toString() + (Object)ChatColor.STRIKETHROUGH + "-------" + (Object)ChatColor.AQUA + " [ " + WordUtils.capitalizeFully((String)this.label) + " Help" + " ] " + ChatColor.AQUA.toString() + (Object)ChatColor.STRIKETHROUGH + "-------");
            for (CommandArguement argument : this.arguments) {
                String permission = argument.getPermission();
                if (permission != null && !sender.hasPermission(permission)) continue;
                sender.sendMessage((Object)ChatColor.GOLD + argument.getUsage(label) + (Object)ChatColor.AQUA + " - " + (Object)ChatColor.YELLOW + argument.getDescription() + '.');
            }
            return true;
        }
        CommandArguement argument2 = this.getArgument(args[0]);
        String string = permission2 = argument2 == null ? null : argument2.getPermission();
        if (argument2 == null || permission2 != null && !sender.hasPermission(permission2)) {
            sender.sendMessage((Object)ChatColor.RED + WordUtils.capitalizeFully((String)this.label) + " sub-command " + args[0] + " not found.");
            return true;
        }
        argument2.onCommand(sender, command, label, args);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List results = new ArrayList<String>();
        if (args.length < 2) {
            for (CommandArguement argument : this.arguments) {
                String permission = argument.getPermission();
                if (permission != null && !sender.hasPermission(permission)) continue;
                results.add(argument.getName());
            }
        } else {
            CommandArguement argument2 = this.getArgument(args[0]);
            if (argument2 == null) {
                return results;
            }
            String permission2 = argument2.getPermission();
            if ((permission2 == null || sender.hasPermission(permission2)) && (results = argument2.onTabComplete(sender, command, label, args)) == null) {
                return null;
            }
        }
        return APIUtils.getCompletions(args, results);
    }
}

