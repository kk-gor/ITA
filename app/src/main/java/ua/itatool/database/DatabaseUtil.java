package ua.itatool.database;

import android.arch.persistence.room.Room;
import android.content.Context;

import ua.itatool.database.dao.ProductDao;

/**
 * Created by djdf.crash on 07.03.2018.
 */

public class DatabaseUtil {

    private static AppDatabase db;

    public static ProductDao getProductDao(Context ctx){
        db = getDb(ctx);
        return db.productDao();
    }

    public static AppDatabase getDb(Context ctx){
        if (db == null) {
            db = Room.databaseBuilder(ctx, AppDatabase.class, DatabaseConstants.NAME_DATABASE)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return db;
    }
}
