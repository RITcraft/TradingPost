package edu.rit.chrisbitler.ritcraft.tradingpost.data;

import json.JsonConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by cbitler on 11/3/15.
 */
public class Sale {
    private ItemStack item;
    private String itemJSON;
    private String owner;
    private int price;
    int id;

    public Sale(String owner, int price, int id, String itemJson) {
        this.owner = owner;
        this.price = price;
        this.id = id;
        this.itemJSON = itemJson;
        JsonConfiguration config = new JsonConfiguration();
        try {
            config.loadFromString(itemJson);
            this.item = config.getItemStack("item");
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Sale(String owner, int price, ItemStack item, int id) {
        this.owner = owner;
        this.price = price;
        this.item = item;
        JsonConfiguration config = new JsonConfiguration();
        config.set("item", item);
        itemJSON = config.saveToString();
        this.id = id;
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
            try {
                Class clazz = Class.forName("org.bukkit.craftbukkit."+getNMSVersion()+".inventory.CraftItemStack");
                Method method = clazz.getDeclaredMethod("asNMSCopy", ItemStack.class);
                method.setAccessible(true);
                Object nmsItem = method.invoke(null, item);
                Method n = nmsItem.getClass().getDeclaredMethod("getName");
                return (String) n.invoke(nmsItem);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getNMSVersion() {
        return Bukkit.getServer().getClass().getName().split("\\.")[3];
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

    public String getItemJSON() {
        return itemJSON;
    }

    public void setItemJSON(String itemJSON) {
        this.itemJSON = itemJSON;
    }
}
