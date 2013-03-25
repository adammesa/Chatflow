package me.muffinjello.chatflow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class MessageHandler extends Thread
{
    Socket s;
    chatflow p;
    //static String playerName = "";

    public MessageHandler(Socket s, chatflow p)
    {
        this.p = p;
        this.s = s;
        start();
    }

    public void run()
    {
        try
        {
            BufferedReader bis = new BufferedReader(new InputStreamReader(this.s.getInputStream(), "UTF-8"));
            String input = bis.readLine();
            String message = "";
            while (input != null)
            {
                message = message + input + "\n";
                input = bis.readLine();
            }
            if (message.endsWith("\n"))
            {
                message = message.substring(0, message.length() - 1);
            }
            if (message.startsWith("message: "))
            {
                handleMessage(message.substring(9));
            } else if (message.startsWith("command: "))
            {
                handleCommand(message.substring(9));
            } else if (message.startsWith("answer: "))
            {
                handleAnswer(message.substring(8));
            }
            this.s.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void handleAnswer(String answer)
    {
        if (answer.startsWith("list"))
        {
            answer = answer.substring(4);
            String[] args = answer.split(Pattern.quote("|"));
            if (args.length != 2)
            {
                return;
            }
            String player = args[0].trim();
            String answerstring = args[1].trim();
            if (player.equalsIgnoreCase("*console*"))
            {
                Bukkit.getConsoleSender().sendMessage(answerstring.split(Pattern.quote("\n")));
            }
            else {
                Player p = Bukkit.getPlayer(player);
                if (p != null)
                    p.sendMessage(answerstring.split(Pattern.quote("\n")));
            }
        }
        if (answer.startsWith("msg"))
        {
            answer = answer.substring(3);
            String[] args = answer.split(Pattern.quote("|"));
            if (args.length != 2)
            {
                return;
            }
            String player = args[0].trim();
            if (player.length() < 3)
                return;
            player = player.substring(3);
            String answerstring = args[1].trim();
            Player p = Bukkit.getPlayer(player);
            if (p != null)
                p.sendMessage(answerstring.split(Pattern.quote("\n")));
        }
    }

    public void handleCommand(String command)
    {
        if (command.startsWith("list"))
        {
            command = command.substring(4);
            String[] args = command.split(Pattern.quote("|"));
            if (args.length != 2)
            {
                return;
            }
            String player = args[0].trim();
            String portstring = args[1].trim();
            int port = Integer.parseInt(portstring);
            String output = ChatColor.YELLOW + "List of online players on server " + chatflow.serverName + ":";
            for (Player p : Bukkit.getOnlinePlayers())
            {
                output = output + "\n" + p.getDisplayName();
            }
            try
            {
                Socket s = new Socket(this.s.getInetAddress(), port);
                BufferedWriter bos = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
                bos.append("answer: list" + player + "|" + output);
                bos.flush();
                s.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if (command.startsWith("msg"))
        {
            command = command.substring(3);
            String[] args = command.split(Pattern.quote("|"));
            if (args.length != 2)
            {
                return;
            }
            String[] msgstring = args[0].trim().split("Â°");
            String portstring = args[1].trim();
            int port = Integer.parseInt(portstring);
            if (msgstring.length != 2)
            {
                return;
            }
            String playername = msgstring[0].trim();
            String message = msgstring[1].trim();
            Player p = Bukkit.getPlayer(playername);
            //playerName = p;
            if (p != null)
            {
                p.sendMessage(message.split(Pattern.quote("\n")));
                try
                {
                    if ((message.startsWith(ChatColor.YELLOW + "From ")) && (message.contains(":")))
                    {
                        String sender = message.substring(message.indexOf(' '));
                        sender = sender.substring(0, sender.indexOf(':'));
                        Socket s = new Socket(this.s.getInetAddress(), port);
                        BufferedWriter bos = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
                        bos.append("answer: msg" + sender + "|" + "To " + p.getDisplayName() + ": " + message.substring(
                                message.indexOf(":") + 1));
                        bos.flush();
                        s.close();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        if (command.startsWith("playerdisconnect"))
        {
            command = command.substring("playerdisconnect".length());
            String rest = command.substring(0, command.lastIndexOf("|"));
            String portstring = command.substring(command.lastIndexOf("|") + 1);
            String shortservername = rest.substring(0, rest.indexOf("Â°"));
            rest = rest.substring(rest.indexOf("Â°") + 1);
            final String servername = rest.substring(0, rest.indexOf("Â°")).trim();
            rest = rest.substring(rest.indexOf("Â°") + 1);
            String displayname = rest.substring(0, rest.indexOf("Â"));
            final String realname = rest.substring(rest.indexOf("Â°") + 1);
            int port = Integer.parseInt(portstring);
            Bukkit.broadcastMessage(chatflow.cy + chatflow.shortserverName + " " + "Player " + displayname + chatflow.cy + " disconnected".replaceAll("°", ""));
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.p, new Runnable()
            {
                public void run()
                {
                    for (ConnectedServer cs : chatflow.connectedServers)
                    {
                        if (cs.longservername.equalsIgnoreCase(servername))
                        {
                            cs.connectedPlayers.remove(realname);
                            break;
                        }
                    }
                }
            });
        }
        if (command.startsWith("playerconnect"))
        {
            command = command.substring("playerdisconnect".length());
            String rest = command.substring(0, command.lastIndexOf("|"));
            String portstring = command.substring(command.lastIndexOf("|") + 1);
            String shortservername = rest.substring(0, rest.indexOf("Â°"));
            rest = rest.substring(rest.indexOf("Â°") + 1);
            final String servername = rest.substring(0, rest.indexOf("Â°")).trim();
            rest = rest.substring(rest.indexOf("Â°") + 1);
            final String displayname = rest.substring(0, rest.indexOf("Â"));
            final String realname = rest.substring(rest.indexOf("Â°") + 1);
            int port = Integer.parseInt(portstring);
            Bukkit.broadcastMessage(chatflow.cy + chatflow.shortserverName + " " + "Player " + displayname + " connected".replaceAll("°", ""));
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.p, new Runnable()
            {
                public void run()
                {
                    for (ConnectedServer cs : chatflow.connectedServers)
                    {
                        if (cs.longservername.equalsIgnoreCase(servername))
                        {
                            cs.connectedPlayers.put(realname, displayname);
                            break;
                        }

                    }

                }

            });
        }

        if (command.startsWith("playerlist"))
        {
            command = command.substring("playerlist".length());
            String rest = command.substring(0, command.lastIndexOf("|"));
            final String shortservername = rest.substring(0, rest.indexOf("Â°"));
            rest = rest.substring(rest.indexOf("Â°") + 1);
            final String servername = rest.substring(0, rest.indexOf("Â°")).trim();
            rest = rest.substring(rest.indexOf("Â°") + 1);
            String portstring = command.substring(command.lastIndexOf("|") + 1);
            final int port = Integer.parseInt(portstring);

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.p, new Runnable()
            {
                public void run()
                {
                    for (ConnectedServer cs : chatflow.connectedServers)
                    {
                        if (cs.longservername.equalsIgnoreCase(servername))
                        {
                            return;
                        }
                    }
                    chatflow.connectedServers.add(
                            new ConnectedServer(MessageHandler.this.s.getInetAddress().getHostAddress() + ":" + port, shortservername,
                                    servername, MessageHandler.this.p));
                }
            });
        }
    }

    public void handleMessage(final String message)
    {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.p, new Runnable()
        {
            public void run()
            {
                Bukkit.getServer().broadcastMessage(message);
            }
        });
    }
}