package edu.rit.chrisbitler.ritcraft.tradingpost.web;

import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sale;
import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sales;
import net.minidev.json.JSONArray;
import org.bukkit.enchantments.Enchantment;
import org.json.simple.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

/**
 * Created by cbitler on 11/11/15.
 */
public class DataPage implements Route {
    public Object handle(Request request, Response response) throws Exception {
        if (request.queryParams("id") != null) {
            int id = Integer.parseInt(request.queryParams("id"));
            Sale sale = Sales.getById(id);
            if (sale != null) {
                JSONArray total = new JSONArray();
                JSONObject object = new JSONObject();
                object.put("itemName", sale.getName());
                object.put("imgsrc", "../../" + sale.getItem().getTypeId() + "-" + sale.getDataValue() + ".png");
                object.put("sid", id);
                object.put("amount", sale.getItem().getAmount());
                object.put("price", sale.getPrice());
                if(request.session().attribute("loggedIn") != null) {
                    object.put("isOwner", sale.getOwner().equals(request.session().attribute("uuid")));
                }else{
                    object.put("isOwner", false);
                }
                object.put("enchanted", sale.getItem().getEnchantments().size() != 0);
                if (sale.getItem().getEnchantments().size() != 0) {
                    JSONArray array = new JSONArray();
                    for (Map.Entry<Enchantment, Integer> enchant : sale.getItem().getEnchantments().entrySet()) {
                        JSONObject obj = new JSONObject();
                        obj.put("eName", enchant.getKey().getName());
                        obj.put("level", enchant.getValue());
                        array.add(obj);
                    }
                    object.put("enchants", array);
                }
                return object.toJSONString();
            } else {
                return "Sale doesn't exist";
            }
        } else {
            return "Error!";
        }
    }
}
