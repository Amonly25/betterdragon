package com.ar.askgaming.betterdragon.Events;

import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import com.ar.askgaming.betterdragon.Main;

public class PlayerChangedWorldListener implements Listener{

    private Main plugin;
    public PlayerChangedWorldListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
	public void onChangeWorld(PlayerChangedWorldEvent e) {
		
		Player p = (Player) e.getPlayer();

        World world = p.getWorld();
        
        //Remove bossbars
        plugin.getDragon().getBossBars().forEach((uuid, bossBar) -> {
            bossBar.removePlayer(p);
        });

        //Add bossbars
        world.getLivingEntities().forEach(entity -> {
            if (entity instanceof EnderDragon) {
                EnderDragon dragon = (EnderDragon) entity;
                plugin.getDragon().getBossBar(dragon).addPlayer(p);
            }
        });


    }
}