package me.ledovec.auth.velocityauth;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import me.ledovec.auth.velocityauth.events.PlayerListener;
import me.ledovec.auth.velocityauth.commands.LoginCommand;
import me.ledovec.auth.velocityauth.commands.RegisterCommand;
import me.ledovec.auth.velocityauth.session.SessionFactory;
import me.ledovec.auth.velocityauth.session.SessionSecurity;
import me.zort.sqllib.SQLConnectionBuilder;
import me.zort.sqllib.pool.SQLConnectionPool;
import org.slf4j.Logger;

import java.io.File;

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
    public void onProxyInitialization(ProxyInitializeEvent event) {
        configFile = new File("config.yml");

        SQLConnectionPool.Options options = new SQLConnectionPool.Options();
        options.setMaxConnections(1000);
        options.setBorrowObjectTimeout(5000L);
        options.setBlockWhenExhausted(true);

        connectionPool = SQLConnectionBuilder.of("localhost", 3306, "velocity_auth", "root", "")
                        .createPool(options);

        SessionFactory.getInstance().clearAllSessions();
        new SessionSecurity(proxyServer, this).begin();
        proxyServer.getEventManager().register(this, new PlayerListener());
        proxyServer.getCommandManager().register("register", new RegisterCommand(proxyServer), "reg");
        proxyServer.getCommandManager().register("login", new LoginCommand(proxyServer), "log");
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
