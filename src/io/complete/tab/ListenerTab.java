package io.complete.tab;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.alexandeh.kraken.tab.PlayerTab;
import com.alexandeh.kraken.tab.TabEntry;
import com.alexandeh.kraken.tab.event.PlayerTabCreateEvent;
import com.google.common.base.Preconditions;

import io.complete.Essentials;
import io.complete.utils.Color;
import server.wenjapvp.hcf.faction.event.PlayerJoinFactionEvent;
import server.wenjapvp.hcf.faction.event.PlayerLeftFactionEvent;
import server.wenjapvp.hcf.faction.struct.Role;
import server.wenjapvp.hcf.faction.type.PlayerFaction;

public class ListenerTab implements Listener
{
	private final Essentials main = Essentials.getInstance();
	private TabUpdateTask tabUpdateTask;

	public String format(Number number, int decimalPlaces)
	{
		return format(number, decimalPlaces, RoundingMode.HALF_DOWN);
	}

	public String format(Number number, int decimalPlaces, RoundingMode roundingMode)
	{
		Preconditions.checkNotNull(number, "The number cannot be null");
		return new BigDecimal(number.toString()).setScale(decimalPlaces, roundingMode).stripTrailingZeros().toPlainString();
	}

	private void clearTabSlots(PlayerTab playerTab)
	{
		for (int i = 0; i < 60; i++)
		{
			int x = i % 3;
			int y = i / 3;
			playerTab.getByPosition(x, y).text(ChatColor.RESET.toString()).send();
		}
	}

	private static String getCardinalDirection(Player player)
	{
		double rotation = (player.getLocation().getYaw() - 90) % 360;
		if (rotation < 0)
		{
			rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5)
		{
			return "N";
		}
		else if (22.5 <= rotation && rotation < 67.5)
		{
			return "NE";
		}
		else if (67.5 <= rotation && rotation < 112.5)
		{
			return "E";
		}
		else if (112.5 <= rotation && rotation < 157.5)
		{
			return "SE";
		}
		else if (157.5 <= rotation && rotation < 202.5)
		{
			return "S";
		}
		else if (202.5 <= rotation && rotation < 247.5)
		{
			return "SW";
		}
		else if (247.5 <= rotation && rotation < 292.5)
		{
			return "W";
		}
		else if (292.5 <= rotation && rotation < 337.5)
		{
			return "NW";
		}
		else if (337.5 <= rotation && rotation < 360.0)
		{
			return "N";
		}
		else
		{
			return null;
		}
	}

	@EventHandler
	public void onPlayerTabCreateEvent(PlayerTabCreateEvent event)
	{
		PlayerTab playerTab = event.getPlayerTab();
		this.tabUpdateTask = new TabUpdateTask(playerTab);
		this.tabUpdateTask.runTaskTimer(this.main, 0L, 4L);
	}

	@EventHandler
	public void onPlayerJoinFaction(PlayerJoinFactionEvent event)
	{
		Player player = (Player) event.getPlayer().get();
		PlayerTab playerTab = PlayerTab.getByPlayer(player);
		if (playerTab != null)
		{
			clearTabSlots(playerTab);
		}
	}

	@EventHandler
	public void onPlayerLeftFaction(PlayerLeftFactionEvent event)
	{
		Player player = (Player) event.getPlayer().get();
		PlayerTab playerTab = PlayerTab.getByPlayer(player);
		if (playerTab != null)
		{
			clearTabSlots(playerTab);
		}
	}

	private class TabUpdateTask extends BukkitRunnable
	{
		private final Player player;
		private final PlayerTab playerTab;

		public TabUpdateTask(PlayerTab playerTab)
		{
			this.player = playerTab.getPlayer();
			this.playerTab = playerTab;
		}

