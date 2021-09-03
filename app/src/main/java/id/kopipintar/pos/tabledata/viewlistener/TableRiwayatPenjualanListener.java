package id.kopipintar.pos.tabledata.viewlistener;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.ITableView;
import com.evrencoskun.tableview.listener.ITableViewListener;

import id.kopipintar.pos.ActivityPembatalanPenjualan;
import id.kopipintar.pos.model.RiwayatPenjualan;

public class TableRiwayatPenjualanListener implements ITableViewListener {

    private static final String LOG_TAG = TableRiwayatPenjualanListener.class.getSimpleName();

    public static final String TRX_NO = "trxNo";
    public static final String TGL = "tgl";
    public static final String TOTAL_ITEM = "totalItem";
    public static final String TOTAL_HARGA = "totalHarga";

    private ITableView mTableView;
    private Activity activity;
    private RiwayatPenjualan riwayatPenjualan;

    public TableRiwayatPenjualanListener(ITableView mTableView, Activity activity, RiwayatPenjualan riwayatPenjualan) {
        this.mTableView = mTableView;
        this.activity = activity;
        this.riwayatPenjualan = riwayatPenjualan;
    }

    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
        Log.d(LOG_TAG, "onCellClicked has been clicked for x= " + column + " y= " + row);

        if (column == 4 && riwayatPenjualan.getTrx_list().get(row).getIs_approved() == 1){

            Intent myIntent = new Intent(activity, ActivityPembatalanPenjualan.class);
            myIntent.putExtra(TRX_NO, riwayatPenjualan.getTrx_list().get(row).getTrx_no());
            myIntent.putExtra(TGL, riwayatPenjualan.getTrx_list().get(row).getTrx_date());
            myIntent.putExtra(TOTAL_ITEM, String.valueOf(riwayatPenjualan.getTrx_list().get(row).getQty()));
            myIntent.putExtra(TOTAL_HARGA, String.valueOf(riwayatPenjualan.getTrx_list().get(row).getTotal_price()));
            activity.startActivity(myIntent);

        }

    }

    @Override
    public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
        Log.d(LOG_TAG, "onCellLongPressed has been clicked for " + row);
    }

    @Override
    public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int
            column) {
        Log.d(LOG_TAG, "onColumnHeaderClicked has been clicked for " + column);
    }

    @Override
    public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
        Log.d(LOG_TAG, "onRowHeaderClicked has been clicked for " + row);
    }

    @Override
    public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
        Log.d(LOG_TAG, "onRowHeaderLongPressed has been clicked for " + row);
    }
}
