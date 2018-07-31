package com.altaik.bp;


import com.altaik.db.IDatabaseManager;

import java.util.Properties;

/**
 * Created by admin on 12.10.2017.
 */
public class DataBaseProcesses extends BaseProcesses {
    protected IDatabaseManager dbManager;

    public DataBaseProcesses(IDatabaseManager dbManager, Properties properties) {
        super(properties);
        this.dbManager = dbManager;
    }

    public DataBaseProcesses(IDatabaseManager dbManager) {
        this(dbManager, new Properties());
    }

    protected void onClose() {
        dbManager.close();
    }

    @Override
    protected void onStart() {

    }
}
