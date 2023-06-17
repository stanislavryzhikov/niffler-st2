package guru.qa.niffler.db.springJDBCMappers;

import guru.qa.niffler.db.entity.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserMapper implements RowMapper<UserEntity> {
    @Override
    public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserEntity userEntity = new UserEntity();

        userEntity.setId(UUID.fromString(rs.getString(1)));
        userEntity.setUsername(rs.getString(2));
        userEntity.setPassword(rs.getString(3));
        userEntity.setEnabled(rs.getBoolean(4));
        userEntity.setAccountNonExpired(rs.getBoolean(5));
        userEntity.setAccountNonLocked(rs.getBoolean(6));
        userEntity.setCredentialsNonExpired(rs.getBoolean(7));

        return userEntity;
    }
}