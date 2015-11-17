package edu.rit.chrisbitler.ritcraft.tradingpost.web;

import edu.rit.chrisbitler.ritcraft.tradingpost.TradingPost;
import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sale;
import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sales;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import java.util.*;

/**
 * Created by cbitler on 11/10/15.
 */
public class ViewPage implements TemplateViewRoute {
    public ModelAndView handle(Request request, Response response) throws Exception {
        Map<String, Object> viewModel = new HashMap<String, Object>();
        String material = request.params("id");
        List<Sale> applicable = Sales.get(material.toUpperCase().replace(" ", "_"),40);
        Collections.reverse(applicable);
        Collections.sort(applicable,new SaleComparator<Sale>());
        viewModel.put("listings", applicable);
        viewModel.put("loggedIn", request.session().attribute("loggedIn") != null);
        viewModel.put("material", getName(material));
        if (request.session().attribute("loggedIn") != null) {
            OfflinePlayer oplayer = Bukkit.getOfflinePlayer(UUID.fromString((String) request.session().attribute("uuid")));
            viewModel.put("money", TradingPost.instance.economy.getBalance(oplayer));
        }
        return new ModelAndView(viewModel, "view.ftl");
    }

    public String getName(String material) {
        String name = material.toLowerCase();
        name = name.replaceAll("_", " ");
        String first = name.substring(0, 1);
        name = name.substring(1);
        name = first.toUpperCase() + name;
        return name;
    }

    private class SaleComparator<T> implements Comparator<Sale> {

        public int compare(Sale o1, Sale o2) {
            if(o1.getPrice() < o2.getPrice()) {
                return -1;
            }else if(o1.getPrice() == o2.getPrice()) {
                return 0;
            }else if(o1.getPrice() > o2.getPrice()) {
                return 1;
            }
            return 0;
        }
    }
}
