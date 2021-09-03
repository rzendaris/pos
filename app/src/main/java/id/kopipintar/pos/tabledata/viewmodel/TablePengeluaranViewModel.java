package id.kopipintar.pos.tabledata.viewmodel;

import java.util.ArrayList;
import java.util.List;

import id.kopipintar.pos.model.Pengeluaran;
import id.kopipintar.pos.tabledata.model.CellModel;
import id.kopipintar.pos.tabledata.model.ColumnHeaderModel;
import id.kopipintar.pos.tabledata.model.RowHeaderModel;

public class TablePengeluaranViewModel {

    public static final int BTN_TYPE = 5;

    private List<ColumnHeaderModel> mColumnHeaderModelList;
    private List<RowHeaderModel> mRowHeaderModelList;
    private List<List<CellModel>> mCellModelList;

    public int getCellItemViewType(int column) {

        if (column == 6) {// 6. column header action.
            return BTN_TYPE;
        }
        return 0;
    }

    private List<ColumnHeaderModel> createColumnHeaderModelList() {
        List<ColumnHeaderModel> list = new ArrayList<>();

        // Create Column Headers
        list.add(new ColumnHeaderModel("Tanggal"));
        list.add(new ColumnHeaderModel("Nama Item"));
        list.add(new ColumnHeaderModel("Quantity"));
        list.add(new ColumnHeaderModel("Satuan"));
        list.add(new ColumnHeaderModel("Harga Satuan"));
        list.add(new ColumnHeaderModel("Total Harga"));
        list.add(new ColumnHeaderModel("Action"));

        return list;
    }

    private List<List<CellModel>> createCellModelList(Pengeluaran pengeluarans) {
        List<List<CellModel>> lists = new ArrayList<>();

        // Creating cell model list from Pengeluaran list for Cell Items

        for (int i = 0; i < pengeluarans.getExpense_list().size(); i++) {

            List<CellModel> list = new ArrayList<>();

            // The order should be same with column header list;
            list.add(new CellModel("1-" + i, pengeluarans.getExpense_list().get(i).getExpense_date()));
            list.add(new CellModel("2-" + i, pengeluarans.getExpense_list().get(i).getExpense_name()));
            list.add(new CellModel("3-" + i, pengeluarans.getExpense_list().get(i).getQty()));
            list.add(new CellModel("4-" + i, pengeluarans.getExpense_list().get(i).getMeasure_name()));
            list.add(new CellModel("5-" + i, pengeluarans.getExpense_list().get(i).getPrice()));
            list.add(new CellModel("6-" + i, pengeluarans.getExpense_list().get(i).getTotal_price()));

            if (pengeluarans.getExpense_list().get(i).getIs_approved() == 3){
                list.add(new CellModel("7-" + i, 3));                    // Action
            }else {
                list.add(new CellModel("7-" + i, 1));
            }           // Action

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


    public void generateListForTableView(Pengeluaran pengeluarans) {
        mColumnHeaderModelList = createColumnHeaderModelList();
        mCellModelList = createCellModelList(pengeluarans);
        mRowHeaderModelList = createRowHeaderList(pengeluarans.getExpense_list().size());
    }

}



