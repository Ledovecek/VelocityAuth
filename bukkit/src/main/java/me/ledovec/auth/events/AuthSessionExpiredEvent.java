package me.ledovec.auth.events;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AuthSessionExpiredEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private Player player;

    public AuthSessionExpiredEvent(Player player) {
        this.player = player;
    }

    public void cancelLoginSession() {
        player.kick(Component.text("§cAuth session expired"));
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

}
