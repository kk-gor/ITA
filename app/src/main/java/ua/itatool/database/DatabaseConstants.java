package ua.itatool.database;

/**
 * Created by djdf.crash on 07.03.2018.
 */

public interface DatabaseConstants {

    String NAME_DATABASE = "app_products";


    String COUNT_TABLE = "products_count";

    //price
    String PRICE_TABLE = "products_price";

    String PRICE_ID_FIELD = "id";
    String PRICE_ID_PRODUCT_FIELD = "id_product";
    String PRICE_PRICE_FIELD = "code";

    //products
    String PRODUCT_TABLE = "products";

    String PRODUCT_ID_FIELD = "id";
    String PRODUCT_ARTICLE_FIELD = "article";
    String PRODUCT_CODE_FIELD = "code";
}
