package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class ProfilePage extends BasePage<ProfilePage> {

    private SelenideElement nameInput = $("#root > div > div.main-container > main > div > section:nth-child(1) > div > form > div.profile__info-container > label:nth-child(1) > input");
    private SelenideElement surnameInput = $("#root > div > div.main-container > main > div > section:nth-child(1) > div > form > div.profile__info-container > label:nth-child(2) > input");
    private SelenideElement submitBtn = $(".header__title");
    private SelenideElement categoryNameInput = $("#root > div > div.main-container > main > div > section:nth-child(2) > div.main-content__section-add-category > form > div > label > input");
    private SelenideElement createBtn = $("#root > div > div.main-container > main > div > section:nth-child(2) > div.main-content__section-add-category > form > div > button");

    @Override
    public ProfilePage checkThatPageLoad() {
        return null;
    }

    public ProfilePage setName(CharSequence s) {
        nameInput.sendKeys(s);
        return this;
    }

    public ProfilePage setSurname(CharSequence s) {
        surnameInput.sendKeys(s);
        return this;
    }

    public ProfilePage submit() {
        submitBtn.click();
        return this;
    }
    public ProfilePage createCategory(CharSequence s){
        categoryNameInput.sendKeys(s);
        createBtn.click();
        return this;
    }
}
