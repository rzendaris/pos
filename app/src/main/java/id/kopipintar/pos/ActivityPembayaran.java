package id.kopipintar.pos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;
import com.mazenrashed.printooth.utilities.PrintingCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;
import id.kopipintar.pos.api.RetrofitClientInstance;
import id.kopipintar.pos.model.Product;
import id.kopipintar.pos.model.SalesCreateRequest;
import id.kopipintar.pos.model.SalesSuccessResponse;
import id.kopipintar.pos.model.User;
import id.kopipintar.pos.utils.CommonUtils;
import id.kopipintar.pos.utils.DelayedProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityPembayaran extends AppCompatActivity implements View.OnClickListener, PrintingCallback {

    private Button btnFinalBayar, btnSeratusLimaPuluhRibu, btnUangPas, btnDuaRatusRibu, btnDuaPuluhRibu, btnLimaPuluhRibu, btnSeratusRibu,
            btnDana, btnGopay, btnOvo, btnShopeePay;
    private EditText etNominalPembayaran, etTotalBelanja;
    private List<Product> products;
    private String totalBelanja;
    private String metodePembayaran = "TUNAI";

    public static final String TOTAL_BELANJA = "totalBelanja";
    public static final String WAKTU_TRANSAKSI = "waktuTransaksi";
    public static final String TOTAL_ITEM = "totalItem";
    public static final String NO_TRX = "noTrx";
    public static final String KEMBALIAN = "kembalian";

    private Printing printing;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);

        initView();

        Intent intent = getIntent();
        totalBelanja = intent.getStringExtra(ActivityPenjualan.TOTAL_BELANJA);
        products = (List<Product>) getIntent().getSerializableExtra(ActivityPenjualan.PRODUCTS);

        for (Product product : products){
            Log.i("PRODUKNYAy",product.getProduct_name()+" qty: "+product.getQty());
        }

        etTotalBelanja.setText(totalBelanja);

        //set Data
        user = CommonUtils.getUserLoginData(getApplicationContext());

    }

    private void setBtnSelected(int idBtnSelected, List<Integer> idUnselecteds){

        Button buttonSelected = findViewById(idBtnSelected);
        buttonSelected.setBackground(getResources().getDrawable(R.drawable.bg_round_corner_blue));
        buttonSelected.setTextColor(getResources().getColor(R.color.white));

        for(Integer id : idUnselecteds){

            Button buttonUnselected = findViewById(id);
            buttonUnselected.setBackground(getResources().getDrawable(R.drawable.bg_outline_square));
            buttonUnselected.setTextColor(getResources().getColor(R.color.black));
        }

    }

    private void initView() {

        btnFinalBayar = findViewById(R.id.btn_final_bayar);
        btnUangPas = findViewById(R.id.btn_uang_pas);
        btnDuaRatusRibu = findViewById(R.id.btn_dua_ratus_ribu);
        btnDuaPuluhRibu = findViewById(R.id.btn_dua_puluh_ribu);
        btnLimaPuluhRibu = findViewById(R.id.btn_lima_puluh_ribu);
        btnSeratusRibu = findViewById(R.id.btn_seratus_ribu);
        btnSeratusLimaPuluhRibu = findViewById(R.id.btn_seratus_lima_puluh_ribu);
        btnDana = findViewById(R.id.btn_dana);
        btnGopay = findViewById(R.id.btn_gopay);
        btnOvo = findViewById(R.id.btn_ovo);
        btnShopeePay = findViewById(R.id.btn_shopee_pay);

        etNominalPembayaran = findViewById(R.id.et_pembayaran);
        etTotalBelanja = findViewById(R.id.et_total_belanja);

        etNominalPembayaran.setEnabled(false);

        btnFinalBayar.setOnClickListener(this);
        btnSeratusLimaPuluhRibu.setOnClickListener(this);
        btnUangPas.setOnClickListener(this);
        btnDuaRatusRibu.setOnClickListener(this);
        btnDuaPuluhRibu.setOnClickListener(this);
        btnLimaPuluhRibu.setOnClickListener(this);
        btnSeratusRibu.setOnClickListener(this);
        btnDana.setOnClickListener(this);
        btnGopay.setOnClickListener(this);
        btnOvo.setOnClickListener(this);
        btnShopeePay.setOnClickListener(this);

        if (printing != null){
            printing.setPrintingCallback(this);
        }

    }

    private void setNominalPembayaran(int nominalPembayaran, Boolean isEnabled){
        etNominalPembayaran.setText(String.valueOf(nominalPembayaran));
        if (isEnabled){
            etNominalPembayaran.setText("");
        }
        etNominalPembayaran.setEnabled(isEnabled);
    }

    private void setPaymentMethod(String paymentMethod){
        if(!paymentMethod.equalsIgnoreCase("")){
            metodePembayaran = paymentMethod;
        } else {
            metodePembayaran = "TUNAI";
        }
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(ActivityPembayaran.this, ActivityPenjualan.class));
        finish();

        super.onBackPressed();
    }


    private void createSales(User user, DelayedProgressDialog progressDialog, List<Product> products, String pembayaran) {

        Log.i("PAYMENT", "Method: "+pembayaran);
        SalesCreateRequest salesCreateRequest = new SalesCreateRequest();
        salesCreateRequest.setBranch_id(user.getBranch_id());
        salesCreateRequest.setUser_id(user.getUser_id());
        salesCreateRequest.setPayment_method(pembayaran);

        List<SalesCreateRequest.TransactionDetail> trxDetail = new ArrayList<>();

        for (Product newProduct: products){
           /* if (!productIds.contains(product.getProduct_id())) {
                productIds.add(product.getProduct_id());
            }*/

            newProduct.setDiscount(newProduct.getDiscount()*newProduct.getQty());
            newProduct.setTotal_price(newProduct.getTotal_price()*newProduct.getQty());
            newProduct.setPpn(newProduct.getPpn()*newProduct.getQty());

            int indexnya = products.indexOf(newProduct);
            Log.i("INDEXEUY", "index: "+indexnya+" produknya: "+newProduct.getProduct_name()+" qty: "+newProduct.getQty());

            List<Product.Item> items = new ArrayList<>();
            for (Product.Item item: newProduct.getItems()){
                item.setQty(item.getQty() * newProduct.getQty());

                items.add(item);
            }

            newProduct.setItems(items);

            //myNewProducts.add(newProduct);

            trxDetail.add(new SalesCreateRequest.TransactionDetail(newProduct.getProduct_id(),newProduct.getQty(),newProduct.getTotal_price(),newProduct.getPpn(),newProduct.getDiscount()));

        }

       /* List<Product> myNewProducts = new ArrayList<>();

        for (int index=0; index < productIds.size(); index++){

            Product newProduct = products.get(index);

            newProduct.setDiscount(newProduct.getDiscount()*newProduct.getQty());
            newProduct.setTotal_price(newProduct.getTotal_price()*newProduct.getQty());
            newProduct.setPpn(newProduct.getPpn()*newProduct.getQty());


            int indexnya = products.indexOf(newProduct);
            Log.i("INDEXEUY", "index: "+indexnya+" produknya: "+newProduct.getProduct_name()+" qty: "+newProduct.getQty());

            List<Product.Item> items = new ArrayList<>();
            for (Product.Item item: newProduct.getItems()){
                item.setQty(item.getQty() * newProduct.getQty());

                items.add(item);
            }

            newProduct.setItems(items);

            myNewProducts.add(newProduct);

            trxDetail.add(new SalesCreateRequest.TransactionDetail(newProduct.getProduct_id(),newProduct.getQty(),newProduct.getTotal_price(),newProduct.getPpn(),newProduct.getDiscount()));
        }*/

        salesCreateRequest.setTransaction_detail(trxDetail);

        progressDialog.show(getSupportFragmentManager(),"tag");
        RetrofitClientInstance.get().createSales(user.getToken(),salesCreateRequest).enqueue(new Callback<SalesSuccessResponse>() {
            @Override
            public void onResponse(Call<SalesSuccessResponse> call, Response<SalesSuccessResponse> response) {
                progressDialog.dismiss();

                if (response.code() == 200){

                    if (!Printooth.INSTANCE.hasPairedPrinter()){
                        pairPrinter();
                    }else {

                        String noTrx = response.body().getTrx_no();
                        String waktuTrx = response.body().getCreated_at();
                        String totalItem = String.valueOf(products.size());
                        String totalHarga = totalBelanja;

                        int cash = Integer.parseInt(etNominalPembayaran.getText().toString());
                        int kembalian = cash - Integer.parseInt(totalBelanja);

                        printText(waktuTrx,noTrx,products,etNominalPembayaran.getText().toString(),String.valueOf(kembalian));

                        Intent intent = new Intent(ActivityPembayaran.this, ActivityPembayaranBerhasil.class);
                        intent.putExtra(NO_TRX,noTrx);
                        intent.putExtra(WAKTU_TRANSAKSI,waktuTrx);
                        intent.putExtra(TOTAL_ITEM,totalItem);
                        intent.putExtra(TOTAL_BELANJA,totalHarga);
                        intent.putExtra(KEMBALIAN,String.valueOf(kembalian));

                        startActivity(intent);
                        finish();
                    }

                }else if(response.code() == 403){
                    Toasty.warning(getApplicationContext(),response.message(), Toasty.LENGTH_LONG).show();
                    CommonUtils.clearUserLoginData(getApplicationContext());
                    finish();
                    startActivity(new Intent(ActivityPembayaran.this, ActivityLogin.class));
                }else{
                    Toasty.error(getApplicationContext(),response.message(), Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SalesSuccessResponse> call, Throwable t) {
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
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_uang_pas:

                setBtnSelected(view.getId(), Arrays.asList(R.id.btn_seratus_lima_puluh_ribu,R.id.btn_dua_puluh_ribu,R.id.btn_dua_ratus_ribu,R.id.btn_lima_puluh_ribu,R.id.btn_seratus_ribu,R.id.btn_dana,R.id.btn_gopay,R.id.btn_ovo,R.id.btn_shopee_pay));
                setNominalPembayaran(0,true);
                setPaymentMethod("");
                break;
            case R.id.btn_seratus_lima_puluh_ribu:

                setBtnSelected(view.getId(), Arrays.asList(R.id.btn_uang_pas,R.id.btn_dua_puluh_ribu,R.id.btn_dua_ratus_ribu,R.id.btn_lima_puluh_ribu,R.id.btn_seratus_ribu,R.id.btn_dana,R.id.btn_gopay,R.id.btn_ovo,R.id.btn_shopee_pay));
                setNominalPembayaran(150000,false);
                setPaymentMethod("");
                break;
            case R.id.btn_dua_puluh_ribu:

                setBtnSelected(view.getId(), Arrays.asList(R.id.btn_seratus_lima_puluh_ribu,R.id.btn_uang_pas,R.id.btn_dua_ratus_ribu,R.id.btn_lima_puluh_ribu,R.id.btn_seratus_ribu,R.id.btn_dana,R.id.btn_gopay,R.id.btn_ovo,R.id.btn_shopee_pay));
                setNominalPembayaran(20000,false);
                setPaymentMethod("");
                break;
            case R.id.btn_dua_ratus_ribu:

                setBtnSelected(view.getId(), Arrays.asList(R.id.btn_seratus_lima_puluh_ribu,R.id.btn_dua_puluh_ribu,R.id.btn_uang_pas,R.id.btn_lima_puluh_ribu,R.id.btn_seratus_ribu,R.id.btn_dana,R.id.btn_gopay,R.id.btn_ovo,R.id.btn_shopee_pay));
                setNominalPembayaran(200000,false);
                setPaymentMethod("");
                break;
            case R.id.btn_lima_puluh_ribu:

                setBtnSelected(view.getId(), Arrays.asList(R.id.btn_seratus_lima_puluh_ribu,R.id.btn_dua_puluh_ribu,R.id.btn_dua_ratus_ribu,R.id.btn_uang_pas,R.id.btn_seratus_ribu,R.id.btn_dana,R.id.btn_gopay,R.id.btn_ovo,R.id.btn_shopee_pay));
                setNominalPembayaran(50000,false);
                setPaymentMethod("");
                break;
            case R.id.btn_seratus_ribu:

                setBtnSelected(view.getId(), Arrays.asList(R.id.btn_seratus_lima_puluh_ribu,R.id.btn_dua_puluh_ribu,R.id.btn_dua_ratus_ribu,R.id.btn_lima_puluh_ribu,R.id.btn_uang_pas,R.id.btn_dana,R.id.btn_gopay,R.id.btn_ovo,R.id.btn_shopee_pay));
                setNominalPembayaran(100000,false);
                setPaymentMethod("");
                break;
            case R.id.btn_dana:
                setBtnSelected(view.getId(), Arrays.asList(R.id.btn_seratus_lima_puluh_ribu,R.id.btn_dua_puluh_ribu,R.id.btn_dua_ratus_ribu,R.id.btn_lima_puluh_ribu,R.id.btn_seratus_ribu,R.id.btn_uang_pas,R.id.btn_gopay,R.id.btn_ovo,R.id.btn_shopee_pay));
                setNominalPembayaran(Integer.parseInt(totalBelanja),false);
                setPaymentMethod("DANA");
                break;
            case R.id.btn_gopay:
                setBtnSelected(view.getId(), Arrays.asList(R.id.btn_seratus_lima_puluh_ribu,R.id.btn_dua_puluh_ribu,R.id.btn_dua_ratus_ribu,R.id.btn_lima_puluh_ribu,R.id.btn_seratus_ribu,R.id.btn_uang_pas,R.id.btn_dana,R.id.btn_ovo,R.id.btn_shopee_pay));
                setNominalPembayaran(Integer.parseInt(totalBelanja),false);
                setPaymentMethod("GoPay");
                break;
            case R.id.btn_ovo:
                setBtnSelected(view.getId(), Arrays.asList(R.id.btn_seratus_lima_puluh_ribu,R.id.btn_dua_puluh_ribu,R.id.btn_dua_ratus_ribu,R.id.btn_lima_puluh_ribu,R.id.btn_seratus_ribu,R.id.btn_uang_pas,R.id.btn_dana,R.id.btn_gopay,R.id.btn_shopee_pay));
                setNominalPembayaran(Integer.parseInt(totalBelanja),false);
                setPaymentMethod("OVO");
                break;
            case R.id.btn_shopee_pay:
                setBtnSelected(view.getId(), Arrays.asList(R.id.btn_seratus_lima_puluh_ribu,R.id.btn_dua_puluh_ribu,R.id.btn_dua_ratus_ribu,R.id.btn_lima_puluh_ribu,R.id.btn_seratus_ribu,R.id.btn_uang_pas,R.id.btn_dana,R.id.btn_gopay,R.id.btn_ovo));
                setNominalPembayaran(Integer.parseInt(totalBelanja),false);
                setPaymentMethod("Shopee Pay");
                break;
            case R.id.btn_final_bayar:

                //Crashlytics.getInstance().crash();

                if(TextUtils.isEmpty(etNominalPembayaran.getText().toString())){
                    Toasty.warning(getApplicationContext(),getString(R.string.error_nol_bayar),Toasty.LENGTH_LONG).show();
                }else if (Integer.parseInt(etNominalPembayaran.getText().toString()) < Integer.parseInt(totalBelanja)){
                    Toasty.warning(getApplicationContext(),getString(R.string.error_jumlah_bayar),Toasty.LENGTH_LONG).show();
                }else {

                    DelayedProgressDialog progressDialog = new DelayedProgressDialog();
                    progressDialog.setCancelable(false);

                    createSales(user,progressDialog,products,metodePembayaran);

                }

                break;
        }
    }

    @Override
    public void connectingWithPrinter() {
        //Snackbar.make(clContainer, "Connecting Printer...", Snackbar.LENGTH_LONG).show();
        Toast.makeText(this,"Connecting Printer...",Toast.LENGTH_LONG).show();
    }

    @Override
    public void connectionFailed(String s) {
        //Snackbar.make(clContainer, "Fail Connecting Printer: "+s, Snackbar.LENGTH_LONG).show();
        Toast.makeText(this,"Fail Connecting Printer: "+s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(String s) {
        //Snackbar.make(clContainer, "Error: "+s, Snackbar.LENGTH_LONG).show();
        Toast.makeText(this, "Error: "+s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMessage(String s) {

        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
        //Snackbar.make(clContainer, s, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void printingOrderSentSuccessfully() {
        //Snackbar.make(clContainer, "Order sent to printer", Snackbar.LENGTH_LONG).show();

        Toast.makeText(this,"Order sent to printer",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK){
            initPrinting();
        }
        //changePairAndUnpair();
    }

    private void initPrinting() {
        if (!Printooth.INSTANCE.hasPairedPrinter()){
            printing = Printooth.INSTANCE.printer();
        }
        if (printing != null){
            printing.setPrintingCallback(this);
        }
    }

    private void pairPrinter() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null){
            Toasty.error(getApplicationContext(), "Bluetooth Printer is not detected!",Toasty.LENGTH_LONG).show();
        }else {
            startActivityForResult(new Intent(ActivityPembayaran.this, ScanningActivity.class),ScanningActivity.SCANNING_FOR_PRINTER);
        }
    }

    private void printText(String waktu, String noTrx,List<Product> products, String cash, String kembalian) {

        ArrayList<Printable> printables = new ArrayList<>();

        int ppn = 0;
        int subTotal = 0;
        int diskon = 0;
        int total = 0;

        String branchName = user.getBranch_name() == null ? " " : user.getBranch_name();
        String branchAddress = user.getBranch_address() == null ? " " : user.getBranch_address();
        String phoneNo = user.getPhone_no() == null ? " " : user.getPhone_no();
        String name = user.getName() == null ? " " : user.getName();

        printables.add(new TextPrintable.Builder()
                .setText("--------------------------------")
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("Kopi Pintar")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText(branchName)
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText(branchAddress)
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText(phoneNo)
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("kopipintar.com")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("--------------------------------")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText("Waktu :   "+waktu)
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText("No.Trx:   "+noTrx)
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText("Kasir :   "+name)
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText("--------------------------------")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        for (Product product : products){

            ppn += product.getPpn();
            diskon += product.getDiscount();
            subTotal += product.getTotal_price();

            printables.add(new TextPrintable.Builder()
                    .setText(product.getProduct_name()+"        ")
                    .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                    .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                    .setNewLinesAfter(1)
                    .build());

            int hargaSatuan = product.getTotal_price() / product.getQty();
            int diskonSatuan = product.getDiscount() / product.getQty();

            printables.add(new TextPrintable.Builder()
                    .setText(product.getQty()+" x "+hargaSatuan)
                    .setAlignment(DefaultPrinter.Companion.getALIGNMENT_RIGHT())
                    .setNewLinesAfter(1)
                    .build());

            if(diskonSatuan > 0){
                printables.add(new TextPrintable.Builder()
                        .setText("Diskon        ")
                        .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                        .build());
                printables.add(new TextPrintable.Builder()
                        .setText(product.getQty()+" x -"+diskonSatuan)
                        .setNewLinesAfter(1)
                        .build());
            }

//            for (Product.Item item : product.getItems()){
//                printables.add(new TextPrintable.Builder()
//                        .setText("|- "+item.getQty()+" x "+item.getItem_name())
//                        .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
//                        .setNewLinesAfter(1)
//                        .build());
//
//                Log.i("PRODUKITEMLENGKAP",product.getProduct_name()+" Itemnya: "+item.getQty()+" x "+item.getItem_name());
//            }

        }

        total = subTotal - diskon;

        printables.add(new TextPrintable.Builder()
                .setText("--------------------------------")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText("PPN                    ")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .build());
        printables.add(new TextPrintable.Builder()
                .setText(""+ppn)
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText("Subtotal               ")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .build());
        printables.add(new TextPrintable.Builder()
                .setText(""+subTotal)
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText("Diskon                 ")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .build());
        printables.add(new TextPrintable.Builder()
                .setText("-"+diskon)
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("--------------------------------")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("Total                  ")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .build());
        printables.add(new TextPrintable.Builder()
                .setText(""+total)
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("--------------------------------")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText("Cash                   ")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .build());
        printables.add(new TextPrintable.Builder()
                .setText(cash)
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText("Kembalian              ")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .build());
        printables.add(new TextPrintable.Builder()
                .setText(kembalian)
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("--------------------------------")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("Struk belanja yang sah dari")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("Kopi Pintar")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("* Terima Kasih *")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("")
                .setNewLinesAfter(2)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("--------------------------------")
                .build());

        Printooth.INSTANCE.printer().print(printables);

    }
}
