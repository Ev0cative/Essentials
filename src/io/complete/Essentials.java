package io.complete;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.alexandeh.kraken.Kraken;

import io.complete.api.CommandManager;
import io.complete.api.CommandManagerUtil;
import io.complete.command.StaffModule;
import io.complete.command.staff.JudgementDayCommand;
import io.complete.dtc.DTC;
import io.complete.dtc.DTCListener;
import io.complete.security.SecurityCommand;
import io.complete.security.SecurityHandler;
import io.complete.security.SecurityListener;
import io.complete.security.SecurityUtils;
import io.complete.tab.ListenerTab;
import io.complete.utils.Methods;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import server.wenjapvp.hcf.HCF;

public class Essentials extends JavaPlugin implements Listener
{

	@Getter public static Essentials instance;
	@Getter private CommandManager commandManager;
	private static ArrayList<String> blockedCommands = new ArrayList<String>();
	private HCF hcf;
	@Getter private ListenerTab tabListener;

	public void onEnable()
	{
		instance = this;
		createFiles();
		blockedCommands.add("/me");
		blockedCommands.add("/bukkit:me");
		this.commandManager = new CommandManagerUtil(this);
		this.commandManager.registerAll(new StaffModule(this));

	    this.hcf = ((HCF)JavaPlugin.getPlugin(HCF.class));
	    
	    new Kraken(this);
		
		Bukkit.getServer().getPluginManager().registerEvents(new DTC(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new DTCListener(), this);
		getCommand("judgementday").setExecutor(new JudgementDayCommand());
		getCommand("pin").setExecutor(new SecurityCommand());
		Bukkit.getServer().getPluginManager().registerEvents(new SecurityListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Methods(), this);
	    this.tabListener = new ListenerTab();
	    Bukkit.getServer().getPluginManager().registerEvents(this.tabListener, this);

		Plugin plugin = this.getServer().getPluginManager().getPlugin("ProtocolLib");
		if (plugin != null && plugin.isEnabled())
		{
			try
			{}
			catch (Exception ex)
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Error: Syncing with ProtocolLib");
				ex.printStackTrace();
			}
		}

		Essentials.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(Essentials.getInstance(), new Runnable()
		{
			// »§
			public void run()
			{

			}

		}, 0L, 20 * 360);

		Essentials.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(Essentials.getInstance(), new Runnable()
		{

			public void run()
			{
				for (String s : SecurityHandler.getLoggedOutPlayers())
				{
					if (Essentials.getInstance().getServer().getPlayer(s) == null)
						return;
					Player p = Essentials.getInstance().getServer().getPlayer(s);
					SecurityUtils.sendLoginMessage(p);
					p.playSound(p.getLocation(), Sound.NOTE_PLING, 4F, 4F);
				}
			}

		}, 0L, 20 * 10);
	}

	public void onDisable()
	{
		super.onDisable();

		for (Player player : Bukkit.getOnlinePlayers())
		{
			player.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&dServer restarting for plugin updates ¬"));
		}
		instance = null;
	}

	public void createFile(String name)
	{
		File file = new File(getDataFolder(), name + ".yml");
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void checkLeak()
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					URL url = new URL("https://pastebin.com/raw/Fzay6mBd");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String inputLine = in.readLine();
					Boolean run = Boolean.valueOf(inputLine);
					if (run.booleanValue())
					{
						Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cAn error occured while loading Scorpion."));
						Essentials.this.setEnabled(false);
					}
					in.close();
				}
				catch (Exception e)
				{
					Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cAn error occured while loading Scorpion."));
				}
			}
		})

				.start();
	}

	public void createFiles()
	{
		if (!getDataFolder().exists())
		{
			getDataFolder().mkdir();
			getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&dEssentials&8] &cCould not find DTC.yml, creating..."));
		}
		saveDefaultConfig();
		getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&dEssentials&8] &aSuccessfully loaded DTC.yml"));
		createFile("dtc");
	}

	public ArrayList<String> getBlockedCommands()
	{
		return blockedCommands;
	}

	public HCF getHCF()
	{
		return hcf;
	}

	public ArrayList<String> getLogin(){
		return SecurityHandler.getLoggedOutPlayers();
	}
	
}
