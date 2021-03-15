package net.mossx.superheroes.Heroes.Powers;

import net.mossx.superheroes.Superheroes;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

public abstract class HeroPowers {
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
            return stack.getItemMeta().getPersistentDataContainer().has(invKey, PersistentDataType.DOUBLE);
        }



        ItemStack getStack();

        int getPosition();


    }
}
