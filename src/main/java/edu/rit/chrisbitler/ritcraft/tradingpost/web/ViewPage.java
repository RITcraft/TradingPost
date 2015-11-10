package edu.rit.chrisbitler.ritcraft.tradingpost.web;

import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sale;
import edu.rit.chrisbitler.ritcraft.tradingpost.data.Sales;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cbitler on 11/10/15.
 */
public class ViewPage implements TemplateViewRoute {
    public ModelAndView handle(Request request, Response response) throws Exception {
        Map<String, Object> viewModel = new HashMap<String, Object>();
        String material = request.params("id");
        List<Sale> applicable = Sales.get(material.toUpperCase().replace(" ", "_"));
        Collections.reverse(applicable);
        viewModel.put("listings", applicable);
        viewModel.put("material", getName(material));
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
}
