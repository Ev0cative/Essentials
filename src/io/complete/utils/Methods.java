package io.complete.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.complete.Essentials;
import net.md_5.bungee.api.ChatColor;

public class Methods implements Listener
{

	ArrayList<String> tpCooldown = new ArrayList<String>();
	ArrayList<String> banCooldown = new ArrayList<String>();
	HashMap<Player, Integer> banCount = new HashMap<Player, Integer>();

	public static void wipe(Player player)
	{
		player.setFoodLevel(10);
		player.setHealth(20L);
		player.getInventory().clear();
		player.sendMessage(ChatColor.RED + "Cleared Inventory" + " Ping: " + player.getPing());
	}

	public static void ping(Player player)
	{
		player.sendMessage(ChatColor.GREEN + "Ping: " + player.getPing());
	}

	@EventHandler
	public void br(BlockBreakEvent e)
	{
		Player player = e.getPlayer();
		ItemStack stack = player.getItemInHand();
		if (stack.getType() == Material.DIAMOND_PICKAXE || stack.getType() == Material.IRON_PICKAXE || stack.getType() == Material.GOLD_PICKAXE || stack.getType() == Material.WOOD_PICKAXE || stack.getType() == Material.HOPPER_MINECART)
		{
			ItemMeta sm = stack.getItemMeta();
			if (sm.hasLore())
			{
				sm.getLore().clear();
			}
			ArrayList<String> lore = new ArrayList<String>();
			Player target = player;
			lore.add("§6» §bDiamond Ore: §7" + target.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE));
			lore.add("§6» §aEmerald Ore: §7" + target.getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE));
			lore.add("§6» §7Iron Ore: §7" + target.getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE));
			lore.add("§6» §eGold Ore: §7" + target.getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE));
			lore.add("§6» §cRedstone Ore: §7" + target.getStatistic(Statistic.MINE_BLOCK, Material.REDSTONE_ORE));
			lore.add("§6» §8Coal Ore: §7" + target.getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE));
			lore.add("§6» §9Lapis Ore: §7" + target.getStatistic(Statistic.MINE_BLOCK, Material.LAPIS_ORE));
			sm.setLore(lore);
			stack.setItemMeta(sm);
		}
	}

	public void exec(String s)
	{
		Essentials.getInstance().getServer().dispatchCommand(Essentials.getInstance().getServer().getConsoleSender(), s);
	}

	@EventHandler
	public void antiAbuse(PlayerCommandPreprocessEvent e)
	{
		String message = e.getMessage().toLowerCase();
		final Player p = e.getPlayer();
		if (message.startsWith("/f setdtr all"))
		{
			if (!p.hasPermission("mod"))
				return;
			e.setCancelled(true);
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "ipban " + p.getName() + " Security Ban: Attempted to set dtr of all factions.");
		}
		else if (message.startsWith("/tphere"))
		{
			if (!p.hasPermission("mod"))
				return;
			if (!tpCooldown.contains(p.getName()))
			{
				tpCooldown.add(p.getName());
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Essentials.getInstance(), new Runnable()
				{
					public void run()
					{
						tpCooldown.remove(p.getName());
					}
				}, 20 * 10);
			}
			else
			{
				p.sendMessage("§dHQPots §7» §rFor security reasons, you cannot teleport more than 1 player to yourself within 10 seconds.");
				p.sendMessage("§dHQPots §7» §rSorry for any inconvenience caused!");
				e.setCancelled(true);
			}
		}
		else if (message.startsWith("/ban") || message.startsWith("/ipban"))
		{
			if (!p.hasPermission("mod"))
				return;
			if (!banCooldown.contains(p.getName()))
			{
				banCooldown.add(p.getName());
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Essentials.getInstance(), new Runnable()
				{
					public void run()
					{
						banCooldown.remove(p.getName());
						banCount.put(p, 0);
					}
				}, 20 * 3);
			}
			else
			{
				if (banCount.get(p) == null)
				{
					banCount.put(p, 1);
					e.setCancelled(true);
					p.sendMessage("§dHQPots §7» §rFor security reasons, you cannot ban more than 1 player within 3 seconds.");
					p.sendMessage("§dHQPots §7» §rSorry for any inconvenience caused!");
					return;
				}
				if (banCount.get(p) > 4)
				{
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "ipban " + p.getName() + " Security Ban: Attempted to ban more than 5 players in 3 seconds.");
				}
				e.setCancelled(true);
				banCount.put(p, banCount.get(p) + 1);
				p.sendMessage("§dHQPots §7» §rFor security reasons, you cannot ban more than 1 player within 3 seconds.");
				p.sendMessage("§dHQPots §7» §rSorry for any inconvenience caused!");
			}
		}
		else
		{
			for (String s : Essentials.getInstance().getBlockedCommands())
			{
				if (message.startsWith(s))
				{
					e.setCancelled(true);
					p.sendMessage("§dHQPots §7» §rThat command is §e§nblocked§r due to an exploit or related issue.");
				}
			}
		}
	}
}
