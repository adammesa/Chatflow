package me.muffinjello.chatflow.listeners;

import me.muffinjello.chatflow.chatflow;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener{
	
    private final chatflow plugin;

    public JoinEvent(chatflow plugin) {   
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		  Player player = event.getPlayer();
		  if(player.hasPermission("Chatflow.seeupdate") && chatflow.update)
		  {
		    player.sendMessage(chatflow.cy + "An update is available: " + chatflow.name + "(" + chatflow.size + " bytes)");
		    player.sendMessage(chatflow.cy + "Type /flowupdate if you would like to update.");
		  }
		if (plugin.autotoggle.equals("true") && !plugin.silencedPlayers.contains(player.getName()))
		{
			plugin.toggledPlayers.add(event.getPlayer().getDisplayName());
		}
		if (plugin.joinAndLeaveMsgs.equals("true"))
		{
			String serverheader = chatflow.shortserverName + "°" + chatflow.serverName + "°";
			//String message = cy + shortserverName + SSNspacer + event.getPlayer().getDisplayName() + cy + " joined the server " + serverName;
			plugin.sendFlowCommand("playerconnect", serverheader + event.getPlayer().getDisplayName() + "°" + event.getPlayer().getName());
			//sendFlowMessage(message);
		}
	}
}