package guru.qa.niffler.page;

import guru.qa.niffler.page.component.Header;

public class FriendsPage extends BasePage<FriendsPage> {
    private final Header header = new Header();
    @Override
    public FriendsPage checkThatPageLoad() {
        return this;
    }
    public Header getHeader() {
        return header;
    }
}
