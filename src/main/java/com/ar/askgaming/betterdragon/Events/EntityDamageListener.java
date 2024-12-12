package com.ar.askgaming.betterdragon.Events;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.ar.askgaming.betterdragon.BetterDragon;

public class EntityDamageListener implements Listener{

    private BetterDragon plugin;
    public EntityDamageListener(BetterDragon main){
        plugin = main;
    }

    @EventHandler()
    public void onEntityDamage(EntityDamageByEntityEvent e){
        
        // Handle logic when dragon makes damage
        if (e.getDamager() instanceof EnderDragon) {

			if (e.getEntity() instanceof Enderman) {
				if (plugin.getConfig().getBoolean("options.dragon_damage_enderman") == false) {
					e.setCancelled(true);
				}
			}
			if (e.getEntity() instanceof Player) {
				 double dmg = e.getDamage();
				 int multiply = plugin.getDataHandler().getDragonDataConfig().getInt("damage_multiplier");
				 if (multiply > 1) {
					 e.setDamage(dmg*multiply);
				 }
			}
            return;
        }

        // Handle logic when dragon makes damage
        if (e.getEntity() instanceof EnderDragon) {

            EnderDragon eDragon = (EnderDragon) e.getEntity();

            // Update bossbar
            plugin.getDragon().updateBossBar(eDragon, -e.getDamage());

            // Abilities
            if (e.getDamager() instanceof Arrow) {
                Arrow a = (Arrow) e.getDamager();
                
                if (a.getShooter() instanceof Player) {	
                    Player p = (Player) a.getShooter();
                    plugin.getDragonAbilities().createCounterAttack(e, p);
                }
                return;
            }
            if (e.getDamager() instanceof Player) {
                Player p = (Player) e.getDamager();
                plugin.getDragonAbilities().createCounterAttack(e, p);
            }
        }
    }
}
