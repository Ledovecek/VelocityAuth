package me.ledovec.velocityauth.commands;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.ledovec.velocityauth.VelocityAuth;
import me.ledovec.velocityauth.constants.Strings;
import me.ledovec.velocityauth.security.Security;
import me.zort.sqllib.SQLDatabaseConnection;
import me.zort.sqllib.api.data.Row;
import net.kyori.adventure.text.Component;

import java.sql.SQLException;
import java.util.Optional;

public class LoginCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] arguments = invocation.arguments();
        if (source instanceof Player) {
            Player player = (Player) source;
            if (arguments.length > 0) {
                String username = player.getUsername();
                SQLDatabaseConnection resource;
                try {
                    resource = VelocityAuth.connectionPool.getResource();
                    Optional<Row> result = resource.select("secret").from("player_credentials").where().isEqual("player", username).obtainOne();
                    if (result.isPresent()) {
                        boolean match = Security.passwordsMatch(arguments[0], result.get().getString("secret"));
                        if (match) {
                            player.sendMessage(Component.text(Strings.PREFIX + "§aSuccessfully logged in!"));
                            player.createConnectionRequest(new VelocityAuth().getProxyServer().getServer("lobby").get()).connect();
                        } else {
                            player.sendMessage(Component.text(Strings.PREFIX + "§cIncorrect password!"));
                        }
                    } else {
                        player.sendMessage(Component.text(Strings.PREFIX + "§cYou are not registered."));
                        player.sendMessage(Component.text("§fUse: §e/register <password> <password>"));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                source.sendMessage(Component.text(Strings.PREFIX + "§cIncorrect command usage:"));
                source.sendMessage(Component.text("§fUse: §e/login <password>"));
            }
        }
    }

}
