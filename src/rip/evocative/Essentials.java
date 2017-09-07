package rip.evocative;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import rip.evocative.command.commands.EnderChestCommand;
import rip.evocative.command.commands.InvseeCommand;
import rip.evocative.listener.AntiEnderChestListener;
import rip.evocative.listener.HeadInfoListener;
import rip.evocative.listener.SignColorsListener;

public class Essentials extends JavaPlugin implements Listener
{

	@Getter public static Essentials instance;
	public static FileConfiguration config;
	public static File conf;
	
	public void onEnable()
	{
		instance = this;

		loadListeners();
		loadCommands();
		save();
		Config();
	}

	public void onDisable()
	{
		save();

		instance = null;
	}

	public void Config() {
		config = this.getConfig();
		config.options().copyDefaults(true);
		conf = new File(this.getDataFolder(), "config.yml");
		this.saveConfig();
		this.saveDefaultConfig();
	}
	
	public void save()
	{
		new BukkitRunnable()
		{

			@Override
			public void run()
			{
				for (Player player : Bukkit.getOnlinePlayers())
				{
					// TODO:
				}
			}
		}.runTaskTimer(this, 6000L, 6000L);
	}

	public void loadListeners()
	{
		PluginManager pluginManager = getServer().getPluginManager();
		
		pluginManager.registerEvents(new AntiEnderChestListener(), this);
		pluginManager.registerEvents(new HeadInfoListener(), this);
		pluginManager.registerEvents(new SignColorsListener(), this);
	}

	public void loadCommands()
	{
		getCommand("enderchest").setExecutor(new EnderChestCommand());
		getCommand("invsee").setExecutor(new InvseeCommand());
	}
}
