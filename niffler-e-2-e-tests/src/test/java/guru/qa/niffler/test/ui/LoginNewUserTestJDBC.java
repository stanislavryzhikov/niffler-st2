package guru.qa.niffler.test.ui;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.dao.NifflerUsersDAO;
import guru.qa.niffler.db.dao.NifflerUsersDAOJdbc;
import guru.qa.niffler.db.entity.UserEntity;
import guru.qa.niffler.jupiter.annotation.GenerateUser;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class LoginNewUserTestJDBC extends BaseWebTest {
    private final NifflerUsersDAO usersDAO = new NifflerUsersDAOJdbc();

    @GenerateUser(
            username = "Valentin",
            password = "12345"
    )
    @Test
    void loginTest(UserEntity user) {
        Allure.step("open page", () -> Selenide.open("http://127.0.0.1:3000/main"));
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(user.getUsername());
        $("input[name='password']").setValue(user.getPassword());
        $("button[type='submit']").click();

        $("a[href*='friends']").click();
        $(".header").should(visible).shouldHave(text("Niffler. The coin keeper."));
    }

    @GenerateUser(
            username = "Lena",
            password = "12345"
    )
    @Test
    void checkUpdateUser(UserEntity user){
        UserEntity updUserEntity = new UserEntity();
        updUserEntity.setId(usersDAO.getUserId(user.getUsername()));
        updUserEntity.setUsername(user.getUsername() + "-updated");
        updUserEntity.setPassword("123456");
        updUserEntity.setEnabled(false);
        updUserEntity.setAccountNonExpired(false);
        updUserEntity.setAccountNonLocked(false);
        updUserEntity.setCredentialsNonExpired(false);

        usersDAO.updateUser(updUserEntity);

        Allure.step("open page", () -> Selenide.open("http://127.0.0.1:3000/main"));
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(updUserEntity.getUsername());
        $("input[name='password']").setValue(updUserEntity.getPassword());
        $("button[type='submit']").click();

        $(byText("User account is locked")).should(visible);
    }

    @GenerateUser(
            username = "Ivan",
            password = "12345"
    )
    @Test
    void checkDeleteUser(UserEntity user){
        usersDAO.deleteUser(usersDAO.getUserId(user.getUsername()));

        Allure.step("open page", () -> Selenide.open("http://127.0.0.1:3000/main"));
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(user.getUsername());
        $("input[name='password']").setValue(user.getPassword());
        $("button[type='submit']").click();

        $(byText("Bad credentials")).should(visible);
    }

    @GenerateUser(
            username = "Maxim",
            password = "12345"
    )
    @Test
    void checkReadUser(UserEntity user){
        UserEntity readUser = usersDAO.readUser(usersDAO.getUserId(user.getUsername()));
        Assertions.assertEquals(user,readUser);
    }
}