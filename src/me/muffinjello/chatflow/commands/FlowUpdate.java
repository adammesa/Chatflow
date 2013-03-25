package me.muffinjello.chatflow.commands;

import me.muffinjello.chatflow.chatflow;
import me.muffinjello.chatflow.util.Updater;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class FlowUpdate extends JavaPlugin implements CommandExecutor{
	
	private chatflow plugin;
	
	public FlowUpdate(chatflow plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(chatflow.hasPermissions(sender, "Chatflow.admin")){
		if(chatflow.update){
			Updater updater = new Updater(plugin, "chatflow", getFile(), Updater.UpdateType.NO_VERSION_CHECK, true); 
			sender.sendMessage(chatflow.cy + "Downloading the latest version of Chatflow!");
		}else{
			sender.sendMessage(chatflow.cy + "Update not found!");
		}
		}else{
			sender.sendMessage(chatflow.cy + "You do not have permission to do this!");
		}
		return true;
	}

}