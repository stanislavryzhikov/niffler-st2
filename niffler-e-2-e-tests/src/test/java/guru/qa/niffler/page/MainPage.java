package guru.qa.niffler.page;

import com.codeborne.selenide.*;
import guru.qa.niffler.page.component.Header;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage extends BasePage<MainPage> {
    private final Header header = new Header();
    private ElementsCollection spendTable = $(".spendings-table tbody").$$("tr");
    private SelenideElement deleteButton = $$(".button_type_small").find(text("Delete selected"));
    private SelenideElement categoryToggle =  $("#root > div > div.main-container > main > div > section.main-content__section.main-content__section-add-spending > form > label:nth-child(1) > div > div > div > div.css-1fdsijx-ValueContainer > div.css-uj28k3-Input");



    private SelenideElement amountInput =  $("#root > div > div.main-container > main > div > section.main-content__section.main-content__section-add-spending > form > label:nth-child(2) > input");
    private SelenideElement addNewSpendingInput = $("#root > div > div.main-container > main > div > section.main-content__section.main-content__section-add-spending > form > button");

    @Override
    public MainPage checkThatPageLoad() {
        $(byText("History of spendings")).shouldBe(Condition.visible);
        return this;
    }
    public Header getHeader() {
        return header;
    }

    public MainPage selectSpendCheckboxInTable(String spendName) {
            spendTable.find(text(spendName)).find("td").scrollTo().click();
        return this;
    }

    public MainPage clickOnDeleteButton() {
        deleteButton.click();
        return this;
    }

    public MainPage checkCountEntry(int size) {
        spendTable.shouldHave(CollectionCondition.size(size));
        return this;
    }

    public MainPage addNewSpending(String s, CharSequence amountValue) {
        categoryToggle.click();
        $(By.xpath("//*[text()=\""+s+"\"]")).click();
        amountInput.sendKeys(amountValue);
        addNewSpendingInput.click();
        return this;
    }
}
