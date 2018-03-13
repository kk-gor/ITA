package ua.itatool.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import ua.itatool.database.dao.ProductDao;
import ua.itatool.database.model.Product;

/**
 * Created by djdf.crash on 07.03.2018.
 */

@Database(entities = {Product.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
}
