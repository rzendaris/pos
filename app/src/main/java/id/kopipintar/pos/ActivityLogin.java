package id.kopipintar.pos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.Objects;

import es.dmoral.toasty.Toasty;
import id.kopipintar.pos.api.RetrofitClientInstance;
import id.kopipintar.pos.model.User;
import id.kopipintar.pos.utils.DelayedProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLogin extends AppCompatActivity {

    private Button btnLogin;
    private EditText etEmail, etPassword;
    public static String DATAUSER = "dataUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE);

        initView();

        btnLogin.setOnClickListener(view -> {

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toasty.error(this,"Masukkan email atau password dengan benar", Toasty.LENGTH_LONG).show();
            }else{

                DelayedProgressDialog progressDialog = new DelayedProgressDialog();
                progressDialog.setCancelable(false);
                progressDialog.show(getSupportFragmentManager(),"tag");

                RetrofitClientInstance.get().getUserLogin(email, password).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        progressDialog.dismiss();

                        if (response.code() == 200){

                            User userLogin = response.body();

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(userLogin);
                            editor.putString(DATAUSER, json);
                            editor.apply();

                            Intent intent = new Intent(getApplicationContext(), ActivityPenjualan.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toasty.error(getApplicationContext(),response.message(), Toasty.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        progressDialog.dismiss();

                        String msg = "oops something went wrong!";

                        if (t.getMessage() != null){
                            msg = t.getMessage();
                        }

                        Toasty.error(getApplicationContext(),msg, Toasty.LENGTH_LONG).show();
                    }
                });
            }

        });
    }

    private void initView(){
        Objects.requireNonNull(getSupportActionBar()).hide();

        btnLogin = findViewById(R.id.btn_login);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
    }
}
