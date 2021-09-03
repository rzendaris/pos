package id.kopipintar.pos.tabledata.holder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import id.kopipintar.pos.R;
import id.kopipintar.pos.tabledata.model.CellModel;

public class BtnDelCellViewHolder extends AbstractViewHolder {

    private final ImageButton btnDelete, btnEditPengeluaran, btnDelPengeluaran, btnDone ;
    private final Button btnAction;
    private final TextView tvCancelled;
    private final LinearLayout llPengeluaran, llAction, llDone;
    private int type;


    public BtnDelCellViewHolder(View itemView, int type) {
        super(itemView);

        this.type = type;

        llDone = itemView.findViewById(R.id.ll_done);
        llAction = itemView.findViewById(R.id.ll_action);
        btnAction = itemView.findViewById(R.id.btn_action);
        btnDelete = itemView.findViewById(R.id.btn_delete);
        btnEditPengeluaran = itemView.findViewById(R.id.btn_edit_pengeluaran);
        btnDelPengeluaran = itemView.findViewById(R.id.btn_delete_pengeluaran);
        llPengeluaran = itemView.findViewById(R.id.ll_pengeluaran);
        tvCancelled = itemView.findViewById(R.id.tv_cancelled);
        btnDone = itemView.findViewById(R.id.btn_done);

    }

    public void setCellModel(CellModel p_jModel) {
        int isApproved = Integer.valueOf(p_jModel.getData().toString());
        int isDone = Integer.valueOf(p_jModel.getData().toString());

        if (type == 1){
            if (isApproved == 1) {
                tvCancelled.setVisibility(View.GONE);
                btnDelete.setVisibility(View.VISIBLE);
            } else if (isApproved == 2) {
                tvCancelled.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.GONE);
            }
        }else{

            if (isDone == 3){
                llDone.setVisibility(View.VISIBLE);
                btnDone.setVisibility(View.VISIBLE);
                tvCancelled.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
                llPengeluaran.setVisibility(View.GONE);
                llAction.setVisibility(View.VISIBLE);
                btnAction.setVisibility(View.VISIBLE);
            }else{
                tvCancelled.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
                llPengeluaran.setVisibility(View.GONE);
                llAction.setVisibility(View.VISIBLE);
                btnAction.setVisibility(View.VISIBLE);
            }

        }

    }
}
