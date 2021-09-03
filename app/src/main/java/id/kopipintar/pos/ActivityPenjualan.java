package id.kopipintar.pos;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import id.kopipintar.pos.adapter.PaketAdapter;
import id.kopipintar.pos.adapter.ViewPagerAdapter;
import id.kopipintar.pos.api.RetrofitClientInstance;
import id.kopipintar.pos.fragment.FragmentPaket;
import id.kopipintar.pos.model.Category;
import id.kopipintar.pos.model.Product;
import id.kopipintar.pos.model.User;
import id.kopipintar.pos.utils.CommonUtils;
import id.kopipintar.pos.utils.DelayedProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityPenjualan extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout tlPaket;
    private ViewPager vpPaket;
    private Toolbar toolbar;
    private RecyclerView rvPaket;
    private Button btnBayar;
    private Button btnReset;
    private static PaketAdapter paketAdapter;
    private static List<Product> products = new ArrayList<>();
    private static Map<Integer, Integer> integerListMap = new HashMap<>();
    private static TextView tvSubtotal;
    private static TextView tvDiskon;
    private static TextView tvTotal;

    public static final String TOTAL_BELANJA = "totalBelanja";
    public static final String PRODUCTS = "products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan);

        User user = CommonUtils.getUserLoginData(getApplicationContext());

        if (user == null){
            startActivity(new Intent(getApplicationContext(), ActivityLogin.class));
            finish();
        }else{
            initView();

            setSupportActionBar(toolbar);

            setupDrawer();

            List<TextView> textViews = new ArrayList<>();
            textViews.add(tvTotal);
            textViews.add(tvDiskon);
            textViews.add(tvSubtotal);

            paketAdapter = new PaketAdapter(this,products,integerListMap, textViews, btnReset, btnBayar);
            rvPaket.setAdapter(paketAdapter);
            rvPaket.setLayoutManager(new LinearLayoutManager(this));

            DelayedProgressDialog progressDialog = new DelayedProgressDialog();
            progressDialog.setCancelable(false);
            progressDialog.show(getSupportFragmentManager(),"tag");

            getCategories(user, progressDialog);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        calculateTotalPrice();

    }

    public static void calculateTotalPrice() {

        int diskon = 0;
        int subtotal = 0;
        int total;

        for (Product product : products){
            diskon += product.getDiscount() * integerListMap.get(product.getProduct_id());
            subtotal += product.getTotal_price() * integerListMap.get(product.getProduct_id());

            product.setQty(integerListMap.get(product.getProduct_id()));

            //newProducts.add(product);
        }

        total = subtotal - diskon;

        tvDiskon.setText(String.valueOf(diskon));
        tvSubtotal.setText(String.valueOf(subtotal));
        tvTotal.setText(String.valueOf(total));

    }

    private void getCategories(User user, DelayedProgressDialog progressDialog) {
        RetrofitClientInstance.get().getCategories(user.getToken()).enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                progressDialog.dismiss();

                if (response.code() == 200){

                    List<Category.CategoryData> categories = response.body().getData();

                    setupViewPager(vpPaket, categories);
                    tlPaket.setupWithViewPager(vpPaket);

                }else if(response.code() == 403){
                    Toasty.warning(getApplicationContext(),"Session Expired!", Toasty.LENGTH_LONG).show();
                    CommonUtils.clearUserLoginData(getApplicationContext());
                    finish();
                    startActivity(new Intent(ActivityPenjualan.this, ActivityLogin.class));
                }else{
                    Toasty.error(getApplicationContext(),response.message(), Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                progressDialog.dismiss();
                String msg = "oops something went wrong!";

                if (t.getMessage() != null){
                    msg = t.getMessage();
                }

                Toasty.error(getApplicationContext(),msg, Toasty.LENGTH_LONG).show();
            }
        });
    }

    public static class PaketOnClickListener implements View.OnClickListener{

        Product product;
        int jml = 0;

        public PaketOnClickListener(Product product) {
            this.product = product;
        }

        @Override
        public void onClick(View view) {

            if (products.contains(product)){
                jml = jml+1;
            }else {
                jml = 1;
                products.add(product);
            }

            integerListMap.put(product.getProduct_id(),jml);

            paketAdapter.notifyDataSetChanged();

            calculateTotalPrice();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initView(){
        rvPaket = findViewById(R.id.rv_pakets);
        toolbar = findViewById(R.id.toolbar);
        tlPaket = findViewById(R.id.tl_paket);
        vpPaket = findViewById(R.id.vp_paket);
        btnBayar = findViewById(R.id.btn_bayar);
        btnReset = findViewById(R.id.btn_reset);
        tvDiskon = findViewById(R.id.tv_diskon);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvTotal = findViewById(R.id.tv_total);
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

    private void setupViewPager(ViewPager viewPager, List<Category.CategoryData> categoryDatas) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (Category.CategoryData categoryData : categoryDatas){
            adapter.addFragment(new FragmentPaket(categoryData.getCategory_id()), categoryData.getCategory_name());
        }

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_penjualan) {

        } else if (id == R.id.nav_riwayat_penjualan) {
            startActivity(new Intent(this, ActivityRiwayatPenjualan.class));
        } else if (id == R.id.nav_pengeluaran) {
            startActivity(new Intent(this, ActivityPengeluaran.class));
        } else if (id == R.id.nav_ubah_password) {
            startActivity(new Intent(this, ActivityUbahPassword.class));
        } else if (id == R.id.nav_logout) {
            CommonUtils.logout(this);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
