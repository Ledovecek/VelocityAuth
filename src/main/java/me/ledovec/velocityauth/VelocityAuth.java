package me.ledovec.velocityauth;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import me.ledovec.velocityauth.commands.LoginCommand;
import me.ledovec.velocityauth.commands.RegisterCommand;
import me.ledovec.velocityauth.events.PlayerListener;
import me.zort.sqllib.SQLConnectionBuilder;
import me.zort.sqllib.SQLDatabaseConnection;
import me.zort.sqllib.pool.SQLConnectionPool;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.sql.ConnectionBuilder;
import java.sql.SQLException;

@Plugin(
        id = "velocity_auth",
        name = "VelocityAuth",
        version = BuildConstants.VERSION
)
public class VelocityAuth {

    @Inject private Logger logger;
    @Inject private ProxyServer proxyServer;
    private File configFile;

    public static SQLConnectionPool connectionPool;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws SQLException {
        configFile = new File("config.yml");
        SQLConnectionBuilder template = SQLConnectionBuilder.of("localhost", 3306, "velocity_auth", "root", "");

        SQLConnectionPool.Options options = new SQLConnectionPool.Options();
        options.setMaxConnections(10);
        options.setBorrowObjectTimeout(5000L);
        options.setBlockWhenExhausted(true);

        connectionPool = new SQLConnectionPool(template, options);
        SQLDatabaseConnection resource = connectionPool.getResource();

        if (!resource.connect()) {
            logger.error("Could not connect to the database!");
        }
        proxyServer.getEventManager().register(this, new PlayerListener());
        proxyServer.getCommandManager().register("register", new RegisterCommand(), "reg");
        proxyServer.getCommandManager().register("login", new LoginCommand(), "log");
    }

    public File getConfigFile() {
        return configFile;
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public Logger getLogger() {
        return logger;
    }

}
