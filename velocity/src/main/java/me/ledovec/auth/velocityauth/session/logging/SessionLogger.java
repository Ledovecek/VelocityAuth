package me.ledovec.auth.velocityauth.session.logging;

import me.ledovec.auth.velocityauth.VelocityAuth;
import me.ledovec.auth.velocityauth.session.PlayerSession;
import me.ledovec.auth.velocityauth.utils.Time;
import me.zort.sqllib.SQLDatabaseConnection;

import java.sql.SQLException;

public class SessionLogger {

    public static void record(PlayerSession playerSession) {
        String hostAddress = playerSession.getPlayer().getRemoteAddress().getAddress().getHostAddress();
        try {
            SQLDatabaseConnection resource = VelocityAuth.connectionPool.getResource();
            resource.insert()
                    .into("sessions_log", "ip", "session_id", "expired")
                    .values(hostAddress, playerSession.getId(), Time.getCurrentTime()).execute();
            resource.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
