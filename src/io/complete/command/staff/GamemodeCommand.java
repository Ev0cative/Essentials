package io.complete.command.staff;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;import org.bukkit.craftbukkit.libs.joptsimple.ArgumentAcceptingOptionSpec;
import org.bukkit.entity.Player;

import io.complete.api.CommandClass;
import io.complete.utils.Methods;

public class GamemodeCommand extends CommandClass
{
	public GamemodeCommand()
	{
		super("gamemode", "puts you in gamemode creative or survival");
	}

	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
	{
		if (!(commandSender instanceof Player))
		{
			commandSender.sendMessage((Object) ChatColor.RED + "Please use the server to execute this command.");
			return true;
		}
		
		Player player = (Player) commandSender;
		if(args.length == 0)
		{
			player.sendMessage(ChatColor.RED + "Usage: /gamemode <1, 0>");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("1"))
		{
			player.setGameMode(GameMode.CREATIVE);
			player.sendMessage(ChatColor.RED + "Gamemode Creative");
		}
		else if(args[0].equalsIgnoreCase("0"))
		{
			player.setGameMode(GameMode.SURVIVAL);
			player.sendMessage(ChatColor.RED + "Gamemode Survival");
			
			/**
			 * Optional this prevents people spawning things in and going out of gamemode to abuse :D
			 * 
			 * This can be disabled my hashing it out
			 */
			
			Methods.wipe(player);
		}
		return false;
	}
}
