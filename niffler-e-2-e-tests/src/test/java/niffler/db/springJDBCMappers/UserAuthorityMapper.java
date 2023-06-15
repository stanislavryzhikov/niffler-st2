package niffler.db.springJDBCMappers;

import niffler.db.entity.Authority;
import niffler.db.entity.AuthorityEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserAuthorityMapper implements RowMapper<AuthorityEntity> {
    @Override
    public AuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthorityEntity authorityEntity = new AuthorityEntity();

        authorityEntity.setId(UUID.fromString(rs.getString(1)));
        authorityEntity.setAuthority(Authority.valueOf(rs.getString(3)));

        return authorityEntity;
    }
}

