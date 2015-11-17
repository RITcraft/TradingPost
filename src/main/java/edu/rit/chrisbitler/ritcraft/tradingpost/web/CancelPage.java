package edu.rit.chrisbitler.ritcraft.tradingpost.web;

import edu.rit.chrisbitler.ritcraft.tradingpost.TradingPost;
import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sales;
import org.json.simple.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by cbitler on 11/14/15.
 */
public class CancelPage implements Route {
    public Object handle(Request request, Response response) throws Exception {
        if(request.session().attribute("loggedIn") != null) {
            String uuid = request.session().attribute("uuid");
            int id = Integer.parseInt(request.queryParams("id"));
            Statement stmt = TradingPost.instance.getMYSQL().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM `listings` WHERE `id`="+id+" AND `owner`='"+uuid+"'");
            if(rs.next()) {
                stmt.execute("INSERT INTO `claims` (`owner`,`item`) VALUES ('" + rs.getString("owner") + "','" + rs.getString("item") + "')");
                stmt.execute("DELETE FROM `listings` WHERE `owner`='" + uuid + "' AND `id`="+id);
                Sales.remove(id);
                rs.close();
                stmt.close();
                return alertJson("Listing deleted. You can claim your item back ingame now.");
            }else{
                return errorJson("No such item exists");
            }
        }else{
            return errorJson("You need to be logged in to perform this action");
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
