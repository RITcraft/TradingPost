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
        viewObjects.put("loggedIn", request.session().attribute("loggedIn") != null);
        if (request.queryParams("e") != null) {
            if (request.queryParams("e").equalsIgnoreCase("dne")) {
                viewObjects.put("ojs", "<script>modal('Error','No such item exists.');</script>");
            }
        }
        if (request.session().attribute("loggedIn") != null) {
            OfflinePlayer oplayer = Bukkit.getOfflinePlayer(UUID.fromString(request.session().attribute("uuid").toString()));
            viewObjects.put("money", TradingPost.instance.economy.getBalance(oplayer));
        }
        if (request.queryParams("srch-term") != null) {
            String term = request.queryParams("srch-term");
            term = term.replace(" ", "_");
            if (TradingPost.instance.getEssentials() != null) {
                try {
                    ItemStack termItem = TradingPost.instance.getEssentials().getItemDb().get(term);
                    if (termItem != null) {
                        term = termItem.getType().name();
                    }
                } catch (Exception e) {
                    response.redirect("/?e=dne");
                    return null;
                }
            }
            response.redirect("/view/" + term);
        }
        List<Sale> copy = (List<Sale>) Sales.sales.clone();
        Collections.reverse(copy);
        List<Sale> show = new ArrayList<Sale>();
        for(Sale sale : copy) {
            show.add(sale);
            if(show.size() == 20)
                break;
        }
        viewObjects.put("listings", show);

        if (request.session().attribute("loggedIn") != null) {
            Statement stmt = TradingPost.instance.getMYSQL().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT *  FROM `listings` WHERE `owner`='" + request.session().attribute("uuid") + "'");
            List<Sale> yourListings = new ArrayList<Sale>();
            while (rs.next()) {
                yourListings.add(new Sale(rs.getString("owner"), rs.getInt("price"), rs.getInt("id"), rs.getString("item")));
            }
            viewObjects.put("yourListings", yourListings);
        }
        if (request.queryParams("error") != null) {
            String error = request.queryParams("error");
            error = StringEscapeUtils.escapeHtml(error);
            viewObjects.put("error", error);
        }

        return new ModelAndView(viewObjects, "index.ftl");
    }

}
