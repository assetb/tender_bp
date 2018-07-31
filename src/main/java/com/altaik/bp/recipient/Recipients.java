/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.altaik.bp.recipient;

import com.altaik.bo.Customer;
import com.altaik.bo.Recipient;
import com.altaik.bo.RecipientsBase;
import com.altaik.bo.User;
import com.altaik.bo.settings.DeliverySettings;
import com.altaik.bp.CustomerProcesses;
import com.altaik.db.IDatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Aset
 */
public class Recipients extends RecipientsBase {

    protected final IDatabaseManager dbManager;

    public Recipients(IDatabaseManager dbManager) {
        super();
        this.dbManager = dbManager;
        Reload();
        Reset();
    }

    @Override
    protected boolean LoadIds() {
        String query = "SELECT u.id, u.email,u.lastpurchase FROM users u, deliveries d WHERE u.id=d.userid";
        try (ResultSet usersSet = dbManager.Execute(query)) {
            while (null != usersSet && usersSet.next()) {
                User user = new User();

                user.id = usersSet.getInt("id");
                if (user.id == 0) {
                    continue;
                }

                user.email = usersSet.getString("email");
                if (null == user.email || user.email.isEmpty()) {
                    CustomerProcesses customerProcesses = new CustomerProcesses(dbManager);
                    user.email = customerProcesses.GetEmail(user.id);
                }

                user.lastpurchase = usersSet.getInt("lastpurchase");
                if (user.lastpurchase == 0) {
                    user.lastpurchase = 1000;
                }

                if (users == null) {
                    users = new ArrayList<>();
                }

                users.add(user);
            }

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Recipients.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public Recipient GetByIndex(int index) {
        Recipient recipient = new Recipient();
        recipient.user = users.get(index);
        recipient.deliverySettingsCollection = new ArrayList<>();

        String whereQuerySection = " WHERE d.userid = " + recipient.user.id;

        String deliveriesQuery = "SELECT d.id,d.no,d.keyword1,d.keyword2,d.keyword3 FROM deliveries d" + whereQuerySection;
        try (ResultSet deliveriesSet = dbManager.Execute(deliveriesQuery)) {
            while (null != deliveriesSet && deliveriesSet.next()) {
                DeliverySettings deliverySettings = new DeliverySettings();

                deliverySettings.keyword1 = deliveriesSet.getString("keyword1");
                deliverySettings.keyword2 = deliveriesSet.getString("keyword2");
                deliverySettings.keyword3 = deliveriesSet.getString("keyword3");

                int deliveryId = deliveriesSet.getInt("id");
                String deliveryIdQuerySection = whereQuerySection + " AND d.deliveryid = " + deliveryId;

                try (ResultSet regionSet = dbManager.Execute("SELECT region FROM userregions d" + deliveryIdQuerySection)) {
                    deliverySettings.regions = new ArrayList<>();
                    while (null != regionSet && regionSet.next()) {
                        deliverySettings.regions.add(regionSet.getInt("region"));
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(com.altaik.bo.RecipientsBase.class.getName()).log(Level.SEVERE, null, ex);
                }

                try (ResultSet methodSet = dbManager.Execute("SELECT method FROM usermethods d" + deliveryIdQuerySection)) {
                    deliverySettings.methods = new ArrayList<>();
                    while (null != methodSet && methodSet.next()) {
                        deliverySettings.methods.add(methodSet.getInt("method"));
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(com.altaik.bo.RecipientsBase.class.getName()).log(Level.SEVERE, null, ex);
                }

                try (ResultSet sourceSet = dbManager.Execute("SELECT source FROM usersources d" + deliveryIdQuerySection)) {
                    deliverySettings.sources = new ArrayList<>();
                    while (null != sourceSet && sourceSet.next()) {
                        deliverySettings.sources.add(sourceSet.getInt("source"));
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(com.altaik.bo.RecipientsBase.class.getName()).log(Level.SEVERE, null, ex);
                }

                try (ResultSet sumrangeSet = dbManager.Execute("SELECT minsum,maxsum FROM usersumrange d" + deliveryIdQuerySection)) {
                    if (null != sumrangeSet && sumrangeSet.next()) {
                        deliverySettings.minsum = sumrangeSet.getDouble("minsum");
                        deliverySettings.maxsum = sumrangeSet.getDouble("maxsum");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(com.altaik.bo.RecipientsBase.class.getName()).log(Level.SEVERE, null, ex);
                }

                recipient.deliverySettingsCollection.add(deliverySettings);
            }
        } catch (SQLException ex) {
            Logger.getLogger(com.altaik.bo.RecipientsBase.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (ResultSet customerSet = dbManager.Execute("SELECT d.runame,d.email,d.firstname,d.lastname FROM customer d" + whereQuerySection)) {
            if (null != customerSet && customerSet.next()) {
                recipient.customer = new Customer(customerSet.getString("email"), customerSet.getString("runame"), customerSet.getString("firstname"), customerSet.getString("lastname"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(com.altaik.bo.RecipientsBase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return recipient;
    }

    @Override
    public void remove() {
    }
}
