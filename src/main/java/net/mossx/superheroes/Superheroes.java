package net.mossx.superheroes;

import net.mossx.superheroes.Heroes.Powers.FlashPowers;
import net.mossx.superheroes.Heroes.Powers.HeroPowers;
import net.mossx.superheroes.Heroes.Superman;
import net.mossx.superheroes.Heroes.Hero;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;

import static net.mossx.superheroes.CommandSuperhero.playerHasHero;


public final class Superheroes extends JavaPlugin implements Listener {
    public static Superheroes plugin;

    @Override
    public void onEnable() {
        System.out.println("[Superheroes] Started Up");
        this.getCommand("Superhero").setExecutor(new CommandSuperhero());
        this.getCommand("Superhero").setTabCompleter(new CommandSuperhero());
        this.getCommand("Potions").setExecutor(new Potions());
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CommandSuperhero(), this);
        getServer().getPluginManager().registerEvents(new Superman.smfly(), this);

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            for (Player p : players) {
                for (Hero h : CommandSuperhero.getPlayerHeroes(p)) {
                    h.tick(p);
                }
            }
        }, 0L, 1L);
        plugin = this;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        for (Hero h : CommandSuperhero.getPlayerHeroes(e.getPlayer())) {
            h.playerInteractEvent(e);
        }
    }

    @EventHandler
    public void onPlayerPlace(BlockPlaceEvent event) {
        if (HeroPowers.inv.isInventory(event.getItemInHand()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (FlashPowers.freeze && !playerHasHero(event.getPlayer(), CommandSuperhero.heroes.flash.ref)) {
            Location to = event.getFrom();
            to.setPitch(event.getTo().getPitch());
            to.setYaw(event.getTo().getYaw());
            event.setTo(to);
            return;
        }
        for (Hero h : CommandSuperhero.getPlayerHeroes(event.getPlayer())) {
                h.playerMoveEvent(event);
            }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent e) {
            for (Hero h : CommandSuperhero.getPlayerHeroes(e.getPlayer())) {
                h.playerSneakEvent(e);
            }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (e.getEntity().hasMetadata("DeathMessage"))
            e.setDeathMessage(((String)e.getEntity().getMetadata("DeathMessaege").get(0).value()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {}


    /**
     * Sends Given Message to My Minecraft Account(mossx) for debugging
     * @deprecated
     * @param message
     */
    public static void sendMossxMessage(String message) {
        if (Bukkit.getPlayerExact("mossx") != null)
            Bukkit.getPlayerExact("mossx").sendMessage(message);
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (Hero h : CommandSuperhero.getPlayerHeroes(p)) {
                h.onDisable(p);
                }
            }
        }
    }
