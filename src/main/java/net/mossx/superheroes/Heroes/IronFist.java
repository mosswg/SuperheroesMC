package net.mossx.superheroes.Heroes;

import net.mossx.superheroes.Heroes.Powers.HeroPowers;
import net.mossx.superheroes.Heroes.Powers.IronFistPowers;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IronFist extends Hero {
    private boolean toggle = false;

    @Override
    public void tick(Player p) {
        for (effects e : effects.values()) {
                e.giveEffect(p);
        }
    }

    public void playerSneakEvent(PlayerToggleSneakEvent e) {
        if (toggle) {
            e.getPlayer().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        }
        else
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999, 3, false, false));
        toggle = !toggle;
    }

    @Override
    public void onEnable(Player p) {
        p.openInventory(HeroPowers.inv.getInv(27, IronFistPowers.inventory.values()));
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
        return "Iron Fist";
    }


    private enum effects {
        Strength(PotionEffectType.INCREASE_DAMAGE, 1),
        Speed(PotionEffectType.SPEED, 0),
        Haste(PotionEffectType.FAST_DIGGING, 3)
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
}
