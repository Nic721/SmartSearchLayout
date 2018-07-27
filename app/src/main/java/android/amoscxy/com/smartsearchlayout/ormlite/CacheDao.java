package android.amoscxy.com.smartsearchlayout.ormlite;

import android.content.Context;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by cxy on 2018/7/3.
 */

public class CacheDao {
    private Context context;
    private Dao<Cache,Integer> cacheDaoOpe;
    private DatabaseHelper helper;

    public CacheDao(Context context, Class classz) {
        this.context = context.getApplicationContext();
        helper = DatabaseHelper.getHelper(this.context);
        try {
            cacheDaoOpe = helper.getDao(classz);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加一个搜索数据
     * @param cache
     */
    public void insert(Cache cache){
        ArrayList<Cache> caches = queryAll();
        if(cache != null && !TextUtils.isEmpty(cache.getCache())){
            if(caches.size() == 0){
                try {
                    cacheDaoOpe.createIfNotExists(cache);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                if(caches.size() > 100){
                    return;
                }
                for(Cache ele:caches){
                    if(cache.getCache().equals(ele.getCache())){
                        return;
                    }
                }
                try {
                    cacheDaoOpe.createIfNotExists(cache);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取全部数据
     * @return
     */
    public ArrayList<Cache> queryAll(){
        try {
            return (ArrayList<Cache>) cacheDaoOpe.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 删除数据
     * @param cache
     */
    public void deleteByCache(Cache cache){
        try {
            cacheDaoOpe.delete(cache);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除全部数据
     */
    public void deleteAllCache(){
        try {
            cacheDaoOpe.delete(queryAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        helper.close();
    }
}
