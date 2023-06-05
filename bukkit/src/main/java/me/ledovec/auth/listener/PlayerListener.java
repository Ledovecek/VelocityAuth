package me.ledovec.auth.listener;

import me.ledovec.auth.events.AuthSessionExpiredEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.concurrent.CompletableFuture;

public class PlayerListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            Bukkit.getPluginManager().callEvent(new AuthSessionExpiredEvent(player));
        });
    }

    @EventHandler
    public void onAuthExpire(AuthSessionExpiredEvent e) {
        boolean isOnline = e.getPlayer().isOnline();
        if (isOnline) {
            e.cancelLoginSession();
        }
    }

}
