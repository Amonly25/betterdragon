package com.ar.askgaming.betterdragon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.betterdragon.Dragon.Dragon;
import com.ar.askgaming.betterdragon.Dragon.DragonAbilities;
import com.ar.askgaming.betterdragon.Dragon.DragonStatue;
import com.ar.askgaming.betterdragon.Events.CreatureSpawnListener;
import com.ar.askgaming.betterdragon.Events.EntityDamageListener;
import com.ar.askgaming.betterdragon.Events.EntityDeathListener;
import com.ar.askgaming.betterdragon.Events.EntityRegainHealthListener;
import com.ar.askgaming.betterdragon.Events.EntityTargetListener;
import com.ar.askgaming.betterdragon.Events.PlayerChangedWorldListener;
import com.ar.askgaming.betterdragon.Events.PlayerInteractListener;
import com.ar.askgaming.betterdragon.Events.PlayerJoinListener;

import net.md_5.bungee.api.ChatColor;

public class BetterDragon extends JavaPlugin{
    
    private Dragon dragon;
    private DataHandler dataHandler;
    private DragonStatue statue;
    private DragonAbilities dragonAbilities;

    public DragonAbilities getDragonAbilities() {
        return dragonAbilities;
    }
    public DragonStatue getStatue() {
        return statue;
    }
    public DataHandler getDataHandler() {
        return dataHandler;
    }
    public Dragon getDragon() {
        return dragon;
    }
  
    
    public void onEnable() {

        saveDefaultConfig();

        dataHandler = new DataHandler(this);
        dragon = new Dragon(this);
        statue = new DragonStatue(this);
        dragonAbilities = new DragonAbilities(this);

        Bukkit.getPluginManager().registerEvents(new CreatureSpawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChangedWorldListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityRegainHealthListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityTargetListener(this), this);
          
        Bukkit.getPluginCommand("dragon").setExecutor(new Commands(this));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new RespawnTask(this), 0L, 1200L);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlacerHolderHook(this).register();
      }
    }

    public void onDisable() {

        dragon.getDragonsAlive().forEach(eDragon -> {
            dragon.getBossBar(eDragon).removeAll();            
        });
    }

    public String getLang(String s){
        if (getConfig().get(s) != null) {
            return ChatColor.translateAlternateColorCodes('&', getConfig().getString(s));
        }
        return "Undefined path " + s;
    }
}
