package com.ar.askgaming.betterdragon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.betterdragon.Dragon.DragonData;
import com.ar.askgaming.betterdragon.Abilities.CounterAttack;
import com.ar.askgaming.betterdragon.Dragon.DragonBossBar;
import com.ar.askgaming.betterdragon.Dragon.DragonManager;
import com.ar.askgaming.betterdragon.Dragon.DragonStatue;
import com.ar.askgaming.betterdragon.Handlers.DataHandler;
import com.ar.askgaming.betterdragon.Listeners.PlayerInteractListener;
import com.ar.askgaming.betterdragon.Listeners.PlayerJoinListener;
import com.ar.askgaming.betterdragon.Listeners.EntityListeners.CreatureSpawnListener;
import com.ar.askgaming.betterdragon.Listeners.EntityListeners.EntityDamageListener;
import com.ar.askgaming.betterdragon.Listeners.EntityListeners.EntityDeathListener;
import com.ar.askgaming.betterdragon.Listeners.EntityListeners.EntityTargetListener;
import com.ar.askgaming.betterdragon.Listeners.EntityListeners.PlayerChangeWorldListener;
import com.ar.askgaming.betterdragon.Listeners.EntityListeners.RegainHealthListener;
import com.ar.askgaming.betterdragon.Utils.Leaderboard;
import com.ar.askgaming.betterdragon.Utils.PlacerHolderHook;
import com.ar.askgaming.betterdragon.Utils.RespawnTask;

import net.md_5.bungee.api.ChatColor;

public class BetterDragon extends JavaPlugin{
    
    private DataHandler dataHandler;
    private DragonManager dragonManager;
    private DragonData dragon;
    private DragonBossBar dragonBossBar;
    private DragonStatue statue;
    private CounterAttack dragonAbilities;
    private Leaderboard leaderboard;
    
    public void onEnable() {

        saveDefaultConfig();

        dataHandler = new DataHandler(this);
        dragon = new DragonData(this);
        dragonManager = new DragonManager(this);
        statue = new DragonStatue(this);
        dragonAbilities = new CounterAttack(this);
        dragonBossBar = new DragonBossBar(this);
        leaderboard = new Leaderboard(this);

        Bukkit.getPluginManager().registerEvents(new CreatureSpawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityTargetListener(this), this);

        new PlayerChangeWorldListener(this);
        new RegainHealthListener(this);
          
        Bukkit.getPluginCommand("dragon").setExecutor(new Commands(this));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new RespawnTask(this), 0L, 1200L);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlacerHolderHook(this).register();
        }
    }

    public void onDisable() {
        dragonBossBar.removeAllBossBars();
        getStatue().remove();
        getLeaderboard().removeLeaderBoard();
    }

    public String getLang(String s){
        if (getConfig().get(s) != null) {
            return ChatColor.translateAlternateColorCodes('&', getConfig().getString(s));
        }
        return "Undefined path " + s;
    }
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }
    public CounterAttack getDragonAbilities() {
        return dragonAbilities;
    }
    public DragonStatue getStatue() {
        return statue;
    }
    public DataHandler getDataHandler() {
        return dataHandler;
    }
    public DragonData getDragon() {
        return dragon;
    }

    public DragonManager getDragonManager() {
        return dragonManager;
    }
    public void setDragon(DragonData dragon) {
        this.dragon = dragon;
    }
    public DragonBossBar getDragonBossBar() {
        return dragonBossBar;
    }
}
