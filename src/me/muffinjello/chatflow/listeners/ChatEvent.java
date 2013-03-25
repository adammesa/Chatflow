package me.muffinjello.chatflow.listeners;

import java.util.Map;
import java.util.regex.Pattern;
import me.muffinjello.chatflow.ConnectedServer;
import me.muffinjello.chatflow.chatflow;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent implements Listener{
	
	private chatflow plugin;
	
	public ChatEvent(chatflow plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		String message = event.getMessage();
		message = event.getMessage();
		if(message.startsWith("/a")){
			event.getPlayer().sendMessage(plugin.cy + "Stopped Chatflow message!");
			event.setCancelled(true);
		}
		if (message.startsWith("!") && message.length() > 1)
		{
			if (plugin.silencedPlayers.contains(event.getPlayer().getName().toLowerCase()))
			{
				event.getPlayer().sendMessage(ChatColor.YELLOW + "You are silenced and can't chat with other servers.");
				return;
			}
			
			message = message.substring(1);
			/*if (message.trim().equalsIgnoreCase("ignore"))
			{
				if(ignoredPlayers.contains(event.getPlayer().getName()))
				{
					ignoredPlayers.remove(event.getPlayer().getName());
					event.getPlayer().sendMessage(cy + "Flow messages toggled " + ChatColor.GREEN + "on" + cy + ".");
				}else
				{
					ignoredPlayers.add(event.getPlayer().getName());
					event.getPlayer().sendMessage(cy + "Flow messages toggled " + ChatColor.RED + "off" + cy + ".");
				}
			}*/
			if (message.trim().equalsIgnoreCase("list"))
			{
				plugin.sendFlowCommand("list", event.getPlayer().getName());
				event.setCancelled(true);
				return;
			}
			if (message.trim().equalsIgnoreCase("toggle"))
			{
				if (plugin.toggledPlayers.contains(event.getPlayer().getName()))
				{
					plugin.toggledPlayers.remove(event.getPlayer().getName());
					event.getPlayer().sendMessage(plugin.cy + "Toggled chatflow " + ChatColor.RED + "off" + plugin.cy + ".");
				} else
				{
					plugin.toggledPlayers.add(event.getPlayer().getName());
					event.getPlayer().sendMessage(plugin.cy + "Toggled chatflow " + ChatColor.GREEN + "on" + plugin.cy + ".");
				}
				event.setCancelled(true);
				return;
			}
			if (message.trim().startsWith("msg"))
			{
				String[] args = message.split(Pattern.quote(" "));
				if (args.length < 3)
				{
					return;
				}
				String player = args[1].trim();
				String pmessage = args[2];
				for (int i = 3; i < args.length; i++)
				{
					pmessage += " " + args[i];
				}
				event.setCancelled(true);
				//Bukkit search algorithm!
				int delta = 2147483647;
				String playername = null;
				for (ConnectedServer cs : plugin.connectedServers)
				{
					for (Map.Entry<String, String> users : cs.connectedPlayers.entrySet())
					{
						if (users.getKey().toLowerCase().startsWith(player.toLowerCase()))
						{
							int curDelta = users.getKey().length() - player.toLowerCase().length();
							if (curDelta < delta)
							{
								playername = users.getValue();
								delta = curDelta;
							}
							if (curDelta == 0)
								break;
						}
					}
				}
					plugin.sendFlowCommand("msg",
							player + "°" + ChatColor.YELLOW + "From " + ChatColor.GOLD + event.getPlayer().getName() + ":\n" + ChatColor.YELLOW + ChatColor.ITALIC + ChatColor.WHITE + pmessage);
					event.getPlayer().sendMessage(
							ChatColor.GOLD + "[Me -> " + ChatColor.YELLOW + player + ChatColor.GOLD + "]: " + ChatColor.WHITE + pmessage);
				return;
			}
			if (plugin.italics.equals("true") && !plugin.toggledPlayers.contains(event.getPlayer().getName()))
			{
				event.setMessage(ChatColor.ITALIC + message);
				message = plugin.cy + plugin.shortserverName + plugin.SSNspacer + event.getPlayer().getDisplayName() + ": " + ChatColor.ITALIC + plugin.cy2 + message;
				plugin.sendFlowMessage(message);
			} else if (plugin.italics.equals("false") && !plugin.toggledPlayers.contains(event.getPlayer().getName()))
			{
				event.setMessage(message);
				message = plugin.cy + plugin.shortserverName + plugin.SSNspacer + event.getPlayer().getDisplayName() + ": " + plugin.cy2 + message;
				plugin.sendFlowMessage(message);
			}
		}
		if (plugin.toggledPlayers.contains(event.getPlayer().getName()) && plugin.italics.equals(
				"true") && !event.getMessage().startsWith("/"))
		{
			event.setMessage(ChatColor.ITALIC + message);
			message = plugin.cy + plugin.shortserverName + plugin.SSNspacer + event.getPlayer().getDisplayName() + ": " + ChatColor.ITALIC + plugin.cy2 + message;
			plugin.sendFlowMessage(message);
		} else
		{
			if (plugin.toggledPlayers.contains(event.getPlayer().getName()) && plugin.italics.equals(
					"false") && !event.getMessage().startsWith("/"))
			{
				event.setMessage(message);
				message = plugin.cy + plugin.shortserverName + plugin.SSNspacer + event.getPlayer().getDisplayName() + ": " + plugin.cy2 + message;
				plugin.sendFlowMessage(message);
			}
		}
	}

}
