package id.kopipintar.pos.model;

public class SalesSuccessResponse {

    private String trx_no;
    private String created_at;
    private Boolean status;

    public String getTrx_no() {
        return trx_no;
    }

    public void setTrx_no(String trx_no) {
        this.trx_no = trx_no;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
