package id.kopipintar.pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import id.kopipintar.pos.ActivityPenjualan;
import id.kopipintar.pos.R;
import id.kopipintar.pos.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> products;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.card_item,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String baseURL = "http://pos.kopipintar.com/images/";

        Picasso.get().load(baseURL+products.get(position).getPicture()).into(holder.ivProduct);

        holder.ivProduct.setOnClickListener(new ActivityPenjualan.PaketOnClickListener(products.get(position)));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivProduct;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct =  itemView.findViewById(R.id.iv_menu);
        }
    }

}
