package com.ar.askgaming.betterdragon.Listeners.EntityListeners;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.ar.askgaming.betterdragon.BetterDragon;

public class CreatureSpawnListener implements Listener{

    private BetterDragon plugin;

    public CreatureSpawnListener(BetterDragon main){
        plugin = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent e){

        if (e.getEntityType() == EntityType.ENDER_DRAGON) {

            EnderDragon eDragon = (EnderDragon) e.getEntity();
            
            eDragon.setCustomName(plugin.getDragon().getName());
            eDragon.setPhase(EnderDragon.Phase.CIRCLING);
            
            eDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(plugin.getDragon().getHealth());
            eDragon.setHealth(plugin.getDragon().getHealth());
         
                  
            eDragon.setCustomNameVisible(plugin.getConfig().getBoolean("options.name_visible"));
            
            Bukkit.broadcastMessage(plugin.getLang("messages.respawn"));
            
            if (eDragon.getDragonBattle() != null) {
                BossBar bossBar = eDragon.getDragonBattle().getBossBar();
                plugin.getDragonManager().updateBossBar(bossBar);
            }
        }
    }
}
