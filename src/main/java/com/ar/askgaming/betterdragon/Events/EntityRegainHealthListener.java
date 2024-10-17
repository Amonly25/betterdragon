package com.ar.askgaming.betterdragon.Events;

import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import com.ar.askgaming.betterdragon.Main;

public class EntityRegainHealthListener implements Listener{

    private Main plugin;
    public EntityRegainHealthListener(Main plugin) {
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
