package com.ar.askgaming.betterdragon.Events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.ar.askgaming.betterdragon.Main;

import net.md_5.bungee.api.ChatColor;

public class EntityDeathListener implements Listener{

    private Main plugin;
    public EntityDeathListener(Main main){
        plugin = main;
    }

    @EventHandler()
    public void onEntityDeath(EntityDeathEvent e){
        if (e.getEntity() instanceof EnderDragon) {

            EnderDragon eDragon = (EnderDragon) e.getEntity();

            //Set the death time
            long deathTime = System.currentTimeMillis() / 60000;				
            plugin.getDragon().setDeathTime(deathTime);

            // Remove all entrys from the bossbar 
            plugin.getDragon().getBossBar(eDragon).removeAll();

            if (e.getEntity().getKiller() instanceof Player) {	
                
                // Set Dragon killes
                Player p = (Player) e.getEntity().getKiller();
                plugin.getDragon().setKiller(p.getName());

                // Update the player's kills in the database
                plugin.getDataHandler().updateDragonKills(p);

                FileConfiguration cfg = plugin.getConfig();

                //Handling drops
                double randDoubleEgg = Math.random(), randDoubleHead = Math.random(), chance = Math.random();
                
                if (cfg.getDouble("drops.egg") >= randDoubleEgg) {
                
        			p.sendMessage(plugin.getLang("messages.dropegg"));
					e.getDrops().add(new ItemStack(Material.DRAGON_HEAD));
        		}
        		
        		if (cfg.getDouble("drops.head") >= randDoubleHead) {	

        			p.sendMessage(plugin.getLang("messages.drophead"));
					e.getDrops().add(new ItemStack(Material.DRAGON_EGG));
        		}

                //Handling rewards

                for (String key : cfg.getConfigurationSection("rewards").getKeys(false)){
					
				    String txt = cfg.getString("rewards." + key + ".broadcast_text");
				    List<String> drop = cfg.getStringList("rewards." + key + ".drops");
				    List<String> commands = cfg.getStringList("rewards." + key + ".commands");
				    				    
				    if (cfg.getDouble("rewards." + key + ".chance") >= chance) {
				    					    
				    	if (!txt.equals("")){ 
						    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', txt));
				    	}
				    	for (String item : drop) {
				    		e.getDrops().add(new ItemStack(Material.valueOf(item)));
				    	}
					    
						for (String s : commands) {						
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("%player%", p.getName()));
						}
				    }
				}
            }
        }
    }
}
