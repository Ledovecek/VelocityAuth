package me.ledovec.auth.velocityauth.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import me.ledovec.auth.velocityauth.VelocityAuth;
import me.ledovec.auth.velocityauth.constants.Strings;
import me.ledovec.auth.velocityauth.constants.Titles;
import me.ledovec.auth.velocityauth.session.PlayerSession;
import me.ledovec.auth.velocityauth.session.SessionFactory;
import me.zort.sqllib.SQLDatabaseConnection;
import me.zort.sqllib.api.data.Row;
import net.kyori.adventure.text.Component;

import java.sql.SQLException;
import java.util.Optional;

public class PlayerListener {

    @Subscribe
    public void onConnect(PostLoginEvent event) {
        Player player = event.getPlayer();
        player.clearTitle();
        player.sendMessage(Component.text(Strings.PREFIX + "Successfully established connection with auth server."));
        try {
            SQLDatabaseConnection resource = VelocityAuth.connectionPool.getResource();
            Optional<Row> result = resource.select("id").from("player_credentials").where().isEqual("player", player.getUniqueId().toString()).obtainOne();
            resource.close();
            if (result.isPresent()) {
                player.sendMessage(Component.text(Strings.PREFIX + "Login using §e/login <password>"));
                Titles.showLoginTitle(player);
            } else {
                player.sendMessage(Component.text(Strings.PREFIX + "Register using §e/register <password> <password>"));
                Titles.showRegisterTitle(player);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        PlayerSession playerSession = SessionFactory.getInstance().getPlayerSession(event.getPlayer(), false);
        if (playerSession != null) {
            playerSession.log();
            SessionFactory.getInstance().cancelPlayerSession(event.getPlayer());
        }
        event.getPlayer().clearTitle();
    }

    @Subscribe
    public void onChat(PlayerChatEvent e) {
        Player player = e.getPlayer();
        if (player.getCurrentServer().isPresent()) {
            ServerConnection serverConnection = player.getCurrentServer().get();
            String name = serverConnection.getServerInfo().getName();
            if (VelocityAuth.authServers.contains(name)) e.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }

}
