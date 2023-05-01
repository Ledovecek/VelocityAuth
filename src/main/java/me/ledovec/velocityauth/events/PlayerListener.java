package me.ledovec.velocityauth.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import me.ledovec.velocityauth.VelocityAuth;
import me.ledovec.velocityauth.constants.Strings;
import me.zort.sqllib.SQLDatabaseConnection;
import me.zort.sqllib.api.data.Row;
import net.kyori.adventure.text.Component;

import java.sql.SQLException;
import java.util.Optional;

public class PlayerListener {

    @Subscribe
    public void onConnect(PostLoginEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Component.text(Strings.PREFIX + "Successfully established connection with auth server."));
        try {
            SQLDatabaseConnection resource = VelocityAuth.connectionPool.getResource();
            Optional<Row> result = resource.select("id").from("player_credentials").where().isEqual("player", player.getUsername()).obtainOne();
            if (result.isPresent()) {
                player.sendMessage(Component.text(Strings.PREFIX + "Login using §e/login <password>"));
            } else {
                player.sendMessage(Component.text(Strings.PREFIX + "Register using §e/register <password> <password>"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
