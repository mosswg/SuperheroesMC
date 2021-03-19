package net.mossx.superheroes.Heroes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.mossx.superheroes.Superheroes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Spiderman extends Hero{
        private final Set<ArrowVelocity> arrows = new HashSet<>();
        private static final double gravityConstant = 100;

    @Override
    public void tick(Player p) {
        if (hasSwingDir(p)) {
            if (p.getLocation().getWorld() != getSwingPos(p).getWorld()) {
                removeSwingPos(p);
                removeSwingDir(p);
            }
            Vector vel = getVelocity(p);
            if (vel != null)
                p.setVelocity(vel);
            else
                p.sendMessage("ERROR: Velocity Is Null");
        }


        Iterator<ArrowVelocity> iterator = arrows.iterator();
        while (iterator.hasNext()) {
            ArrowVelocity next = iterator.next();
            if (next.getArrow().isDead() || !next.getArrow().isValid()) {
                iterator.remove();
                continue;
            }
            if (next.getArrow().getVelocity().equals(next.getVelocity())) {
                Bukkit.getServer().getPluginManager().callEvent(new ProjectileHitEvent(next.getArrow()));
                continue;
            }
            next.setVelocity(next.getArrow().getVelocity());
        }
    }

    public void onEnable(Player p) {}

    @Override
    public void onDisable(Player p) {}

    @Override
    public String getName() {
        return "Spiderman";
    }


        @EventHandler(priority = EventPriority.MONITOR)
        public void onEntityDamageEvent(EntityDamageEvent event) {
            if (event.getEntity() instanceof Player) {
                if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
                    event.setDamage(event.getFinalDamage() / 3.5D);
            } else if (event.getEntity() instanceof Bat) {
                if (((Bat)event.getEntity()).hasPotionEffect(PotionEffectType.INVISIBILITY))
                    event.setCancelled(true);
            }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        public void onGrapple(PlayerGrappleEvent event) {
            Player player = event.getPlayer();
            player.sendMessage("Grapple Event");
            Entity entity = event.getPulledEntity();
            Location location = event.getPullLocation();
            if (player.equals(entity) && hasSwingDir(player)) {
                player.setFallDistance(-35.0F);
            } else {
                setSwingDir(player, player.getEyeLocation().getDirection());
                setSwingPos(player, location);
            }
            location.getWorld().playSound(location, Sound.ENTITY_MAGMA_CUBE_JUMP, 10.0F, 1.0F);
            player.setFallDistance(0.0F);
        }

        @EventHandler
        public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Arrow && event.getDamager().hasMetadata("arrow"))
                event.setCancelled(true);
        }

        @EventHandler
        public void onProjHit(ProjectileHitEvent e) {
            if (e.getEntity() instanceof Arrow) {
                Arrow arrow = (Arrow)e.getEntity();
                this.arrows.remove(arrow);
                if (arrow.getShooter() instanceof Player) {
                    ((Player) arrow.getShooter()).sendMessage("Arrow Landed at " + arrow.getLocation());
                    Player p = (Player)arrow.getShooter();
                    if (p.getInventory().getItemInMainHand().getType() != Material.BOW)
                        arrow.remove();
                    if (!arrow.hasMetadata("arrow"))
                        return;
                    Entity found = null;
                    for (Entity en : arrow.getNearbyEntities(0.1D, 1.0D, 0.1D)) {
                        if (!(en instanceof Arrow)) {
                            found = en;
                            break;
                        }
                    }
                    for (Entity en : p.getNearbyEntities(4.0D, 4.0D, 4.0D)) {
                        if (en instanceof Bat) {
                            ((Bat)en).setLeashHolder(null);
                            en.remove();
                            break;
                        }
                    }
                    if (!hasSwingDir(p) && !p.getInventory().getItemInMainHand().getType().equals(Material.BOW))
                        if (found == null) {
                            Location loc = arrow.getLocation();
                            for (Entity ent : arrow.getNearbyEntities(1.5D, 1.0D, 1.5D)) {
                                if (ent instanceof org.bukkit.entity.Item) {
                                    PlayerGrappleEvent playerGrappleEvent = new PlayerGrappleEvent(p, ent, p.getLocation());
                                    Bukkit.getServer().getPluginManager().callEvent(playerGrappleEvent);
                                    return;
                                }
                            }
                            PlayerGrappleEvent event = new PlayerGrappleEvent(p, p, loc);
                            Bukkit.getServer().getPluginManager().callEvent(event);
                        } else if (found != null) {
                            if (found instanceof Player) {
                                Player hooked = (Player)found;
                                PlayerGrappleEvent event = new PlayerGrappleEvent(p, hooked, p.getLocation());
                                Bukkit.getServer().getPluginManager().callEvent(event);
                            } else {
                                PlayerGrappleEvent event = new PlayerGrappleEvent(p, found, p.getLocation());
                                Bukkit.getServer().getPluginManager().callEvent(event);
                            }
                        }
                }
            }
        }

        @Override
        public void playerInteractEvent(PlayerInteractEvent e) {
            final Player p = e.getPlayer();
            if (!hasSwingDir(p) && !hasSwingPos(p)) {
                Action a = e.getAction();
                if ((a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) && e.getPlayer().getInventory().getItemInMainHand().getType() == Material.WHITE_DYE) {
                    Vector direction = p.getEyeLocation().getDirection().multiply(4);
                    Arrow arrow = p.getWorld().spawn(e.getPlayer().getEyeLocation().add(direction.multiply(1.5D)), Arrow.class);
                    this.arrows.add(new ArrowVelocity(arrow));
                    arrow.setMetadata("arrow", new FixedMetadataValue(Superheroes.plugin, true));
                    arrow.setVelocity(direction);
                    arrow.setShooter(p);
                    final Bat bat = p.getWorld().spawn(p.getEyeLocation(), Bat.class);
                    bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 100000, true, false));
                    bat.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 100000, true, false));
                    bat.setLeashHolder(arrow);
                    (new BukkitRunnable() {
                        public void run() {
                            if (p != null && p.isOnline() && bat != null && bat.isValid() && !bat.isDead() && bat.isLeashed()) {
                                bat.teleport(p.getEyeLocation());
                            } else {
                                cancel();
                                if (bat != null && !bat.isDead())
                                    bat.remove();
                            }
                        }
                    }).runTaskTimer(Superheroes.plugin, 1L, 1L);
                }
            } else if (hasSwingDir(p)) {
                removeSwingDir(p);
                removeSwingPos(p);
                p.setVelocity(p.getEyeLocation().getDirection());
                for (Entity en : p.getNearbyEntities(20.0D, 30.0D, 20.0D)) {
                    if (en instanceof Bat) {
                        ((Bat)en).setLeashHolder(null);
                        en.remove();
                    }
                }
            }
        }
        private static Vector getSwingDir(Player player) {
            return (Vector)player.getMetadata("swingDirection").get(0).value();
        }

        private static boolean hasSwingDir(Player player) {
            return player.hasMetadata("swingDirection");
        }
        private static void setSwingDir(Player player, Vector Vect) {
            player.setMetadata("swingDirection", new FixedMetadataValue(Superheroes.plugin, Vect));
        }
        private static void removeSwingDir(Player player) {
            player.removeMetadata("swingDirection", Superheroes.plugin);
        }
    private static Location getSwingPos(Player player) {
        return (Location) player.getMetadata("swingPosition").get(0).value();
    }
    private static boolean hasSwingPos(Player player) {
        return player.hasMetadata("swingPosition");
    }
    private static void setSwingPos(Player player, Location Vect) {
        player.setMetadata("swingPosition", new FixedMetadataValue(Superheroes.plugin, Vect));
    }
    private static void removeSwingPos(Player player) {
        player.removeMetadata("swingPosition", Superheroes.plugin);
    }


        private static double getAngle(Location start, Location end) {
        Vector comp = new Vector(end.getX(), start.getY(), end.getZ());
        double a = start.toVector().distance(comp);
        double b = start.distance(end);
        double c = end.getY() - start.getY();
        return Math.acos((Math.pow(a,2) - Math.pow(b, 2) - Math.pow(c, 2))/(-2*b*c));
        }

        private static Vector getVelocity(Player p) {
        if (!hasSwingDir(p) || !hasSwingPos(p))
            return null;
        Vector dir = getSwingDir(p);
        Location swingLocation = getSwingPos(p);
        if (dir == null || swingLocation == null)
            return null;
        double l = swingLocation.distance(p.getLocation());
        double angle = getAngle(swingLocation, p.getLocation());

        p.sendMessage("Angle: " + Math.toDegrees(angle));

        angle = (-gravityConstant * Math.sin(angle))/l;

        double x = l * Math.sin(angle);
        double y = l * Math.cos(angle);


        return new Vector(dir.getX()*(1+x), y, dir.getZ()*(1+x));
        }






        public static class ArrowVelocity {
            private Arrow arrow;

            private Vector velocity;

            public ArrowVelocity(Arrow arrow) {
                this.arrow = arrow;
                this.velocity = arrow.getVelocity();
            }

            public Arrow getArrow() {
                return this.arrow;
            }

            public Vector getVelocity() {
                return this.velocity;
            }

            public void setVelocity(Vector velocity) {
                this.velocity = velocity;
            }
        }
    public static class PlayerGrappleEvent extends Event implements Cancellable {
        private static final HandlerList handlers = new HandlerList();

        private final Player player;

        private final Entity entity;

        private final Location pullLocation;

        private final ItemStack hookItem;

        private boolean cancelled = false;

        public PlayerGrappleEvent(Player player, Entity entity, Location location) {
            this.player = player;
            this.entity = entity;
            this.pullLocation = location;
            this.hookItem = player.getInventory().getItemInMainHand();
        }

        public Player getPlayer() {
            return this.player;
        }

        public Entity getPulledEntity() {
            return this.entity;
        }

        public Location getPullLocation() {
            return this.pullLocation;
        }

        public ItemStack getHookItem() {
            return this.hookItem;
        }

        public HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }

        public boolean isCancelled() {
            return this.cancelled;
        }

        public void setCancelled(boolean set) {
            this.cancelled = set;
        }
    }

}
