package id.kopipintar.pos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.evrencoskun.tableview.TableView;
import com.google.android.material.navigation.NavigationView;

import es.dmoral.toasty.Toasty;
import id.kopipintar.pos.api.RetrofitClientInstance;
import id.kopipintar.pos.model.RiwayatPenjualan;
import id.kopipintar.pos.model.User;
import id.kopipintar.pos.tabledata.adapter.TableRiwayatPenjualanAdapter;
import id.kopipintar.pos.tabledata.viewlistener.TableRiwayatPenjualanListener;
import id.kopipintar.pos.utils.CommonUtils;
import id.kopipintar.pos.utils.DelayedProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityRiwayatPenjualan extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar toolbar;
    private TableView tblRiwayatPenjualan;
    private TableRiwayatPenjualanAdapter tableRiwayatPenjualanAdapter;

    RiwayatPenjualan riwayatPenjualan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_penjualan);

        initView();

        setSupportActionBar(toolbar);

        setupDrawer();

        initializeTableView(tblRiwayatPenjualan);

        getTableData();

    }

    private void getTableData() {
        //set Data
        User user = CommonUtils.getUserLoginData(getApplicationContext());

        DelayedProgressDialog progressDialog = new DelayedProgressDialog();
        progressDialog.setCancelable(false);
        progressDialog.show(getSupportFragmentManager(),"tag");

        RetrofitClientInstance.get().getSalesHistory(user.getBranch_id(), user.getToken()).enqueue(new Callback<RiwayatPenjualan>() {
            @Override
            public void onResponse(Call<RiwayatPenjualan> call, Response<RiwayatPenjualan> response) {
                progressDialog.dismiss();

                if (response.code() == 200){
                    riwayatPenjualan = response.body();
                    tableRiwayatPenjualanAdapter.setRiwayatPenjualanList(riwayatPenjualan);

                    // Create listener
                    tblRiwayatPenjualan.setTableViewListener(new TableRiwayatPenjualanListener(tblRiwayatPenjualan, ActivityRiwayatPenjualan.this, riwayatPenjualan));
                }else if(response.code() == 403){
                    Toasty.warning(getApplicationContext(),"Session Expired!", Toasty.LENGTH_LONG).show();
                    CommonUtils.clearUserLoginData(getApplicationContext());
                    finish();
                    startActivity(new Intent(ActivityRiwayatPenjualan.this, ActivityLogin.class));
                }else{
                    Toasty.error(getApplicationContext(),response.message(), Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RiwayatPenjualan> call, Throwable t) {
                progressDialog.dismiss();
                String msg = "oops something went wrong!";

                if (t.getMessage() != null){
                    msg = t.getMessage();
                }

                Toasty.error(getApplicationContext(),msg, Toasty.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTableData();
    }

    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        tblRiwayatPenjualan = findViewById(R.id.tbl_riwayat_penjualan);
    }

    private void initializeTableView(TableView tableView){

        // Create TableView Adapter
        tableRiwayatPenjualanAdapter = new TableRiwayatPenjualanAdapter(this);
        tableView.setAdapter(tableRiwayatPenjualanAdapter);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_penjualan) {
            startActivity(new Intent(this,ActivityPenjualan.class));
        } else if (id == R.id.nav_riwayat_penjualan) {

        } else if (id == R.id.nav_pengeluaran) {
            startActivity(new Intent(this,ActivityPengeluaran.class));
        } else if (id == R.id.nav_ubah_password) {
            startActivity(new Intent(this,ActivityUbahPassword.class));
        } else if (id == R.id.nav_logout) {
            CommonUtils.logout(this);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
