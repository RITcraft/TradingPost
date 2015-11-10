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

    public boolean onCommand(final CommandSender commandSender, Command command, final String s, final String[] args) {
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
                                            stmnt.execute("INSERT INTO `listings` (`owner`,`price`,`item`) VALUES ('" + owner + "','" + price + "','" + json + "')");
                                            if (inHand.getAmount() > amount) {
                                                inHand.setAmount(inHand.getAmount() - amount);
                                            } else {
                                                ((Player) commandSender).setItemInHand(new ItemStack(Material.AIR));
                                            }
                                            Sale sale = new Sale(owner, price, toAdd);
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
                }
            }else{
                commandSender.sendMessage(ChatColor.DARK_RED + "Incorrect command syntax. /tradepost [register/add/claim]");
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
}
