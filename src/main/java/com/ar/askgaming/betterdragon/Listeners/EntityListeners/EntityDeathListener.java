package com.ar.askgaming.betterdragon.Listeners.EntityListeners;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.ar.askgaming.betterdragon.BetterDragon;
import com.ar.askgaming.universalnotifier.UniversalNotifier;
import com.ar.askgaming.universalnotifier.Managers.AlertManager.Alert;

public class EntityDeathListener implements Listener{

    private final BetterDragon plugin;
    public EntityDeathListener(){
        plugin = BetterDragon.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler()
    public void onEntityDeath(EntityDeathEvent e){
        if (e.getEntity() instanceof EnderDragon) {

            EnderDragon eDragon = (EnderDragon) e.getEntity();

            //Set the death time
            long deathTime = System.currentTimeMillis() / 60000;				
            plugin.getDragonData().setDeathTime(deathTime);
            plugin.getDragonBossBar().removeBossBar(eDragon);

            int exp = plugin.getConfig().getInt("options.xp_drop",500);
            e.setDroppedExp(exp);
            if (e.getEntity().getKiller() instanceof Player) {	
                
                // Set Dragon killes
                Player p = (Player) e.getEntity().getKiller();
                plugin.getDragonData().setKiller(p.getName());

                // Update the player's kills in the database
                plugin.getDataHandler().updateDragonKills(p);

                plugin.getDragonManager().proccesCustomDrops(p, eDragon.getLocation());
                plugin.getDragonManager().proccesRewards(p, eDragon.getLocation(), "who_killed_dragon");
                plugin.getDragonManager().checkDamagersAndReward(eDragon);

            } else{
                plugin.getDragonData().setKiller("Unknown");

            }
            if (plugin.getServer().getPluginManager().getPlugin("UniversalNotifier") != null) {
                UniversalNotifier notifier = UniversalNotifier.getInstance();
                String start = plugin.getConfig().getString("notifier.dragon_kill", "🐉 A dragon has been killed").replace("%player%", plugin.getDragonData().getKillerName());
                notifier.getNotificationManager().broadcastToAll(Alert.CUSTOM, start);
            } 
            plugin.getLeaderboard().updateText();
        }
    }
}
