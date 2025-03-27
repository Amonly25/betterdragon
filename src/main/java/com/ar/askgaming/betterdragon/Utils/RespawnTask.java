package com.ar.askgaming.betterdragon.Utils;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.betterdragon.BetterDragon;

public class RespawnTask extends BukkitRunnable {

    private final BetterDragon plugin;

    public RespawnTask() {
        this.plugin = BetterDragon.getInstance();

        runTaskTimer(plugin, 1200L, 1200L);
    }

    @Override
    public void run() {

        if (!plugin.getConfig().getBoolean("respawn.enable", true)){ 
            return;
        }
                
        long actualTime = System.currentTimeMillis() / 60000;
		long deathTime = plugin.getDragonData().getDeathTime();

        long respawnTime = plugin.getDragonData().getRespawnTime();

        if ((actualTime - deathTime) >= respawnTime) {

            Location l = plugin.getDragonData().getRespawnLocacion();

            if (l == null){
                plugin.getLogger().log(Level.WARNING, "The respawn location is not set, no dragon will be respawned.");
                return;
            }

            if (!l.getChunk().isLoaded()){
                plugin.getLogger().log(Level.WARNING, "The chunk of the respawn location is not loaded, no dragon will be respawned.");
                return;
            }
            String mode = plugin.getConfig().getString("respawn.mode", "default");

            if (mode.equalsIgnoreCase("default")){
                plugin.getDragonManager().newDragonBattle(); 
                
            } else if (!plugin.getDragonManager().isDragonAlive(l.getWorld())){
                l.getWorld().spawnEntity(l, EntityType.ENDER_DRAGON);
                plugin.getDragonManager().respawnCrystals();
            }       
        }
    }
}
