package me.ledovec.velocityauth.session;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import me.ledovec.velocityauth.VelocityAuth;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class SessionSecurity {

    private final ProxyServer proxyServer;
    private final VelocityAuth plugin;

    public SessionSecurity(ProxyServer proxyServer, VelocityAuth plugin) {
        this.proxyServer = proxyServer;
        this.plugin = plugin;
    }

    public void begin() {
        CompletableFuture.runAsync(() -> proxyServer.getScheduler().buildTask(plugin, () -> proxyServer.getAllPlayers().forEach(player -> {
            Optional<ServerConnection> currentServer = player.getCurrentServer();
            if (currentServer.isPresent()) {
                ServerConnection server = currentServer.get();
                if (!server.getServerInfo().getName().contains("auth")) {
                    PlayerSession playerSession = SessionFactory.getInstance().getPlayerSession(player, true);
                    if (playerSession == null) {
                        player.disconnect(Component.text("§cInvalid authentication session."));
                    }
                }
            }
        })).repeat(500, TimeUnit.MILLISECONDS).schedule());
    }

}
