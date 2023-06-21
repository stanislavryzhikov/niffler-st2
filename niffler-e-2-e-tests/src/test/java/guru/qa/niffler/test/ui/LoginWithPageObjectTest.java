package guru.qa.niffler.test.ui;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.entity.UserEntity;
import guru.qa.niffler.jupiter.annotation.ClasspathUser;
import guru.qa.niffler.jupiter.annotation.GenerateUser;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainFrontPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
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
  void positiveLogin(@ClasspathUser UserJson user) {
    Selenide.open(MainFrontPage.URL, MainFrontPage.class).clickOnLoginButton()
            .checkThatPageLoad()
            .fillLoginForm(user.getUsername(), "12345");
    new MainPage().checkThatPageLoad();
  }

  @ValueSource(strings = {
          "testdata/dima.json",
          "testdata/emma.json"
  })
  @ParameterizedTest
  @Test
  void setUserNameTest(@ClasspathUser UserJson user) {

    Selenide.open(MainFrontPage.URL, MainFrontPage.class).clickOnLoginButton()
            .checkThatPageLoad()
            .fillLoginForm(user.getUsername(), "12345");
    new MainPage().checkThatPageLoad();
    new MainPage().getHeader().goToProfilePage();
    new ProfilePage().setName(user.getUsername()).setSurname("TestSurname").submit();

  }
  @ValueSource(strings = {
          "testdata/dima.json",
          "testdata/emma.json"
  })
  @ParameterizedTest
  @Test
  void addCategoryTest(@ClasspathUser UserJson user) {

    Selenide.open(MainFrontPage.URL, MainFrontPage.class).clickOnLoginButton()
            .checkThatPageLoad()
            .fillLoginForm(user.getUsername(), "12345");
    new MainPage().checkThatPageLoad();
    new MainPage().getHeader().goToProfilePage();
    new ProfilePage().createCategory("Test category");

  }
  @ValueSource(strings = {
          "testdata/dima.json",
          "testdata/emma.json"
  })
  @ParameterizedTest
  @Test
  void addNewSpendingTest(@ClasspathUser UserJson user){
    Selenide.open(MainFrontPage.URL, MainFrontPage.class).clickOnLoginButton()
            .checkThatPageLoad()
            .fillLoginForm(user.getUsername(), "12345");
    new MainPage().checkThatPageLoad();
    new MainPage().addNewSpending("Test","999");
  }

  @Test
  void dummy() {
    Selenide.open(MainFrontPage.URL, MainFrontPage.class).clickOnLoginButton()
            .checkThatPageLoad()
            .fillLoginForm("dima", "12345");
    new MainPage().checkThatPageLoad();
    new MainPage().addNewSpending("Test","999");

  }
}
