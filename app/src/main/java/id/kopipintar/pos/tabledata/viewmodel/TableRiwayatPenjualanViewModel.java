package id.kopipintar.pos.tabledata.viewmodel;

import java.util.ArrayList;
import java.util.List;

import id.kopipintar.pos.model.RiwayatPenjualan;
import id.kopipintar.pos.tabledata.model.CellModel;
import id.kopipintar.pos.tabledata.model.ColumnHeaderModel;
import id.kopipintar.pos.tabledata.model.RowHeaderModel;

public class TableRiwayatPenjualanViewModel {

    public static final int BTN_TYPE = 1;

    private List<ColumnHeaderModel> mColumnHeaderModelList;
    private List<RowHeaderModel> mRowHeaderModelList;
    private List<List<CellModel>> mCellModelList;

    public int getCellItemViewType(int column) {

        if (column == 4) {// 4. column header action.
            return BTN_TYPE;
        }
        return 0;
    }

    private List<ColumnHeaderModel> createColumnHeaderModelList() {
        List<ColumnHeaderModel> list = new ArrayList<>();

        // Create Column Headers
        list.add(new ColumnHeaderModel("Tanggal"));
        list.add(new ColumnHeaderModel("Nomor Transaksi"));
        list.add(new ColumnHeaderModel("Total Item"));
        list.add(new ColumnHeaderModel("Total Harga"));
        list.add(new ColumnHeaderModel("Action"));

        return list;
    }

    private List<List<CellModel>> createCellModelList(RiwayatPenjualan riwayatPenjualans) {
        List<List<CellModel>> lists = new ArrayList<>();

        // Creating cell model list from RiwayatPenjualan list for Cell Items

        for (int i = 0; i < riwayatPenjualans.getTrx_list().size(); i++) {

            List<CellModel> list = new ArrayList<>();

            // The order should be same with column header list;
            list.add(new CellModel("1-" + i, riwayatPenjualans.getTrx_list().get(i).getTrx_date()));       // Tanggal
            list.add(new CellModel("2-" + i, riwayatPenjualans.getTrx_list().get(i).getTrx_no()));   // No Transaksi
            list.add(new CellModel("3-" + i, riwayatPenjualans.getTrx_list().get(i).getQty()));     // Total Item
            list.add(new CellModel("4-" + i, riwayatPenjualans.getTrx_list().get(i).getTotal_price()));    // Total Harga

            if (riwayatPenjualans.getTrx_list().get(i).getIs_approved() == 1){
                list.add(new CellModel("5-" + i, 1));                    // Action
            }else {
                list.add(new CellModel("5-" + i, 2));
            }


            // Add
            lists.add(list);
        }

        return lists;
    }

    private List<RowHeaderModel> createRowHeaderList(int size) {
        List<RowHeaderModel> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            // In this example, Row headers just shows the index of the TableView List.
            list.add(new RowHeaderModel(String.valueOf(i + 1)));
        }
        return list;
    }


    public List<ColumnHeaderModel> getColumHeaderModeList() {
        return mColumnHeaderModelList;
    }

    public List<RowHeaderModel> getRowHeaderModelList() {
        return mRowHeaderModelList;
    }

    public List<List<CellModel>> getCellModelList() {
        return mCellModelList;
    }


    public void generateListForTableView(RiwayatPenjualan riwayatPenjualans) {
        mColumnHeaderModelList = createColumnHeaderModelList();
        mCellModelList = createCellModelList(riwayatPenjualans);
        mRowHeaderModelList = createRowHeaderList(riwayatPenjualans.getTrx_list().size());
    }

}



