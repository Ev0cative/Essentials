package io.complete.command;

import org.bukkit.command.defaults.GameModeCommand;

import io.complete.Essentials;
import io.complete.api.Module;
import io.complete.command.staff.GamemodeCommand;
import io.complete.command.staff.StaffChatCommand;
import io.complete.dtc.DTCCommand;

public class StaffModule extends Module
{
	public StaffModule(Essentials essentials)
	{
		this.commands.add(new GamemodeCommand());
		this.commands.add(new DTCCommand(essentials));
		this.commands.add(new StaffChatCommand());
	}
}
