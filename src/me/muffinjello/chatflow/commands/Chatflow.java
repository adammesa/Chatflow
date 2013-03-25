package me.muffinjello.chatflow.commands;

import me.muffinjello.chatflow.chatflow;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class Chatflow implements CommandExecutor{
	
	private chatflow plugin;
	
	public Chatflow(chatflow plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        Player player = (Player) sender;
		PluginDescriptionFile pdfFile = plugin.getDescription();
        if(args.length == 0){
		player.sendMessage(
				ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
		player.sendMessage(ChatColor.YELLOW + "Chatflow is a plugin made by Pylamo, Muffinjello and Jeff.");
		player.sendMessage(ChatColor.GOLD + "Chatflow usage:");
		player.sendMessage(
				ChatColor.YELLOW + "Type " + ChatColor.RED + "!<Message> " + ChatColor.YELLOW + "to send a cross server message.");
		player.sendMessage(
				ChatColor.YELLOW + "Type " + ChatColor.RED + "!msg <Player> <Message> " + ChatColor.YELLOW + "cross server whispering." + ChatColor.RED + " [Beta]");
		player.sendMessage(
				ChatColor.YELLOW + "Type " + ChatColor.RED + "!toggle" + ChatColor.YELLOW + " to toggle automatic cross server chat.");
		player.sendMessage(
				ChatColor.YELLOW + "Type " + ChatColor.RED + "!list" + ChatColor.YELLOW + " to get a list of online players from other servers.");
		if (chatflow.hasPermissions(sender, "Chatflow.admin"))
		{
			player.sendMessage(
					ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Administrator Commands:");
			player.sendMessage(
					ChatColor.RED + "/flowsilence <player> " + ChatColor.YELLOW + "to stop a user from using ChatFlow");
			player.sendMessage(
					ChatColor.RED + "/flowunsilence <player> " + ChatColor.YELLOW + "to allow a user to use ChatFlow");
            player.sendMessage(ChatColor.RED + "/chatflow debug " + ChatColor.YELLOW + "to see debugging information");
			player.sendMessage(
					ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
		}
		player.sendMessage(
				ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
		return true;
        }
        if(args.length != 0 && args[0].trim().equalsIgnoreCase("debug") && chatflow.hasPermissions(sender, "Chatflow.admin")){
            player.sendMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
            player.sendMessage(ChatColor.RED + "ChatFlow Debug Information:");
            player.sendMessage(ChatColor.GRAY + "Your Chatflow version is " + ChatColor.YELLOW + pdfFile.getVersion());
            if (chatflow.serverName.length() > 1)
            {
                player.sendMessage(ChatColor.GRAY + "Your current server name is " + ChatColor.YELLOW + plugin.serverName);
            }
            if  (chatflow.shortserverName.length() > 1){
                player.sendMessage(ChatColor.GRAY + "Your current short server name is " + ChatColor.YELLOW + plugin.shortserverName);
            }
            if (plugin.italics.equals("true"))
            {
                player.sendMessage(ChatColor.GRAY + "Italics are " + ChatColor.GREEN + "ENABLED");
            } else if (plugin.italics.equals("false"))
            {
                player.sendMessage(ChatColor.GRAY + "Italics are " + ChatColor.YELLOW + "DISABLED");
            } else
            {
                player.sendMessage(ChatColor.GRAY + "Italics are " + ChatColor.RED + "misconfigured!");
            }
            if (plugin.autotoggle.equals("true"))
            {
                player.sendMessage(ChatColor.GRAY + "Automatic toggling is " + ChatColor.GREEN + "ENABLED");
            } else if (plugin.autotoggle.equals("false"))
            {
                player.sendMessage(ChatColor.GRAY + "Automatic toggling is " + ChatColor.YELLOW + "DISABLED");
            } else
            {
                player.sendMessage(ChatColor.GRAY + "Automatic toggling is " + ChatColor.RED + "misconfigured!");
            }
            if (plugin.joinAndLeaveMsgs.equals("true"))
            {
                player.sendMessage(ChatColor.GRAY + "Join/Leave messages are " + ChatColor.GREEN + "ENABLED");
            } else if (plugin.joinAndLeaveMsgs.equals("false"))
            {
                player.sendMessage(ChatColor.GRAY + "Join/Leave messages are " + ChatColor.YELLOW + "DISABLED");
            } else
            {
                player.sendMessage(ChatColor.GRAY + "Join/Leave messages are " + ChatColor.RED + "misconfigured!");
            }
            player.sendMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
            return true;
	}
		return true;
	}
}