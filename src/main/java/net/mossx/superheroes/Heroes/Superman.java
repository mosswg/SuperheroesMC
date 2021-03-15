package net.mossx.superheroes.Heroes;

import net.mossx.superheroes.CommandSuperhero;
import net.mossx.superheroes.Heroes.Powers.HeroPowers;
import net.mossx.superheroes.Heroes.Powers.SupermanPowers;
import net.mossx.superheroes.Superheroes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static net.mossx.superheroes.Heroes.Powers.HeroPowers.inv.invKey;

public class Superman extends Hero {
    static final ArrayList<Player> flyingPlayers = new ArrayList<>();
    SupermanPowers powers = new SupermanPowers();
    boolean singleShift = false;

    @Override
    public void tick(Player p) {
        for (effects e : effects.values()) {
            e.giveEffect(p);
        }
        if (powers.lazer) {
            Damageable d = ((Damageable)Hero.playerLookingAt(p));
            if (d != null) {
                d.setHealth(d.getHealth()-1);
            }
        }
    }

    @Override
    public void onEnable(Player p) {
        p.openInventory(HeroPowers.inv.getInv(27, SupermanPowers.inventory.values()));
        for (effects e : effects.values()) {
            e.giveEffect(p);
        }
    }

    @Override
    public void onDisable(Player p) {
        for (effects e : effects.values()) {
            e.removeEffect(p);
        }
    }

    @Override
    public String getName() {
        return "Superman";
    }


    public void playerInteractEvent(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem() != null) {
                PersistentDataContainer pdc = e.getItem().getItemMeta().getPersistentDataContainer();
                if (pdc.has(invKey, PersistentDataType.DOUBLE) && !e.getPlayer().hasCooldown(e.getItem().getType()))
                    SupermanPowers.inventory.getByMaterial(e.getMaterial()).run(e.getPlayer());
            }
        }
    }

    @Override
    public void playerSneakEvent(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
                if (singleShift) {
                    if (CommandSuperhero.playerHasHero(e.getPlayer(), new smfly())) {
                        e.getPlayer().setGliding(false);
                        flyingPlayers.remove(e.getPlayer());
                        CommandSuperhero.removeHero(e.getPlayer(), new smfly());
                    }
                    else {
                        e.getPlayer().setGliding(true);
                        flyingPlayers.add(e.getPlayer());
                        CommandSuperhero.addHeroToPlayer(e.getPlayer(), new smfly());

                    }
                } else {
                    singleShift = true;
                }
            (new BukkitRunnable() {
                @Override
                public void run() {
                    singleShift = false;
                }
            }).runTaskLater(Superheroes.plugin, 10L);


        }
    }

    public void setLazer(boolean lazer) {
        this.powers.lazer = lazer;
    }

    private enum effects {
        Resistance(PotionEffectType.DAMAGE_RESISTANCE, 2),
        Haste(PotionEffectType.FAST_DIGGING, 30),
        Strength(PotionEffectType.INCREASE_DAMAGE, 10),
        WaterBreathing(PotionEffectType.WATER_BREATHING, 10),
        Speed(PotionEffectType.SPEED, 10),
        Jump(PotionEffectType.JUMP, 2),
        Regen(PotionEffectType.REGENERATION, 10),
        ;


        PotionEffectType type; int amplitude;
        effects(PotionEffectType type, int amplitude) {
            this.type = type; this.amplitude = amplitude;
        }

        public boolean hasEffect(Player p) {
            return p.hasPotionEffect(type);
        }

        public void removeEffect(Player p) {
            p.removePotionEffect(type);
        }

        public void giveEffect(Player p) {
            p.addPotionEffect(new PotionEffect(type, 99999, amplitude, false, false));
        }
    }
    public static class smfly extends Hero implements Listener, CommandExecutor {
        boolean enabled;
        final static double defaultSpeed = 1.5D;
        double speed = defaultSpeed;


        @Override
        public void tick(Player p) {
            p.setVelocity(p.getEyeLocation().getDirection().multiply(speed));
        }

        @Override
        public void onEnable(Player p) {}

        @Override
        public void onDisable(Player p) {}

        public String getName() {
            return "smfly";
        }

        @EventHandler
        public void onPlayerJoinEvent(PlayerJoinEvent event) {
            if (flyingPlayers.contains(event.getPlayer()))
                event.getPlayer().setGliding(true);
        }

        @EventHandler
        public void onEntityToggleGlideEvent(EntityToggleGlideEvent event) {
            if (event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length > 0) {
                if (args[0].equals("enable"))
                    this.enabled = true;
                else if (args[0].equals("disabled"))
                    this.enabled = false;
                else
                    this.enabled = !this.enabled;
            } else
                this.enabled = !this.enabled;
            speed = defaultSpeed;
            if (args.length != 0) {
                speed = Double.parseDouble(args[0]);
            }

            if (this.enabled) {
                ((Player)sender).setGliding(true);
                sender.sendMessage("smfly enabled");
                flyingPlayers.add((Player)sender);
                CommandSuperhero.addHeroToPlayer((Player)sender, this);
            }
            if (!this.enabled) {
                sender.sendMessage("smfly disabled");
                ((Player)sender).setGliding(false);
                flyingPlayers.remove(sender);
                CommandSuperhero.removeHero((Player)sender, this);
            }
            return true;
        }
    }

}
