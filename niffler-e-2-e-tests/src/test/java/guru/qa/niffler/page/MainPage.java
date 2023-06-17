package guru.qa.niffler.page;

import com.codeborne.selenide.*;
import com.codeborne.selenide.selector.ByText;
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
}
