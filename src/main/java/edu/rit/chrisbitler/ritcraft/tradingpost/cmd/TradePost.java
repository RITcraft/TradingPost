package edu.rit.chrisbitler.ritcraft.tradingpost.cmd;

import com.dumptruckman.bukkit.configuration.json.JsonConfiguration;
import edu.rit.chrisbitler.ritcraft.tradingpost.TradingPost;
import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sale;
import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sales;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Random;

/**
 * Created by Chris on 11/1/2015.
 */
public class TradePost implements CommandExecutor {

    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
        if(commandSender instanceof Player) {
            if(args.length > 0) {
                if (args[0].equals("register")) {
                    final Player p = (Player) commandSender;
                    if (args.length == 2) {
                        p.sendMessage(ChatColor.YELLOW + "Connecting to user service..");
                        Bukkit.getScheduler().runTaskAsynchronously(TradingPost.instance, new Runnable() {

                            public void run() {
                                try {
                                    Statement st = TradingPost.instance.getMYSQL().createStatement();
                                    ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM `users` WHERE `uuid`='" + p.getUniqueId().toString() + "'");
                                    rs.first();
                                    if (rs.getInt("COUNT(*)") < 1) {

                                        String password = args[1];
                                        SecureRandom sr = new SecureRandom();
                                        byte[] salt = new byte[256];
                                        sr.nextBytes(salt);
                                        String saltString = base64Encode(salt);
                                        String hash = hash(password, salt);
                                        st.execute("INSERT INTO `users` (`uuid`,`password`,`salt`) VALUES ('" + p.getUniqueId().toString() + "','" + hash + "','" + saltString + "')");
                                        p.sendMessage(ChatColor.GREEN + "You have registered and should be able to use the trading post with your username and the password you selected.");
                                    } else {
                                        p.sendMessage(ChatColor.RED + "You already have an account registered!");
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    p.sendMessage(ChatColor.RED + "A database error has occured! Please try again later.");
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        commandSender.sendMessage(ChatColor.DARK_RED + "Incorrect command syntax - try /tradepost register [password]");
                    }
                } else if (args[0].equals("add")) {
                    if (args.length == 3) {
                        final int amount = Integer.parseInt(args[1]);
                        final int price = Integer.parseInt(args[2]);
                        final ItemStack inHand = ((Player) commandSender).getItemInHand();
                        if (inHand.getAmount() >= amount && amount > 0) {
                            if (price > -1) {
                                Bukkit.getScheduler().runTaskAsynchronously(TradingPost.instance, new Runnable() {

                                    public void run() {
                                        try {
                                            ItemStack toAdd = inHand.clone();
                                            toAdd.setAmount(amount);
                                            Statement stmnt = TradingPost.instance.getMYSQL().createStatement();
                                            JsonConfiguration config = new JsonConfiguration();
                                            config.set("item", toAdd);
                                            String json = config.saveToString();
                                            String owner = ((Player) commandSender).getUniqueId().toString();
                                            int id = stmnt.executeUpdate("INSERT INTO `listings` (`owner`,`price`,`item`) VALUES ('" + owner + "','" + price + "','" + json + "')",Statement.RETURN_GENERATED_KEYS);
                                            if (inHand.getAmount() > amount) {
                                                inHand.setAmount(inHand.getAmount() - amount);
                                            } else {
                                                ((Player) commandSender).setItemInHand(new ItemStack(Material.AIR));
                                            }
                                            Sale sale = new Sale(owner, price, toAdd, id);
                                            Sales.sales.add(sale);
                                            commandSender.sendMessage(ChatColor.GREEN + "Listing added to the trading post!");
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            } else {
                                commandSender.sendMessage(ChatColor.DARK_RED + "You cannot use a negative price.");
                            }
                        } else {
                            commandSender.sendMessage(ChatColor.DARK_RED + "You cannot add more of this item than you have.");
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.DARK_RED + "Incorrect command snytax. Try /tradepost add [amount] [price]");
                    }
                }else if(args[0].equalsIgnoreCase("help")){
                    commandSender.sendMessage(ChatColor.GOLD + "TradingPost Help");
                    commandSender.sendMessage(ChatColor.GOLD + "-----------------");
                    commandSender.sendMessage(ChatColor.YELLOW + "/tradepost register [password] " + ChatColor.WHITE + " Register for the trading post with the specified password.");
                    commandSender.sendMessage(ChatColor.YELLOW + "/tradepost add [amount] [price] " + ChatColor.WHITE + " Adds a new offer to the trading post of the item you are holding with the specified amount and price.");
                    commandSender.sendMessage(ChatColor.YELLOW + "/tradepost claim " + ChatColor.WHITE + " Claim your bought/canceled items from the trading post.");
                    commandSender.sendMessage(ChatColor.YELLOW + "/tradepost help " + ChatColor.WHITE + " You are reading it.");
                }else if(args[0].equalsIgnoreCase("claim")) {
                    commandSender.sendMessage(ChatColor.YELLOW + "Contacting claims service...");
                    Bukkit.getScheduler().runTaskAsynchronously(TradingPost.instance, new Runnable() {
                        public void run() {
                            try {
                                Player p = (Player) commandSender;
                                Statement stmt = TradingPost.instance.getMYSQL().createStatement();
                                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `claims` WHERE `owner`='"+p.getUniqueId().toString()+"'");
                                rs.first();
                                int items = rs.getInt("COUNT(*)");
                                if(items > 0) {
                                    p.sendMessage(ChatColor.GREEN + "You have " + items + " items to claim.");
                                    if(p.getInventory().firstEmpty() > 0) {
                                        int given = 0;
                                        ResultSet rs2 = stmt.executeQuery("SELECT * FROM `claims` WHERE `owner`='" + p.getUniqueId().toString() + "'");
                                        while(p.getInventory().firstEmpty() > 0 && rs2.next()) {
                                            JsonConfiguration config = new JsonConfiguration();
                                            config.loadFromString(rs2.getString("item"));
                                            ItemStack item = config.getItemStack("item");
                                            p.getInventory().addItem(item);
                                            p.sendMessage(ChatColor.GREEN + "Claimed item: " + getName(item) + " x" + item.getAmount());
                                            given+=1;
                                            Statement stmt2 = TradingPost.instance.getMYSQL().createStatement();
                                            stmt2.execute("DELETE FROM `claims` WHERE `owner`='" + p.getUniqueId().toString() + "' AND `id`='" + rs2.getInt("id") + "'");
                                            stmt2.close();
                                        }
                                        if(given == items) {
                                            p.sendMessage(ChatColor.GREEN + "You have claimed all of your items!");
                                        }else{
                                            p.sendMessage(ChatColor.YELLOW + "You have claimed " + given + " out of " + items + " items. Clear more inventory space to claim the rest.");
                                        }
                                        rs2.close();
                                        rs.close();
                                        stmt.close();
                                    }else{
                                        p.sendMessage(ChatColor.RED + "Please open up some inventory space before you claim your items.");
                                    }
                                }else{
                                    p.sendMessage(ChatColor.RED + "You have no items to claim.");
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } catch (InvalidConfigurationException e) {
                                e.printStackTrace();
                            }
                        }
                    });
		        }
            }else{
                commandSender.sendMessage(ChatColor.DARK_RED + "Incorrect command syntax. /tradepost [register/add/claim/help]");
            }
        }
        return true;
    }

    private String hash(String password, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(salt);
        byte[] input = digest.digest(password.getBytes("UTF-8"));
        for (int i = 0; i < 1000; i++) {
            digest.reset();
            input = digest.digest(input);
        }
        return base64Encode(input);
    }

    private String base64Encode(byte[] data) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    public String getName(ItemStack item) {
            if (item.getItemMeta().getDisplayName() != null) {
                String name = item.getType().name().toLowerCase();
                name = name.replaceAll("_", " ");
                String first = name.substring(0, 1);
                name = name.substring(1);
                name = first.toUpperCase() + name;
                return "\"" + item.getItemMeta().getDisplayName() + "\" (" + name + ")";
            } else {
                String name = item.getType().name().toLowerCase();
                name = name.replaceAll("_", " ");
                String first = name.substring(0, 1);
                name = name.substring(1);
                name = first.toUpperCase() + name;
                return name;
            }
    }
}
