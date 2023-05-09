package niffler.test;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import niffler.db.dao.NifflerUsersDAO;
import niffler.db.dao.NifflerUsersDAOJdbc;
import niffler.db.entity.UserEntity;
import niffler.jupiter.annotation.GenerateUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;

public class LoginCRUDUserTest extends BaseWebTest {
    private final NifflerUsersDAO usersDAO = new NifflerUsersDAOJdbc();
    public String generateName(){
        LocalTime.now();
        String pattern = "HHmmss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        String name = "test_user" + date;
        return name;
    }

    @GenerateUser(
            username = "test_user",
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
            username = "test_user",
            password = "12345"
    )
    @Test
    void checkUpdateUser(UserEntity user){

        UserEntity updUserEntity = new UserEntity();
        updUserEntity.setId(usersDAO.getUserId(user.getUsername()));
        updUserEntity.setUsername(user.getUsername() + "_new");
        updUserEntity.setPassword("_new");
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

        $("body > main > form > div > p").should(visible);

    }

    @GenerateUser(
            username = "test_user",
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

        $("body > main > form > div > p").should(visible);
    }

    @GenerateUser(
            username = "test_userss",
            password = "12345"
    )
    @Test
    void checkReadUser(UserEntity user){

        UserEntity readUser = usersDAO.readUser(usersDAO.getUserId(user.getUsername()));
        Assertions.assertEquals(user,readUser);
    }
}
