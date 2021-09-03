package id.kopipintar.pos.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.counterview.CounterListner;
import edu.counterview.CounterView;
import id.kopipintar.pos.ActivityPembayaran;
import id.kopipintar.pos.ActivityPenjualan;
import id.kopipintar.pos.R;
import id.kopipintar.pos.model.Product;
import id.kopipintar.pos.utils.CustomListView;

public class PaketAdapter extends RecyclerView.Adapter<PaketAdapter.ViewHolder> {

    private Context context;
    private List<Product> products;
    private Map<Integer, Integer> integerMap;
    private TextView tvTotal;
    private TextView tvDiskon;
    private TextView tvSubTotal;
    private Button btnReset, btnBayar;

    int ttotal;
    int tsubTotal;
    int tdiskon;

    public PaketAdapter(Context context, List<Product> products, Map<Integer, Integer> integerMap, List<TextView> textViews, Button btnReset, Button btnBayar) {
        this.context = context;
        this.products = products;
        this.integerMap = integerMap;
        this.tvTotal = textViews.get(0);
        this.tvDiskon = textViews.get(1);
        this.tvSubTotal = textViews.get(2);

        this.btnBayar = btnBayar;
        this.btnReset = btnReset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_paket,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ttotal = Integer.parseInt(tvTotal.getText().toString());
        tsubTotal = Integer.parseInt(tvSubTotal.getText().toString());
        tdiskon = Integer.parseInt(tvDiskon.getText().toString());

        holder.labelDiskon.setText("Diskon  :");
        holder.labelHarga.setText("Harga    :");

        Product product = products.get(position);

        final int[] jmlProduk = {integerMap.get(product.getProduct_id())};

        int diskon;
        int harga;

        holder.counterView.setStartCounterValue(String.valueOf(jmlProduk[0]));
        holder.counterView.setColor(R.color.red,R.color.green,R.color.black);

        diskon = product.getDiscount()* jmlProduk[0];
        harga = product.getTotal_price()* jmlProduk[0];

        List<String> itemNames = new ArrayList<>();
        for (Product.Item item : product.getItems()){
            itemNames.add(item.getQty()* jmlProduk[0] +" X "+item.getItem_name());
        }

        holder.tvDiskon.setText(String.valueOf(diskon));
        holder.tvHarga.setText(String.valueOf(harga));
        holder.tvNamaPaket.setText(product.getProduct_name());

        holder.lvItem.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, itemNames));

        if (jmlProduk[0] != 1){
            holder.btn_remove.setVisibility(View.GONE);
        }else {
            setRemoveBtn(holder, product);
        }

        final int[] sisa = {jmlProduk[0]};

        holder.counterView.setCounterListener(new CounterListner() {
            @Override
            public void onIncClick(String s) {

                product.setQty(product.getQty() + 1);
                int index = products.indexOf(product);
                products.set(index,product);

                sisa[0]++;

                jmlProduk[0] = Integer.parseInt(s);

                valueOnChange(s, product, holder, jmlProduk,true);

                if (jmlProduk[0] == 1){
                    setRemoveBtn(holder, product);
                }else {
                    holder.btn_remove.setVisibility(View.GONE);
                }

            }

            @Override
            public void onDecClick(String s) {

                product.setQty(product.getQty() - 1);
                int index = products.indexOf(product);
                products.set(index,product);

                sisa[0]--;

                jmlProduk[0] = Integer.parseInt(s);

                if (jmlProduk[0] == 1){
                    setRemoveBtn(holder, product);
                }else {
                    holder.btn_remove.setVisibility(View.GONE);
                }

                if (sisa[0] > 0){
                    valueOnChange(s, product, holder, jmlProduk,false);
                }

            }
        });


        btnBayar.setOnClickListener(view -> {

            Intent intent = new Intent(context, ActivityPembayaran.class);
            intent.putExtra(ActivityPenjualan.TOTAL_BELANJA,tvTotal.getText().toString());

            intent.putExtra(ActivityPenjualan.PRODUCTS, (Serializable) products);
            context.startActivity(intent);

            products.clear();
            integerMap.clear();

            PaketAdapter.this.notifyDataSetChanged();
        });

        btnReset.setOnClickListener(view -> {

            products.clear();
            integerMap.clear();

            PaketAdapter.this.notifyDataSetChanged();

            tvDiskon.setText(String.valueOf(0));
            tvSubTotal.setText(String.valueOf(0));
            tvTotal.setText(String.valueOf(0));
        });

    }

    private void setRemoveBtn(@NonNull ViewHolder holder, Product product) {
        holder.btn_remove.setVisibility(View.VISIBLE);
        holder.btn_remove.setOnClickListener(view -> {

            products.remove(product);
            integerMap.remove(product.getProduct_id());

            holder.btn_remove.setVisibility(View.GONE);

            tsubTotal = tsubTotal - product.getTotal_price();
            tdiskon = tdiskon - product.getDiscount();

            int newTotal = tsubTotal - tdiskon;

            tvSubTotal.setText(String.valueOf(tsubTotal));
            tvTotal.setText(String.valueOf(newTotal));
            tvDiskon.setText(String.valueOf(tdiskon));

            PaketAdapter.this.notifyDataSetChanged();

        });
    }

    private void valueOnChange(String s, Product product, @NonNull ViewHolder holder, int[] jmlProduk, boolean isAdd) {
        int newDiskon = product.getDiscount() * Integer.parseInt(s);
        int newHarga = product.getTotal_price() * Integer.parseInt(s);

        holder.tvDiskon.setText(String.valueOf(newDiskon));
        holder.tvHarga.setText(String.valueOf(newHarga));

        jmlProduk[0] = Integer.parseInt(s);

        List<String> itemNames = new ArrayList<>();
        for (Product.Item item : product.getItems()) {
            itemNames.add(item.getQty() * jmlProduk[0] + " X " + item.getItem_name());
        }

        holder.lvItem.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, itemNames));

        if (isAdd){
            tsubTotal = tsubTotal + product.getTotal_price();
            tdiskon = tdiskon + product.getDiscount();
        }else {
            tsubTotal = tsubTotal - product.getTotal_price();
            tdiskon = tdiskon - product.getDiscount();
        }

        int newTotal = tsubTotal - tdiskon;

        tvSubTotal.setText(String.valueOf(tsubTotal));
        tvTotal.setText(String.valueOf(newTotal));
        tvDiskon.setText(String.valueOf(tdiskon));

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CounterView counterView;
        TextView tvHarga, labelHarga;
        TextView tvDiskon, labelDiskon;
        TextView tvNamaPaket, btn_remove;
        CustomListView lvItem;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            labelHarga = itemView.findViewById(R.id.label_harga);
            labelDiskon = itemView.findViewById(R.id.label_diskon);
            counterView =  itemView.findViewById(R.id.cv);
            tvHarga = itemView.findViewById(R.id.tv_harga);
            tvNamaPaket = itemView.findViewById(R.id.nama_paket);
            tvDiskon = itemView.findViewById(R.id.tv_diskon1);
            lvItem = itemView.findViewById(R.id.lv_item);
            btn_remove = itemView.findViewById(R.id.btn_remove);
        }
    }


}
