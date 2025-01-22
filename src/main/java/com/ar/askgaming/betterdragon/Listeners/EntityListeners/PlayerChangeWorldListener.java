package com.ar.askgaming.betterdragon.Listeners.EntityListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import com.ar.askgaming.betterdragon.BetterDragon;

public class PlayerChangeWorldListener implements Listener{

    private BetterDragon plugin;
    public PlayerChangeWorldListener(BetterDragon main){
        plugin = main;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e){
        Player p = e.getPlayer();
        plugin.getDragonBossBar().checkToBossBar(p);
    }

}
