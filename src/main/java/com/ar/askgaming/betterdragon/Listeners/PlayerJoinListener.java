package com.ar.askgaming.betterdragon.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.ar.askgaming.betterdragon.BetterDragon;

public class PlayerJoinListener implements Listener{

    private BetterDragon plugin;
    public PlayerJoinListener(BetterDragon main){
        plugin = main;
    }

    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent e){

		Player p = (Player) e.getPlayer();      
        plugin.getDataHandler().updateDragonKills(p);

    }
}
