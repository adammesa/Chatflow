package me.muffinjello.chatflow.commands;

import me.muffinjello.chatflow.ConnectedServer;
import me.muffinjello.chatflow.chatflow;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FlowServerList implements CommandExecutor{
	
	private chatflow plugin;
	
	public FlowServerList(chatflow plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
			for (ConnectedServer cs : chatflow.connectedServers)
			{
				sender.sendMessage(cs.shortservername);
			}
		return true;
	}

}