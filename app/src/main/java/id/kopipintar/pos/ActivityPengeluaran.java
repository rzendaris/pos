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
import android.widget.ArrayAdapter;

import com.evrencoskun.tableview.TableView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.tiper.MaterialSpinner;

import es.dmoral.toasty.Toasty;
import id.kopipintar.pos.api.RetrofitClientInstance;
import id.kopipintar.pos.model.Pengeluaran;
import id.kopipintar.pos.model.User;
import id.kopipintar.pos.tabledata.adapter.TablePengeluaranAdapter;
import id.kopipintar.pos.tabledata.viewlistener.TablePengeluaranListener;
import id.kopipintar.pos.utils.CommonUtils;
import id.kopipintar.pos.utils.DelayedProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityPengeluaran extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar toolbar;
    private MaterialSpinner materialSpinner;
    private TableView tblPengeluaran;
    private TablePengeluaranAdapter tablePengeluaranAdapter;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengeluaran);

        initView();

        setSupportActionBar(toolbar);

        setupDrawer();

        initializeTableView(tblPengeluaran);
        //set Data
        User user = CommonUtils.getUserLoginData(getApplicationContext());

        DelayedProgressDialog progressDialog = new DelayedProgressDialog();
        progressDialog.setCancelable(false);
        progressDialog.show(getSupportFragmentManager(),"tag");

        getExpenseData(user, progressDialog);

        fab.setOnClickListener(view -> startActivity(new Intent(ActivityPengeluaran.this, ActivityTambahPengeluaran.class)));
    }

    private void getExpenseData(User user, DelayedProgressDialog progressDialog) {
        RetrofitClientInstance.get().getExpenses(user.getBranch_id(), user.getToken()).enqueue(new Callback<Pengeluaran>() {
            @Override
            public void onResponse(Call<Pengeluaran> call, Response<Pengeluaran> response) {
                progressDialog.dismiss();

                if (response.code() == 200){
                    Pengeluaran pengeluaran = response.body();
                    tablePengeluaranAdapter.setPengeluaranList(pengeluaran);

                    // Create listener
                    tblPengeluaran.setTableViewListener(new TablePengeluaranListener(tblPengeluaran, ActivityPengeluaran.this, pengeluaran, progressDialog, user.getToken(), getSupportFragmentManager()));
                }else if(response.code() == 403){
                    Toasty.warning(getApplicationContext(),"Session Expired!", Toasty.LENGTH_LONG).show();
                    CommonUtils.clearUserLoginData(getApplicationContext());
                    finish();
                    startActivity(new Intent(ActivityPengeluaran.this, ActivityLogin.class));
                }else{
                    Toasty.error(getApplicationContext(),response.message(), Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Pengeluaran> call, Throwable t) {
                progressDialog.dismiss();
                String msg = "oops something went wrong!";

                if (t.getMessage() != null){
                    msg = t.getMessage();
                }

                Toasty.error(getApplicationContext(),msg, Toasty.LENGTH_LONG).show();
            }
        });
    }

    private void initView(){

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.bulan));

        tblPengeluaran = findViewById(R.id.tbl_pengeluaran);
        toolbar = findViewById(R.id.toolbar);
        materialSpinner = findViewById(R.id.spinner_bulan);
        fab = findViewById(R.id.fab);
        materialSpinner.setAdapter(adapter);
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

    private void initializeTableView(TableView tableView){

        // Create TableView Adapter
        tablePengeluaranAdapter = new TablePengeluaranAdapter(this);
        tableView.setAdapter(tablePengeluaranAdapter);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_penjualan) {
            startActivity(new Intent(this,ActivityPenjualan.class));
        } else if (id == R.id.nav_riwayat_penjualan) {
            startActivity(new Intent(this,ActivityRiwayatPenjualan.class));
        } else if (id == R.id.nav_pengeluaran) {

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
