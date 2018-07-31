/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.altaik.bp.purchase;

import com.altaik.bo.Lot;
import com.altaik.bo.Purchase;
import com.altaik.bo.PurchaseAddition;
import com.altaik.bo.Purchases;
import com.altaik.bp.DataBaseProcesses;
import com.altaik.db.IDatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Collects all functions over Purchase and Purchases.
 *
 * @author Aset
 */
public class PurchaseProcesses extends DataBaseProcesses {
    private static final Logger logger = Logger.getLogger(PurchaseProcesses.class.getName());

    public PurchaseProcesses(IDatabaseManager dbManager) {
        super(dbManager);
    }

    /**
     * Извлечь число из поля в результате запросае
     *
     * @param resultSet результат запроса
     * @param fieldName натменоваие поля
     * @return В случаи успеха вернет число, иначе 0
     */
    private int filedGetInt(ResultSet resultSet, String fieldName) {
        try {
            return resultSet.getInt(fieldName);
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error get int field {0} from ResultSet. Error: {1}", new Object[]{fieldName, ex});
            return 0;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="SupportFunctions">
    private String FieldLoad(ResultSet resultSet, String fieldName) {
        try {
            return resultSet.getString(fieldName);
        } catch (SQLException ex) {
            logger.log(Level.WARNING, null, ex);
            return "";
        }
    }

    private Lot LotLoad(ResultSet lotSet) {
        if (lotSet == null) {
            return null;
        }
        Lot lot = new Lot();
        lot.purchaseNumber = FieldLoad(lotSet, "l.negnumber");
        lot.lotNumber = FieldLoad(lotSet, "l.number");
        lot.ruName = FieldLoad(lotSet, "l.ruName");
        //lot.kzName = FieldLoad(lotSet, "l.kzName");
        lot.ruDescription = FieldLoad(lotSet, "l.ruDescription");
        //lot.kzDescription = FieldLoad(lotSet, "l.kzDescription");
        lot.quantity = FieldLoad(lotSet, "l.quantity");
        lot.unit = FieldLoad(lotSet, "l.unit");
        //lot.vid = FieldLoad(lotSet, "l.vid");
        lot.price = FieldLoad(lotSet, "l.price");
        lot.sum = FieldLoad(lotSet, "l.sum");
        lot.deliveryPlace = FieldLoad(lotSet, "l.deliveryPlace");
        lot.deliverySchedule = FieldLoad(lotSet, "l.deliverySchedule");
        lot.deliveryTerms = FieldLoad(lotSet, "l.deliveryTerms");
        //lot.ktru = FieldLoad(lotSet, "l.ktru");
        //lot.kind = FieldLoad(lotSet, "l.kind");
        //lot.kato = FieldLoad(lotSet, "l.kato");
        lot.link = FieldLoad(lotSet, "l.link");

        return lot;
    }

    private void SourceLoad(Purchase purchase, ResultSet purchaseSet) {
        purchase.setSource(filedGetInt(purchaseSet, "source"));
        try {
            purchase.isource = purchaseSet.getInt("source");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            purchase.isource = 0;
        }
        if (purchase.isource > 0) {
            try (ResultSet sourceSet = dbManager.Execute("SELECT sitename FROM sites WHERE siteid = " + purchase.isource)) {
                if (sourceSet != null && sourceSet.next()) {
                    purchase.addition.sitename = sourceSet.getString("sitename");
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, null, ex);
                purchase.addition.sitename = "";
            }
        }
    }

    private void VenueLoad(Purchase purchase, ResultSet purchaseSet) {
        purchase.venue = FieldLoad(purchaseSet, "venue");
        try {
            purchase.ivenue = purchaseSet.getInt("venue");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            purchase.ivenue = 0;
        }
        if (purchase.ivenue > 0) {
            try (ResultSet venueSet = dbManager.Execute("SELECT name FROM regionsenum WHERE id = " + purchase.ivenue)) {
                if (venueSet != null && venueSet.next()) {
                    purchase.addition.venuename = venueSet.getString("name");
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, null, ex);
                purchase.addition.venuename = "";
            }
        }
    }

    private void MethodLoad(Purchase purchase, ResultSet purchaseSet) {
        purchase.method = FieldLoad(purchaseSet, "method");
        try {
            purchase.imethod = purchaseSet.getInt("method");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            purchase.imethod = 0;
        }
        if (purchase.imethod > 0) {
            try (ResultSet methodSet = dbManager.Execute("SELECT name FROM regionsenum WHERE id = " + purchase.imethod)) {
                if (methodSet != null && methodSet.next()) {
                    purchase.addition.methodname = methodSet.getString("name");
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, null, ex);
                purchase.addition.methodname = "";
            }
        }
    }

    private void StatusLoad(Purchase purchase, ResultSet purchaseSet) {
        purchase.status = FieldLoad(purchaseSet, "status");
        try {
            purchase.istatus = purchaseSet.getInt("istatus");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            purchase.istatus = 0;
        }
//                if (purchase.istatus > 0) {
//                    try (ResultSet statusSet = dbManager.Execute("select name from regionsenum where id = " + purchase.imethod)) {
//                        if (methodSet != null && methodSet.next()) {
//                            if (purchase.addition == null) {
//                                purchase.addition = new PurchaseAddition();
//                            }
//                            purchase.addition.methodname = methodSet.getString("name");
//                        }
//                    }
//                }
    }

    private int PurchaseIdLoad(ResultSet purchaseSet) {
        try {
            return purchaseSet.getInt("id");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    private void PurchaseLinkLoad(Purchase purchase, ResultSet purchaseSet) {
        if (purchase == null) return;
        String number = purchase.getNumber();
        int source = purchase.getSource();
        purchase.link = FieldLoad(purchaseSet, "link");
        if (purchase.link == null || purchase.link.isEmpty()) {
            switch (source) {
                case 1:
                    switch (purchase.type) {
                        case "1":
                            purchase.link = "http://portal.goszakup.gov.kz/portal/index.php/ru/oebs/showbuy/" + number + "/" + number;
                            break;
                        case "2":
                            purchase.link = "http://portal.goszakup.gov.kz/portal/index.php/ru/publictrade/showbuy/" + number;
                            break;
                        case "3":
                            purchase.link = "http://portal.goszakup.gov.kz/portal/index.php/ru/oebs/showauc/" + number;
                            break;
                        case "4":
                            purchase.link = "https://v3bl.goszakup.gov.kz/ru/announce/actionAjaxModalShowFiles/" + number;
                            break;
                    }
                    break;
                case 2:
                    purchase.link = "http://tender.sk.kz/index.php/ru/negs/show/" + number;
                    break;
            }
        }
    }

    private String PathToStorageLoad(int purchaseId) {
        try (ResultSet docsSet = dbManager.Execute("select path from purchasedocs where purchaseid = " + purchaseId)) {
            if (docsSet != null && docsSet.next()) {
                return docsSet.getString("path");
            }
        } catch (SQLException sqlex) {
            logger.log(Level.SEVERE, null, sqlex);
        }
        return "";
    }

    private Purchase NewPurchaseLoad(ResultSet purchaseSet) {
        if (purchaseSet == null) {
            return null;
        }
        Purchase purchase = new Purchase();
        purchase.addition = new PurchaseAddition();

        purchase.addition.id = PurchaseIdLoad(purchaseSet);
        SourceLoad(purchase, purchaseSet);

        purchase.type = FieldLoad(purchaseSet, "type");
        purchase.setNumber(FieldLoad(purchaseSet, "number"));
        purchase.kzName = FieldLoad(purchaseSet, "kzName");
        purchase.ruName = FieldLoad(purchaseSet, "ruName");

        purchase.customer = FieldLoad(purchaseSet, "customer");
        //purchase.organizer = FieldLoad(viewSet, "organizer");
        purchase.organizer = purchase.customer;
        if (!purchase.organizer.isEmpty() && purchase.customer.isEmpty()) {
            purchase.customer = purchase.organizer;
        }
        if (purchase.organizer.isEmpty() && !purchase.customer.isEmpty()) {
            purchase.organizer = purchase.customer;
        }

        VenueLoad(purchase, purchaseSet);
        MethodLoad(purchase, purchaseSet);
        StatusLoad(purchase, purchaseSet);

        purchase.publishDay = FieldLoad(purchaseSet, "publishday");
        purchase.startDay = FieldLoad(purchaseSet, "startday");
        purchase.endDay = FieldLoad(purchaseSet, "endday");
        //purchase.sum        = FieldLoad(viewSet, "sum");

        PurchaseLinkLoad(purchase, purchaseSet);

        purchase.attribute = FieldLoad(purchaseSet, "attribute");
        purchase.priceSuggestion = FieldLoad(purchaseSet, "pricesuggestion");
        purchase.pathToStogare = PathToStorageLoad(purchase.addition.id);
        //purchase.attribute2      = FieldLoad(viewSet, "attribute2");

        try {
            purchase.isDocs = purchaseSet.getInt("isdocs");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
            purchase.isDocs = 0;
        }

        return purchase;
    }
//</editor-fold>

    /**
     * Purchases load fully (with full number of lots). Which ones is indicated by purchaseQuery.
     *
     * @param purchaseQuery
     * @return
     */
    public Purchases PurchasesLoad(String purchaseQuery) {
        Purchases purchases = new Purchases();

        try (ResultSet purchaseSet = dbManager.Execute(purchaseQuery)) {
            while (null != purchaseSet && purchaseSet.next()) {
                Purchase purchase = NewPurchaseLoad(purchaseSet);
                if (purchase == null)
                    continue;

                String number = purchase.getNumber();

                if (number != null && !number.isEmpty()) {
                    purchase.nLots = 0;
                    List<Lot> lots = null;
                    try (ResultSet lotSet = dbManager.Execute("SELECT l.* FROM lots l WHERE negnumber='" + number + "'")) {
                        int sum = 0;
                        while (null != lotSet && lotSet.next()) {
                            if (null == lots) {
                                lots = new ArrayList<>();
                                sum = 0;
                            }

                            Lot lot = LotLoad(lotSet);
                            lots.add(lot);
                            purchase.nLots++;

                            try {
                                sum += Integer.parseInt(lot.sum);
                            } catch (NumberFormatException nfex) {
                                logger.log(Level.WARNING, null, nfex);
                            }
                        }

                        if (sum > 0) purchase.sum = "" + sum;
                    } catch (SQLException sqlex) {
                        logger.log(Level.SEVERE, null, sqlex);
                    }

                    purchase.lots = lots;
                }
                purchases.add(purchase);
            }
        } catch (SQLException sqlex) {
            logger.log(Level.SEVERE, null, sqlex);
        }

        return purchases;
    }


    public Purchases ViewLoad(String viewQuery) {
        Purchases purchases = new Purchases();

        try (ResultSet viewSet = dbManager.Execute(viewQuery)) {
            int loadedId = -1;
            Purchase purchase = new Purchase();

            while (null != viewSet && viewSet.next()) {
                int currentId = PurchaseIdLoad(viewSet);

                if (currentId == -1) continue;

                if (currentId != loadedId) {
                    if (purchase.lots != null) purchases.add(purchase);
                    purchase = NewPurchaseLoad(viewSet);
                    purchase.lots = new ArrayList<>();
                    loadedId = currentId;
                }

                purchase.lots.add(LotLoad(viewSet));
            }
        } catch (SQLException sqlex) {
            logger.log(Level.SEVERE, null, sqlex);
        }

        return purchases;
    }


}
