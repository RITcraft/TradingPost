package edu.rit.chrisbitler.ritcraft.tradingpost.cmd;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Chris on 11/1/2015.
 */
public class TradePost implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player) {
            if(args.length > 0) {
                switch(args[0]) {
                    case "register":
                        if(Market)
                }
            }else{
                commandSender.sendMessage(ChatColor.DARK_RED + "Incorrect command syntax. /tradepost [register/add/claim]");
            }
        }
    }
}
