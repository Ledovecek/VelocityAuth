package me.ledovec.auth.velocityauth.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.ledovec.auth.velocityauth.VelocityAuth;
import me.ledovec.auth.velocityauth.constants.Strings;
import me.ledovec.auth.velocityauth.constants.Titles;
import me.ledovec.auth.velocityauth.security.Security;
import me.ledovec.auth.velocityauth.session.SessionFactory;
import me.zort.sqllib.SQLDatabaseConnection;
import me.zort.sqllib.api.data.QueryResult;
import me.zort.sqllib.api.data.Row;
import net.kyori.adventure.text.Component;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

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
            String ip = player.getRemoteAddress().getAddress().getHostAddress();
            if (arguments.length > 1) {
                SQLDatabaseConnection resource;
                if (arguments[0].equals(arguments[1])) {
                    try {
                        resource = VelocityAuth.connectionPool.getResource();
                        Optional<Row> result = resource.select("id").from("player_credentials").where().isEqual("player", player.getUsername()).obtainOne();
                        if (result.isEmpty()) {
                            SecureRandom random = new SecureRandom();
                            byte[] salt = new byte[16];
                            random.nextBytes(salt);
                            String saltString = Base64.getEncoder().encodeToString(salt);
                            String secret = Security.hashPassword(arguments[0], saltString);
                            QueryResult execute = resource.insert().into("player_credentials", "player", "secret", "reg_ip", "salt")
                                    .values(player.getUsername(), secret, ip, saltString).execute();
                            resource.close();
                            if (execute.isSuccessful()) {
                                source.sendMessage(Component.text(Strings.PREFIX + "§aYou have been successfully registered."));
                                Titles.sendRedirectTitle(player);
                                SessionFactory.getInstance().createPlayerSession(player);
                                String randomLobby = VelocityAuth.lobbyServers.get(new Random().nextInt(VelocityAuth.lobbyServers.size()));
                                player.createConnectionRequest(proxyServer.getServer(randomLobby).get()).connect();
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
