package net.mossx.superheroes.Heroes;


import net.mossx.superheroes.Heroes.Powers.HeroPowers;
import net.mossx.superheroes.Superheroes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;

import java.util.function.Predicate;

public abstract class hero implements Cloneable, Listener {
    public HeroPowers powers;

    public abstract void tick(Player p);

    public void playerInteractEvent(PlayerInteractEvent e) {}

    public void playerMoveEvent(PlayerMoveEvent e) {}

    public void playerSneakEvent(PlayerToggleSneakEvent e) {}

    public abstract void onEnable(Player p);

    public abstract void onDisable(Player p);

    @Override
    public hero clone() {
        try {
            return (hero)super.clone();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public abstract String getName();

    public String toString() {
        return getName();
    }

    public static Entity playerLookingAt(Player p) {
        RayTraceResult ray = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), 100, new Predicate<Entity>() {
            @Override
            public boolean test(Entity entity) {
                if (entity instanceof Player) {
                    return !(entity.getUniqueId().equals(p.getUniqueId()));
                }
                return true;
            }
        });
        return ray != null ? ray.getHitEntity() : null;
    }


    public static ItemStack createPower(Material stackType, String name, hero.Tag... Tags) {
        if (name == null) {
            System.out.println(stackType + " Name was Null");
            return new ItemStack(stackType);
        }
        ItemStack stack = new ItemStack(stackType);
        ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        for (hero.Tag tag : Tags) {
            tag.set(pdc);
        }
        hero.Tag.invTag.set(pdc);
        stack.setItemMeta(meta);
        return stack;
    }



    public static class Tag {
        public PersistentDataType type;
        public static final Tag invTag = new Tag(PersistentDataType.DOUBLE, "Inventory", Math.PI);
        public String name;
        public Object value;
        public <T, Z> Tag(PersistentDataType<T, Z> type, String name, Z initValue) {
            this.type = type;
            this.name = name;
            this.value = initValue;
        }
        public void set(PersistentDataContainer pdc) {
            pdc.set(new NamespacedKey(Superheroes.plugin, name), type, value);
        }
    }

}
