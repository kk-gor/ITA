package ua.itatool.services;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.itatool.database.DatabaseUtil;
import ua.itatool.database.model.Product;
import ua.itatool.utils.NetworkUtils;
import ua.itatool.utils.ParserHelper;

/**
 * Created by djdf.crash on 09.03.2018.
 */

public class UpdateProductsService extends IntentService {
    public static final String ACTION_REFRESH_INTENT_SERVICE = "UPDATE.PRODUCTS";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";
    public static final String MESSAGE_TEXT = "MESSAGE_TEXT";

    public UpdateProductsService() {
        super("Update products service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //DatabaseUtil.getProductDao(getApplicationContext()).clearTable();

        String url = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("url_server_sync","");
        String login = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("server_login_sync","");
        String password = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("server_password_sync","");

        List<Product> list = new ArrayList<>();

        Map<String, String> messageMap = new HashMap<>();

        messageMap.put("MESSAGE_OK", "Дані оновлено!");

//        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(login) || TextUtils.isEmpty(password)) {
//            list = ParserHelper.parseFile(getApplicationContext(), messageMap);
//        }else {
            if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                list = ParserHelper.parseUrlFile(getApplicationContext(), url, login, password, messageMap);
            }else {
                messageMap.put("MESSAGE_ERROR", "Перевірте з'єднання із інтернетом та спробуйте ще раз");
            }
//        }
        DatabaseUtil.getProductDao(getApplicationContext()).insertAllProduct(list);

        Intent responseIntent = new Intent();
        responseIntent.setAction(ACTION_REFRESH_INTENT_SERVICE);
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        responseIntent.putExtra(EXTRA_KEY_OUT, View.GONE);
        if (messageMap.containsKey("MESSAGE_ERROR")) {
            responseIntent.putExtra(MESSAGE_TEXT, messageMap.get("MESSAGE_ERROR"));
        }else {
            responseIntent.putExtra(MESSAGE_TEXT, messageMap.get("MESSAGE_OK"));
        }
        sendBroadcast(responseIntent);
    }
}
