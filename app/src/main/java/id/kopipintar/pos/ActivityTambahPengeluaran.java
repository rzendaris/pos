package id.kopipintar.pos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.tiper.MaterialSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import id.kopipintar.pos.api.RetrofitClientInstance;
import id.kopipintar.pos.model.Item;
import id.kopipintar.pos.model.ItemData;
import id.kopipintar.pos.model.Measure;
import id.kopipintar.pos.model.MeasureData;
import id.kopipintar.pos.model.PengeluaranData;
import id.kopipintar.pos.model.User;
import id.kopipintar.pos.tabledata.viewlistener.TablePengeluaranListener;
import id.kopipintar.pos.utils.CommonUtils;
import id.kopipintar.pos.utils.DelayedProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityTambahPengeluaran extends AppCompatActivity {

    private EditText spSatuan, spItem, etTanggal, etQty, etHargaSatuan, etKeterangan;
    private Button btnSaveExpense;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pengeluaran);

        initView();

    }

    private void initView(){

        spSatuan = findViewById(R.id.spinner_satuan);
        spItem = findViewById(R.id.sp_item);

        etTanggal = findViewById(R.id.et_tanggal);
        etQty = findViewById(R.id.et_qty);
        etHargaSatuan = findViewById(R.id.et_harga_satuan);
        etKeterangan = findViewById(R.id.et_keterangan);

        btnSaveExpense = findViewById(R.id.btn_simpan);

        DatePickerDialog.OnDateSetListener date = (datePicker, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        etTanggal.setOnClickListener(view -> new DatePickerDialog(ActivityTambahPengeluaran.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        User user = CommonUtils.getUserLoginData(this);

        DelayedProgressDialog progressDialog = new DelayedProgressDialog();
//        progressDialog.setCancelable(false);
//        progressDialog.show(getSupportFragmentManager(),"tag");

//        Intent myIntent = getIntent();
//        PengeluaranData pengeluaranData = null;
//        PengeluaranData pengeluaranData = (PengeluaranData) myIntent.getSerializableExtra(TablePengeluaranListener.EXPENSE_DATA);
//        if (pengeluaranData != null){
//
//            String newDate = pengeluaranData.getExpense_date().substring(0,10);
//
//            etTanggal.setText(newDate);
//            etQty.setText(String.valueOf(pengeluaranData.getQty()));
//            etHargaSatuan.setText(String.valueOf(pengeluaranData.getPrice()));
//
//        }

        btnSaveExpense.setOnClickListener(view -> {

            if (TextUtils.isEmpty(spSatuan.getText().toString())){
                Toasty.error(getApplicationContext(), "Isi satuan terlebih dahulu", Toasty.LENGTH_LONG).show();
            }else if(TextUtils.isEmpty(spItem.getText().toString())){
                Toasty.error(getApplicationContext(), "Isi item terlebih dahulu", Toasty.LENGTH_LONG).show();
            }else if(TextUtils.isEmpty(etTanggal.getText().toString())){
                Toasty.error(getApplicationContext(), "Isi tanggal dengan benar", Toasty.LENGTH_LONG).show();
            }else if(TextUtils.isEmpty(etQty.getText().toString())){
                Toasty.error(getApplicationContext(), "Isi qty dengan benar", Toasty.LENGTH_LONG).show();
            }else if(TextUtils.isEmpty(etHargaSatuan.getText().toString())){
                Toasty.error(getApplicationContext(), "Isi harga satuan dengan benar", Toasty.LENGTH_LONG).show();
            }else if(TextUtils.isEmpty(etKeterangan.getText().toString())){
                Toasty.error(getApplicationContext(), "Isi keterangan dengan benar", Toasty.LENGTH_LONG).show();
            }else{

                int userId = user.getUser_id();
                int branchId = user.getBranch_id();
                String itemId = spItem.getText().toString();
                String dateExpense = etTanggal.getText().toString();
                int qty = Integer.parseInt(etQty.getText().toString());
                int price = Integer.parseInt(etHargaSatuan.getText().toString());
                String measure = spSatuan.getText().toString();
                String keterangan = etKeterangan.getText().toString();

                progressDialog.show(getSupportFragmentManager(),"tag");

                addExpense(user, progressDialog, userId, branchId, itemId, dateExpense, qty, price, measure, keterangan);

            }

        });

    }

    private void editExpense(User user, DelayedProgressDialog progressDialog, int expenseId, String itemId, String dateExpense, int qty, int price, String measure, String keterangan) {

        RetrofitClientInstance.get().updateExpense(expenseId,itemId,dateExpense,qty,price,measure,keterangan,user.getToken()).enqueue(new Callback<Map<String, Boolean>>() {
            @Override
            public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> response) {
                progressDialog.dismiss();

                if (response.code() == 200){

                    Toasty.success(getApplicationContext(),"Berhasil edit pengeluaran!",Toasty.LENGTH_LONG).show();

                    startActivity(new Intent(ActivityTambahPengeluaran.this, ActivityPengeluaran.class));
                    finish();

                }else if(response.code() == 403){
                    Toasty.warning(getApplicationContext(),"Session Expired!", Toasty.LENGTH_LONG).show();
                   /* CommonUtils.clearUserLoginData(getApplicationContext());
                    finish();
                    startActivity(new Intent(ActivityTambahPengeluaran.this, ActivityLogin.class));*/
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

    private void addExpense(User user, DelayedProgressDialog progressDialog, int userId, int branchId, String itemId, String dateExpense, int qty, int price, String measure, String keterangan) {

        RetrofitClientInstance.get().createExpense(userId,branchId,itemId,dateExpense,qty,price,measure,keterangan,user.getToken()).enqueue(new Callback<Map<String, Boolean>>() {
            @Override
            public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> response) {

                progressDialog.dismiss();

                if (response.code() == 200){

                    Toasty.success(getApplicationContext(),"Berhasil tambah pengeluaran!",Toasty.LENGTH_LONG).show();

                    startActivity(new Intent(ActivityTambahPengeluaran.this, ActivityPengeluaran.class));
                    finish();

                }else if(response.code() == 403){
                    Toasty.warning(getApplicationContext(),"Session Expired!", Toasty.LENGTH_LONG).show();
                    CommonUtils.clearUserLoginData(getApplicationContext());
                    finish();
                    startActivity(new Intent(ActivityTambahPengeluaran.this, ActivityLogin.class));
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

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etTanggal.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(ActivityTambahPengeluaran.this, ActivityPengeluaran.class));
        finish();

        super.onBackPressed();
    }
}
