package io.complete.dtc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import server.wenjapvp.hcf.HCF;
import server.wenjapvp.hcf.eventgame.faction.EventFaction;
import server.wenjapvp.hcf.faction.type.Faction;

public class DTCListener
  implements Listener
{
  @EventHandler
  public void onInteract(PlayerInteractEvent e)
  {
    Player p = e.getPlayer();
    if ((p.getItemInHand().getType() == Material.WOOD_HOE) && (p.getItemInHand().hasItemMeta()) && 
      (p.getItemInHand().getItemMeta().hasDisplayName()) && (p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "DTC Wand")) && 
      (p.hasPermission("scorpion.dtc")))
    {
      if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
      {
        p.sendMessage(ChatColor.RED + "Right click a block to select DTC location.");
        return;
      }
      DTC.pos.put(p, e.getClickedBlock().getLocation());
      p.sendMessage(ChatColor.GREEN + "Posision set! " + ChatColor.GOLD + "(" + ChatColor.GRAY + 
        e.getClickedBlock().getLocation().getBlockX() + ", " + e.getClickedBlock().getLocation().getBlockY() + ", " + e.getClickedBlock().getLocation().getBlockZ() + ChatColor.GOLD + ")");
    }
  }
  
  @EventHandler
  public void onDrop(PlayerDropItemEvent e)
  {
    Player p = e.getPlayer();
    if ((p.getItemInHand().getType() == Material.WOOD_HOE) && (p.getItemInHand().hasItemMeta()) && 
      (p.getItemInHand().getItemMeta().hasDisplayName()) && (p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "DTC Wand"))) {
      e.getItemDrop().remove();
    }
  }
  
  @EventHandler
  public void onBreak(BlockBreakEvent e)
  {
    Faction a = HCF.getPlugin().getFactionManager().getFactionAt(e.getBlock().getLocation());
    if ((e.getBlock().getType() == Material.OBSIDIAN) && (a != null) && ((a instanceof EventFaction))) {
      for (String s : DTC.data.getKeys(false)) {
        if (DTC.getActiveDTC.containsKey(s))
        {
          e.setCancelled(true);
          DTC.getActiveDTC.put(s, Integer.valueOf(((Integer)DTC.getActiveDTC.get(s)).intValue() - 1));
          if ((String.valueOf(DTC.getActiveDTC.get(s)).endsWith("0")) && (((Integer)DTC.getActiveDTC.get(s)).intValue() != 0)) {
            Bukkit.broadcastMessage(ChatColor.GOLD + " » " + ChatColor.GRAY + "(DTC) " + ChatColor.DARK_GREEN + s + ChatColor.YELLOW + 
              " has " + DTC.getActiveDTC.get(s) + " points left!");
          }
          if (((Integer)DTC.getActiveDTC.get(s)).intValue() == 0)
          {
            Bukkit.broadcastMessage(ChatColor.GOLD + " » " + ChatColor.GRAY + "(DTC) " + ChatColor.DARK_GREEN + s + ChatColor.RED + 
              " has been broken by " + ChatColor.YELLOW + e.getPlayer().getName() + ChatColor.RED + "!");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "key give " + e.getPlayer().getName() + " koth 1");
            DTC.getActiveDTC.clear();
          }
        }
      }
    }
  }
}
