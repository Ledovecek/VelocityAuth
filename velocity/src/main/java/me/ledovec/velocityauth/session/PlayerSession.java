package me.ledovec.velocityauth.session;

import com.velocitypowered.api.proxy.Player;
import me.ledovec.velocityauth.VelocityAuth;
import me.ledovec.velocityauth.exceptions.CreateSessionException;
import me.ledovec.velocityauth.exceptions.InvalidPlayerException;
import me.ledovec.velocityauth.exceptions.InvalidSessionException;
import me.ledovec.velocityauth.utils.Time;
import me.zort.sqllib.SQLDatabaseConnection;
import me.zort.sqllib.api.data.QueryResult;
import me.zort.sqllib.api.data.Row;
import net.kyori.adventure.text.Component;

import java.sql.SQLException;
import java.util.Optional;

public class PlayerSession implements Session<PlayerSession>, Cancelable, Verifiable {

    private final long playerId;
    private final Player player;
    protected PlayerSession(Player player) {
        this.player = player;
        try {
            SQLDatabaseConnection resource = VelocityAuth.connectionPool.getResource();
            Optional<Row> result = resource.select("id").from("player_credentials").where().isEqual("player", player.getUsername()).obtainOne();
            resource.close();
            if (result.isPresent()) playerId = result.get().getLong("id");
            else throw new InvalidPlayerException();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean cancel() {
        try {
            SQLDatabaseConnection resource = VelocityAuth.connectionPool.getResource();
            QueryResult execute = resource.delete().from("active_sessions").where().isEqual("player_id", playerId).execute();
            resource.close();
            if (execute.isSuccessful()) {
                if (player.isActive()) {
                    player.disconnect(Component.text("§cYour authentication session has been cancelled."));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PlayerSession create() {
        try {
            SQLDatabaseConnection resource = VelocityAuth.connectionPool.getResource();
            QueryResult execute = resource.insert().into("active_sessions", "player_id", "since").values(playerId, Time.getCurrentTime()).execute();
            resource.close();
            if (execute.isSuccessful()) return this;
            else throw new CreateSessionException();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getId() {
        try {
            SQLDatabaseConnection resource = VelocityAuth.connectionPool.getResource();
            Optional<Row> result = resource.select("id").from("active_sessions").where()
                    .isEqual("player_id", playerId).obtainOne();
            resource.close();
            if (result.isPresent()) return result.get().getLong("id");
            else throw new InvalidSessionException();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isValid() {
        try {
            SQLDatabaseConnection resource = VelocityAuth.connectionPool.getResource();
            Optional<Row> result = resource.select("id").from("active_sessions").where().isEqual("player_id", playerId).obtainOne();
            resource.close();
            return result.isPresent();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
