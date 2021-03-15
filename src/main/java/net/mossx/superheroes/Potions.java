package net.mossx.superheroes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class Potions implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            for (PotionEffect potion : ((Player) sender).getActivePotionEffects()) {
                sender.sendMessage(potion.getType().toString() + ": " + potion.getAmplifier());
            }
            return true;
        }
        else {
            return false;
        }
    }
}
