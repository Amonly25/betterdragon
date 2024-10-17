package com.ar.askgaming.betterdragon;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Statistic;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class DataHandler {

    private FileConfiguration dragonDataConfig;
    private FileConfiguration playersDataConfig;

    public void saveDragonDataConfig() {
        try {
	    	getDragonDataConfig().save(dragonDataFile);
	    } catch (IOException ev) {
	        ev.printStackTrace();
	        			
		}
    }
    public void savePlayersDataConfig() {
        try {
	    	getPlayersDataConfig().save(playersDataFile);
	    } catch (IOException ev) {
	        ev.printStackTrace();      			
		}
    }
    public FileConfiguration getDragonDataConfig() {
        return dragonDataConfig;
    }
    public FileConfiguration getPlayersDataConfig() {
        return playersDataConfig;
    }
    private File dragonDataFile;
    private File playersDataFile;

    private Main plugin;
    public DataHandler(Main main) {
        plugin = main;

        //Creating files

        dragonDataFile = new File(plugin.getDataFolder(),"dragon_data.yml");
        playersDataFile = new File(plugin.getDataFolder(),"players_data.yml");

        if (!dragonDataFile.exists()) {
            plugin.saveResource("dragon_data.yml", false);
        }

        if (!playersDataFile.exists()) {
            plugin.saveResource("players_data.yml", false);
        }

        dragonDataConfig = new YamlConfiguration();
        playersDataConfig = new YamlConfiguration();

        // Handle loading Configuration

        try {
            dragonDataConfig.load(dragonDataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        
        try {
            playersDataConfig.load(playersDataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }   

    public void updateDragonKills(Player p) {

        int kills = p.getStatistic(Statistic.KILL_ENTITY,EntityType.ENDER_DRAGON);
        getPlayersDataConfig().set(p.getName(), kills);
        savePlayersDataConfig();
    }

    public Map<String, Integer> getKillsLeaderboard() {
    
        Map<String, Integer> kills = new HashMap<>();
                    
        for (String player : getPlayersDataConfig().getKeys(true)) {      	
            kills.put(player, getPlayersDataConfig().getInt(player));             
        }

        if (!kills.isEmpty()) {
            return kills.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (i, i2) -> i, LinkedHashMap::new));
        } else return null;
    }
    
}