		public void run()
		{
			if (this.player.isOnline())
			{
				PlayerFaction playerFaction = ListenerTab.this.main.getHCF().getFactionManager().getPlayerFaction(this.player);
				if (playerFaction != null)
				{
					this.playerTab.getByPosition(0, 0).text(Color.translate("&d&lHome")).send();
					TabEntry homeCoords = this.playerTab.getByPosition(0, 1);
					if (playerFaction.getHome() == null)
					{
						homeCoords.text(Color.translate("&cNo home set.")).send();
					}
					else
					{
						homeCoords.text(Color.translate("&e" + playerFaction.getHome().getBlockX() + ", " + playerFaction.getHome().getBlockY() + ", " + playerFaction.getHome().getBlockZ())).send();
					}
					this.playerTab.getByPosition(0, 3).text(Color.translate("&d&lFaction Info")).send();
					double dtr = playerFaction.getDeathsUntilRaidable();

					this.playerTab.getByPosition(0, 4).text(Color.translate("&fDTR: " + ListenerTab.this.format(Double.valueOf(dtr), 2))).send();
					this.playerTab.getByPosition(0, 5).text(Color.translate("&fOnline: " + playerFaction.getOnlinePlayers().size() + "/" + playerFaction.getMembers().size())).send();
					this.playerTab.getByPosition(0, 6).text(Color.translate("&fBalance: $" + playerFaction.getBalance())).send();

					this.playerTab.getByPosition(0, 8).text(Color.translate("&d&lPlayer Info")).send();
					this.playerTab.getByPosition(0, 9).text(Color.translate("&fKills: " + ListenerTab.this.main.getHCF().getUserManager().getUser(this.player.getUniqueId()).getKills())).send();

					this.playerTab.getByPosition(0, 11).text(Color.translate("&d&lLocation")).send();
					this.playerTab.getByPosition(0, 12).text(Color.translate(ListenerTab.this.main.getHCF().getFactionManager().getFactionAt(this.player.getLocation()).getDisplayName(this.player))).send();
					this.playerTab.getByPosition(0, 13).text(Color.translate("&7(" + this.player.getLocation().getBlockX() + ", " + this.player.getLocation().getBlockZ() + ") [" + ListenerTab.this.getCardinalDirection(this.player) + "]")).send();

					this.playerTab.getByPosition(1, 0).text(Color.translate("&d&lHQPots")).send();

					this.playerTab.getByPosition(1, 2).text(Color.translate("&f" + playerFaction.getName())).send();

					List<Player> factionMembers = new ArrayList();
					for (Player factionLeader : playerFaction.getOnlinePlayers())
					{
						if (playerFaction.getMember(factionLeader).getRole() == Role.LEADER)
						{
							factionMembers.add(factionLeader);
						}
					}
					for (Player factionCaptain : playerFaction.getOnlinePlayers())
					{
						if (playerFaction.getMember(factionCaptain).getRole() == Role.COLEADER)
						{
							factionMembers.add(factionCaptain);
						}
					}
					for (Player factionCaptain : playerFaction.getOnlinePlayers())
					{
						if (playerFaction.getMember(factionCaptain).getRole() == Role.CAPTAIN)
						{
							factionMembers.add(factionCaptain);
						}
					}
					for (Player factionMember : playerFaction.getOnlinePlayers())
					{
						if (playerFaction.getMember(factionMember).getRole() == Role.MEMBER)
						{
							factionMembers.add(factionMember);
						}
					}
					int index = 3;
					for (Player factionPlayer : playerFaction.getOnlinePlayers())
					{
						factionPlayer = (Player) factionMembers.get(index - 3);
						switch (playerFaction.getMember(factionPlayer).getRole())
						{
							case LEADER:
								this.playerTab.getByPosition(1, index++).text(ChatColor.GREEN + playerFaction.getMember(factionPlayer).getRole().getAstrix() + factionPlayer.getName()).send();
								break;
							case CAPTAIN:
								this.playerTab.getByPosition(1, index++).text(ChatColor.GREEN + playerFaction.getMember(factionPlayer).getRole().getAstrix() + factionPlayer.getName()).send();
								break;
							default:
								this.playerTab.getByPosition(1, index++).text(ChatColor.GREEN + playerFaction.getMember(factionPlayer).getRole().getAstrix() + factionPlayer.getName()).send();
						}
					}
					for (;

							index < 19; index++)
					{
						this.playerTab.getByPosition(1, index).text(ChatColor.RESET.toString()).send();
					}
					this.playerTab.getByPosition(2, 0).text(Color.translate("&d&lMap Kit")).send();
					this.playerTab.getByPosition(2, 1).text(Color.translate("&fProt 1, Sharp 1")).send();
					
//					 this.playerTab.getByPosition(2,
//					 3).text(Color.translate("&6&lBorder")).send();
//					 this.playerTab.getByPosition(2,
//					 4).text(Color.translate("&f" +
//					 WorldData.getInstance().getConfig().getInt("world-NORMAL-border"))).send();

					this.playerTab.getByPosition(2, 6).text(Color.translate("&d&lPlayers Online")).send();
					this.playerTab.getByPosition(2, 7).text(Color.translate("&f" + Bukkit.getServer().getOnlinePlayers().length)).send();
				}
				else
				{
					this.playerTab.getByPosition(0, 0).text(Color.translate("&d&lPlayer Info")).send();
					this.playerTab.getByPosition(0, 1).text(Color.translate("&fKills: " + ListenerTab.this.main.getHCF().getUserManager().getUser(this.player.getUniqueId()).getKills())).send();

					this.playerTab.getByPosition(0, 3).text(Color.translate("&d&lYour Location")).send();
					this.playerTab.getByPosition(0, 4).text(Color.translate(ListenerTab.this.main.getHCF().getFactionManager().getFactionAt(this.player.getLocation()).getDisplayName(this.player))).send();
					this.playerTab.getByPosition(0, 5).text(Color.translate("&7(" + this.player.getLocation().getBlockX() + ", " + this.player.getLocation().getBlockZ() + ") [" + ListenerTab.this.getCardinalDirection(this.player) + "]")).send();

					this.playerTab.getByPosition(1, 0).text(Color.translate("&d&lHQPots")).send();

					this.playerTab.getByPosition(2, 0).text(Color.translate("&d&lMap Kit")).send();
					this.playerTab.getByPosition(2, 1).text(Color.translate("&fProt 1, Sharp 1")).send();

					this.playerTab.getByPosition(2, 3).text(Color.translate("&d&lBorder")).send();
					this.playerTab.getByPosition(2, 4).text(Color.translate("&f2000")).send();

					this.playerTab.getByPosition(2, 6).text(Color.translate("&d&lPlayers Online")).send();
					this.playerTab.getByPosition(2, 7).text(Color.translate("&f" + Bukkit.getServer().getOnlinePlayers().length)).send();
				}
			}
			else
			{
				cancel();
			}
		}
	}
}
