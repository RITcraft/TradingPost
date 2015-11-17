package edu.rit.chrisbitler.ritcraft.tradingpost.web;

import edu.rit.chrisbitler.ritcraft.tradingpost.TradingPost;
import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sale;
import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sales;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import spark.*;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * Created by Chris on 11/2/2015.
 */
public class IndexPage implements TemplateViewRoute {
    public ModelAndView handle(Request request, Response response) throws Exception {
        Map<String, Object> viewObjects = new HashMap<String, Object>();
        //if(request.session().attribute("loggedIn") != null) {
        viewObjects.put("loggedIn", request.session().attribute("loggedIn") != null);
        //}
        String player = request.params().get("player");
        OfflinePlayer oplayer = Bukkit.getOfflinePlayer("VoidWhisperer");
        System.out.println(oplayer.getName());
        viewObjects.put("money", TradingPost.instance.economy.getBalance(oplayer));
        List<Sale> copy = (List<Sale>) Sales.sales.clone();
        Collections.reverse(copy);
        List<Sale> show = new ArrayList<Sale>();
        for(Sale sale : copy) {
            show.add(sale);
            if(show.size() == 20)
                break;
        }
        viewObjects.put("listings", show);

        if (request.queryParams("error") != null) {
            String error = request.queryParams("error");
            error = StringEscapeUtils.escapeHtml(error);
            viewObjects.put("error", error);
        }

        return new ModelAndView(viewObjects, "index.ftl");
    }

}
