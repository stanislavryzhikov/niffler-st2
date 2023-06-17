package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.Assertions;
import org.springframework.lang.NonNull;

import java.io.IOException;

public class SpendRestClient extends BaseRestClient{
    public SpendRestClient() {
        super(Config.getConfig().getSpendUrl());
    }
    private final SpendService spendService = retrofit.create(SpendService.class);

    public @NonNull SpendJson addSpend(SpendJson spendJson){
        try {
            return spendService.addSpend(spendJson).execute().body();
        } catch (IOException e) {
            Assertions.fail("Не смогли подключиться к niffler-spend "+e.getMessage());
            return null;
        }
    }

}