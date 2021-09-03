package id.kopipintar.pos.model;

public class RiwayatPenjualanData{
    private String trx_date;
    private String trx_no;
    private int total_price;
    private int qty;
    private int is_approved;

    public String getTrx_date() {
        return trx_date;
    }

    public void setTrx_date(String trx_date) {
        this.trx_date = trx_date;
    }

    public String getTrx_no() {
        return trx_no;
    }

    public void setTrx_no(String trx_no) {
        this.trx_no = trx_no;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getIs_approved() {
        return is_approved;
    }

    public void setIs_approved(int is_approved) {
        this.is_approved = is_approved;
    }
}
