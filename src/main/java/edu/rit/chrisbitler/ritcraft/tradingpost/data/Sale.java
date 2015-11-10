package edu.rit.chrisbitler.ritcraft.tradingpost.data;

import com.dumptruckman.bukkit.configuration.json.JsonConfiguration;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by cbitler on 11/3/15.
 */
public class Sale {
    private ItemStack item;
    private String owner;
    private int price;
    int id;

    public Sale(String owner, int price, int id, String itemJson) {
        this.owner = owner;
        this.price = price;
        this.id = id;
        JsonConfiguration config = new JsonConfiguration();
        try {
            config.loadFromString(itemJson);
            this.item = config.getItemStack("item");
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Sale(String owner, int price, ItemStack item) {
        this.owner = owner;
        this.price = price;
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        if (item.getItemMeta().getDisplayName() != null) {
            return "\"" + item.getItemMeta().getDisplayName() + "\"";
        } else {
            String name = item.getType().name().toLowerCase();
            name = name.replaceAll("_", " ");
            String first = name.substring(0, 1);
            name = name.substring(1);
            name = first.toUpperCase() + name;
            return name;
        }
    }

    public boolean isEnchanted() {
        return !item.getEnchantments().isEmpty();
    }

    public int getDataValue() {
        if (item.getType().getMaxDurability() > 1) {
            return 0;
        } else {
            return item.getDurability();
        }
    }

    public int getId() {
        return id;
    }
}
