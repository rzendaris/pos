package id.kopipintar.pos.model;

public class CancelSales {

    private int trx_list;
    private boolean status;

    public int getTrx_list() {
        return trx_list;
    }

    public void setTrx_list(int trx_list) {
        this.trx_list = trx_list;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
