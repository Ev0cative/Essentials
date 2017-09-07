package io.complete.command.staff;

import java.util.ArrayList;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import io.complete.api.CommandClass;

public class StaffChatCommand extends CommandClass implements Listener
{

	public StaffChatCommand()
	{
		super("staffchat", "staffchat");
	}
	
	public static ArrayList<UUID> inStaffChat = new ArrayList();

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("staffchat"))
		{
			if (!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.RED + "You are not a player!");
				return true;
			}
			if (!sender.hasPermission("scorpion.staff"))
			{
				sender.sendMessage(ChatColor.RED + "You don't have permission.");
				return true;
			}
			Player p = (Player) sender;
			if (args.length == 0)
			{
				if (!inStaffChat.contains(p.getUniqueId()))
				{
					inStaffChat.add(p.getUniqueId());
					p.sendMessage(ChatColor.YELLOW + "Now speaking in staff chat.");
					return true;
				}
				inStaffChat.remove(p.getUniqueId());
				p.sendMessage(ChatColor.YELLOW + "Now speaking in global chat.");
				return true;
			}
			if (args.length >= 1)
			{
				Bukkit.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "SC" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA + p.getName() + ChatColor.GRAY + ": " + ChatColor.YELLOW + StringUtils.join(args, " "), "scorpion.staff");
			}
		}
		return false;
	}
}
