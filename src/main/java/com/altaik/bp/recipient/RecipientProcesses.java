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
import com.altaik.bp.DataBaseProcesses;
import com.altaik.db.IDatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Aset
 */
public class RecipientProcesses extends DataBaseProcesses {

    public RecipientProcesses(IDatabaseManager dbManager) {
        super(dbManager);
    }

    public Recipient ConstructRecipient(IDatabaseManager dbManager, ResultSet set) throws SQLException {
        Recipient recipient = new Recipient();

        recipient.user = new User();
        recipient.user.id = set.getInt("id");
        if (recipient.user.id == 0) {
            recipient.user.email = set.getString("primeemail");
            if (null == recipient.user.email || recipient.user.email.isEmpty()) {
                recipient.user.email = CustomerProcesses.GetEmail(dbManager, recipient.user.id);
            }

            recipient.user.lastpurchase = set.getInt("lastpurchase");
            if (recipient.user.lastpurchase == 0) {
                recipient.user.lastpurchase = 1000;
            }
            recipient.deliverySettings = new DeliverySettings();
            recipient.deliverySettings.keyword1 = set.getString("keyword1");
            recipient.deliverySettings.keyword2 = set.getString("keyword2");
            recipient.deliverySettings.keyword3 = set.getString("keyword3");

            try (ResultSet regionSet = dbManager.Execute("SELECT region FROM userregions WHERE userid = " + recipient.user.id)) {
                recipient.deliverySettings.regions = new ArrayList<>();
                while (null != regionSet && regionSet.next()) {
                    recipient.deliverySettings.regions.add(regionSet.getInt("region"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(RecipientsBase.class.getName()).log(Level.SEVERE, null, ex);
            }

            try (ResultSet methodSet = dbManager.Execute("SELECT method FROM usermethods WHERE userid = " + recipient.user.id)) {
                recipient.deliverySettings.methods = new ArrayList<>();
                while (null != methodSet && methodSet.next()) {
                    recipient.deliverySettings.methods.add(methodSet.getInt("method"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(RecipientsBase.class.getName()).log(Level.SEVERE, null, ex);
            }

            try (ResultSet sourceSet = dbManager.Execute("SELECT source FROM usersources WHERE userid = " + recipient.user.id)) {
                recipient.deliverySettings.sources = new ArrayList<>();
                while (null != sourceSet && sourceSet.next()) {
                    recipient.deliverySettings.sources.add(sourceSet.getInt("method"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(RecipientsBase.class.getName()).log(Level.SEVERE, null, ex);
            }

            try (ResultSet sumrangeSet = dbManager.Execute("SELECT minsum,maxsum FROM usersumranges WHERE userid = " + recipient.user.id)) {
                if (null != sumrangeSet && sumrangeSet.next()) {
                    recipient.deliverySettings.minsum = sumrangeSet.getDouble("minsum");
                    recipient.deliverySettings.maxsum = sumrangeSet.getDouble("maxsum");
                }
            } catch (SQLException ex) {
                Logger.getLogger(RecipientsBase.class.getName()).log(Level.SEVERE, null, ex);
            }

            try (ResultSet customerSet = dbManager.Execute("SELECT c.runame,c.email,c.firstname,c.lastname FROM customer c WHERE c.userid = " + recipient.user.id)) {
                if (null != customerSet && customerSet.next()) {
                    recipient.customer = new Customer(customerSet.getString("email"), customerSet.getString("runame"), customerSet.getString("firstname"), customerSet.getString("lastname"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(RecipientsBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return recipient;
    }
}
