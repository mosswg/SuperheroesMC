package net.mossx.superheroes;

import net.mossx.superheroes.Heroes.Powers.FlashPowers;
import net.mossx.superheroes.Heroes.Superman;
import net.mossx.superheroes.Heroes.hero;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import java.util.ArrayList;
import java.util.UUID;

import static net.mossx.superheroes.CommandSuperhero.playerHasHero;
import static net.mossx.superheroes.CommandSuperhero.playerHeroes;


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
        for (Player p : Bukkit.getOnlinePlayers()) {
            playerHeroes.put(p.getUniqueId(), new ArrayList<>());
            p.setMetadata("speed", new FixedMetadataValue(this, 0));
            p.setMetadata("wallrun", new FixedMetadataValue(this, false));
        }

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            Object[] players = playerHeroes.keySet().toArray();
            int i = 0;
            for (ArrayList<hero> ar : playerHeroes.values()) {
                for (hero h : ar) {
                    h.tick(Bukkit.getPlayer((UUID)players[i]));
                }
                i++;
            }
        }, 0L, 1L);
        plugin = this;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        for (ArrayList<hero> ar : playerHeroes.values()) {
            for (hero h : ar) {
                    h.playerInteractEvent(e);
                }
            }
        }

    @EventHandler
    public void onPlayerPlace(BlockPlaceEvent event) {
        if (hero.inv.isInventory(event.getItemInHand()))
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
        for (ArrayList<hero> ar : playerHeroes.values()) {
            for (hero h : ar) {
                h.playerMoveEvent(event);
            }
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent e) {
        for (ArrayList<hero> ar : playerHeroes.values()) {
            for (Object h : ar.toArray()) {
                ((hero)h).playerSneakEvent(e);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.getPlayer().setMetadata("speed", new FixedMetadataValue(this, 0));
        e.getPlayer().setMetadata("wallrun", new FixedMetadataValue(this, false));
        playerHeroes.put(e.getPlayer().getUniqueId(), new ArrayList<>());
    }

    public static void sendMossxMessage(String message) {
        if (Bukkit.getPlayerExact("mossx") != null)
            Bukkit.getPlayerExact("mossx").sendMessage(message);
    }

    @Override
    public void onDisable() {
        for (int i = 0; i < playerHeroes.size(); i++) {
            for (ArrayList<hero> ar : playerHeroes.values()) {
                for (Object h : ar.toArray()) {
                    ((hero) h).onDisable(((Bukkit.getPlayer((UUID)playerHeroes.keySet().toArray()[i]))));
                }
            }
        }
    }
}
