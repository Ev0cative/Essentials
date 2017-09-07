package io.complete.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.scheduler.BukkitRunnable;

import io.complete.Essentials;

public class CommandManagerUtil
  implements CommandManager
{
  private static final String PERMISSION_MESSAGE = ChatColor.RED + "You do not have permission to execute this command.";
  private final Map<String, CommandClass> commandMap;
  
  public CommandManagerUtil(final Essentials plugin)
  {
    this.commandMap = new HashMap();
    final ConsoleCommandSender console = plugin.getServer().getConsoleSender();
    new BukkitRunnable()
    {
      public void run()
      {
        Collection<CommandClass> commands = CommandManagerUtil.this.commandMap.values();
        for (CommandClass command : commands)
        {
          String commandName = command.getName();
          PluginCommand pluginCommand = plugin.getCommand(commandName);
          if (pluginCommand == null)
          {
            Bukkit.broadcastMessage(commandName);
            console.sendMessage('[' + plugin.getName() + "] " + ChatColor.YELLOW + "Failed to register command '" + commandName + "'.");
            console.sendMessage('[' + plugin.getName() + "] " + ChatColor.YELLOW + "Reason: Undefined in plugin.yml.");
          }
          else
          {
            pluginCommand.setAliases(Arrays.asList(command.getAliases()));
            pluginCommand.setDescription(command.getDescription());
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
            pluginCommand.setUsage(command.getUsage());
            pluginCommand.setPermission("base.command." + command.getName());
            pluginCommand.setPermissionMessage(CommandManagerUtil.PERMISSION_MESSAGE);
          }
        }
      }
    }
    
      .runTask(plugin);
  }
  
  public boolean containsCommand(CommandClass command)
  {
    return this.commandMap.containsValue(command);
  }
  
  public void registerAll(Module module)
  {
    if (module.isEnabled())
    {
      Set<CommandClass> commands = module.getCommands();
      for (CommandClass command : commands) {
        this.commandMap.put(command.getName(), command);
      }
    }
  }
  
  public void registerCommand(CommandClass command)
  {
    this.commandMap.put(command.getName(), command);
  }
  
  public void registerCommands(CommandClass[] commands)
  {
    for (CommandClass command : commands) {
      this.commandMap.put(command.getName(), command);
    }
  }
  
  public void unregisterCommand(CommandClass command)
  {
    this.commandMap.values().remove(command);
  }
  
  public CommandClass getCommand(String id)
  {
    return (CommandClass)this.commandMap.get(id);
  }
}

