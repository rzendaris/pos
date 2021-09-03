package id.kopipintar.pos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import es.dmoral.toasty.Toasty;
import id.kopipintar.pos.api.RetrofitClientInstance;
import id.kopipintar.pos.model.CancelSales;
import id.kopipintar.pos.tabledata.viewlistener.TableRiwayatPenjualanListener;
import id.kopipintar.pos.utils.CommonUtils;
import id.kopipintar.pos.utils.DelayedProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityPembatalanPenjualan extends AppCompatActivity {

    private EditText etNoTrx, etTgl, etQty, etPrice, etAlasan;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembatalan_penjualan);

        initView();
    }

    private void initView() {

        etNoTrx = findViewById(R.id.et_no_trx);
        etTgl = findViewById(R.id.et_tanggal_trx);
        etQty = findViewById(R.id.et_total_item);
        etPrice = findViewById(R.id.et_harga);
        etAlasan = findViewById(R.id.et_alasan);
        btnSubmit = findViewById(R.id.btn_simpan_pembatalan);

        Intent myIntent = getIntent();
        String tgl = myIntent.getStringExtra(TableRiwayatPenjualanListener.TGL);
        String noTrx = myIntent.getStringExtra(TableRiwayatPenjualanListener.TRX_NO);
        String qty = myIntent.getStringExtra(TableRiwayatPenjualanListener.TOTAL_ITEM);
        String price = myIntent.getStringExtra(TableRiwayatPenjualanListener.TOTAL_HARGA);

        etNoTrx.setText(noTrx);
        etTgl.setText(tgl);
        etQty.setText(qty);
        etPrice.setText(price);

        btnSubmit.setOnClickListener(view -> {

            String alasan = etAlasan.getText().toString();

            if (alasan.equals("") || TextUtils.isEmpty(alasan)){
                Toasty.warning(this, "Masukkan alasan!",Toasty.LENGTH_LONG).show();
            }else{

                DelayedProgressDialog progressDialog = new DelayedProgressDialog();
                progressDialog.setCancelable(false);
                progressDialog.show(getSupportFragmentManager(),"tag");

                RetrofitClientInstance.get().cancelSales(Integer.valueOf(noTrx), alasan,CommonUtils.getUserLoginData(this).getToken()).enqueue(new Callback<CancelSales>() {
                    @Override
                    public void onResponse(Call<CancelSales> call, Response<CancelSales> response) {
                        progressDialog.dismiss();
                        if (response.code() == 200){
                            Toasty.success(getApplicationContext(),"Berhasil Update!",Toasty.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(ActivityPembatalanPenjualan.this,ActivityRiwayatPenjualan.class));
                        }else if(response.code() == 403){
                            Toasty.warning(getApplicationContext(),response.message(), Toasty.LENGTH_LONG).show();
                            CommonUtils.clearUserLoginData(getApplicationContext());
                            finish();
                            startActivity(new Intent(ActivityPembatalanPenjualan.this,ActivityLogin.class));
                        }else{
                            Toasty.error(getApplicationContext(),response.message(), Toasty.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CancelSales> call, Throwable t) {
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


    @Override
    public void onBackPressed() {

        startActivity(new Intent(ActivityPembatalanPenjualan.this, ActivityRiwayatPenjualan.class));
        finish();

        super.onBackPressed();
    }
}
