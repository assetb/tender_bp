/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.altaik.bp;


import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Aset
 */
public abstract class BaseProcesses implements Runnable, AutoCloseable {
//    protected IDatabaseManager dbManager;
    protected final Logger logger;
    protected Properties properties = new Properties();

    public  BaseProcesses(){
        logger = Logger.getLogger(this.getClass().getName());
    }

    public BaseProcesses(Properties properties) {
        this();
        this.properties = properties;
    }

    protected abstract void onClose();

    protected abstract void onStart();

    @Override
    public void run() {
        onStart();
    }

    @Override
    public void close() {
        onClose();
    }
}
