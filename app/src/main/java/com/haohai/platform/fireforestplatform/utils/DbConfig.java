package com.haohai.platform.fireforestplatform.utils;

import android.content.Context;

import com.haohai.platform.fireforestplatform.ui.bean.Area;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

/**
 * Created by qc
 * on 2023/7/26.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class DbConfig {
    public Context context;

    public DbConfig(Context context) {
        this.context = context;
    }

    public DbManager.DaoConfig getDaoConfig() {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("rebuild.db")
                .setAllowTransaction(true)
                .setDbDir(context.getFilesDir())
                .setDbVersion(10);

        return daoConfig;
    }
    public DbManager getDbManager(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        return db;
    }
    public List<Area> getGridList(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<Area> areaList = db.selector(Area.class)
                    .findAll();
            return areaList;

        } catch (DbException e) {
        }
        return null;
    }
}
