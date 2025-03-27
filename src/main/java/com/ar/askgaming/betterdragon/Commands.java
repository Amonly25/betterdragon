package com.ar.askgaming.betterdragon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Commands implements TabExecutor{

    private final BetterDragon plugin;
    public Commands() {
        plugin = BetterDragon.getInstance();

        plugin.getServer().getPluginCommand("dragon").setExecutor(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        
        if (args.length == 1) {
            return new ArrayList<>(Arrays.asList("kill", "respawn","set_respawn","set_statue","tp","get_top","set_top","reload","next","add_custom_drop","test_rewards","add_crystal"));
        }

        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (args.length == 0) {
            sender.sendMessage("Use /command <subcommand>");
            return true;
        }
        Player p = null;
        if (sender instanceof Player){
            p = (Player)sender;
        }

        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "kill":
                    if (!plugin.getDragonManager().getDragonsAlive().isEmpty()){
                        for (EnderDragon e : plugin.getDragonManager().getDragonsAlive()){
                            e.setHealth(0);
                        }
                    } else sender.sendMessage("There is not an alive dragon.");

                break;

                case "tp":
                    if (!plugin.getDragonManager().getDragonsAlive().isEmpty()){
                        if (p != null){
                            p.teleport(plugin.getDragonManager().getDragonsAlive().get(0));
                        }
                    } else sender.sendMessage("There is not an alive dragon.");
                break;

                case "get_top":
                    sender.sendMessage(plugin.getLeaderboard().getTop());
                break;
                case "set_top":
                    if (p != null){
                        plugin.getLeaderboard().createOrUpdateLeaderBoard(p.getLocation());
                        sender.sendMessage("Leaderboard set.");
                    } else sender.sendMessage("This command must be sended by a player.");
                    break;
                case "test_rewards":
                    if (p != null){
                        plugin.getDragonManager().proccesCustomDrops(p, p.getLocation());
                        plugin.getDragonManager().proccesRewards(p, p.getLocation(), "who_killed_dragon");
                    } else sender.sendMessage("This command must be sended by a player.");
                    return true;
                case "set_respawn":
                    if (p != null){
                        plugin.getDragonData().setRespawnLocacion(((Player) sender).getLocation());
                        sender.sendMessage("New respawn location set.");
                    } else sender.sendMessage("This command must be sended by a player.");
                break;
                    
                case "add_crystal":
                    if (p != null){
                        plugin.getDragonManager().addCrystal(p.getLocation());
                        sender.sendMessage("Crystal added.");

                    } else sender.sendMessage("This command must be sended by a player.");
                    break;
                case "respawn":
                    Location l = plugin.getDragonData().getRespawnLocacion();
                    if (l == null){
                        sender.sendMessage("The respawn location is not set.");
                        return true;
                    } 
                    String mode = plugin.getConfig().getString("respawn.mode","default");
                    if (mode.equals("default")){
                        if (l.getWorld().getEnvironment() == Environment.THE_END){
                            plugin.getDragonManager().newDragonBattle();
                            sender.sendMessage("Starting new dragon battle... please wait.");

                        } else sender.sendMessage("The respawn location is not in the end world.");
                    } else {
                        l.getWorld().spawn(l, EnderDragon.class);
                        plugin.getDragonManager().respawnCrystals();
                        sender.sendMessage("Dragon respawned.");
                    }

                    break;

                case "reload":
                    plugin.reloadConfig();
                    plugin.getDragonData().load();
                    plugin.getDragonAbilities().load();
                    plugin.getLeaderboard().reload();
                    plugin.getDragonStatue().load();
                    plugin.getLangHandler().load();
                    sender.sendMessage("Config reloaded.");
                break;

                case "next":
                    sender.sendMessage(plugin.getDragonManager().getNext());
                break;

                case "add_custom_drop":
                    if (p == null){
                        sender.sendMessage("This command must be sended by a player.");
                        return true;
                    }
                    ItemStack item = p.getInventory().getItemInMainHand();
                    if (item != null){
                        String id = System.currentTimeMillis() + "";
                        plugin.getConfig().set("custom_drops." + id + ".item", item);
                        plugin.getConfig().set("custom_drops." + id + ".chance", 0.5);
                        plugin.getConfig().set("custom_drops." + id + ".broadcast_text", "");
                        plugin.saveConfig();
                        sender.sendMessage("Item added to custom drops to the config, see to edit chance and broadcast text.");
                    } else sender.sendMessage("You must hold an item in your hand.");

                break;

                case "set_statue":
                    if (p != null){
                        plugin.getDragonStatue().move(p.getLocation());
                        sender.sendMessage("Statue spawned.");
                    } else sender.sendMessage("This command must be sended by a player.");
                break;
            
                default:
                sender.sendMessage("Invalid Command type.");
                break;
            }
        }
        return true;
    }
}
