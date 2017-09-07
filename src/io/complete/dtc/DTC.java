package io.complete.dtc;

import java.io.File;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import io.complete.Essentials;
import io.complete.listener.ItemBuilder;

public class DTC
  implements Listener
{
  public static ItemStack DTC_WAND = new ItemBuilder(Material.WOOD_HOE).displayName(ChatColor.GREEN + "DTC Wand").build();
  public static HashMap<Player, Location> pos = new HashMap();
  public static HashMap<String, Integer> getActiveDTC = new HashMap();
  public static File file = new File(Essentials.getInstance().getDataFolder(), "dtc.yml");
  public static FileConfiguration data = YamlConfiguration.loadConfiguration(file);
}
