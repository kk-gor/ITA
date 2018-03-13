package ua.itatool.vivewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.kk.test002.R;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import ua.itatool.database.DatabaseUtil;
import ua.itatool.database.model.Product;
import ua.itatool.services.UpdateProductsService;
import ua.itatool.utils.DateTimeUtil;

/**
 * Created by djdf.crash on 07.03.2018.
 */

public class MainViewModel extends AndroidViewModel {


    private MutableLiveData<MainModelBinding> modelBindingMutableLiveData= new MutableLiveData<>();
    private MutableLiveData<List<Product>> filteringProductLiveData = new MutableLiveData<>();


    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<MainModelBinding> getModelBindingMutableLiveData() {
        if (modelBindingMutableLiveData == null || modelBindingMutableLiveData.getValue() == null){
            MainModelBinding modelBinding = new MainModelBinding();
            modelBindingMutableLiveData.setValue(modelBinding);
        }
        return modelBindingMutableLiveData;
    }

    public void onUpdateAllProduct(final MainModelBinding mainModelBinding){
        if (mainModelBinding.getShowProgress().get() != View.VISIBLE) {

            mainModelBinding.getShowProgress().set(View.VISIBLE);

            Intent updateService = new Intent(this.getApplication(), UpdateProductsService.class);
            getApplication().startService(updateService);

            final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int showProgress = intent.getIntExtra(UpdateProductsService.EXTRA_KEY_OUT, View.GONE);

                    mainModelBinding.getShowProgress().set(showProgress);

                    long millis = System.currentTimeMillis();

                    long lastModified = PreferenceManager.getDefaultSharedPreferences(context).getLong("sync_modified_file",0);

                    String dateUpdate = DateTimeUtil.getDate(lastModified);

                    String strUpdateInfo = context.getResources().getString(R.string.update_info_data_from, dateUpdate) + " " +
                            context.getResources().getString(R.string.update_info_last_check,"щойно" , "");


                    mainModelBinding.getUpdateInfo().set(strUpdateInfo);

                    PreferenceManager.getDefaultSharedPreferences(context).edit().putLong("update_info",millis).apply();

                    Toast.makeText(context, intent.getStringExtra(UpdateProductsService.MESSAGE_TEXT), Toast.LENGTH_SHORT).show();
                    context.unregisterReceiver(this);

                }
            };

            IntentFilter intentFilter = new IntentFilter(
                    UpdateProductsService.ACTION_REFRESH_INTENT_SERVICE);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            getApplication().registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    public void onArticleChanged(String article) {
        updateProducts(this.getApplication(), article);
    }

    public LiveData<List<Product>> getFilteringProductLiveData() {

        return filteringProductLiveData;
    }


    private void updateProducts(Context ctx, String article) {
        if (!TextUtils.isEmpty(article)){
            if (article.length() >= 4) {
                WeakReference<Context> contextWeakReference = new WeakReference<>(ctx);
                new AsyncGetProduct(contextWeakReference, article).execute();
            }else {
                filteringProductLiveData.setValue(Collections.<Product>emptyList());
            }
        }else {
            filteringProductLiveData.setValue(Collections.<Product>emptyList());
        }
    }
    public class AsyncGetProduct extends AsyncTask<Void,Void,List<Product>>{

        private String article;
        private WeakReference<Context> ctx;

        AsyncGetProduct(WeakReference<Context> ctx, String article) {
            this.ctx = ctx;
            this.article = article;
        }

        @Override
        protected List<Product> doInBackground(Void... voids) {
            List<Product> productList = Collections.emptyList();
            if (ctx.get() != null) {
                boolean sortingArticle = PreferenceManager.getDefaultSharedPreferences(ctx.get()).getBoolean(ctx.get().getResources().getString(R.string.pref_key_sorting),false);
                productList = DatabaseUtil.getProductDao(ctx.get()).getProductsByArticle("%" + article + "%", sortingArticle);
            }
            return productList;
        }

        @Override
        protected void onPostExecute(List<Product> productList) {
            super.onPostExecute(productList);
            filteringProductLiveData.setValue(productList);
        }
    }
}
