package net.mossx.superheroes.Heroes.Powers;

import net.mossx.superheroes.CommandSuperhero;
import net.mossx.superheroes.Heroes.Superman;
import net.mossx.superheroes.Heroes.Hero;
import net.mossx.superheroes.Superheroes;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SupermanPowers extends HeroPowers {
    public boolean heatVision = false;


    public static void HeatVision(Player p) {
        for (double j = 0; j < 200; j+=.5) {
            Location particleLocation = p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(j + 1));
            particleLocation.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, 1, new Particle.DustOptions(Color.RED, 0.4F));
        }
        ((Superman)CommandSuperhero.getPlayerHero(p, new Superman())).setHeatVision(true);
        (new BukkitRunnable() {
            @Override
            public void run() {
                ((Superman)CommandSuperhero.getPlayerHero(p, new Superman())).setHeatVision(false);
            }
        }).runTaskLater(Superheroes.plugin, 5L);
    }
    public static void FrostBreath(Player p) {
        for (double j = 0; j < 6; j+=.5) {
            Location particleLocation = p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(j + 1));
            particleLocation.getWorld().spawnParticle(Particle.SNOWBALL, particleLocation, 1, new Particle.DustOptions(Color.RED, 0.4F));
        }
        RayTraceResult ray = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), 100, 4, new Predicate<Entity>() {
            @Override
            public boolean test(Entity entity) {
                if (entity instanceof Player) {
                    return !(entity.getUniqueId().equals(p.getUniqueId()));
                }
                return true;
            }
        });
        if (ray != null) {
            Entity e = ray.getHitEntity();
            if (e instanceof LivingEntity) {
                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 2, false, false));
            }
        }
    }



    public enum inventory implements inv {
        Helmet(new ItemStack(Material.LEATHER_HELMET), HeroPowers.inv.setColor((LeatherArmorMeta)new ItemStack(Material.LEATHER_HELMET).getItemMeta(), Color.YELLOW), 0),
        Leggings(new ItemStack(Material.LEATHER_LEGGINGS), HeroPowers.inv.setColor((LeatherArmorMeta)new ItemStack(Material.LEATHER_LEGGINGS).getItemMeta(), Color.fromRGB(44, 180, 2)), 1),
        Boots(new ItemStack(Material.LEATHER_BOOTS), HeroPowers.inv.setColor((LeatherArmorMeta)new ItemStack(Material.LEATHER_BOOTS).getItemMeta(), Color.YELLOW), 2),

        HeatVision(Hero.createPower(Material.RED_DYE, "Heat Vision"), 8, SupermanPowers::HeatVision),
        FrostBreath(Hero.createPower(Material.SNOW, "Frost Breath"), 8, SupermanPowers::FrostBreath)

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
