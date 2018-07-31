/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.altaik.bp.proxy;


import com.altaik.db.IDatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Aset
 */
public class Proxies implements AutoCloseable {

    private static final Logger logger = Logger.getLogger(Proxies.class.getName());
    private final IDatabaseManager dbManager;
    private ArrayList<String[]> proxies;
    private int iproxy;

    public Proxies(IDatabaseManager dbManager) {
        this.dbManager = dbManager;
        IProxy();
        Do();
    }

    public static boolean SetNullProxy() {
        try {
            System.setProperty("http.proxyHost", null);
            System.setProperty("http.proxyPort", null);
            return true;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public int IProxy() {
        try {
            ResultSet temp = dbManager.Execute("select * from iproxy");
            while (temp.next()) {
                iproxy = temp.getInt("num");
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, sqlex.getMessage());
            iproxy = 0;
        }
        return iproxy;
    }

    public ArrayList<String[]> Do() {
        proxies = (ArrayList<String[]>) dbManager.Execute("select * from proxylist", new String[]{"ip", "port"});
        logger.log(Level.INFO, "proxies added");
        return proxies;
    }

    public ArrayList<String[]> GetAll() {
        if (proxies == null) {
            return Do();
        }
        return proxies;
    }

    public String[] GetProxy(int i) {
        return GetAll().get(i);
    }

    public boolean SetNextProxy() {
        try {
            System.setProperty("http.proxyHost", GetProxy(iproxy)[0]);
            System.setProperty("http.proxyPort", GetProxy(iproxy)[1]);
            SetNextIProxy();
            return true;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void SetNextIProxy() {
        int prev = iproxy;
        iproxy++;
        if (iproxy > GetAll().size() - 2) {
            iproxy = 0;
        }
        try {
            dbManager.Update("update iproxy set num = '" + String.valueOf(iproxy) + "'");
        } catch (Exception ex) {
            logger.log(Level.WARNING, ex.getMessage());
            iproxy = prev;
        }
    }

    //Возвращает индекс текущего прокси адреса
    public int GetIProxy() {
        return iproxy;
    }


    @Override
    public void close() {
        proxies = null;
    }
}
