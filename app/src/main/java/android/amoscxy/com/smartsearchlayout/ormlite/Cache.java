package android.amoscxy.com.smartsearchlayout.ormlite;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by cxy on 2018/7/3.
 */

public class Cache {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "cache")
    private String cache;

    public Cache() {
    }

    public Cache(String cache) {
        this.cache = cache;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }
}
