package com.ar.askgaming.betterdragon;

import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.List;

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
                return plugin.getDragon().getNext();

            default:
                break;
        }

        if (params.startsWith("top_")) {

        	Set<Entry<String, Integer>> set = plugin.getDataHandler().getKillsLeaderboard().entrySet();
	       List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);

            String prefix = plugin.getLang("leaderboard.prefix");
            String separator = plugin.getLang("leaderboard.separator");
            String suffix = plugin.getLang("leaderboard.suffix");

            String pos = params.replace("top_", "");
            int position = Integer.parseInt(pos);
            if (position <= plugin.getDataHandler().getKillsLeaderboard().size()){
                return prefix + list.get(position-1).toString().replace("=", separator) + suffix;
            } else return "-";

        }
        
        return "Invalid Placeholder"; // Placeholder is unknown by the Expansion
    }
}
