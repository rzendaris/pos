package id.kopipintar.pos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.navigation.NavigationView;

import java.util.Map;

import es.dmoral.toasty.Toasty;
import id.kopipintar.pos.api.RetrofitClientInstance;
import id.kopipintar.pos.model.User;
import id.kopipintar.pos.utils.CommonUtils;
import id.kopipintar.pos.utils.DelayedProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityUbahPassword extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private EditText etNewPass;
    private EditText etNewPassConf;
    private Button btnSaveNewPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_password);

        initView();

        setSupportActionBar(toolbar);

        setupDrawer();
    }

    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        etNewPass = findViewById(R.id.et_new_pass);
        etNewPassConf = findViewById(R.id.et_new_pass_konf);
        btnSaveNewPass = findViewById(R.id.btn_save_new_pass);

        btnSaveNewPass.setOnClickListener(view -> {
            if (TextUtils.isEmpty(etNewPass.getText().toString()) || TextUtils.isEmpty(etNewPassConf.getText().toString())){
                Toasty.error(this,"Isi form dengan benar!", Toasty.LENGTH_LONG).show();
            }else if(!TextUtils.equals(etNewPass.getText().toString(),etNewPassConf.getText().toString())){
                Toasty.error(this,"Password baru dengan konfirmasi password baru tidak sama!", Toasty.LENGTH_LONG).show();
            }else{

                DelayedProgressDialog progressDialog = new DelayedProgressDialog();
                progressDialog.setCancelable(false);
                progressDialog.show(getSupportFragmentManager(),"tag");

                User user = CommonUtils.getUserLoginData(this);

                RetrofitClientInstance.get().changePassword(user.getUser_id(), etNewPassConf.getText().toString(), user.getToken()).enqueue(new Callback<Map<String, Boolean>>() {
                    @Override
                    public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> response) {
                        progressDialog.dismiss();

                        if (response.code() == 200){
                            Toasty.success(getApplicationContext(),"Password berhasil diubah!", Toasty.LENGTH_LONG).show();
                        }else if(response.code() == 403){
                            Toasty.warning(getApplicationContext(),"Session Expired!", Toasty.LENGTH_LONG).show();
                            CommonUtils.clearUserLoginData(getApplicationContext());
                            finish();
                            startActivity(new Intent(ActivityUbahPassword.this,ActivityLogin.class));
                        }else{
                            Toasty.error(getApplicationContext(),response.message(), Toasty.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {
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

    private void setupDrawer(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_penjualan) {
            startActivity(new Intent(this,ActivityPenjualan.class));
        } else if (id == R.id.nav_riwayat_penjualan) {
            startActivity(new Intent(this,ActivityRiwayatPenjualan.class));
        } else if (id == R.id.nav_pengeluaran) {
            startActivity(new Intent(this,ActivityPengeluaran.class));
        } else if (id == R.id.nav_ubah_password) {

        } else if (id == R.id.nav_logout) {
            CommonUtils.logout(this);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
