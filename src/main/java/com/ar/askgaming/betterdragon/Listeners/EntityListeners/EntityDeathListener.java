package com.ar.askgaming.betterdragon.Listeners.EntityListeners;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.ar.askgaming.betterdragon.BetterDragon;

public class EntityDeathListener implements Listener{

    private BetterDragon plugin;
    public EntityDeathListener(BetterDragon main){
        plugin = main;
    }

    @EventHandler()
    public void onEntityDeath(EntityDeathEvent e){
        if (e.getEntity() instanceof EnderDragon) {

            EnderDragon eDragon = (EnderDragon) e.getEntity();

            //Set the death time
            long deathTime = System.currentTimeMillis() / 60000;				
            plugin.getDragon().setDeathTime(deathTime);

            if (e.getEntity().getKiller() instanceof Player) {	
                
                // Set Dragon killes
                Player p = (Player) e.getEntity().getKiller();
                plugin.getDragon().setKiller(p.getName());

                // Update the player's kills in the database
                plugin.getDataHandler().updateDragonKills(p);

                plugin.getDragonManager().proccesRewards(p, eDragon.getLocation());


            }
        }
    }
}
