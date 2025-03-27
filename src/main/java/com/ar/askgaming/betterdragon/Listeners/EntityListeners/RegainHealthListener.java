package com.ar.askgaming.betterdragon.Listeners.EntityListeners;

import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import com.ar.askgaming.betterdragon.BetterDragon;

public class RegainHealthListener implements Listener {

    private final BetterDragon plugin;
    public RegainHealthListener(){
        plugin = BetterDragon.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof EnderDragon) {
            EnderDragon dragon = (EnderDragon) e.getEntity();
            plugin.getDragonBossBar().updateBossBar(dragon);
        }
    }
}
