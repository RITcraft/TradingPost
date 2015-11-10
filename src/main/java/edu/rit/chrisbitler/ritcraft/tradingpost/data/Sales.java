package edu.rit.chrisbitler.ritcraft.tradingpost.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbitler on 11/6/15.
 */
public class Sales {
    public static ArrayList<Sale> sales = new ArrayList<Sale>();

    public static List<Sale> get(String material) {
        List<Sale> listings = new ArrayList<Sale>();
        for (Sale sale : sales) {
            if (sale.getItem().getType().name().equals(material)) {
                listings.add(sale);
            }
        }
        return listings;
    }

    public static Sale getById(int id) {
        for (Sale sale : sales) {
            if (sale.getId() == id) {
                return sale;
            }
        }
        return null;
    }
}
