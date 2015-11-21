package edu.rit.chrisbitler.ritcraft.tradingpost;

import com.earth2me.essentials.Essentials;
import edu.rit.chrisbitler.ritcraft.tradingpost.cmd.TradePost;
import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sale;
import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sales;
import edu.rit.chrisbitler.ritcraft.tradingpost.web.Index;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.SubstituteLoggerFactory;
import org.slf4j.impl.SimpleLoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.*;
import java.util.Enumeration;
import java.util.logging.LogManager;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Chris on 11/1/2015.
 */
public class TradingPost extends JavaPlugin implements Listener {
    private Connection conn;
    private FileConfiguration config;
    public static TradingPost instance;
    private Essentials essentials;
    public Economy economy;
    public void onEnable() {
        instance = this;

        //Load configuration
        config = getConfig();
        String mysqlUsername,mysqlPassword,mysqlHost,mysqlDb;
        String ip;
        int port;
        System.out.println("Loading configuration..");
        if(getConfig().getString("mysql_username") != null && !getConfig().getString("mysql_username").equalsIgnoreCase("username")) {
            mysqlUsername = getConfig().getString("mysql_username");
            mysqlPassword = getConfig().getString("mysql_password");
            mysqlHost = getConfig().getString("mysql_host");
            mysqlDb = getConfig().getString("mysql_db");
            ip = getConfig().getString("website_ip");
            port = Integer.parseInt(getConfig().getString("website_port"));
        }else{
            if(getConfig().getString("mysql_username") == null) {
                getConfig().addDefault("mysql_username", "username");
                getConfig().addDefault("mysql_password", "password");
                getConfig().addDefault("mysql_host", "host");
                getConfig().addDefault("mysql_db", "tp_db");
                getConfig().addDefault("website_ip","0.0.0.0");
                getConfig().addDefault("website_port","33333");
                getConfig().options().copyDefaults(true);
                saveConfig();
            }
            System.out.println("Please configure the mysql and other information for this plugin before using it.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //Attempt to connect to mysql
        try {
            conn = DriverManager.getConnection("jdbc:mysql://"+mysqlHost+"/"+mysqlDb+"?user="+mysqlUsername+"&password="+mysqlPassword);
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS `alerts` (`owner` varchar(45) NOT NULL,`id` int(11) NOT NULL AUTO_INCREMENT,`type` varchar(45) NOT NULL,`text` varchar(100) NOT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1");
            stmt.execute("CREATE TABLE IF NOT EXISTS `claims` (`owner` varchar(45) NOT NULL,`item` text NOT NULL,`id` int(11) NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1");
            stmt.execute("CREATE TABLE IF NOT EXISTS `listings` (`id` int(11) NOT NULL AUTO_INCREMENT,`owner` varchar(45) NOT NULL,`price` int(11) DEFAULT NULL,`item` text NOT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1");
            stmt.execute("CREATE TABLE IF NOT EXISTS `users` (`uuid` varchar(200) NOT NULL,`password` varchar(200) NOT NULL,`id` int(11) NOT NULL AUTO_INCREMENT,`salt` text, PRIMARY KEY (`id`), UNIQUE KEY `unique_id` (`id`)) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1");
        } catch (SQLException e) {
            System.out.println("Error connecting to mysql! Check your connection info - " + e.getMessage());
        }

        //Load sales data
        System.out.println("Loading offers..");
        try {
            Statement stmt = getMYSQL().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM `listings`");
            while (rs.next()) {
                Sale sale = new Sale(rs.getString("owner"), rs.getInt("price"), rs.getInt("id"), rs.getString("item"));
                Sales.sales.add(sale);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Look for essentials
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
            essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        }
        System.out.println("------ The text related to 'Thread-8' is the embedded webserver starting. ------");
        //Attempt to start the spark site
        Index.register(ip,port);
        //Register commands
        getCommand("tradepost").setExecutor(new TradePost());

        setupEconomy();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public Connection getMYSQL() {
        return conn;
    }

    public Essentials getEssentials() {
        return essentials;
    }
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

            public void run() {
                try {
                    Statement stmt = getMYSQL().createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `claims` WHERE `owner`='" + e.getPlayer().getUniqueId().toString() + "'");
                    rs.first();
                    if(rs.getInt("COUNT(*)") > 0) {
                        e.getPlayer().sendMessage(ChatColor.GOLD + "You have some items to claim from the trading post!");
                    }
                    rs.close();

                    Statement delete = getMYSQL().createStatement();
                    ResultSet rs2 = stmt.executeQuery("SELECT * FROM `alerts` WHERE `owner`='" + e.getPlayer().getUniqueId().toString() + "'");
                    while(rs2.next()) {
                        if(rs2.getString("type").equals("buy")) {
                            e.getPlayer().sendMessage(ChatColor.GOLD + rs2.getString("text"));
                            delete.execute("DELETE FROM `alerts` WHERE `id`=" + rs2.getInt("id"));
                        }
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }


}
