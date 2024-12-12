package com.ar.askgaming.betterdragon.Events;

import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import com.ar.askgaming.betterdragon.BetterDragon;

public class EntityRegainHealthListener implements Listener{

    private BetterDragon plugin;
    public EntityRegainHealthListener(BetterDragon plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    	public void regen(EntityRegainHealthEvent e) {
				
		if (e.getEntity() instanceof EnderDragon) {
			
			EnderDragon entity = (EnderDragon) e.getEntity();
			
			plugin.getDragon().updateBossBar(entity, e.getAmount());
		}
	}
}
