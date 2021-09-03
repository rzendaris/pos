package id.kopipintar.pos.model;

public class Paket {
    private String nama;
    private int harga;
    private int diskon;
    private int quantity;
    private String[] items;

    public Paket(String nama, int harga, int diskon, int quantity, String[] items) {
        this.nama = nama;
        this.harga = harga;
        this.diskon = diskon;
        this.quantity = quantity;
        this.items = items;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getDiskon() {
        return diskon;
    }

    public void setDiskon(int diskon) {
        this.diskon = diskon;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }
}
