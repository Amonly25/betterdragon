package com.ar.askgaming.betterdragon.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay;

import com.ar.askgaming.betterdragon.BetterDragon;

import net.md_5.bungee.api.ChatColor;

public class Leaderboard {

    private BetterDragon plugin;
    public Leaderboard(BetterDragon main) {
        plugin = main;

        reload();
    }

    private String format;
    private TextDisplay display;
    private int positions;
    private Location location;
    private String title;

    public void reload() {
        // Update the leader board
        format = plugin.getConfig().getString("leaderboard.format","&f%position%. &7%player% &8>> &6%kills% &7kills");
        title = plugin.getConfig().getString("leaderboard.title","Leaderboard").replace('&', '§');
        positions = plugin.getConfig().getInt("leaderboard.positions",10);

        if (display != null) {
            display.remove();
        }

        location = plugin.getConfig().getLocation("leaderboard.location");
        if (location == null) {
            plugin.getLogger().warning("Leaderboard location is not set in the config");
            return;
        }
        World world = location.getWorld();
        if (world == null) {
            plugin.getLogger().warning("Leaderboard location world is not set in the config");
            return;
        }
        display = world.spawn(location, TextDisplay.class);
        display.setText(title + "\n");
        display.setBillboard(Billboard.CENTER);
        updateText();
        
    }
    public void removeLeaderBoard() {
        if (display != null) {
            display.remove();
        }
    }
    public void updateText() {

        if (display == null) {
            return;
        }

        display.setText(getTop());
    }
    public String getTop() {

        List<String> lines = getLines();
        
        StringBuilder text = new StringBuilder(title + "\n");
        for (String line : lines) {
            text.append(line).append("\n");

        }
        return text.toString();
    }
    public List<String> getLines() {
        HashMap<String, Integer> playersElo = plugin.getDataHandler().getKills();

        List<String> lines = new ArrayList<>();

        AtomicInteger position = new AtomicInteger(1);
        playersElo.entrySet().stream().sorted((e1,e2) -> e2.getValue().compareTo(e1.getValue())).limit(positions).forEach(entry -> {

            String playerName = entry.getKey();
            int elo = entry.getValue();
            String text = format.replace("%position%", String.valueOf(position.getAndIncrement())).replace("%player%", playerName).replace("%kills%", String.valueOf(elo));
            text = ChatColor.translateAlternateColorCodes('&', text);
            lines.add(text);
        });
        return lines;
    }

    public void createOrUpdateLeaderBoard(Location location) {
        plugin.getConfig().set("leaderboard.location", location);
        plugin.saveConfig();
        reload();
        
    }
}
