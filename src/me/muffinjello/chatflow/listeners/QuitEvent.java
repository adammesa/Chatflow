package me.muffinjello.chatflow.listeners;

import me.muffinjello.chatflow.chatflow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEvent implements Listener{
	
	/*private chatflow plugin;
	
	public QuitEvent(chatflow plugin){
		this.plugin = plugin;
	}*/
	private final chatflow plugin; // pointer to your main class, unrequired if you don't need methods from the main class
	 
	public QuitEvent(chatflow plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		if (plugin.joinAndLeaveMsgs.equals("true"))
		{
			String serverheader = chatflow.shortserverName + "°" + chatflow.serverName + "°";
			String message = chatflow.cy + chatflow.shortserverName + plugin.SSNspacer + event.getPlayer().getDisplayName() + chatflow.cy + " left the server " + chatflow.serverName;
			plugin.sendFlowCommand("playerdisconnect", serverheader + event.getPlayer().getDisplayName() + "°" + event.getPlayer().getName());
		}
	}

}