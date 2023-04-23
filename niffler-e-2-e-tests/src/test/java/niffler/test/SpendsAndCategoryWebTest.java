package niffler.test;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import niffler.jupiter.GenerateCategory;
import niffler.jupiter.annotation.GenerateSpend;
import niffler.jupiter.GenerateSpendAndCategoryExtension;
import niffler.model.CurrencyValues;
import niffler.model.SpendJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

@ExtendWith(GenerateSpendAndCategoryExtension.class)
public class SpendsAndCategoryWebTest {

    @BeforeEach
    void doLogin() {
        Configuration.baseUrl = "http://127.0.0.1:3000";

        Selenide.open("/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue("test");
        $("input[name='password']").setValue("test");
        $("button[type='submit']").click();
    }

    @GenerateSpend(
            username = "test",
            description = "4",
            currency = CurrencyValues.RUB,
            amount = 44.00,
            category = "test category"
    )
    @GenerateCategory(
            username = "test",
            category = "test"
    )
    @Test
    void spendShouldBeDeletedByActionInTable(SpendJson spend) {
        open("/profile");
        $(".main-content__section-categories ul")
                .shouldHave(text(spend.getCategory()));

        Selenide.open("/main");
        $(".spendings-table tbody").$$("tr")
                .find(text(spend.getDescription()))
                .$$("td").first()
                .scrollTo()
                .click();

        $$(".button_type_small").find(text("Delete selected"))
                .click();

        $(".spendings-table tbody")
                .$$("tr")
                .shouldHave(CollectionCondition.size(0));
    }

}