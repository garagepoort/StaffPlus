package net.shortninja.staffplus.staff.mute.database;

import net.shortninja.staffplus.player.PlayerManager;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.staff.mute.Mute;
import net.shortninja.staffplus.util.database.migrations.mysql.MySQLConnection;

import java.sql.*;

public class MysqlMuteRepository extends AbstractSqlMuteRepository {

    public MysqlMuteRepository(PlayerManager playerManager, Options options) {
        super(playerManager, options);
    }

    @Override
    protected Connection getConnection() throws SQLException {
        return MySQLConnection.getConnection();
    }

    @Override
    public int addMute(Mute mute) {
        try (Connection sql = getConnection();
             PreparedStatement insert = sql.prepareStatement("INSERT INTO sp_muted_players(reason, player_uuid, issuer_uuid, end_timestamp, creation_timestamp, server_name) " +
                 "VALUES(?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, mute.getReason());
            insert.setString(2, mute.getTargetUuid().toString());
            insert.setString(3, mute.getIssuerUuid().toString());
            if (mute.getEndTimestamp() == null) {
                insert.setNull(4, Types.BIGINT);
            } else {
                insert.setLong(4, mute.getEndTimestamp());
            }
            insert.setLong(5, mute.getCreationTimestamp());
            insert.setString(6, options.serverName);
            insert.executeUpdate();

            ResultSet generatedKeys = insert.getGeneratedKeys();
            int generatedKey = -1;
            if (generatedKeys.next()) {
                generatedKey = generatedKeys.getInt(1);
            }

            return generatedKey;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
