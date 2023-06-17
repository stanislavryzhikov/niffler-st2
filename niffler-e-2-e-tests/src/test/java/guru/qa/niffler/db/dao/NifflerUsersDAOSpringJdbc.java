package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.entity.AuthorityEntity;
import guru.qa.niffler.db.entity.UserEntity;
import guru.qa.niffler.db.springJDBCMappers.UserAuthorityMapper;
import guru.qa.niffler.db.springJDBCMappers.UserMapper;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class NifflerUsersDAOSpringJdbc implements NifflerUsersDAO {

  private final TransactionTemplate transactionTemplate;
  private final JdbcTemplate jdbcTemplate;

  public NifflerUsersDAOSpringJdbc() {
    DataSourceTransactionManager transactionManager = new JdbcTransactionManager(
        DataSourceProvider.INSTANCE.getDataSource(ServiceDB.NIFFLER_AUTH));
    this.transactionTemplate = new TransactionTemplate(transactionManager);
    this.jdbcTemplate = new JdbcTemplate(transactionManager.getDataSource());
  }

  @Override
  public int createUser(UserEntity user) {

    return transactionTemplate.execute(st -> {
      KeyHolder keyHolder = new GeneratedKeyHolder();

      jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement("INSERT INTO users (username, password, " +
                                " enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                                " VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, pe.encode(user.getPassword()));
                ps.setBoolean(3, user.getEnabled());
                ps.setBoolean(4, user.getAccountNonExpired());
                ps.setBoolean(5, user.getAccountNonLocked());
                ps.setBoolean(6, user.getCredentialsNonExpired());
                return ps;
              },
              keyHolder
      );

      final UUID createdUserId;

      if (keyHolder.getKeys().get("id") != null) {
        createdUserId = (UUID) keyHolder.getKeys().get("id");
        user.setId(createdUserId);
      } else {
        throw new IllegalArgumentException("Unable to create user, no uuid");
      }

      String insertAuthoritiesSql = "INSERT INTO authorities (user_id, authority) VALUES ('%s', '%s')";

      List<String> sqls = user.getAuthorities()
              .stream()
              .map(ae -> ae.getAuthority().name())
              .map(a -> String.format(insertAuthoritiesSql, createdUserId, a))
              .toList();

      for (String sql : sqls) {
        jdbcTemplate.update(sql);
      }

      return 1;
    });
  }
  @Override
  public UserEntity readUser(UUID uuid) {

    UserEntity userEntity = jdbcTemplate.query("SELECT * FROM users WHERE id=(?)",
                    new BeanPropertyRowMapper<>(UserEntity.class),
                    uuid)
            .stream()
            .findAny()
            .orElse(null);

    if (userEntity != null) {
      List<AuthorityEntity> listAuths = jdbcTemplate.query("SELECT * FROM authorities WHERE user_id=(?)",
              new UserAuthorityMapper(),
              uuid);
      userEntity.setAuthorities(listAuths);
    } else {
      throw new IllegalArgumentException("Unable to read user authorities, no uuid");
    }

    return userEntity;
  }
  @Override
  public UUID getUserId(String userName) {

    return (Objects.requireNonNull(jdbcTemplate.query("SELECT * FROM users WHERE username = ?",
                    new UserMapper(),
                    userName)
            .stream()
            .findAny()
            .orElse(null)))
            .getId();
  }

  @Override
  public int removeUser(UserEntity user) {
    return transactionTemplate.execute(st -> {
      jdbcTemplate.update("DELETE FROM authorities WHERE user_id = ?", user.getId());
      return jdbcTemplate.update("DELETE FROM users WHERE id = ?", user.getId());
    });
  }

  @Override
  public int updateUser(UserEntity user) {
    return jdbcTemplate.update("UPDATE users SET "
                    + "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)="
                    + "(?, ?, ?, ?, ?, ?) WHERE id=(?)",
            user.getUsername(),
            user.getPassword(),
            user.getEnabled(),
            user.getAccountNonExpired(),
            user.getAccountNonLocked(),
            user.getCredentialsNonExpired(),
            user.getId());
  }

  @Override
  public int deleteUser(UUID uuid) {
    return transactionTemplate.execute(st -> {
      jdbcTemplate.update("DELETE FROM authorities WHERE user_id = ?", uuid);

      return jdbcTemplate.update("DELETE FROM users WHERE id = ?", uuid);
    });
  }
}
