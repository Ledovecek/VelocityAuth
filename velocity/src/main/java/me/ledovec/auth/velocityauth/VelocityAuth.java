package me.ledovec.auth.velocityauth;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.ledovec.auth.velocityauth.events.PlayerListener;
import me.ledovec.auth.velocityauth.commands.LoginCommand;
import me.ledovec.auth.velocityauth.commands.RegisterCommand;
import me.ledovec.auth.velocityauth.session.SessionFactory;
import me.ledovec.auth.velocityauth.session.SessionSecurity;
import me.zort.sqllib.SQLConnectionBuilder;
import me.zort.sqllib.pool.SQLConnectionPool;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Plugin(
        id = "velocity_auth",
        name = "VelocityAuth",
        version = BuildConstants.VERSION
)
public class VelocityAuth {

    @Inject private Logger logger;

    @Inject private ProxyServer proxyServer;

    private final Path dataDirectory;

    @Inject
    public VelocityAuth(@DataDirectory Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public static FileConfiguration CONFIGURATION;

    public static SQLConnectionPool connectionPool;

    public static List<String> authServers;

    public static List<String> lobbyServers;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        try {
            CONFIGURATION = YamlConfiguration.loadConfiguration(new File(dataDirectory + "/config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        authServers = CONFIGURATION.getStringList("auth_servers");
        lobbyServers = CONFIGURATION.getStringList("lobby_servers");

        SQLConnectionPool.Options options = new SQLConnectionPool.Options();
        options.setMaxConnections(1000);
        options.setBorrowObjectTimeout(5000L);
        options.setBlockWhenExhausted(true);

        connectionPool = SQLConnectionBuilder.of(
                CONFIGURATION.getString("mysql.address"),
                CONFIGURATION.getInt("mysql.port"),
                CONFIGURATION.getString("mysql.database"),
                CONFIGURATION.getString("mysql.username"),
                CONFIGURATION.getString("mysql.password"))
                .createPool(options);

        SessionFactory.getInstance().clearAllSessions();
        new SessionSecurity(proxyServer, this).begin();
        proxyServer.getEventManager().register(this, new PlayerListener());
        proxyServer.getCommandManager().register("register", new RegisterCommand(proxyServer), "reg");
        proxyServer.getCommandManager().register("login", new LoginCommand(proxyServer), "log");
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public Logger getLogger() {
        return logger;
    }

}
