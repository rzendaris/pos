package id.kopipintar.pos.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import id.kopipintar.pos.ActivityLogin;
import id.kopipintar.pos.R;
import id.kopipintar.pos.adapter.ProductAdapter;
import id.kopipintar.pos.api.RetrofitClientInstance;
import id.kopipintar.pos.model.Product;
import id.kopipintar.pos.model.User;
import id.kopipintar.pos.utils.CommonUtils;
import id.kopipintar.pos.utils.DelayedProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPaket extends Fragment {

    private RecyclerView rvContainer;
    private int catId;

    public FragmentPaket(int catId) {
        this.catId = catId;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.container_items, container, false);
        rvContainer = view.findViewById(R.id.rv_container);

        User user = CommonUtils.getUserLoginData(getActivity().getApplicationContext());

        DelayedProgressDialog progressDialog = new DelayedProgressDialog();
        progressDialog.setCancelable(false);
        progressDialog.show(getFragmentManager(),"tag");

        RetrofitClientInstance.get().getProductsById(catId,user.getToken()).enqueue(new Callback<Map<String, List<Product>>>() {
            @Override
            public void onResponse(Call<Map<String, List<Product>>> call, Response<Map<String, List<Product>>> response) {
                progressDialog.dismiss();

                rvContainer.setLayoutManager(new GridLayoutManager(getActivity(), 3));

                if (response.code() == 200){

                    List<Product> products = response.body().get("products");

                    rvContainer.setAdapter(new ProductAdapter(getActivity(),products));

                }else if(response.code() == 403){
                    Toasty.warning(getActivity().getApplicationContext(),"Session Expired!", Toasty.LENGTH_LONG).show();
                    CommonUtils.clearUserLoginData(getActivity().getApplicationContext());
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), ActivityLogin.class));
                }else{
                    Toasty.error(getActivity().getApplicationContext(),response.message(), Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, List<Product>>> call, Throwable t) {
                progressDialog.dismiss();
                String msg = "oops something went wrong!";

                if (t.getMessage() != null){
                    msg = t.getMessage();
                }

                Toasty.error(getActivity().getApplicationContext(),msg, Toasty.LENGTH_LONG).show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
