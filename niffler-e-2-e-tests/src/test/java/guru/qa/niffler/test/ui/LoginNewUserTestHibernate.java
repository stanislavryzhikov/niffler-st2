package guru.qa.niffler.test.ui;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.dao.NifflerUsersDAO;
import guru.qa.niffler.db.dao.NifflerUsersDAOHibernate;
import guru.qa.niffler.db.entity.UserEntity;
import io.qameta.allure.Allure;
import guru.qa.niffler.jupiter.annotation.GenerateUserHibernate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;


public class LoginNewUserTestHibernate extends BaseWebTest {
    private final NifflerUsersDAO usersDAO = new NifflerUsersDAOHibernate();

    @GenerateUserHibernate(
            username = "Valentin",
            password = "12345"
    )
    @Test
    void loginTest(UserEntity user) {
        Allure.step("open page", () -> Selenide.open("http://127.0.0.1:3000/main"));
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(user.getUsername());
        $("input[name='password']").setValue(user.getDecodedPassword());
        $("button[type='submit']").click();

        $("a[href*='friends']").click();
        $(".header").should(visible).shouldHave(text("Niffler. The coin keeper."));
    }

    @GenerateUserHibernate(
            username = "Lena",
            password = "12345"
    )
    @Test
    void checkUpdateUser(UserEntity user){
        UserEntity updUser = usersDAO.readUser(user.getId());
        updUser.setUsername(user.getUsername() + "-updated");
        updUser.setPassword("123");
        updUser.setDecodedPassword(user.getDecodedPassword());
        updUser.setEnabled(false);
        updUser.setAccountNonExpired(false);
        updUser.setAccountNonLocked(false);
        updUser.setCredentialsNonExpired(false);

        usersDAO.updateUser(updUser);

        Allure.step("open page", () -> Selenide.open("http://127.0.0.1:3000/main"));
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(user.getUsername());
        $("input[name='password']").setValue(user.getDecodedPassword());
        $("button[type='submit']").click();

        $(byText("User account is locked")).should(visible);
    }

    @GenerateUserHibernate(
            username = "Ivan",
            password = "12345"
    )
    @Test
    void checkDeleteUser(UserEntity user){
        usersDAO.deleteUser(usersDAO.getUserId(user.getUsername()));

        Allure.step("open page", () -> Selenide.open("http://127.0.0.1:3000/main"));
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(user.getUsername());
        $("input[name='password']").setValue(user.getDecodedPassword());
        $("button[type='submit']").click();

        $(byText("Bad credentials")).should(visible);
    }

    @GenerateUserHibernate(
            username = "Maxim",
            password = "12345"
    )
    @Test
    void checkReadUser(UserEntity user){
        UserEntity readUser = usersDAO.readUser(usersDAO.getUserId(user.getUsername()));
        Assertions.assertEquals(user,readUser);
    }
}