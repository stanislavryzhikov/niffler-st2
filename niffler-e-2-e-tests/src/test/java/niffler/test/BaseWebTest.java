package niffler.test;


import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import niffler.jupiter.annotation.WebTest;
import niffler.jupiter.extension.BrowserExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public abstract class BaseWebTest {

  static {
    Configuration.browserSize = "1920x1080";
  }

  @AfterEach
  void close(){
    Selenide.closeWebDriver();
  }

}
