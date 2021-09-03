package id.kopipintar.pos.model;

import java.util.List;

public class Pengeluaran {

    private List<PengeluaranData> expense_list;

    public List<PengeluaranData> getExpense_list() {
        return expense_list;
    }

    public void setExpense_list(List<PengeluaranData> expense_list) {
        this.expense_list = expense_list;
    }
}
