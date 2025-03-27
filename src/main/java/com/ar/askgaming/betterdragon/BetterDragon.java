package com.ar.askgaming.betterdragon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.betterdragon.Abilities.CounterAttack;
import com.ar.askgaming.betterdragon.Dragon.DragonBossBar;
import com.ar.askgaming.betterdragon.Dragon.DragonData;
import com.ar.askgaming.betterdragon.Dragon.DragonManager;
import com.ar.askgaming.betterdragon.Dragon.DragonStatue;
import com.ar.askgaming.betterdragon.Handlers.DataHandler;
import com.ar.askgaming.betterdragon.Handlers.LangHandler;
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

public class BetterDragon extends JavaPlugin{
    
    private static BetterDragon instance;

    private DataHandler dataHandler;
    private LangHandler langHandler;
    private DragonManager dragonManager;
    private DragonData dragon;
    private DragonBossBar dragonBossBar;
    private DragonStatue statue;
    private CounterAttack dragonAbilities;
    private Leaderboard leaderboard;
    
    public void onEnable() {

        instance = this;
        saveDefaultConfig();

        dataHandler = new DataHandler(this);
        langHandler = new LangHandler();
        dragon = new DragonData(this);
        dragonManager = new DragonManager(this);
        statue = new DragonStatue(this);
        dragonAbilities = new CounterAttack(this);
        dragonBossBar = new DragonBossBar(this);
        leaderboard = new Leaderboard(this);

        new PlayerChangeWorldListener();
        new RegainHealthListener();
        new PlayerInteractListener();
        new EntityDeathListener();
        new CreatureSpawnListener();
        new PlayerJoinListener();
        new EntityDamageListener();
        new EntityTargetListener();

        new Commands();
        new RespawnTask();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlacerHolderHook(this).register();
        }
    }

    public void onDisable() {
        dragonBossBar.removeAllBossBars();
        getDragonStatue().remove();
        getLeaderboard().removeLeaderBoard();
    }

    public Leaderboard getLeaderboard() {
        return leaderboard;
    }
    public LangHandler getLangHandler() {
        return langHandler;
    }
    public CounterAttack getDragonAbilities() {
        return dragonAbilities;
    }
    public DragonStatue getDragonStatue() {
        return statue;
    }
    public DataHandler getDataHandler() {
        return dataHandler;
    }
    public DragonData getDragonData() {
        return dragon;
    }

    public DragonManager getDragonManager() {
        return dragonManager;
    }

    public DragonBossBar getDragonBossBar() {
        return dragonBossBar;
    }
    public static BetterDragon getInstance() {
        return instance;
    }
}
