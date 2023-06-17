package guru.qa.niffler.test.ui;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.entity.UserEntity;
import guru.qa.niffler.jupiter.annotation.ClasspathUser;
import guru.qa.niffler.jupiter.annotation.GenerateUser;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainFrontPage;
import guru.qa.niffler.page.MainPage;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginWithPageObjectTest extends BaseWebTest {

  @ValueSource(strings = {
          "testdata/dima.json",
          "testdata/emma.json"
  })
  @ParameterizedTest
  @Test
  void positiveLogin2(@ClasspathUser UserJson user) {
    System.out.println(user.toString());
    Selenide.open(MainFrontPage.URL, MainFrontPage.class).clickOnLoginButton()
            .checkThatPageLoad()
            .fillLoginForm(user.getUsername(), "12345");
    new MainPage().checkThatPageLoad();
  }

  @Test
  void negativeLoginTest() {
    Selenide.open(MainFrontPage.URL, MainFrontPage.class).clickOnLoginButton()
            .checkThatPageLoad()
            .fillLoginForm("000302032", "ferfejkljvflv")
            .checkErrorMessage("Неверные учетные данные пользователя");
  }

}
