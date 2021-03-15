package net.mossx.superheroes.Heroes.Powers;

import net.mossx.superheroes.Heroes.Flash;
import net.mossx.superheroes.Superheroes;
import net.mossx.superheroes.Heroes.Hero;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

import static net.mossx.superheroes.Heroes.Hero.createPower;

public class FlashPowers extends HeroPowers {
    public static ArrayList<Player> wallrunningtime = new ArrayList<>();
    public static boolean freeze = false;

    public static void HyperMetabolism(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 3 * 20, 240, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 50, false, false));
        p.setCooldown(p.getInventory().getItemInMainHand().getType(), 3 * 20);
    }
    public static void Leap(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 3 * 20, 6, false, false));
        p.setCooldown(p.getInventory().getItemInMainHand().getType(), 5 * 20);
    }
    public static void WaterWalking(Player p) {
        ItemStack boots = p.getInventory().getBoots();
        if (boots == null) {
            System.out.println("Boots were Null");
            return;
        }
        ItemMeta bMeta = boots.getItemMeta();
        bMeta.addEnchant(Enchantment.FROST_WALKER, 20, true);
        boots.setItemMeta(bMeta);
        p.getInventory().setBoots(boots);
        (new BukkitRunnable() {public void run() {
            ItemMeta meta = boots.getItemMeta();
            meta.removeEnchant(Enchantment.FROST_WALKER);
            boots.setItemMeta(meta);
            p.getInventory().setBoots(boots);
        }
        }).runTaskLater(Superheroes.plugin, 20 * 20L);
        p.setCooldown(p.getInventory().getItemInMainHand().getType(), 30 * 20);
    }

    public static void WallRunning(Player p) {
        Flash.setWallrun(p, true);
        (new BukkitRunnable() {public void run() {Flash.setWallrun(p, false);}
        }).runTaskLater(Superheroes.plugin, 6*20);
        p.setCooldown(p.getInventory().getItemInMainHand().getType(), 9 * 20);
        }
        public static boolean WallRunSub(Player p, BlockFace bf) {
            if (p.getLocation().getBlock().getRelative(bf).getType() != Material.AIR) {
                if (p.getLocation().getBlock().getRelative(bf).getType() != Material.WATER &&
                    !wallrunningtime.contains(p)) {
                    wallrunningtime.add(p);
                    (new BukkitRunnable() {
                        @Override
                        public void run() {
                            wallrunningtime.remove(p);
                        }
                    }).runTaskLater(Superheroes.plugin, 60L);
                }
            return true;
            }
            return false;
    }


    public static void ThrowLightning(Player p) {
        Entity pl = Hero.playerLookingAt(p);
        if (pl == null) {
            RayTraceResult ray = p.rayTraceBlocks(1000, FluidCollisionMode.ALWAYS);
            if (ray != null) {
                p.getWorld().strikeLightning(ray.getHitBlock().getLocation());
            }
        }
        else {
            p.getWorld().strikeLightning(pl.getLocation());
        }
        p.setCooldown(p.getInventory().getItemInMainHand().getType(), 10 * 20);
    }


    public static void Speedforce(Player p) {
        Location loc = p.getLocation();
        World startWorld = p.getWorld();
        for (World w : Bukkit.getWorlds()) {
            if (w.getEnvironment() == World.Environment.NETHER) {
                loc.setWorld(w);
                break;
            }
        }
        if (loc.getWorld().getEnvironment() != World.Environment.NETHER) {
            System.out.println("No Nether World Found");
            return;
        }
        p.teleport(loc);
        (new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = p.getLocation();
                loc.setWorld(startWorld);
                p.teleport(loc);
            }
        }).runTaskLater(Superheroes.plugin, 15 * 20L);
        p.setCooldown(p.getInventory().getItemInMainHand().getType(), 25 * 20);
    }

    public static final AttributeModifier attackSpeed = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
    public static final AttributeModifier attackDamage =  new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 9, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);


    public static void Punch(Player p) {

        ItemStack punchItem = p.getInventory().getItemInMainHand();
        ItemMeta punchMeta = punchItem.getItemMeta();


        punchMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeed);
        punchMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);
        punchItem.setItemMeta(punchMeta);

        (new BukkitRunnable() {
            @Override
            public void run() {
                ItemMeta punchMeta = punchItem.getItemMeta();
                punchMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
                punchMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
                punchItem.setItemMeta(punchMeta);
            }
        }).runTaskLater(Superheroes.plugin, 10 * 20L);


        p.setCooldown(p.getInventory().getItemInMainHand().getType(), 15 * 20);
    }
    public static void Vibrate(Player p) {
        p.teleport(p.getLocation().add(p.getEyeLocation().getDirection().multiply(4)));
        p.setFallDistance(0);
        p.setCooldown(p.getInventory().getItemInMainHand().getType(), 17 * 20);
    }

    public static void Freeze(Player p) {
        freeze = true;
        (new BukkitRunnable() {
            @Override
            public void run() {
                freeze = false;
            }
        }).runTaskLater(Superheroes.plugin, 5 * 20L);
        p.setCooldown(p.getInventory().getItemInMainHand().getType(), 25 * 20);
    }


    public enum inventory implements inv {
        Helmet(new ItemStack(Material.LEATHER_HELMET), HeroPowers.inv.setUnbreakable(HeroPowers.inv.setColor((LeatherArmorMeta)(new ItemStack(Material.LEATHER_HELMET).getItemMeta()), Color.RED))),
        Chestplate(new ItemStack(Material.LEATHER_CHESTPLATE), HeroPowers.inv.setUnbreakable(HeroPowers.inv.setColor(((LeatherArmorMeta)(new ItemStack(Material.LEATHER_CHESTPLATE).getItemMeta())), Color.RED))),
        Leggings(new ItemStack(Material.LEATHER_LEGGINGS), HeroPowers.inv.setUnbreakable(HeroPowers.inv.setColor(((LeatherArmorMeta)(new ItemStack(Material.LEATHER_LEGGINGS).getItemMeta())), Color.RED))),
        Boots(new ItemStack(Material.LEATHER_BOOTS), HeroPowers.inv.addEnchantment(HeroPowers.inv.setUnbreakable(HeroPowers.inv.setColor(((LeatherArmorMeta)(new ItemStack(Material.LEATHER_BOOTS).getItemMeta())), Color.RED)), Enchantment.ARROW_INFINITE, 1, true)),

        Slot1(createPower(Material.BLUE_STAINED_GLASS_PANE, "HyperMetabolism", new Hero.Tag(PersistentDataType.INTEGER, "Speed", 10)), 18, FlashPowers::HyperMetabolism),
        Slot2(createPower(Material.LIME_STAINED_GLASS_PANE, "Leap", new Hero.Tag(PersistentDataType.INTEGER, "Speed", 15)), 19, FlashPowers::Leap),
        Slot3(createPower(Material.GREEN_STAINED_GLASS_PANE, "Wall Running", new Hero.Tag(PersistentDataType.INTEGER, "Speed", 25)), 20, FlashPowers::WallRunning),
        Slot4(createPower(Material.YELLOW_STAINED_GLASS_PANE, "Water Walking", new Hero.Tag(PersistentDataType.INTEGER, "Speed", 30)), 21, FlashPowers::WaterWalking),
        Slot5(createPower(Material.ORANGE_STAINED_GLASS_PANE, "Throw Lightning", new Hero.Tag(PersistentDataType.INTEGER, "Speed", 35)), 22, FlashPowers::ThrowLightning),
        Slot6(createPower(Material.RED_STAINED_GLASS_PANE, "Speedforce", new Hero.Tag(PersistentDataType.INTEGER, "Speed", 40)), 23, FlashPowers::Speedforce),
        Slot7(createPower(Material.MAGENTA_STAINED_GLASS_PANE, "Punch", new Hero.Tag(PersistentDataType.INTEGER, "Speed", 45)), 24, FlashPowers::Punch),
        Slot8(createPower(Material.PURPLE_STAINED_GLASS_PANE, "Vibrate", new Hero.Tag(PersistentDataType.INTEGER, "Speed", 50)), 25, FlashPowers::Vibrate),
        Slot9(createPower(Material.BLACK_STAINED_GLASS_PANE, "Freeze", new Hero.Tag(PersistentDataType.INTEGER, "Speed", 55)), 26, FlashPowers::Freeze),

        ;


        ItemStack stack; int position; public Consumer<Player> run;
        inventory(ItemStack stack) {
            ItemMeta st = stack.getItemMeta();
            assert st != null;
            st.getPersistentDataContainer().set(invKey, PersistentDataType.DOUBLE, Math.PI);
            stack.setItemMeta(st);
            this.stack = stack; this.position = -1;
        }
        inventory(ItemStack stack, int position) {
            ItemMeta st = stack.getItemMeta();
            assert st != null;
            st.getPersistentDataContainer().set(invKey, PersistentDataType.DOUBLE, Math.PI);
            stack.setItemMeta(st);
            this.stack = stack; this.position = position;
        }
        inventory(ItemStack stack, ItemMeta meta) {
            meta.getPersistentDataContainer().set(invKey, PersistentDataType.DOUBLE, Math.PI);
            stack.setItemMeta(meta);
            this.stack = stack; this.position = -1;
        }
        inventory(ItemStack stack, ItemMeta meta, int position) {
            meta.getPersistentDataContainer().set(invKey, PersistentDataType.DOUBLE, Math.PI);
            stack.setItemMeta(meta);
            this.stack = stack; this.position = position;
        }
        inventory(ItemStack stack, int position, Consumer<Player> run) {
            this.run = run;
            this.position = position;
            this.stack = stack;
        }
        inventory(ItemStack stack, ItemMeta meta, Consumer<Player> run) {
            this.run = run;
            meta.getPersistentDataContainer().set(invKey, PersistentDataType.DOUBLE, Math.PI);
            stack.setItemMeta(meta);
            this.stack = stack; this.position = -1;
        }
        inventory(ItemStack stack, ItemMeta meta, int position, Consumer<Player> run) {
            this.run = run;
            meta.getPersistentDataContainer().set(invKey, PersistentDataType.DOUBLE, Math.PI);
            stack.setItemMeta(meta);
            this.stack = stack; this.position = position;
        }

        public static int indexOf(Material m) {
            for (int i = 0; i < inventory.values().length; i++) {
                if (inventory.values()[i].stack.getType() == m) {
                    System.out.println(m + " Was Found at index " + i);
                    return i;
                }
            }
            System.out.println("No Item Of Type " + m + " Was Found");
            return -1;
        }

        public static inventory getByMaterial(Material m) {
            for (inventory i : inventory.values()) {
                if (i.stack.getType() == m) {
                    return i;
                }
            }
            System.out.println("No Item Of Type " + m + " Was Found");
            return inventory.values()[0];
        }

        @Override
        public ItemStack getStack() {
            return stack;
        }

        @Override
        public int getPosition() {
            return position;
        }

        @Override
        public String getName() {
            return "Flash";
        }
    }
}
