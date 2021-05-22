package net.shortninja.staffplus.core.session.database;

import be.garagepoort.mcsqlmigrations.SqlConnectionProvider;
import net.shortninja.staffplus.core.common.exceptions.DatabaseException;
import net.shortninja.staffplusplus.vanish.VanishType;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractSqlSessionsRepository implements SessionsRepository {

    private final SqlConnectionProvider sqlConnectionProvider;

    public AbstractSqlSessionsRepository(SqlConnectionProvider sqlConnectionProvider) {
        this.sqlConnectionProvider = sqlConnectionProvider;
    }

    public Connection getConnection() {
        return sqlConnectionProvider.getConnection();
    }

    @Override
    public void update(SessionEntity sessionEntity) {
        try (Connection sql = getConnection();
             PreparedStatement insert = sql.prepareStatement("UPDATE sp_sessions set vanish_type=?, in_staff_mode=?, muted_staff_chat_channels=? , staff_mode_name=? WHERE ID=?")) {
            insert.setString(1, sessionEntity.getVanishType().toString());
            insert.setBoolean(2, sessionEntity.getStaffMode());
            insert.setString(3, String.join(";", sessionEntity.getMutedStaffChatChannels()));
            if (sessionEntity.getStaffModeName() == null) {
                insert.setNull(4, Types.VARCHAR);
            } else {
                insert.setString(4, sessionEntity.getStaffModeName());
            }
            insert.setInt(5, sessionEntity.getId());
            insert.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<SessionEntity> findSession(UUID uuid) {
        try (Connection sql = getConnection();
             PreparedStatement ps = sql.prepareStatement("SELECT * FROM sp_sessions WHERE player_uuid = ?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = rs.next();
                if (first) {
                    return Optional.of(buildSessionEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return Optional.empty();
    }

    private SessionEntity buildSessionEntity(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID");
        UUID playerUuid = UUID.fromString(rs.getString("player_uuid"));
        VanishType vanishType = VanishType.valueOf(rs.getString("vanish_type"));
        boolean inStaffMode = rs.getBoolean("in_staff_mode");
        Set<String> staffChatMuted = rs.getString("muted_staff_chat_channels") == null ? new HashSet<>() : Arrays.stream(rs.getString("muted_staff_chat_channels").split(";")).collect(Collectors.toSet());
        String staffModeName = rs.getString("staff_mode_name");
        return new SessionEntity(
            id,
            playerUuid,
            vanishType,
            inStaffMode,
            staffChatMuted, staffModeName);
    }

}
