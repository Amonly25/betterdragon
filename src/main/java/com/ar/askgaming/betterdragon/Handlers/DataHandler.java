package com.ar.askgaming.betterdragon.Handlers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Statistic;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.ar.askgaming.betterdragon.BetterDragon;

public class DataHandler {

    private FileConfiguration playersDataConfig;

    public void savePlayersDataConfig() {
        try {
	    	getPlayersDataConfig().save(playersDataFile);
	    } catch (IOException ev) {
	        ev.printStackTrace();      			
		}
    }

    public FileConfiguration getPlayersDataConfig() {
        return playersDataConfig;
    }

    private File playersDataFile;

    private BetterDragon plugin;
    public DataHandler(BetterDragon main) {
        plugin = main;

        //Creating files
        playersDataFile = new File(plugin.getDataFolder(),"players_data.yml");

        if (!playersDataFile.exists()) {
            plugin.saveResource("players_data.yml", false);
        }
        playersDataConfig = new YamlConfiguration();

        // Handle loading Configuration
        
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

    public HashMap<String, Integer> getKills() {
    
        HashMap<String, Integer> kills = new HashMap<>();
                    
        for (String player : getPlayersDataConfig().getKeys(true)) {      	
            kills.put(player, getPlayersDataConfig().getInt(player));             
        }

        return kills;
    }
}
