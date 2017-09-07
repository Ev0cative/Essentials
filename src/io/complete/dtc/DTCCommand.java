package io.complete.dtc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import net.minecraft.util.com.google.common.primitives.Ints;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import io.complete.Essentials;
import io.complete.api.CommandClass;

public class DTCCommand extends CommandClass implements CommandExecutor
{
	public DTCCommand(Essentials essentials)
	{
		super("dtc", "");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("dtc"))
		{
			if (!sender.hasPermission("scorpion.dtc"))
			{
				sender.sendMessage(ChatColor.RED + "You don't have permission.");
				return true;
			}
			if (!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.RED + "You are not a player");
				return true;
			}
			Player p = (Player) sender;
			if (args.length == 0)
			{
				p.sendMessage(ChatColor.GOLD + "DTC Help" + ChatColor.GRAY + ":");
				p.sendMessage(ChatColor.RED + "/dtc wand");
				p.sendMessage(ChatColor.RED + "/dtc list");
				p.sendMessage(ChatColor.RED + "/dtc create <name>");
				p.sendMessage(ChatColor.RED + "/dtc remove <name>");
				p.sendMessage(ChatColor.RED + "/dtc start <name> <points>");
				p.sendMessage(ChatColor.RED + "/dtc stop <name>");
				return true;
			}
			if (args.length == 1)
			{
				if (args[0].equalsIgnoreCase("wand"))
				{
					p.getInventory().addItem(new ItemStack[] {
							DTC.DTC_WAND
					});
					p.sendMessage(ChatColor.GREEN + "You have recieved the wand.");
					return true;
				}
				if (args[0].equalsIgnoreCase("list"))
				{
					p.sendMessage(ChatColor.DARK_GREEN + "DTC List" + ChatColor.GRAY + ":");
					for (String s : DTC.data.getKeys(false))
					{
						p.sendMessage(ChatColor.GREEN + s);
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("create"))
				{
					p.sendMessage(ChatColor.RED + "Usage: /dtc create <name>");
					return true;
				}
				if (args[0].equalsIgnoreCase("remove"))
				{
					p.sendMessage(ChatColor.RED + "Usage: /dtc remove <name>");
					return true;
				}
				if (args[0].equalsIgnoreCase("start"))
				{
					p.sendMessage(ChatColor.RED + "Usage: /dtc start <name> <points>");
					return true;
				}
				if (args[0].equalsIgnoreCase("stop"))
				{
					p.sendMessage(ChatColor.RED + "Usage: /dtc stop <name>");
					return true;
				}
			}
			if (args.length == 2)
			{
				if (args[0].equalsIgnoreCase("create"))
				{
					if (DTC.pos.get(p) == null)
					{
						p.sendMessage(ChatColor.RED + "You do not have a block selected!");
						return true;
					}
					if (DTC.data.isConfigurationSection(args[1]))
					{
						p.sendMessage(ChatColor.RED + args[1] + " already exists!");
						return true;
					}
					DTC.data.createSection(args[1]);
					DTC.data.getConfigurationSection(args[1]).set("world", WordUtils.capitalize(((Location) DTC.pos.get(p)).getWorld().getName()));
					DTC.data.getConfigurationSection(args[1]).set("x", Integer.valueOf(((Location) DTC.pos.get(p)).getBlockX()));
					DTC.data.getConfigurationSection(args[1]).set("y", Integer.valueOf(((Location) DTC.pos.get(p)).getBlockY()));
					DTC.data.getConfigurationSection(args[1]).set("z", Integer.valueOf(((Location) DTC.pos.get(p)).getBlockZ()));
					try
					{
						DTC.data.save(DTC.file);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					p.sendMessage(ChatColor.GREEN + args[1] + " has been created!");
					return true;
				}
				if (args[0].equalsIgnoreCase("remove"))
				{
					if (!DTC.data.isConfigurationSection(args[1]))
					{
						p.sendMessage(ChatColor.RED + args[1] + " is not a valid DTC!");
						return true;
					}
					if (DTC.getActiveDTC.containsKey(args[1]))
					{
						p.sendMessage(ChatColor.RED + args[1] + " is currently active!");
						return true;
					}
					DTC.data.set(args[1], null);
					try
					{
						DTC.data.save(DTC.file);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					p.sendMessage(ChatColor.RED + args[1] + " has been removed!");
					return true;
				}
				if (args[0].equalsIgnoreCase("stop"))
				{
					if (!DTC.data.isConfigurationSection(args[1]))
					{
						p.sendMessage(ChatColor.RED + args[1] + " is not a valid DTC!");
						return true;
					}
					if (!DTC.getActiveDTC.containsKey(args[1]))
					{
						p.sendMessage(ChatColor.RED + args[1] + " is not active!");
						return true;
					}
					DTC.getActiveDTC.clear();
					Bukkit.broadcastMessage(ChatColor.GOLD + " »" + ChatColor.GRAY + "(DTC) " + ChatColor.DARK_GREEN + args[1] + ChatColor.RED + " has been cancelled!");
					return true;
				}
				if (args[0].equalsIgnoreCase("start"))
				{
					p.sendMessage(ChatColor.RED + "Usage: /dtc start <name> <points>");
					return true;
				}
			}
			if ((args.length == 3) && (args[0].equalsIgnoreCase("start")))
			{
				if (!DTC.data.isConfigurationSection(args[1]))
				{
					p.sendMessage(ChatColor.RED + args[1] + " is not a valid DTC!");
					return true;
				}
				if (DTC.getActiveDTC.keySet().size() > 0)
				{
					p.sendMessage(ChatColor.RED + "There is already an active DTC!");
					return true;
				}
				if (Ints.tryParse(args[2]) == null)
				{
					p.sendMessage(ChatColor.RED + args[2] + " is not a valid number.");
					return true;
				}
				if (Ints.tryParse(args[2]).intValue() <= 0)
				{
					p.sendMessage(ChatColor.RED + args[2] + " is not a valid number.");
					return true;
				}
				Bukkit.broadcastMessage(ChatColor.GOLD + " »" + ChatColor.GRAY + "(DTC) " + ChatColor.DARK_GREEN + args[1] + ChatColor.GREEN + " has started with " + ChatColor.YELLOW + args[2] + " points" + ChatColor.GREEN + "! " + ChatColor.GOLD + "(" + ChatColor.GRAY + DTC.data.getConfigurationSection(args[1]).getString("world") + ": " + DTC.data.getConfigurationSection(args[1]).getInt("x") + ", " + ChatColor.GRAY + DTC.data.getConfigurationSection(args[1]).getInt("y") + ", " + ChatColor.GRAY + DTC.data.getConfigurationSection(args[1]).getInt("z") + ChatColor.GOLD + ")");
				DTC.getActiveDTC.put(args[1], Ints.tryParse(args[2]));
				return true;
			}
		}
		return false;
	}
}
