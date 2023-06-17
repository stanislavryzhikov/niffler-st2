package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Selenide.$;

public class MainFrontPage extends BasePage<MainFrontPage> {
    public static final String URL = Config.getConfig().getFrontUrl();
    private final SelenideElement mainHeader = $("h1.main__header");
    private final SelenideElement redirectToLoginButton = $("a[href*='redirect']");
    @Override
    public MainFrontPage checkThatPageLoad() {
        mainHeader.shouldBe(Condition.text("Welcome to magic journey with Niffler. The coin keeper"));
        return this;
    }

    public LoginPage clickOnLoginButton() {
        redirectToLoginButton.click();
        return new LoginPage();
    }
}
