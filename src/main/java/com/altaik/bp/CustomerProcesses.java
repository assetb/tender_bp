/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.altaik.bp;

import com.altaik.bo.Customer;
import com.altaik.db.IDatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Aset
 */
public class CustomerProcesses extends DataBaseProcesses {

    public CustomerProcesses(IDatabaseManager dbManager) {
        super(dbManager);
    }

    public static String GetEmail(IDatabaseManager dbManager, int userId) {
        try {
            ResultSet set = dbManager.Execute("select email from customer where userid = " + userId);
            if (null != set && set.next()) {
                return set.getString("email");
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String GetEmail(int userId) {
        return GetEmail(dbManager, userId);
    }

}
