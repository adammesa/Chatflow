package me.muffinjello.chatflow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import me.muffinjello.chatflow.commands.Chatflow;
import me.muffinjello.chatflow.commands.FlowConnect;
import me.muffinjello.chatflow.commands.FlowSilence;
import me.muffinjello.chatflow.commands.FlowUnsilence;
import me.muffinjello.chatflow.commands.FlowUpdate;
import me.muffinjello.chatflow.listeners.ChatEvent;
import me.muffinjello.chatflow.listeners.JoinEvent;
import me.muffinjello.chatflow.listeners.QuitEvent;
import me.muffinjello.chatflow.util.Files;
import me.muffinjello.chatflow.util.Metrics;
import me.muffinjello.chatflow.util.Updater;
import me.muffinjello.chatflow.util.Updater.UpdateResult;
import me.muffinjello.chatflow.util.Updater.UpdateType;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class chatflow extends JavaPlugin
        implements Listener
{
    public final Logger logger = Logger.getLogger("Minecraft");
    public int port = 8976;
    public static String serverName = "";
    public static String shortserverName = "";
    public String SSNspacer = "";
    public String servers = "";
    public String joinAndLeaveMsgs = "true";
    public static String cy = "§1";
    public String autotoggle = "false";
    public String cy2 = "§f";
    public String italics = "true";
    public ServerSocket serverSocket;
    public HashSet<String> addresses = new HashSet();
    public static String version = "";
    public Files silencedPlayers;
    public BukkitTask playerListTask;
    public static HashSet<ConnectedServer> connectedServers = new HashSet();
    public static boolean update = false;
    public static String name = "";
    public static long size = 0L;
    public boolean autoupdate = true;

    boolean enabled = true;

    HashSet<String> ignoredPlayers = new HashSet();
    public static Permission permissionHandler;
    public HashSet<String> toggledPlayers = new HashSet();

    public void onDisable()
    {
        String message = cy + "[" + this.cy2 + "ChatFlow" + cy + "] " + serverName + cy + " has disconnected.";
        sendFlowMessage(message);

        Bukkit.getScheduler().cancelTask(this.playerListTask.getTaskId());
        this.enabled = false;
        this.silencedPlayers.save();
        PluginDescriptionFile pdfFile = getDescription();
        this.logger.info(pdfFile.getName() + " Has Been Disabled!!!");
        if (this.serverSocket != null)
        {
            try
            {
                this.serverSocket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        connectedServers = null;
    }

    public void onEnable()
    {
        try
        {
            Metrics metrics = new Metrics(this);
            metrics.start();
        }
        catch (IOException localIOException)
        {
        }
        this.enabled = true;
        String pluginFolder = getDataFolder().getAbsolutePath();
        new File(pluginFolder).mkdirs();
        this.silencedPlayers = new Files(new File(pluginFolder + File.separator + "silenced-players.txt"));
        this.silencedPlayers.load();
        getServer().getPluginManager().registerEvents(this, this);
        setupPermissions();
        PluginDescriptionFile pdfFile = getDescription();
        version = pdfFile.getVersion();
        this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " Has Been Enabled!!!");
        for (Player p : getServer().getOnlinePlayers()) {
            if ((this.autotoggle.equals("true")) && (!this.silencedPlayers.contains(p.getName())))
            {
                this.toggledPlayers.add(p.getPlayer().getDisplayName());
            }

        }

        this.playerListTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable()
        {
            public void run()
            {
                chatflow.this.sendPlayerList();
            }
        }
                , 0L, 60000L);
        readConfig();
        writeConfig();
        saveConfig();
        setupPermissions();
        new JoinEvent(this);
        new QuitEvent(this);
        new ChatEvent(this);
        getCommand("chatflow").setExecutor(new Chatflow(this));
        getCommand("flowsilence").setExecutor(new FlowSilence(this));
        getCommand("flowconnect").setExecutor(new FlowConnect(this));
        getCommand("flowunsilence").setExecutor(new FlowUnsilence(this));
        getCommand("flowupdate").setExecutor(new FlowUpdate(this));
        setupUpdater();
    }

    public void setupUpdater() {
        if (this.autoupdate) {
            Updater updater = new Updater(this, "chatflow", getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
            update = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
            name = updater.getLatestVersionString();
            size = updater.getFileSize();
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                String message = chatflow.cy + "[" + chatflow.this.cy2 + "ChatFlow" + chatflow.cy + "] " + chatflow.serverName + chatflow.cy + " has connected.";
                chatflow.this.sendFlowMessage(message);
            }
        }
                , 3L);
    }

    public void readConfig() {
        this.port = getConfig().getInt("Port", this.port);
        serverName = getConfig().getString("ServerName", serverName);
        shortserverName = getConfig().getString("ShortServerName", shortserverName);
        this.servers = getConfig().getString("Servers", this.servers);

        //cy = getConfig().getString("PrimaryColor", cy.replaceAll("(&([a-f0-9]))", "§$2"));
        //this.cy2 = getConfig().getString("SecondaryColor", this.cy2.replaceAll("(&([a-f0-9]))", "§$2"));
        this.autotoggle = getConfig().getString("AutoToggle", this.autotoggle);
        this.italics = getConfig().getString("Italics", this.italics);
        this.joinAndLeaveMsgs = getConfig().getString("JoinAndLeaveMessages", this.joinAndLeaveMsgs);
        this.autoupdate = getConfig().getBoolean("Auto-Updater", this.autoupdate);
        if (shortserverName.length() > 1)
        {
            this.SSNspacer = " ";
        }

        String[] split = this.servers.split(Pattern.quote(","));
        for (String s : split)
        {
            this.addresses.add(s.trim());
        }
        startServer();
    }

    public void writeConfig()
    {
        getConfig().set("Port", Integer.valueOf(this.port));
        getConfig().set("ServerName", serverName);
        getConfig().set("JoinAndLeaveMessages", this.joinAndLeaveMsgs);
        getConfig().set("ShortServerName", shortserverName);
        getConfig().set("Servers", this.servers);
        getConfig().set("AutoToggle", this.autotoggle);
        //getConfig().set("PrimaryColor", cy);
        //getConfig().set("SecondaryColor", this.cy2);
        getConfig().set("Italics", this.italics);
        getConfig().set("Auto-Updater", Boolean.valueOf(this.autoupdate));
    }

    public void startServer()
    {
        final chatflow p = this;
        Thread t = new Thread()
        {
            public void run()
            {
                try
                {
                    chatflow.this.serverSocket = new ServerSocket(chatflow.this.port);
                    while (chatflow.this.enabled)
                    {
                        Socket cs = chatflow.this.serverSocket.accept();
                        new MessageHandler(cs, p);
                        chatflow.this.serverSocket.close();
                        chatflow.this.serverSocket = new ServerSocket(chatflow.this.port);
                    }
                    chatflow.this.serverSocket.close();
                }
                catch (IOException localIOException)
                {
                }
            }
        };
        t.start();
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider permissionProvider = getServer().getServicesManager().getRegistration(
                Permission.class);
        if (permissionProvider != null)
        {
            permissionHandler = (Permission)permissionProvider.getProvider();
        }
        return permissionHandler != null;
    }

    public static boolean hasPermissions(CommandSender a, String b)
    {
        if (((a instanceof ConsoleCommandSender)) || ((a instanceof RemoteConsoleCommandSender)) || (b == null) || (b == "") ||
                (b.equals(
                        "")))
            return true;
        if ((permissionHandler != null) && (permissionHandler.has(a, b)))
        {
            return true;
        }
        return (a.hasPermission(b)) || (a.isOp());
    }

    public void sendPlayerList()
    {
        String playerlist = shortserverName + "Â°" + serverName + "Â°";
        for (Player p : Bukkit.getOnlinePlayers())
        {
            playerlist = playerlist + "\"" + p.getName() + "\"Â°\"" + p.getDisplayName() + "\"";
        }
        sendFlowCommand("playerlist", playerlist);
    }

    public void sendFlowMessage(final String message)
    {
        for (String address : this.addresses)
        {
            String[] split = address.split(Pattern.quote(":"));
            if (split.length != 2)
            {
                return;
            }
            try
            {
                final String host = split[0].trim();
                final int port = Integer.parseInt(split[1].trim());
                Thread t = new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            Socket s = new Socket(host, port);
                            BufferedWriter bos = new BufferedWriter(
                                    new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
                            bos.append("message: " + message);
                            bos.flush();
                            s.close();
                        }
                        catch (Exception localException)
                        {
                        }
                    }
                };
                t.start();
            }
            catch (Exception localException)
            {
            }
        }
    }

    public void sendFlowCommand(final String command, final String params)
    {
        for (String address : this.addresses)
        {
            String[] split = address.split(Pattern.quote(":"));
            if (split.length != 2)
            {
                return;
            }
            try
            {
                final String host = split[0].trim();
                final int port = Integer.parseInt(split[1].trim());
                Thread t = new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            Socket s = new Socket(host, port);
                            BufferedWriter bos = new BufferedWriter(
                                    new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
                            bos.append("command: " + command + params + "|" + chatflow.this.port);
                            bos.flush();
                            s.close();
                        }
                        catch (Exception localException)
                        {
                        }
                    }
                };
                t.start();
            }
            catch (Exception localException)
            {
            }
        }
    }
}