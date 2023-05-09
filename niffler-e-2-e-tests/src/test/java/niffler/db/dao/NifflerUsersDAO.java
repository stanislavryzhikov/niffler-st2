package niffler.db.dao;

import niffler.db.entity.UserEntity;

import java.util.UUID;

public interface NifflerUsersDAO {

    int createUser(UserEntity user);

    UserEntity readUser(UUID uuid);

    int updateUser(UserEntity user);

    int deleteUser(UUID uuid);

    UUID getUserId(String userName);
}
