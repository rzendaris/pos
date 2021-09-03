package id.kopipintar.pos.model;

import java.util.List;

public class RiwayatPenjualan {
    private List<RiwayatPenjualanData> trx_list;

    public List<RiwayatPenjualanData> getTrx_list() {
        return trx_list;
    }

    public void setTrx_list(List<RiwayatPenjualanData> trx_list) {
        this.trx_list = trx_list;
    }
}