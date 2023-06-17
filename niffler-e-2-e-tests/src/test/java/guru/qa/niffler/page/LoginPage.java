package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
    public static final String URL = Config.getConfig().getAuthUrl() + "/login";
    private final SelenideElement headerForm = $("h1.form__header");
    private final SelenideElement loginInput = $("input[name=username]");
    private final SelenideElement passwordInput = $("input[type=password]");
    private final SelenideElement singInButton = $("button.form__submit");
    private final SelenideElement errorMessage = $("p.form__error");

    @Override
    public LoginPage checkThatPageLoad() {
        headerForm.shouldHave(Condition.text("Welcome to Niffler. The coin keeper"));
        return this;
    }

    public LoginPage fillLoginForm(String username, String password) {
        loginInput.setValue(username);
        passwordInput.setValue(password);
        singInButton.click();
        return this;
    }

    public LoginPage checkErrorMessage(String expectedMessage) {
        errorMessage.shouldHave(Condition.text(expectedMessage));
        return this;
    }
}
