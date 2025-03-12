package com.ar.askgaming.betterdragon.Utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import com.ar.askgaming.betterdragon.BetterDragon;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlacerHolderHook extends PlaceholderExpansion {

    private BetterDragon plugin;
    public PlacerHolderHook(BetterDragon main){
        plugin = main;
    }
    @Override
    public String getAuthor() {
        return "AskGaming";
    }
    
    @Override
    public String getIdentifier() {
        return "dragon";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }
    
    @Override
    public String onRequest(OfflinePlayer player, String params) {

        switch (params) {
            case "killer":
                return plugin.getDragon().getKillerName();
    
            case "health":
                return plugin.getDragon().getHealth().toString();
    
            case "name":
                return plugin.getDragon().getName();

            case "next":
                return plugin.getDragonManager().getNext();
            case "kills":
                int number = player.getStatistic(Statistic.KILL_ENTITY,EntityType.ENDER_DRAGON);
                return number + "";
            default:
                break;
        }

        if (params.startsWith("top_")) {

            String pos = params.replace("top_", "");
            int position = Integer.parseInt(pos);
            if (position <= plugin.getLeaderboard().getLines().size()) {
                return plugin.getLeaderboard().getLines().get(position - 1);
            } else return "-";
        }
        
        return "Invalid Placeholder"; // Placeholder is unknown by the Expansion
    }
}
