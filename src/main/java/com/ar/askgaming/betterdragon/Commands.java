package com.ar.askgaming.betterdragon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ar.askgaming.betterdragon.Dragon.DragonData;


public class Commands implements TabExecutor{

    private BetterDragon plugin;
    public Commands(BetterDragon main) {
        plugin = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        
        List<String> result = new ArrayList<String>();

        if (args.length == 1) {
            result = new ArrayList<>(Arrays.asList("kill", "respawn","set_respawn","set_statue","tp","top","reload","next","add_custom_drop","test_rewards"));
        }

        return result;
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
                        if (p != null){
                            plugin.getDragon().setKiller("Server");
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

                case "top":
                    if (plugin.getDataHandler().getKillsLeaderboard() != null) {
                        
                        String prefix = plugin.getLang("leaderboard.prefix");
                        String separator = plugin.getLang("leaderboard.separator");
                        String suffix = plugin.getLang("leaderboard.suffix");
                        
                        final int MAX_RESULTS = 10;
                        final AtomicInteger counter = new AtomicInteger(0);
                        
                        plugin.getDataHandler().getKillsLeaderboard().forEach((name, kills) -> {
                            if (counter.getAndIncrement() < MAX_RESULTS) {
                                sender.sendMessage(prefix + name + separator + kills + suffix);
                            } else {
                                return;
                            }
                        });;
                    }
                break;
                case "test_rewards":
                    if (p != null){
                        plugin.getDragonManager().proccesRewards(p, p.getLocation());
                    } else sender.sendMessage("This command must be sended by a player.");
                    return true;
                case "set_respawn":
                    if (p != null){
                        plugin.getDragon().setRespawnLocacion(((Player) sender).getLocation());
                        sender.sendMessage("New respawn location set.");
                    } else sender.sendMessage("This command must be sended by a player.");
                break;

                case "respawn":
                    Location l = plugin.getDragon().getRespawnLocacion();
                    if (l == null){
                        sender.sendMessage("The respawn location is not set.");
                        return true;
                    } 
                    String mode = plugin.getConfig().getString("respawn.mode","default");
                    if (mode.equals("default")){
                        if (l.getWorld().getEnvironment() == Environment.THE_END){
                            plugin.getDragonManager().newDragonBattle();

                        } else sender.sendMessage("The respawn location is not in the end world.");
                    } else {
                        l.getWorld().spawn(l, EnderDragon.class);
                    }

                    break;

                case "reload":
                    plugin.reloadConfig();
                    plugin.setDragon(new DragonData(plugin));
                    sender.sendMessage("Config reloaded.");
                break;

                case "next":
                    sender.sendMessage(plugin.getDragonManager().getNext());
                break;

                case "add_custom_drop":
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
                        plugin.getStatue().spawn(p.getLocation(), p);
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
