package com.ar.askgaming.betterdragon.Listeners.EntityListeners;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import com.ar.askgaming.betterdragon.BetterDragon;

public class EntityTargetListener implements Listener{

    private final BetterDragon plugin;
    public EntityTargetListener() {
        plugin = BetterDragon.getInstance();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler()
	public void onTarget(EntityTargetLivingEntityEvent e) {
		
		if (e.getTarget() instanceof EnderDragon) {
			if (plugin.getConfig().getBoolean("options.enderman_target_dragon") == false) {
				if(e.getEntity() instanceof Enderman) {
					e.setCancelled(true);
				}
			}
		}
	}
}
