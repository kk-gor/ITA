package ua.itatool.view;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.example.kk.test002.R;
import com.example.kk.test002.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ua.itatool.adapter.AdapterRecycleViewProduct;
import ua.itatool.database.model.Product;
import ua.itatool.utils.DateTimeUtil;
import ua.itatool.vivewmodel.MainModelBinding;
import ua.itatool.vivewmodel.MainViewModel;

public class Main extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private AdapterRecycleViewProduct adapterRecycleViewProduct;
    private ActivityMainBinding binding;

    private static final int PERMISSIONS_EXTERNAL_STORAGE = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.setHandler(mainViewModel);

        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        initMainView();

        //checkPermission();

    }

    private void initMainView() {

        binding.articleEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (v.getInputType() == InputType.TYPE_CLASS_TEXT){
                        v.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                    }else if (v.getInputType() == (InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER)) {
                        v.setInputType(InputType.TYPE_CLASS_TEXT);
                    }

                    handled = true;
                }
                return handled;
            }
        });

        setupRecycleView();

        mainViewModel.getModelBindingMutableLiveData().observe(this, new Observer<MainModelBinding>() {
            @Override
            public void onChanged(@Nullable MainModelBinding mainModelBinding) {
                binding.setMainModelBinding(mainModelBinding);

                setupUpdateInfo(mainModelBinding);

                setupSubscribersMainBinding(mainModelBinding);
            }
        });

        setupSwipeLayout();
    }

    private void setupRecycleView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(layoutManager);

        adapterRecycleViewProduct = new AdapterRecycleViewProduct();
        binding.recyclerView.setAdapter(adapterRecycleViewProduct);
        adapterRecycleViewProduct.setProductList(new ArrayList<Product>());

        mainViewModel.getFilteringProductLiveData().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(@Nullable List<Product> products) {
                if (products != null){
                    adapterRecycleViewProduct.setProductList(products);
                    runLayoutAnimation();
                }
            }
        });
    }

    private void setupSubscribersMainBinding(MainModelBinding modelBinding) {
        modelBinding.getArticleText().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                String article = (String) ((ObservableField)observable).get();
                mainViewModel.onArticleChanged(article);
            }
        });
    }

    private void setupUpdateInfo(MainModelBinding modelBinding) {
        long updateInfo = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("update_info",0);
        long lastModified = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("sync_modified_file",0);

        if (lastModified > 0) {

            long timeNow = System.currentTimeMillis() - updateInfo;

            String dateUpdate = DateTimeUtil.getDate(lastModified);

            long minutesHour = TimeUnit.MILLISECONDS.toMinutes(timeNow);
            String prefix = getResources().getString(R.string.update_info_min);
            if (minutesHour > 60){
                minutesHour = TimeUnit.MILLISECONDS.toHours(timeNow);
                prefix = getResources().getString(R.string.update_info_hour);
            }

            String strUpdateInfo = getResources().getString(R.string.update_info_data_from, dateUpdate) + " " +
                    getResources().getString(R.string.update_info_last_check, String.valueOf(minutesHour), prefix);

            modelBinding.getUpdateInfo().set(strUpdateInfo);

        }else {
            String strUpdateInfo = getResources().getString(R.string.update_info_default);
            modelBinding.getUpdateInfo().set(strUpdateInfo);
        }
    }

    private void setupSwipeLayout() {
        binding.swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light));

        binding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainViewModel.onUpdateAllProduct(binding.getMainModelBinding());
                binding.swipeLayout.setRefreshing(false);
            }
        });
    }

    private void runLayoutAnimation() {
        final Context context = getApplicationContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        binding.recyclerView.setLayoutAnimation(controller);
        binding.recyclerView.getAdapter().notifyDataSetChanged();
        binding.recyclerView.scheduleLayoutAnimation();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_EXTERNAL_STORAGE);

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {


                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_settings:
                startActivity(new Intent(this,Settings.class));
                break;
            case R.id.menu_refresh:
                mainViewModel.onUpdateAllProduct(binding.getMainModelBinding());
                break;
        }
        return true;
    }

}
