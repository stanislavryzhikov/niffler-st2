package niffler.jupiter.extension;

import com.github.javafaker.Faker;
import niffler.db.dao.NifflerUsersDAO;
import niffler.db.dao.NifflerUsersDAOJdbc;
import niffler.db.entity.Authority;
import niffler.db.entity.AuthorityEntity;
import niffler.db.entity.UserEntity;
import niffler.jupiter.annotation.GenerateUser;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;

public class GenerateUserExtension implements ParameterResolver, BeforeEachCallback, AfterTestExecutionCallback {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace
            .create(GenerateUserExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Faker faker = new Faker();
        final String testID = context.getRequiredTestClass() + String.valueOf(context.getTestMethod());

        GenerateUser annotation = context.getRequiredTestMethod()
                .getAnnotation(GenerateUser.class);

        if (annotation != null) {

            UserEntity createdUserEntity = new UserEntity();
            createdUserEntity.setUsername(
                    (annotation.username()).equals("") ? faker.name().username() : annotation.username());
            createdUserEntity.setPassword(
                    (annotation.password()).equals("") ? faker.internet().password() : annotation.password());
            createdUserEntity.setEnabled(true);
            createdUserEntity.setAccountNonExpired(true);
            createdUserEntity.setAccountNonLocked(true);
            createdUserEntity.setCredentialsNonExpired(true);
            createdUserEntity.setAuthorities(Arrays.stream(Authority.values()).map(
                    a -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setAuthority(a);
                        return ae;
                    }
            ).toList());

            NifflerUsersDAO usersDAO = new NifflerUsersDAOJdbc();
            usersDAO.createUser(createdUserEntity);

            context.getStore(NAMESPACE).put(testID + "user", createdUserEntity);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserEntity.class);
    }

    @Override
    public UserEntity resolveParameter(ParameterContext parameterContext,
                                       ExtensionContext extensionContext) throws ParameterResolutionException {
        final String testID = extensionContext.getRequiredTestClass() + String.valueOf(extensionContext.getTestMethod());

        return extensionContext.getStore(NAMESPACE).get(testID + "user", UserEntity.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        NifflerUsersDAO usersDAO = new NifflerUsersDAOJdbc();
        final String testID = context.getRequiredTestClass() + String.valueOf(context.getTestMethod());

        usersDAO.deleteUser((context.getStore(NAMESPACE).get(testID + "user", UserEntity.class)).getId());
    }
}