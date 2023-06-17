package guru.qa.niffler.jupiter.extension;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.dao.NifflerUsersDAO;
import guru.qa.niffler.db.dao.NifflerUsersDAOJdbc;
import guru.qa.niffler.db.dao.NifflerUsersDAOSpringJdbc;
import guru.qa.niffler.db.entity.Authority;
import guru.qa.niffler.db.entity.AuthorityEntity;
import guru.qa.niffler.db.entity.UserEntity;
import guru.qa.niffler.jupiter.annotation.GenerateUserSpringJDBC;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;

public class GenerateUserSpringJDBCExtension implements ParameterResolver, BeforeEachCallback, AfterTestExecutionCallback {
    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace
            .create(GenerateUserSpringJDBCExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Faker faker = new Faker();
        final String testID = context.getRequiredTestClass() + String.valueOf(context.getTestMethod());

        GenerateUserSpringJDBC annotation = context.getRequiredTestMethod()
                .getAnnotation(GenerateUserSpringJDBC.class);

        if (annotation != null){

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

            NifflerUsersDAO usersDAO = new NifflerUsersDAOSpringJdbc();
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