package id.kopipintar.pos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ActivityPembayaranBerhasil extends AppCompatActivity {

    private Button btnOke;
    private TextView tvFinalTotalItem, tvFinalTotalBelanja, tvFinalCreatedDate, tvFinalNoTrx, tv_kembalian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_berhasil);

        initView();

        Intent intent = getIntent();
        tvFinalCreatedDate.setText(intent.getStringExtra(ActivityPembayaran.WAKTU_TRANSAKSI));
        tvFinalTotalBelanja.setText(intent.getStringExtra(ActivityPembayaran.TOTAL_BELANJA));
        tvFinalTotalItem.setText(intent.getStringExtra(ActivityPembayaran.TOTAL_ITEM));
        tvFinalNoTrx.setText(intent.getStringExtra(ActivityPembayaran.NO_TRX));
        tv_kembalian.setText(intent.getStringExtra(ActivityPembayaran.KEMBALIAN));

        btnOke.setOnClickListener(view -> {
            startActivity(new Intent(ActivityPembayaranBerhasil.this, ActivityPenjualan.class));
            finish();
        });
    }

    private void initView() {

        btnOke = findViewById(R.id.btn_oke);
        tvFinalTotalItem = findViewById(R.id.tv_total_item);
        tvFinalTotalBelanja = findViewById(R.id.tv_total_harga);
        tvFinalCreatedDate = findViewById(R.id.tv_wktu_trx);
        tvFinalNoTrx = findViewById(R.id.tv_no_trx);
        tv_kembalian = findViewById(R.id.tv_kembalian);

    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(ActivityPembayaranBerhasil.this, ActivityPenjualan.class));
        finish();

        super.onBackPressed();
    }
}
