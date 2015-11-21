package edu.rit.chrisbitler.ritcraft.tradingpost.web;

import edu.rit.chrisbitler.ritcraft.tradingpost.TradingPost;
import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sale;
import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sales;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Statement;
import java.util.UUID;

public class BuyPage implements Route {
    public Object handle(Request request, Response response) throws Exception {
        if (request.queryParams("id") != null) {
            if (request.session().attribute("loggedIn") != null) {
                UUID uuid = UUID.fromString((String) request.session().attribute("uuid"));
                OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                if (p != null) {
                    Sale sale = Sales.getById(Integer.parseInt(request.queryParams("id")));
                    if (sale != null) {
                        int money = (int) TradingPost.instance.economy.getBalance(p);
                        if (money >= sale.getPrice()) {
                            UUID oUUID = UUID.fromString(sale.getOwner());
                            if(!oUUID.toString().equalsIgnoreCase(uuid.toString())) {
                                OfflinePlayer other = Bukkit.getOfflinePlayer(oUUID);
                                if (other != null) {
                                    int id = Integer.parseInt(request.queryParams("id"));
                                    TradingPost.instance.economy.withdrawPlayer(p, sale.getPrice());
                                    TradingPost.instance.economy.depositPlayer(other, sale.getPrice());
                                    Sales.remove(id);

                                    Statement stmt = TradingPost.instance.getMYSQL().createStatement();
                                    stmt.execute("DELETE FROM `listings` WHERE `id`=" + id);
                                    stmt.execute("INSERT INTO `claims` (`owner`,`item`) VALUES ('" + p.getUniqueId().toString() + "','" + sale.getItemJSON() + "')");

                                    if (other.isOnline()) {
                                        Bukkit.getPlayer(other.getUniqueId()).sendMessage(ChatColor.GOLD + p.getName() + " bought your " + sale.getName() + " x" + sale.getItem().getAmount() + " for $" + sale.getPrice());
                                    } else {
                                        stmt.execute("INSERT INTO `alerts` (`owner`,`type`,`text`) VALUES ('" + other.getUniqueId().toString() + "','buy','" + p.getName() + " bought your " + sale.getName() + " x" + sale.getItem().getAmount() + " for $" + sale.getPrice() + "')");
                                    }
                                    if (p.isOnline()) {
                                        Bukkit.getPlayer(p.getUniqueId()).sendMessage(ChatColor.GOLD + "You have some items to claim from the trading post!");
                                    }

                                    stmt.close();
                                    return alertJson("You have bought this item. You can now claim it ingame via /tradepost claim");
                                } else {
                                    return errorJson("The owner of this offer doesn't exist.");
                                }
                            }else{
                                return errorJson("You cannot buy your own items.");
                            }
                        } else {
                            return errorJson("You cannot afford this listing.");
                        }
                    } else {
                        return errorJson("Non-existant listing");
                    }
                } else {
                    return errorJson("Authenthication error");
                }
            } else {
                return errorJson("You must be logged in to access this page.");
            }
        } else {
            return errorJson("Invalid/missing ID");
        }
    }

    public String errorJson(String error) {
        JSONObject obj = new JSONObject();
        obj.put("type", "Error");
        obj.put("text", error);
        return obj.toJSONString();
    }

    public String alertJson(String text) {
        JSONObject obj = new JSONObject();
        obj.put("type", "Success");
        obj.put("text", text);
        return obj.toJSONString();
    }
}
