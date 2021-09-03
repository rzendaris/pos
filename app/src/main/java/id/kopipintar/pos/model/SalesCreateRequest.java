package id.kopipintar.pos.model;

import java.util.List;

public class SalesCreateRequest {

    private int user_id;
    private int branch_id;
    private String payment_method;
    private List<TransactionDetail> transaction_detail;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(int branch_id) {
        this.branch_id = branch_id;
    }

    public String getPayment_method() { return payment_method; }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public List<TransactionDetail> getTransaction_detail() {
        return transaction_detail;
    }

    public void setTransaction_detail(List<TransactionDetail> transaction_detail) {
        this.transaction_detail = transaction_detail;
    }

    public static class TransactionDetail{
        private int product_id;
        private int qty;
        private int price;
        private int ppn;
        private int discount;

        public TransactionDetail(int product_id, int qty, int price, int ppn, int discount) {
            this.product_id = product_id;
            this.qty = qty;
            this.price = price;
            this.ppn = ppn;
            this.discount = discount;
        }

        public int getProduct_id() {
            return product_id;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getPpn() {
            return ppn;
        }

        public void setPpn(int ppn) {
            this.ppn = ppn;
        }

        public int getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
            this.discount = discount;
        }
    }
}
