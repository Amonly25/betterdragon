package com.ar.askgaming.betterdragon.Utils;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.ar.askgaming.betterdragon.BetterDragon;

//change to bukkitrunnable on a manager
public class RespawnTask implements Runnable {

    private BetterDragon plugin;
    public RespawnTask(BetterDragon main) {
        this.plugin = main;
    }

    @Override
    public void run() {

        if (!plugin.getConfig().getBoolean("respawn.enable", true)){ 
            return;
        }
                
        long actualTime = System.currentTimeMillis() / 60000;
		long deathTime = plugin.getDragon().getDeathTime();

        long respawnTime = plugin.getDragon().getRespawnTime();

        if ((actualTime - deathTime) >= respawnTime) {

            Location l = plugin.getDragon().getRespawnLocacion();

            if (l == null){
                plugin.getLogger().log(Level.WARNING, "The respawn location is not set, no dragon will be respawned.");
                return;
            }

            if (!l.getChunk().isLoaded()){
                plugin.getLogger().log(Level.WARNING, "The chunk of the respawn location is not loaded, no dragon will be respawned.");
                return;
            }
            String mode = plugin.getConfig().getString("respawn.mode", "spawn");
            if (mode.equalsIgnoreCase("default")){
                plugin.getDragonManager().newDragonBattle(); 
            } else {
                l.getWorld().spawnEntity(l, EntityType.ENDER_DRAGON);
                Bukkit.broadcastMessage(plugin.getLang("messages.respawn"));  
            }       
        }
    }
}
