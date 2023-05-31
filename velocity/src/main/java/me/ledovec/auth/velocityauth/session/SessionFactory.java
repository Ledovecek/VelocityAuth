package me.ledovec.auth.velocityauth.session;

import com.velocitypowered.api.proxy.Player;
import me.ledovec.auth.velocityauth.VelocityAuth;
import me.zort.sqllib.SQLDatabaseConnection;

import java.sql.SQLException;
import java.util.HashMap;

public final class SessionFactory {

    private static SessionFactory INSTANCE;
    private static final HashMap<Player, PlayerSession> playerSessions = new HashMap<>();

    private SessionFactory() {}

    public static SessionFactory getInstance() {
        if (INSTANCE == null) {
            return INSTANCE = new SessionFactory();
        }
        return INSTANCE;
    }

    public PlayerSession createPlayerSession(Player player) {
        PlayerSession playerSession = new PlayerSession(player).create();
        playerSessions.put(player, playerSession);
        return playerSession;
    }

    public boolean cancelPlayerSession(Player player) {
        PlayerSession playerSession = playerSessions.get(player);
        if (playerSession != null) {
            playerSessions.remove(player);
            return playerSession.cancel();
        }
        return true;
    }

    public PlayerSession getPlayerSession(Player player, boolean validate) {
        PlayerSession playerSession = playerSessions.get(player);
        if (playerSession == null) return null;
        if (validate) {
            boolean valid = playerSession.isValid();
            if (!valid) return null;
        }
        return playerSession;
    }

    public void clearAllSessions() {
        try {
            SQLDatabaseConnection resource = VelocityAuth.connectionPool.getResource();
            resource.delete().from("active_sessions").execute();
            resource.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
