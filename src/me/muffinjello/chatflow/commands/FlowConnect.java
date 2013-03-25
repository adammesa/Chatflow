package me.muffinjello.chatflow.commands;

import me.muffinjello.chatflow.chatflow;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FlowConnect implements CommandExecutor{
	
	private chatflow plugin;
	
	public FlowConnect(chatflow plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if (args.length == 1)
		{
			if (chatflow.hasPermissions(sender, "Chatflow.connect"))
			{
				String address = args[0].trim();
				plugin.addresses.add(address);
				if (plugin.servers.trim().length() > 0)
				{
					plugin.servers = plugin.servers + ", " + address.trim();
				} else
				{
					plugin.servers = address.trim();
				}
				plugin.writeConfig();
			} else
			{
				sender.sendMessage(
						ChatColor.YELLOW + "You don't have the required permissions to use this command");
			}
		} else
		{
			sender.sendMessage(ChatColor.YELLOW + "Correct usage is /flowconnect <address>");
		}
		return true;
	}

}