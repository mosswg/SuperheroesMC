package net.mossx.superheroes.Heroes.Powers;

import net.mossx.superheroes.Heroes.Superman;
import net.mossx.superheroes.Heroes.hero;
import net.mossx.superheroes.Superheroes;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

public class SupermanPowers {

    public static void LazerEyes(Player p) {
        for (double j = 0; j < 200; j+=.5) {
            Location particleLocation = p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(j + 1));
            particleLocation.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, new Particle.DustOptions(Color.RED, 0.4F));
        }
        Damageable d = ((Damageable) hero.playerLookingAt(p));
        if (d != null)
            for (int i = 1; i < 5; i++) {
                d.damage(2);
                (new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.sendMessage("Damaged Entity");
                        d.damage(1);
                    }
                }).runTaskLater(Superheroes.plugin, 1 + i);
            }
    }



    public enum inventory implements hero.inv {
        Helmet(new ItemStack(Material.LEATHER_HELMET), hero.inv.setColor((LeatherArmorMeta)new ItemStack(Material.LEATHER_HELMET).getItemMeta(), Color.YELLOW), 0),
        Leggings(new ItemStack(Material.LEATHER_LEGGINGS), hero.inv.setColor((LeatherArmorMeta)new ItemStack(Material.LEATHER_LEGGINGS).getItemMeta(), Color.fromRGB(44, 180, 2)), 1),
        Boots(new ItemStack(Material.LEATHER_BOOTS), hero.inv.setColor((LeatherArmorMeta)new ItemStack(Material.LEATHER_BOOTS).getItemMeta(), Color.YELLOW), 2),

        LazerEyes(hero.createPower(Material.RED_DYE, "Lazer Eyes"), 8, SupermanPowers::LazerEyes)
        ;

        ItemStack stack; int position; Consumer<Player> run;
        inventory(ItemStack stack, int position, Consumer<Player> run) {
            this.stack = stack;
            this.position = position;
            this.run = run;
        }
        inventory(ItemStack stack, ItemMeta meta, int position) {
            meta.getPersistentDataContainer().set(invKey, PersistentDataType.DOUBLE, Math.PI);
            stack.setItemMeta(meta);
            this.stack = stack; this.position = position;
        }

        public static inventory getByMaterial(Material material) {
                for (inventory i : inventory.values()) {
                    if (i.stack.getType() == material) {
                        return i;
                    }
                }
                System.out.println("No Item Of Type " + material + " Was Found");
                return inventory.values()[0];
            }


        @Override
        public String getName() {
            return "Superman";
        }

        @Override
        public ItemStack getStack() {
            return stack;
        }

        public void run(Player p) {
            this.run.accept(p);
        }

        @Override
        public int getPosition() {
            return position;
        }
    }
}
