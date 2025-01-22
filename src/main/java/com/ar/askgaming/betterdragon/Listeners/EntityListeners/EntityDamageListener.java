package com.ar.askgaming.betterdragon.Listeners.EntityListeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
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

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        Entity entity = e.getEntity();

        if (damager instanceof EnderDragon || damager instanceof DragonFireball) {
            handleDragonDamage(e, entity);
        } else if (entity instanceof EnderDragon) {
            handleDragonBeingDamaged(e, damager);
        }
    }

    private void handleDragonDamage(EntityDamageByEntityEvent e, Entity entity) {
        if (entity instanceof Enderman && !plugin.getConfig().getBoolean("options.dragon_damage_enderman")) {
            e.setCancelled(true);
        } else if (entity instanceof Player) {
            //Bukkit.broadcastMessage("test");
            double dmg = e.getDamage();
            int multiply = plugin.getConfig().getInt("dragon.damage_multiplier");
            if (multiply > 1) {
                e.setDamage(dmg * multiply);
            }
        }
    }

    private void handleDragonBeingDamaged(EntityDamageByEntityEvent e, Entity damager) {
        if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                plugin.getDragonAbilities().createCounterAttack(e, player);
            }
        } else if (damager instanceof Player) {
            Player player = (Player) damager;
            plugin.getDragonAbilities().createCounterAttack(e, player);
        }
    }
}
