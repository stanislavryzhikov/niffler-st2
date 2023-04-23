package niffler.api;

import niffler.model.CategoryJson;
import niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CategoryService {

    @POST("/category")
    Call<CategoryJson> addCategory(@Body CategoryJson category);
}