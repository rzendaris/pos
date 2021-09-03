package id.kopipintar.pos.api;

import java.util.List;
import java.util.Map;

import id.kopipintar.pos.model.CancelSales;
import id.kopipintar.pos.model.Category;
import id.kopipintar.pos.model.Item;
import id.kopipintar.pos.model.Measure;
import id.kopipintar.pos.model.Pengeluaran;
import id.kopipintar.pos.model.Product;
import id.kopipintar.pos.model.RiwayatPenjualan;
import id.kopipintar.pos.model.SalesCreateRequest;
import id.kopipintar.pos.model.SalesSuccessResponse;
import id.kopipintar.pos.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIServiceInterface {

    String KEYNAME = "chopper";

    @FormUrlEncoded
    @POST("login")
    Call<User> getUserLogin(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("sales/list/{branch_id}")
    Call<RiwayatPenjualan> getSalesHistory(@Path("branch_id")int branchId, @Header(KEYNAME) String token);


    @GET("expense/list/{branch_id}")
    Call<Pengeluaran> getExpenses(@Path("branch_id")int branchId, @Header(KEYNAME) String token);

    @FormUrlEncoded
    @PUT("password")
    Call<Map<String, Boolean>> changePassword(
            @Field("user_id") int userId,
            @Field("password") String newPassword,
            @Header(KEYNAME) String token
    );

    @FormUrlEncoded
    @POST("sales/cancel")
    Call<CancelSales> cancelSales(
            @Field("transaction_no") int trxNo,
            @Field("reason") String reason,
            @Header(KEYNAME) String token
    );

    @GET("measure/list")
    Call<Measure> getMeasures(@Header(KEYNAME) String token);

    @GET("item/list")
    Call<Item> getItems(@Header(KEYNAME) String token);

    @FormUrlEncoded
    @POST("expense/create")
    Call<Map<String, Boolean>> createExpense(
            @Field("user_id") int userId,
            @Field("branch_id") int branchId,
            @Field("item_id") String itemId,
            @Field("expense_date") String date,
            @Field("qty") int qty,
            @Field("price") int price,
            @Field("measure") String measure,
            @Field("description") String desc,
            @Header("chopper") String token
    );

    @FormUrlEncoded
    @PUT("expense/delete")
    Call<Map<String, Boolean>> deleteExpense(
            @Field("expense_id") int expenseId,
            @Header(KEYNAME) String token
    );

    @FormUrlEncoded
    @PUT("expense/update")
    Call<Map<String, Boolean>> updateExpense(
            @Field("expense_id") int expenseId,
            @Field("item_id") String itemId,
            @Field("expense_date") String expenseDate,
            @Field("qty") int qty,
            @Field("price") int price,
            @Field("measure") String measureId,
            @Field("description") String desc,
            @Header(KEYNAME) String token
    );

    @GET("category/list")
    Call<Category> getCategories(@Header(KEYNAME) String token);

    @GET("product/{cat_id}")
    Call<Map<String,List<Product>>> getProductsById(@Path("cat_id")int catId, @Header(KEYNAME) String token);

    @POST("sales/create")
    Call<SalesSuccessResponse> createSales(@Header(KEYNAME) String token, @Body SalesCreateRequest salesCreateRequest);
}
