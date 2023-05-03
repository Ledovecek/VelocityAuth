package me.ledovec.velocityauth.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.ledovec.velocityauth.VelocityAuth;
import me.ledovec.velocityauth.constants.Strings;
import me.ledovec.velocityauth.constants.Titles;
import me.ledovec.velocityauth.security.Security;
import me.ledovec.velocityauth.session.SessionFactory;
import me.zort.sqllib.SQLDatabaseConnection;
import me.zort.sqllib.api.data.QueryResult;
import me.zort.sqllib.api.data.Row;
import net.kyori.adventure.text.Component;

import java.sql.SQLException;
import java.util.Optional;

public class RegisterCommand implements SimpleCommand {

    private final ProxyServer proxyServer;

    public RegisterCommand(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] arguments = invocation.arguments();
        if (source instanceof Player) {
            Player player = (Player) source;
            if (arguments.length > 1) {
                SQLDatabaseConnection resource;
                if (arguments[0].equals(arguments[1])) {
                    try {
                        resource = VelocityAuth.connectionPool.getResource();
                        Optional<Row> result = resource.select("id").from("player_credentials").where().isEqual("player", player.getUsername()).obtainOne();
                        if (result.isEmpty()) {
                            String secret = Security.hashPassword(arguments[0]);
                            QueryResult execute = resource.insert().into("player_credentials", "player", "secret").values(player.getUsername(), secret).execute();
                            resource.close();
                            if (execute.isSuccessful()) {
                                source.sendMessage(Component.text(Strings.PREFIX + "§aYou have been successfully registered."));
                                Titles.sendRedirectTitle(player);
                                SessionFactory.getInstance().createPlayerSession(player);
                                player.createConnectionRequest(proxyServer.getServer("lobby").get()).connect();
                            }
                        } else {
                            player.sendMessage(Component.text(Strings.PREFIX + "You are already registered."));
                            player.sendMessage(Component.text("§fUse: §e/login <password>"));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else source.sendMessage(Component.text(Strings.PREFIX + "§cPasswords don't match!"));
            } else {
                source.sendMessage(Component.text(Strings.PREFIX + "§cIncorrect command usage:"));
                source.sendMessage(Component.text("§fUse: §e/register <password> <password>"));
            }
        }

    }

}
