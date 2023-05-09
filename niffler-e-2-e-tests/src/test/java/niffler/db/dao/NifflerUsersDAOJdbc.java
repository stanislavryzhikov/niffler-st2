package niffler.db.dao;

import niffler.db.DataSourceProvider;
import niffler.db.ServiceDB;
import niffler.db.entity.Authority;
import niffler.db.entity.AuthorityEntity;
import niffler.db.entity.UserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NifflerUsersDAOJdbc implements NifflerUsersDAO {

    private static final DataSource dataSource = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.NIFFLER_AUTH);
    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public int createUser(UserEntity user) { int executeUpdate;

        try (Connection conn = dataSource.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO users "
                    + "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) "
                    + " VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, passwordEncoder.encode(user.getPassword()));
                preparedStatement.setBoolean(3, user.getEnabled());
                preparedStatement.setBoolean(4, user.getAccountNonExpired());
                preparedStatement.setBoolean(5, user.getAccountNonLocked());
                preparedStatement.setBoolean(6, user.getCredentialsNonExpired());

                executeUpdate = preparedStatement.executeUpdate();

                final UUID createdUserId;

                try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                    if (keys.next()) {
                        createdUserId = UUID.fromString(keys.getString(1));
                        user.setId(createdUserId);
                    } else {
                        throw new IllegalArgumentException("Unable to create user, no uuid");
                    }
                }

                String insertAuthoritiesSql = "INSERT INTO authorities (user_id, authority) VALUES ('%s', '%s')";

                List<String> sqls = user.getAuthorities()
                        .stream()
                        .map(ae -> ae.getAuthority().name())
                        .map(a -> String.format(insertAuthoritiesSql, createdUserId, a))
                        .toList();

                for (String sql : sqls) {
                    try (Statement st2 = conn.createStatement()) {
                        st2.executeUpdate(sql);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw new RuntimeException(e);
            }

            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return executeUpdate;
        }

    @Override
    public UserEntity readUser(UUID uuid) {

        UserEntity userEntity = new UserEntity();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement prepareStatementUsers =
                     connection.prepareStatement("SELECT * FROM users WHERE id=(?)");
             PreparedStatement prepareStatementAuthorities =
                     connection.prepareStatement("SELECT * FROM authorities WHERE user_id=(?)")
        ) {
            prepareStatementUsers.setObject(1, uuid);
            ResultSet resultSetUsers = prepareStatementUsers.executeQuery();
            if (resultSetUsers.next()) {
                userEntity.setId(UUID.fromString(resultSetUsers.getString(1)));
                userEntity.setUsername(resultSetUsers.getString(2));
                userEntity.setPassword(resultSetUsers.getString(3));
                userEntity.setEnabled(resultSetUsers.getBoolean(4));
                userEntity.setAccountNonExpired(resultSetUsers.getBoolean(5));
                userEntity.setAccountNonLocked(resultSetUsers.getBoolean(6));
                userEntity.setCredentialsNonExpired(resultSetUsers.getBoolean(7));
            } else {
                throw new IllegalArgumentException("Can`t find user by given uuid: " + uuid);
            }

            prepareStatementAuthorities.setObject(1, uuid);
            ResultSet resultSetAuthoritites = prepareStatementAuthorities.executeQuery();
            List<AuthorityEntity> listAuths = new ArrayList<>();

            while (resultSetAuthoritites.next()) {
                AuthorityEntity authorityEntity = new AuthorityEntity();
                authorityEntity.setId(UUID.fromString(resultSetAuthoritites.getString(1)));
                authorityEntity.setAuthority(Authority.valueOf(resultSetAuthoritites.getString(3)));
                listAuths.add(authorityEntity);

                userEntity.setAuthorities(listAuths);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return userEntity;
    }

    @Override
    public int updateUser(UserEntity user) {
        int executeUpdate;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement st1 = conn.prepareStatement("UPDATE users SET "
                     + "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)="
                     + "(?, ?, ?, ?, ?, ?) WHERE id=(?)")) {
            st1.setString(1, user.getUsername());
            st1.setString(2, passwordEncoder.encode(user.getPassword()));
            st1.setBoolean(3, user.getEnabled());
            st1.setBoolean(4, user.getAccountNonExpired());
            st1.setBoolean(5, user.getAccountNonLocked());
            st1.setBoolean(6, user.getCredentialsNonExpired());
            st1.setObject(7, user.getId());

            executeUpdate = st1.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return executeUpdate;
    }
    @Override
    public int deleteUser(UUID uuid) {
        int executeUpdate;

        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(false);

            try (PreparedStatement prepareStatementUsers =
                         connection.prepareStatement("DELETE FROM authorities WHERE user_id=(?)");
                 PreparedStatement prepareStatementAuthoritites = connection.prepareStatement("DELETE FROM users WHERE id=(?)")
            ) {
                prepareStatementUsers.setObject(1, uuid);
                prepareStatementUsers.executeUpdate();

                prepareStatementAuthoritites.setObject(1, uuid);
                executeUpdate = prepareStatementAuthoritites.executeUpdate();

            } catch (SQLException e) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw new RuntimeException(e);
            }

            connection.commit();
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return executeUpdate;
    }
        @Override
    public UUID getUserId(String userName) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement st = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            st.setString(1, userName);
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString(1));
            } else {
                throw new IllegalArgumentException("Can`t find user by given username: " + userName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}