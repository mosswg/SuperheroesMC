package net.mossx.superheroes.Heroes.Powers;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

public class IronFistPowers extends HeroPowers {

    public enum inventory implements inv {
        Helmet(new ItemStack(Material.LEATHER_HELMET), HeroPowers.inv.setColor((LeatherArmorMeta)new ItemStack(Material.LEATHER_HELMET).getItemMeta(), Color.YELLOW), 0),
        Leggings(new ItemStack(Material.LEATHER_LEGGINGS), HeroPowers.inv.setColor((LeatherArmorMeta)new ItemStack(Material.LEATHER_LEGGINGS).getItemMeta(), Color.fromRGB(44, 180, 2)), 1),
        Boots(new ItemStack(Material.LEATHER_BOOTS), HeroPowers.inv.setColor((LeatherArmorMeta)new ItemStack(Material.LEATHER_BOOTS).getItemMeta(), Color.YELLOW), 2),

        ;

        ItemStack stack; int position;
        inventory(ItemStack stack, ItemMeta meta, int position) {
            meta.getPersistentDataContainer().set(invKey, PersistentDataType.DOUBLE, Math.PI);
            stack.setItemMeta(meta);
            this.stack = stack; this.position = position;
        }


        @Override
        public String getName() {
            return "Iron Fist";
        }

        @Override
        public ItemStack getStack() {
            return stack;
        }

        @Override
        public int getPosition() {
            return position;
        }
    }
}
