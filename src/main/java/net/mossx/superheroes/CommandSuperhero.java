package net.mossx.superheroes;

import net.mossx.superheroes.Heroes.Flash;
import net.mossx.superheroes.Heroes.IronFist;
import net.mossx.superheroes.Heroes.Superman;
import net.mossx.superheroes.Heroes.hero;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;


public class CommandSuperhero implements CommandExecutor, TabCompleter, Listener {
    public static Player activePlayer;
    private boolean enable;
    private boolean setEnabled;

    public static hero getHero(String heroName) {
        for (heroes hero : heroes.values()) {
            if (heroName.equalsIgnoreCase(hero.name())) {
                return hero.ref.clone();
            }
        }
        return null;
    }

    public static ArrayList<hero> getPlayerHeroes(Player p) {
        if (!p.hasMetadata("Heroes")) {
            p.setMetadata("Heroes", new FixedMetadataValue(Superheroes.plugin, new ArrayList<hero>()));
        }
        return (ArrayList<hero>)p.getMetadata("Heroes").get(0).value();
        //return playerHeroes.get(p.getUniqueId());
    }

    public static void removeHero(Player playerName, hero h) {
        for (Object o : getPlayerHeroes(playerName)) {
            if (h.getClass() == o.getClass())
                playerName.getMetadata("Heroes").remove(o);
        }
    }
    public static boolean playerHasHero(Player playerName, hero h) {
        for (Object o : getPlayerHeroes(playerName)) {
            if (h.getClass() == o.getClass())
                return true;
        }
        return false;
    }
    public static void addHeroToPlayer(Player playerPointer, hero h) {
        if (playerPointer.hasMetadata("Heroes"))
            if (!playerHasHero(playerPointer, h))
                ((ArrayList<hero>)(playerPointer.getMetadata("Heroes").get(0).value())).add(h);
        else {
            playerPointer.setMetadata("Heroes", new FixedMetadataValue(Superheroes.plugin, new ArrayList<>(Collections.singletonList(h))));
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        String heroName;
        if (commandSender instanceof Player) {
            activePlayer = (Player) commandSender;
        }

        if (args.length == 0) {
            if (activePlayer != null) {
                activePlayer.openInventory(heroMenu());
                return true;
            } else {
                System.out.println("Superhero: Please Enter Hero Name for Command");
                return false;
            }
        }
        else if (args[args.length-1].equals("enable")) {
            enable = true;
            setEnabled = true;
        }
        else if (args[args.length-1].equals("disable")) {
            enable = false;
            setEnabled = true;
        }
        else if (args[0].equals("list")) {
            if (commandSender instanceof Player) {
                for (hero h : getPlayerHeroes((Player)commandSender))
                    commandSender.sendMessage(h.toString());
                    return true;
            }
        }


        if (args.length == 1) {
                if (setEnabled) {
                    activePlayer.openInventory(heroMenu());
                    return true;
                }
                if (activePlayer == null) {
                    System.out.println("Superhero: Please Enter Player and Hero Name for Command");
                    return false;
                } else
                    heroName = args[0];
        }
        else {
                activePlayer = Bukkit.getPlayerExact(args[0]);
                if (activePlayer == null && !setEnabled) {
                    commandSender.sendMessage("Superhero: Invalid Player Name");
                    return false;
                }
                if (getHero(args[0]) == null)
                    heroName = args[1];
                else
                    heroName = args[0];


                if (activePlayer == null) {
                    if (commandSender instanceof Player) {
                        activePlayer = ((Player) commandSender);
                        heroName = args[0];
                    } else {
                        System.out.println("Superhero: Invalid Player Name");
                        return false;
                    }
                }
        }


        hero h = getHero(heroName);

        if (h == null) {
            if (activePlayer != null) {
                activePlayer.sendMessage("Superhero: Invalid Superhero name");
                return false;
            } else {
                System.out.println("Superhero: Invalid Superhero name");
                return false;
            }
        }
        else {
            if (!setEnabled)
                enable = !playerHasHero(activePlayer, h);

            if (enable) {
                h.onEnable(activePlayer);
                addHeroToPlayer(activePlayer, h);
                activePlayer.sendMessage("Superhero: Added " + h.getName());
            } else {
                h.onDisable(activePlayer);
                removeHero(activePlayer, h);
                activePlayer.sendMessage("Superhero: Removed " + h.getName());
            }
        }
        setEnabled = false;
        activePlayer = null;
        return true;

    }
    public Inventory heroMenu() {
        Inventory inv = Bukkit.createInventory(null, 9, "Select Hero");

        for (heroes h : heroes.values())
            inv.setItem(h.pos, h.getItem());

        return inv;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null) {
            if (event.getView().getTitle().equals("Select Hero") && Arrays.equals(event.getClickedInventory().getContents(), heroMenu().getContents())) {
                event.setCancelled(true);
                hero h = getHero(event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Superheroes.plugin, "Hero"), PersistentDataType.STRING));
                if (h == null)
                    if (activePlayer != null) {
                        activePlayer.sendMessage("Superhero: Invalid Superhero name");
                    } else {
                        System.out.println("Superhero: Invalid Superhero name");
                    }
                else {
                    if (!setEnabled)
                        enable = !playerHasHero((Player) event.getWhoClicked(), h);
                    event.getWhoClicked().closeInventory();
                    if (!enable) {
                        h.onDisable(activePlayer);
                        removeHero(activePlayer, h);
                        activePlayer.sendMessage("Superhero: Removed " + h.getName());
                    } else {
                        h.onEnable(activePlayer);
                        addHeroToPlayer(activePlayer, h);
                        activePlayer.sendMessage("Superhero: Added " + h.getName());
                    }
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args){
            if(sender instanceof Player){
                List<String> list = new ArrayList<>();
                if (args.length == 1) {
                    list.add("list");
                    for (Object o : Bukkit.getOnlinePlayers().toArray())
                        list.add(((Player) o).getName());
                }
                else {
                    list.add("enable");
                    list.add("disable");
                }


                if (args.length == 1 || args.length == 2) {
                    for (heroes h : heroes.values())
                        list.add(h.name());
                }


                return list;
            }
            return null;
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {}

    public enum heroes{
        flash(new Flash(), 0, Material.HONEYCOMB, ChatColor.DARK_RED + "Flash"),
        ironfist(new IronFist(), 2, Material.IRON_INGOT, ChatColor.GRAY + "Iron Fist"),
        superman(new Superman(), 4, Material.ANCIENT_DEBRIS, ChatColor.RED + "Superman"),
        ;

        hero ref;
        int pos;
        Material stackType;
        String name;
        heroes(hero reference, int position, Material stackType, String name) {
            this.ref = reference;
            this.pos = position;
            this.stackType = stackType;
            this.name = name;
        }


        ItemStack getItem() {
            ItemStack item = new ItemStack(stackType);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(name);
            hero.inv.addTag(im, "Hero", PersistentDataType.STRING, this.name());
            item.setItemMeta(im);
            return item;
        }


    }
}