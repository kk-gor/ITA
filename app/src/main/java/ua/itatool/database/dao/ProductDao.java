package ua.itatool.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import ua.itatool.database.model.Product;

import java.util.List;

/**
 * Created by djdf.crash on 07.03.2018.
 */

@Dao
public interface ProductDao {

    @Query("SELECT * FROM products")
    LiveData<List<Product>> getAll();

    @Query("SELECT * FROM products WHERE article LIKE :article ORDER BY (CASE WHEN :sortingArticle THEN count END) DESC, article")
    List<Product> getProductsByArticle(String article, boolean sortingArticle);

    @Query("SELECT * FROM products WHERE article = :article")
    Product getProductByArticle(String article);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllProduct(List<Product> productList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateAllProduct(List<Product> productList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateProduct(Product product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProduct(Product product);

    @Query("DELETE FROM products")
    void clearTable();
}
