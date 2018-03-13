package ua.itatool.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import ua.itatool.database.DatabaseConstants;

/**
 * Created by djdf.crash on 07.03.2018.
 */

@Entity(tableName = DatabaseConstants.PRODUCT_TABLE, indices = {@Index(value = {DatabaseConstants.PRODUCT_CODE_FIELD}, unique = true)})
public class Product {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseConstants.PRODUCT_ID_FIELD)
    private long id;

    @ColumnInfo(name = DatabaseConstants.PRODUCT_CODE_FIELD)
    private String code;

    @ColumnInfo(name = DatabaseConstants.PRODUCT_ARTICLE_FIELD)
    private String article;

    public Product(String code, String article) {
        this.code = code;
        this.article = article;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
