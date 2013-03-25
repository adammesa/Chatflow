package me.muffinjello.chatflow.commands;

import me.muffinjello.chatflow.chatflow;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FlowUnsilence implements CommandExecutor{
	
	private chatflow plugin;
	
	public FlowUnsilence(chatflow plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if (args.length == 1)
		{
			if (chatflow.hasPermissions(sender, "Chatflow.silence"))
			{
				String playername = args[0].trim();
				plugin.silencedPlayers.remove(playername.toLowerCase());
				plugin.silencedPlayers.save();
				sender.sendMessage(
						ChatColor.YELLOW + "Successfully unsilenced player " + ChatColor.GOLD + playername);
			} else
			{
				sender.sendMessage(
						ChatColor.YELLOW + "You don't have the required permissions to use this command");
			}
		} else
		{
			sender.sendMessage(ChatColor.YELLOW + "Correct usage is /flowunsilence <player>");
		}
		return true;
	}

}