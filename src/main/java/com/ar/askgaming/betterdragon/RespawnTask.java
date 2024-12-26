package com.ar.askgaming.betterdragon;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

//change to bukkitrunnable on a manager
public class RespawnTask implements Runnable {

    private BetterDragon plugin;
    public RespawnTask(BetterDragon main) {
        this.plugin = main;
    }

    @Override
    public void run() {
        
        if (!plugin.getDragon().getDragonsAlive().isEmpty() ){
            return;
        }

        if (plugin.getDragon().getRespawnLocacion() == null){
            return;
        }
        if (!plugin.getConfig().getBoolean("options.automatic_respawn")) {
            return;
        }
                
        long actualTime = System.currentTimeMillis() / 60000;
		long deathTime = plugin.getDragon().getDeathTime();

        long respawnTime = plugin.getDragon().getRespawnTime();

        if ((actualTime - deathTime) >= respawnTime) {

            if (!plugin.getDragon().getRespawnLocacion().getChunk().isLoaded()){
                plugin.getLogger().log(Level.WARNING, "The chunk of the respawn location is not loaded, no dragon will be respawned.");
                return;
            }

            Location l = plugin.getDragon().getRespawnLocacion();

            l.getWorld().spawnEntity(l, EntityType.ENDER_DRAGON);
            Bukkit.broadcastMessage(plugin.getLang("messages.respawn"));           
        }
    }
}
