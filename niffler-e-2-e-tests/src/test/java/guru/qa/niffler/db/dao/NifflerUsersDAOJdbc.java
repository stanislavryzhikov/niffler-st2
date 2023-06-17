package guru.qa.niffler.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

import guru.qa.niffler.db.entity.AuthorityEntity;
import guru.qa.niffler.db.entity.UserEntity;
import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.entity.Authority;

public class NifflerUsersDAOJdbc implements NifflerUsersDAO {

  private static final DataSource ds = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.NIFFLER_AUTH);

  @Override
  public int createUser(UserEntity user) {
    int executeUpdate;

    try (Connection conn = ds.getConnection()) {

      conn.setAutoCommit(false);

      try (PreparedStatement insertUserSt = conn.prepareStatement("INSERT INTO users "
          + "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) "
          + " VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
          PreparedStatement insertAuthoritySt = conn.prepareStatement(
              "INSERT INTO authorities (user_id, authority) VALUES (?, ?)")) {
        insertUserSt.setString(1, user.getUsername());
        insertUserSt.setString(2, pe.encode(user.getPassword()));
        insertUserSt.setBoolean(3, user.getEnabled());
        insertUserSt.setBoolean(4, user.getAccountNonExpired());
        insertUserSt.setBoolean(5, user.getAccountNonLocked());
        insertUserSt.setBoolean(6, user.getCredentialsNonExpired());
        executeUpdate = insertUserSt.executeUpdate();

        final UUID finalUserId;

        try (ResultSet generatedKeys = insertUserSt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            finalUserId = UUID.fromString(generatedKeys.getString(1));
            user.setId(finalUserId);
          } else {
            throw new SQLException("Creating user failed, no ID present");
          }
        }

        for (AuthorityEntity authority : user.getAuthorities()) {
          insertAuthoritySt.setObject(1, finalUserId);
          insertAuthoritySt.setString(2, authority.getAuthority().name());
          insertAuthoritySt.addBatch();
          insertAuthoritySt.clearParameters();
        }
        insertAuthoritySt.executeBatch();
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

    try (Connection conn = ds.getConnection();
         PreparedStatement st1 = conn.prepareStatement("SELECT * FROM users WHERE id=(?)")) {
      st1.setObject(1, uuid);
      ResultSet rs = st1.executeQuery();
      if (rs.next()) {
        userEntity.setId(UUID.fromString(rs.getString(1)));
        userEntity.setUsername(rs.getString(2));
        userEntity.setPassword(rs.getString(3));
        userEntity.setEnabled(rs.getBoolean(4));
        userEntity.setAccountNonExpired(rs.getBoolean(5));
        userEntity.setAccountNonLocked(rs.getBoolean(6));
        userEntity.setCredentialsNonExpired(rs.getBoolean(7));
      } else {
        throw new IllegalArgumentException("Can`t find user by given uuid: " + uuid);
      }

      try (PreparedStatement st2 = conn.prepareStatement("SELECT * FROM authorities WHERE user_id=(?)")){
        st2.setObject(1, uuid);
        ResultSet rs2 = st2.executeQuery();
        List<AuthorityEntity> listAuths = new ArrayList<>();

        while (rs2.next()){
          AuthorityEntity authorityEntity = new AuthorityEntity();
          authorityEntity.setId(UUID.fromString(rs2.getString(1)));
          authorityEntity.setAuthority(Authority.valueOf(rs2.getString(3)));
          listAuths.add(authorityEntity);
        }

        userEntity.setAuthorities(listAuths);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    return userEntity;
  }

  @Override
  public UUID getUserId(String userName) {
    try (Connection conn = ds.getConnection();
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

  @Override
  public int removeUser(UserEntity user) {
    int executeUpdate;

    try (Connection conn = ds.getConnection()) {

      conn.setAutoCommit(false);

      try (PreparedStatement deleteUserSt = conn.prepareStatement("DELETE FROM users WHERE id = ?");
          PreparedStatement deleteAuthoritySt = conn.prepareStatement(
              "DELETE FROM authorities WHERE user_id = ?")) {
        deleteUserSt.setObject(1, user.getId());
        deleteAuthoritySt.setObject(1, user.getId());

        deleteAuthoritySt.executeUpdate();
        executeUpdate = deleteUserSt.executeUpdate();

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
  public int updateUser(UserEntity user) {
    int executeUpdate;

    try (Connection conn = ds.getConnection();
         PreparedStatement st1 = conn.prepareStatement("UPDATE users SET "
                 + "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)="
                 + "(?, ?, ?, ?, ?, ?) WHERE id=(?)")) {
      st1.setString(1, user.getUsername());
      st1.setString(2, pe.encode(user.getPassword()));
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

    try (Connection conn = ds.getConnection()) {

      conn.setAutoCommit(false);

      try (PreparedStatement st1 = conn.prepareStatement("DELETE FROM authorities WHERE user_id=(?)")) {
        st1.setObject(1, uuid);

        st1.executeUpdate();

        try (PreparedStatement st2 = conn.prepareStatement("DELETE FROM users WHERE id=(?)")) {
          st2.setObject(1, uuid);

          executeUpdate = st2.executeUpdate();
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
}
