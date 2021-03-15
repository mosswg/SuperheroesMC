package net.mossx.superheroes.Heroes;


import net.mossx.superheroes.Superheroes;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;

import java.util.function.Predicate;

public abstract class hero implements Cloneable, Listener {

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
        return p.getWorld().rayTraceEntities(p.getLocation().add(p.getEyeLocation().getDirection().multiply(1.5)), p.getEyeLocation().getDirection(), 100, new Predicate<Entity>() {
            @Override
            public boolean test(Entity entity) {
                return entity.equals(p);
            }
        }).getHitEntity();
    }


    public interface inv {

        String getName();

        NamespacedKey invKey = new NamespacedKey(Superheroes.plugin, "Inventory");
        static Inventory getInv(int size, inv[] spots) {
            if (spots.length == 0)
                return Bukkit.createInventory(null, 0);
            Inventory inventory = Bukkit.createInventory(null, size, spots[0].getName());
            for (inv i : spots) {
                if (i.getPosition() != -1)
                    inventory.setItem(i.getPosition(), i.getStack());
                else
                    inventory.addItem(i.getStack());
            }
            return inventory;
        }
        static LeatherArmorMeta setColor(LeatherArmorMeta meta, Color c) {
            meta.setColor(c);
            return meta;
        }

        static ItemMeta setUnbreakable(ItemMeta meta) {
            meta.setUnbreakable(true);
            return meta;
        }

        static ItemMeta addEnchantment(ItemMeta meta, Enchantment ench, int level, boolean ignoreRestriction) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(ench, level, ignoreRestriction);
            return meta;
        }

        static ItemMeta addTag(ItemMeta meta, String name) {
            NamespacedKey key = new NamespacedKey(Superheroes.plugin, name);
            meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, Math.PI);
            return meta;
        }
        static <K, Z> ItemMeta addTag(ItemMeta meta, String name, PersistentDataType<K, Z> type, Z initValue) {
            NamespacedKey key = new NamespacedKey(Superheroes.plugin, name);
            meta.getPersistentDataContainer().set(key, type, initValue);
            return meta;
        }
        static <K, Z> ItemMeta addTag(ItemMeta meta, NamespacedKey key, PersistentDataType<K, Z> type, Z initValue) {
            meta.getPersistentDataContainer().set(key, type, initValue);
            return meta;
        }
        static ItemMeta addDoubleTag(ItemMeta meta, String name, double initValue) {
            NamespacedKey key = new NamespacedKey(Superheroes.plugin, name);
            meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, initValue);
            return meta;
        }

        static ItemMeta setName(ItemMeta meta, String name) {
            meta.setDisplayName(name);
            return meta;
        }
        static boolean isInventory(ItemStack stack) {
            if (stack == null)
                return false;
            return stack.getItemMeta().getPersistentDataContainer().has(hero.inv.invKey, PersistentDataType.DOUBLE);
        }



        ItemStack getStack();

        int getPosition();


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
