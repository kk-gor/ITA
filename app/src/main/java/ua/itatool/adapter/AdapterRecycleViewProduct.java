package ua.itatool.adapter;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kk.test002.databinding.ItemProductBinding;
import com.example.kk.test002.databinding.ItemProductNoCountBinding;

import java.util.List;

import ua.itatool.database.model.Product;

/**
 * Created by djdf.crash on 07.03.2018.
 */

public class AdapterRecycleViewProduct extends RecyclerView.Adapter<AdapterRecycleViewProduct.ProductViewHolder> {

    private static int TYPE_VIEW_COUNT = 1;
    private static int TYPE_VIEW_NO_COUNT = 2;
    private List<Product> productList;

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_VIEW_COUNT) {
            ItemProductBinding binding = ItemProductBinding.inflate(inflater, parent, false);
            return new ProductViewHolder(binding.getRoot(), viewType);
        }else {
            ItemProductNoCountBinding binding = ItemProductNoCountBinding.inflate(inflater, parent, false);
            return new ProductViewHolder(binding.getRoot(), viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {

        Product product = productList.get(position);
        String strCount = product.getCount();

        try {
            int count = Integer.valueOf(strCount);
            if (count == 0){
                return TYPE_VIEW_NO_COUNT;
            }else {
                return TYPE_VIEW_COUNT;
            }
        }catch (Exception e){
            return TYPE_VIEW_COUNT;
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        Product product = productList.get(position);

        if (holder.viewType == TYPE_VIEW_COUNT) {
            holder.productBinding.setItemProduct(product);
        }else {
            holder.productNoCountBinding.setItemProduct(product);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public List<Product> getProductList() {
        return productList;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{

        ItemProductBinding productBinding;
        ItemProductNoCountBinding productNoCountBinding;
        int viewType;

        ProductViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            if (viewType == TYPE_VIEW_COUNT) {
                productBinding = DataBindingUtil.bind(itemView);
            }else {
                productNoCountBinding = DataBindingUtil.bind(itemView);
            }
        }
    }
}
