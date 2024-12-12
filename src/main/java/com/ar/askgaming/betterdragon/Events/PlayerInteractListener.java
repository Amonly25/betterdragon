package com.ar.askgaming.betterdragon.Events;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.ar.askgaming.betterdragon.BetterDragon;

public class PlayerInteractListener implements Listener{

    private BetterDragon plugin;
    public PlayerInteractListener(BetterDragon main){
        plugin = main;
    }

    @EventHandler()
    public void onPlayerInteract(PlayerInteractEvent event){
        Player p = (Player) event.getPlayer();
    	List<String> disableWorlds = plugin.getConfig().getStringList("options.disable_endcrystal_onbedrock.worlds");
    	
    	if (p.isOp()) return;
    	
        if (event.getMaterial() == Material.END_CRYSTAL && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
        	
            if (event.getClickedBlock() != BlockType.BEDROCK) return;

    		for (String worlds : disableWorlds) {
    			if (p.getLocation().getWorld().getName().equalsIgnoreCase(worlds)) {
        	    	      	       		
        			event.getPlayer().sendMessage(plugin.getLang("messages.endcrystal"));
        			event.setCancelled(true);       			
        		}
        	}
        } 
    }
}
