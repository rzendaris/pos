package id.kopipintar.pos.tabledata.viewlistener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.ITableView;
import com.evrencoskun.tableview.listener.ITableViewListener;

import java.util.Map;

import es.dmoral.toasty.Toasty;
import id.kopipintar.pos.ActivityLogin;
import id.kopipintar.pos.ActivityTambahPengeluaran;
import id.kopipintar.pos.R;
import id.kopipintar.pos.api.RetrofitClientInstance;
import id.kopipintar.pos.model.Pengeluaran;
import id.kopipintar.pos.model.PengeluaranData;
import id.kopipintar.pos.utils.CommonUtils;
import id.kopipintar.pos.utils.DelayedProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TablePengeluaranListener implements ITableViewListener {

    private static final String LOG_TAG = TablePengeluaranListener.class.getSimpleName();
    public static final String EXPENSE_DATA = "expense_data";

    private ITableView mTableView;
    private Activity activity;
    private Pengeluaran pengeluaran;
    private DelayedProgressDialog progressDialog;
    private String token;
    private FragmentManager fragmentManager;

    public TablePengeluaranListener(ITableView mTableView, Activity activity, Pengeluaran pengeluaran, DelayedProgressDialog progressDialog, String token, FragmentManager fragmentManager) {
        this.mTableView = mTableView;
        this.activity = activity;
        this.pengeluaran = pengeluaran;
        this.progressDialog = progressDialog;
        this.token = token;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
        Log.d(LOG_TAG, "onCellClicked has been clicked for x= " + column + " y= " + row);

        if (column == 6 && pengeluaran.getExpense_list().get(row).getIs_approved() != 3){

            initDialog(pengeluaran.getExpense_list().get(row),token);

        }
    }

    private void initDialog(PengeluaranData pengeluaranData, String token) {
        Dialog customDialog = new Dialog(activity);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.dialog_action);
        customDialog.setCancelable(true);
        customDialog.show();

        Button delete = customDialog.findViewById(R.id.btn_delete_expense);
//        Button edit = customDialog.findViewById(R.id.btn_edit_expense);

        delete.setOnClickListener(v ->{
            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("Warning!");
            alertDialog.setMessage("Anda yakin ingin hapus pengeluaran?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ya",
                    (dialog, i) -> {
                        deleteExpense(pengeluaranData.getExpense_id(), token, customDialog);
                        dialog.dismiss();
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Tidak",
                    (dialog, i) -> dialog.dismiss());
            alertDialog.show();
        });

//        edit.setOnClickListener(view -> {
//            editExpense(pengeluaranData);
//            customDialog.dismiss();
//        });
    }

    private void editExpense(PengeluaranData pengeluaranData) {

        Intent myIntent = new Intent(activity, ActivityTambahPengeluaran.class);
        myIntent.putExtra(EXPENSE_DATA, pengeluaranData);

        activity.startActivity(myIntent);

    }

    private void deleteExpense(int expenseId, String token, Dialog customDialog) {
        progressDialog.show(fragmentManager,"tag");

        RetrofitClientInstance.get().deleteExpense(expenseId,token).enqueue(new Callback<Map<String, Boolean>>() {
            @Override
            public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> response) {
                progressDialog.dismiss();
                customDialog.dismiss();

                if (response.code() == 200){
                    activity.recreate();

                    Toasty.success(activity,"Berhasil hapus pengeluaran!", Toasty.LENGTH_LONG).show();

                }else if(response.code() == 403){
                    Toasty.warning(activity,"Session Expired!", Toasty.LENGTH_LONG).show();
                    CommonUtils.clearUserLoginData(activity);
                    activity.finish();
                    activity.startActivity(new Intent(activity, ActivityLogin.class));
                }else{
                    Toasty.error(activity,response.message(), Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {
                progressDialog.dismiss();
                customDialog.dismiss();

                String msg = "oops something went wrong!";

                if (t.getMessage() != null){
                    msg = t.getMessage();
                }

                Toasty.error(activity,msg, Toasty.LENGTH_LONG).show();
            }
        });
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
