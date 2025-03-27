package com.ar.askgaming.betterdragon.Handlers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.ar.askgaming.betterdragon.BetterDragon;

public class DataHandler {

    private final File playersDataFile;
    private FileConfiguration playersDataConfig;

    private final BetterDragon plugin;
    public DataHandler(BetterDragon main) {
        plugin = main;

        playersDataFile = new File(plugin.getDataFolder(),"players_data.yml");

        load();

    } 
    public void load(){
        if (!playersDataFile.exists()) {
            plugin.saveResource("players_data.yml", false);
        }
        playersDataConfig = YamlConfiguration.loadConfiguration(playersDataFile);
    }

    public void savePlayersDataConfig() {
        try {
	    	playersDataConfig.save(playersDataFile);
	    } catch (IOException ev) {
	        ev.printStackTrace();      			
		}
    }


    public void updateDragonKills(Player p) {

        int kills = p.getStatistic(Statistic.KILL_ENTITY,EntityType.ENDER_DRAGON);
        playersDataConfig.set(p.getName(), kills);
        savePlayersDataConfig();
    }

    public HashMap<String, Integer> getKills() {
    
        HashMap<String, Integer> kills = new HashMap<>();
                    
        for (String key : playersDataConfig.getKeys(true)) {      	
            kills.put(key, playersDataConfig.getInt(key));             
        }

        return kills;
    }
}
