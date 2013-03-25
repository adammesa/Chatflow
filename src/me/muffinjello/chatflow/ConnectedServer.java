package me.muffinjello.chatflow;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public class ConnectedServer
{
    String address;
    public ConcurrentHashMap<String, String> connectedPlayers = new ConcurrentHashMap();
    long lastcontact = 0L;
    int cleanTask;
    String longservername;
    public String shortservername;

    public ConnectedServer(String address, String shortservername, String longservername, chatflow cf)
    {
        this.address = address;
        this.shortservername = shortservername;
        this.longservername = longservername;
        this.lastcontact = System.currentTimeMillis();
        this.cleanTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(cf, new Runnable()
        {
            public void run()
            {
                if (System.currentTimeMillis() - ConnectedServer.this.lastcontact >= 180000L)
                {
                    chatflow.connectedServers.remove(this);
                    Bukkit.getScheduler().cancelTask(ConnectedServer.this.cleanTask);
                }
            }
        }
                , 1500L, 1500L);
    }

    public void setPlayers(String playerstring)
    {
        this.lastcontact = System.currentTimeMillis();
        this.connectedPlayers.clear();
        String name = "";

        int curindex = 0;
        int lastindex = 0;
        boolean dname = false;
        boolean open = false;
        while (curindex < playerstring.length())
        {
            if (playerstring.charAt(curindex) == '"')
            {
                if (open)
                {
                    if (dname)
                    {
                        String displayname = playerstring.substring(lastindex, curindex).replace("\"", "");
                        this.connectedPlayers.put(name, displayname);
                    } else if (!dname)
                    {
                        name = playerstring.substring(lastindex, curindex).replace("\"", "");
                    }
                    lastindex = curindex;
                    dname = !dname;
                }
                open = !open;
            }
            curindex++;
        }
    }
}