package niffler.db.dao;

import niffler.db.ServiceDB;
import niffler.db.entity.UserEntity;
import niffler.db.jpa.EmfProvider;
import niffler.db.jpa.JpaTransactionManager;

import java.util.UUID;

public class NifflerUsersDAOHibernate extends JpaTransactionManager implements NifflerUsersDAO {

  public NifflerUsersDAOHibernate() {
    super(EmfProvider.INSTANCE.getEmf(ServiceDB.NIFFLER_AUTH).createEntityManager());
  }

  @Override
  public int createUser(UserEntity user) {
    user.setPassword(pe.encode(user.getPassword()));
    persist(user);
    return 0;
  }

  @Override
  public UserEntity readUser(UUID uuid) {
    return em.createQuery("SELECT u FROM UserEntity u WHERE id=:id", UserEntity.class)
            .setParameter("id", uuid)
            .getSingleResult();
  }

  @Override
  public UUID getUserId(String userName) {
    return em.createQuery("SELECT u FROM UserEntity u WHERE username=:username", UserEntity.class)
            .setParameter("username", userName)
            .getSingleResult()
            .getId();
  }

  @Override
  public int removeUser(UserEntity user) {
    remove(user);
    return 0;
  }

  @Override
  public int updateUser(UserEntity user) {
    merge(user);
    return 0;
  }

  @Override
  public int deleteUser(UUID uuid) {
    remove(readUser(uuid));
    return 0;
  }
}
