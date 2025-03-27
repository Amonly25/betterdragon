package com.ar.askgaming.betterdragon.Listeners.EntityListeners;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.ar.askgaming.betterdragon.BetterDragon;
import com.ar.askgaming.betterdragon.Dragon.DragonData;
import com.ar.askgaming.universalnotifier.UniversalNotifier;
import com.ar.askgaming.universalnotifier.Managers.AlertManager.Alert;

public class CreatureSpawnListener implements Listener{

    private final BetterDragon plugin;
    private final DragonData dragonData;

    public CreatureSpawnListener(){
        plugin = BetterDragon.getInstance();
        dragonData = plugin.getDragonData();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent e){

        if (e.getEntityType() == EntityType.ENDER_DRAGON) {

            EnderDragon eDragon = (EnderDragon) e.getEntity();
            
            eDragon.setCustomName(dragonData.getName());
            eDragon.setPhase(EnderDragon.Phase.CIRCLING);
            
            eDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(dragonData.getHealth());
            eDragon.setHealth(dragonData.getHealth());
         
            eDragon.setCustomNameVisible(plugin.getConfig().getBoolean("options.name_visible"));
            
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.sendMessage(plugin.getLangHandler().getFrom("respawn", pl));
            }
            
            if (eDragon.getDragonBattle() != null) {
                BossBar bossBar = eDragon.getDragonBattle().getBossBar();
                plugin.getDragonManager().updateBossBar(bossBar);
            }
            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> plugin.getDragonBossBar().checkToBossBar(player));
                }
            }, 10);

            if (plugin.getServer().getPluginManager().getPlugin("UniversalNotifier") != null) {
                UniversalNotifier notifier = UniversalNotifier.getInstance();
                String start = plugin.getConfig().getString("notifier.dragon_spawn", "🐉 The dragon has spawned!");
                notifier.getNotificationManager().broadcastToAll(Alert.CUSTOM, start);
            } 
        }
    }
}
