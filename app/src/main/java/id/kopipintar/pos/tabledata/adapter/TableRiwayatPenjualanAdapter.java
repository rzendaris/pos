package id.kopipintar.pos.tabledata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import id.kopipintar.pos.R;
import id.kopipintar.pos.model.RiwayatPenjualan;
import id.kopipintar.pos.tabledata.holder.BtnDelCellViewHolder;
import id.kopipintar.pos.tabledata.holder.CellViewHolder;
import id.kopipintar.pos.tabledata.holder.ColumnHeaderViewHolder;
import id.kopipintar.pos.tabledata.holder.RowHeaderViewHolder;
import id.kopipintar.pos.tabledata.model.CellModel;
import id.kopipintar.pos.tabledata.model.ColumnHeaderModel;
import id.kopipintar.pos.tabledata.model.RowHeaderModel;
import id.kopipintar.pos.tabledata.viewmodel.TableRiwayatPenjualanViewModel;

public class TableRiwayatPenjualanAdapter extends AbstractTableAdapter<ColumnHeaderModel, RowHeaderModel,
        CellModel> {

    private TableRiwayatPenjualanViewModel tableRiwayatPenjualanViewModel;

    public TableRiwayatPenjualanAdapter(Context context) {
        super(context);

        this.tableRiwayatPenjualanViewModel = new TableRiwayatPenjualanViewModel();
    }

    @Override
    public AbstractViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {

        View layout;

        if (viewType == TableRiwayatPenjualanViewModel.BTN_TYPE){
            layout = LayoutInflater.from(mContext).inflate(R.layout.tableview_cell_button_layout,
                    parent, false);

            // Create a Cell ViewHolder
            return new BtnDelCellViewHolder(layout,1);
        }else{
            layout = LayoutInflater.from(mContext).inflate(R.layout.tableview_cell_normal_layout,
                    parent, false);

            // Create a Cell ViewHolder
            return new CellViewHolder(layout);
        }


    }

    @Override
    public void onBindCellViewHolder(AbstractViewHolder holder, Object cellItemModel, int columnPosition, int rowPosition) {
        CellModel cell = (CellModel) cellItemModel;

        if (holder instanceof CellViewHolder){
            ((CellViewHolder) holder).setCellModel(cell, columnPosition);
        }else{
            ((BtnDelCellViewHolder) holder).setCellModel(cell);
        }
    }

    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(mContext).inflate(R.layout
                .tableview_column_header_layout, parent, false);

        return new ColumnHeaderViewHolder(layout, getTableView());
    }

    @Override
    public void onBindColumnHeaderViewHolder(AbstractViewHolder holder, Object columnHeaderItemModel, int columnPosition) {
        ColumnHeaderModel columnHeader = (ColumnHeaderModel) columnHeaderItemModel;

        // Get the holder to update cell item text
        ColumnHeaderViewHolder columnHeaderViewHolder = (ColumnHeaderViewHolder) holder;

        columnHeaderViewHolder.setColumnHeaderModel(columnHeader, columnPosition);
    }

    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(ViewGroup parent, int viewType) {
        // Get Row Header xml Layout
        View layout = LayoutInflater.from(mContext).inflate(R.layout.tableview_row_header_layout,
                parent, false);

        // Create a Row Header ViewHolder
        return new RowHeaderViewHolder(layout);
    }

    @Override
    public void onBindRowHeaderViewHolder(AbstractViewHolder holder, Object rowHeaderItemModel, int rowPosition) {

        RowHeaderModel rowHeaderModel = (RowHeaderModel) rowHeaderItemModel;

        RowHeaderViewHolder rowHeaderViewHolder = (RowHeaderViewHolder) holder;
        rowHeaderViewHolder.row_header_textview.setText(rowHeaderModel.getData());
    }

    @Override
    public View onCreateCornerView() {
        return LayoutInflater.from(mContext).inflate(R.layout.tableview_corner_layout, null, false);
    }

    @Override
    public int getColumnHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRowHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getCellItemViewType(int position) {
        return tableRiwayatPenjualanViewModel.getCellItemViewType(position);
    }

    public void setRiwayatPenjualanList(RiwayatPenjualan riwayatPenjualans) {
        // Generate the lists that are used to TableViewAdapter
        tableRiwayatPenjualanViewModel.generateListForTableView(riwayatPenjualans);

        // Now we got what we need to show on TableView.
        setAllItems(tableRiwayatPenjualanViewModel.getColumHeaderModeList(), tableRiwayatPenjualanViewModel
                .getRowHeaderModelList(), tableRiwayatPenjualanViewModel.getCellModelList());
    }










}