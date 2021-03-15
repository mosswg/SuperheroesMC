package net.mossx.superheroes.Heroes;

import net.mossx.superheroes.Heroes.Powers.HeroPowers;
import net.mossx.superheroes.Superheroes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import net.mossx.superheroes.Heroes.Powers.FlashPowers;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

import static net.mossx.superheroes.Heroes.Powers.FlashPowers.*;
import static net.mossx.superheroes.Heroes.Powers.HeroPowers.inv.invKey;


public class Flash extends hero implements Listener {
    public static final NamespacedKey speedKey = new NamespacedKey(Superheroes.plugin, "Speed");
    public boolean speedLocked = false;

    @Override
    public void tick(Player p) {
        if (speedLocked) {
            p.removePotionEffect(PotionEffectType.SPEED);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, getSpeed(p), false, false));
            return;
        }
        else if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
            PersistentDataContainer pdc = Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getPersistentDataContainer();
            if (pdc.has(speedKey, PersistentDataType.INTEGER)) {
                p.removePotionEffect(PotionEffectType.SPEED);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, pdc.get(speedKey, PersistentDataType.INTEGER), false, false));
                return;
            }
        }

            if (Objects.requireNonNull(p.getPotionEffect(PotionEffectType.SPEED)).getAmplifier() != 0) {
                p.removePotionEffect(PotionEffectType.SPEED);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 0, false, false));
            }
    }

    @Override
    public void onEnable(Player p) {
        p.openInventory(HeroPowers.inv.getInv(27, FlashPowers.inventory.values()));
        p.setMetadata("speed", new FixedMetadataValue(Superheroes.plugin, 0));
        p.setMetadata("wallrun", new FixedMetadataValue(Superheroes.plugin, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 0, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 99999, 3, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999, 0, false, false));
    }

    @Override
    public void onDisable(Player p) {
        p.removePotionEffect(PotionEffectType.SPEED);
        p.removePotionEffect(PotionEffectType.REGENERATION);
        p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }

    public String getName() {
        return "Flash";
    }

    @Override
    public void playerInteractEvent(PlayerInteractEvent e) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getItem() != null) {
                    ItemStack stack = e.getItem();
                    PersistentDataContainer pdc = e.getItem().getItemMeta().getPersistentDataContainer();
                    if (pdc.has(invKey, PersistentDataType.DOUBLE) && !e.getPlayer().hasCooldown(stack.getType()))
                        FlashPowers.inventory.getByMaterial(e.getMaterial()).run.accept(e.getPlayer());
                }
            }
            else if ((e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) && e.getPlayer().isSneaking()) {
                speedLocked = !speedLocked;
                if (speedLocked) {
                    e.getPlayer().sendMessage("Speed has Been Locked");
                    setSpeed(e.getPlayer(), e.getPlayer().getPotionEffect(PotionEffectType.SPEED).getAmplifier());
                }
                else
                    e.getPlayer().sendMessage("Speed has Been Unlocked");
            }
        }

        private static Location oldLoc;
    @Override
    public void playerMoveEvent(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (getWallrun(p) && (e.getPlayer().getLocation().distance(oldLoc) > .001) && !e.getPlayer().isSneaking()) {
            if (WallRunSub(e.getPlayer(), BlockFace.NORTH));
            else if (WallRunSub(e.getPlayer(), BlockFace.SOUTH));
            else if (WallRunSub(e.getPlayer(), BlockFace.EAST));
            else if (WallRunSub(e.getPlayer(), BlockFace.WEST));
            else return;

            p.setVelocity(p.getEyeLocation().getDirection().multiply(0.75D));
        }
        oldLoc = e.getPlayer().getLocation();
    }

    public static int getSpeed(Player p) {
        return (int)p.getMetadata("speed").get(0).value();
    }

    public static void setSpeed(Player p, int value) {
        p.setMetadata("speed", new FixedMetadataValue(Superheroes.plugin, value));
    }

    public static boolean getWallrun(Player p) {
        return (boolean)p.getMetadata("wallrun").get(0).value();
    }

    public static void setWallrun(Player p, boolean value) {
        p.setMetadata("wallrun", new FixedMetadataValue(Superheroes.plugin, value));
    }





}
