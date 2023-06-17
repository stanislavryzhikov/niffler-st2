package guru.qa.niffler.page.component;

import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Selenide.$;

public class Footer extends BaseComponent<Footer> {
    public Footer() {
        super($("footer.footer"));
    }

    @Override
    public Footer checkThatComponentLoad() {
        self.shouldHave(Condition.text("Study project for QA Automation Advanced. 2023"));
        return this;
    }
}
