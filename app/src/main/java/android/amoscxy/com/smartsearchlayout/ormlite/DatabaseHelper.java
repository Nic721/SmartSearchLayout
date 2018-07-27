package android.amoscxy.com.smartsearchlayout.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxy on 2018/7/3.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    //数据库名字
    private static final String DATABASE_NAME = "sqlite-database.db";
    private Map<String,Dao> daos = new HashMap<>();
    public static List<Class> classes = new ArrayList<>();
    public static int version = 1;

    private DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        if(classes == null || classes.size() == 0){
            return;
        }
        try {
            for (Class classz:classes){
                TableUtils.createTable(connectionSource,classz);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        if(classes == null || classes.size() == 0){
            return;
        }
        try {
            for(Class classz:classes){
                TableUtils.dropTable(connectionSource,classz,true);
            }
            onCreate(database,connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getHelper(Context context){
        if(instance == null){
            synchronized (DatabaseHelper.class){
                if(instance == null){
                    instance = new DatabaseHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * 获取classz对应的Dao
     * @param classz
     * @return
     * @throws SQLException
     */
    public synchronized Dao getDao(Class classz) throws SQLException {
        Dao dao = null;
        String className = classz.getSimpleName();
        if(daos.containsKey(className)){
            dao = daos.get(className);
        }
        if(dao == null){
            dao = super.getDao(classz);
            daos.put(className,dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        for(String key:daos.keySet()){
            Dao dao = daos.get(key);
            dao = null;
        }
    }
}
