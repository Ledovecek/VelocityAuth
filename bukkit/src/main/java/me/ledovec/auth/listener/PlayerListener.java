package me.ledovec.auth.listener;

import me.ledovec.auth.events.AuthSessionExpiredEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.nio.Buffer;

public class PlayerListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.teleport(new Location(Bukkit.getWorld("auth"), 0.5, 160, 0.5, 0, 0));
        e.joinMessage(Component.empty());
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onAuthExpire(AuthSessionExpiredEvent e) {
        boolean isOnline = e.getPlayer().isOnline();
        if (isOnline) {
            e.cancelLoginSession();
        }
    }

}
