/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.altaik.bp.service;

import com.altaik.bp.DataBaseProcesses;
import com.altaik.db.IDatabaseManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * @author Aset
 */
public class SystemProcesses extends DataBaseProcesses {

    public SystemProcesses(IDatabaseManager dbManager) {
        super(dbManager);
    }


    /**
     * Execute system console command 'vol' and retrieve volume serial number
     *
     * @param driveLetter - drive letter in convetion 'X:'
     * @return - Volume Serial Number, typically: 'XXXX-XXXX'
     * @throws IOException
     * @throws InterruptedException
     */
    public static final String GetVolumeSerial(String driveLetter) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("cmd /C vol " + driveLetter);
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();
        if (process.waitFor() != 0) {
            Scanner sce = new Scanner(errorStream);
            System.out.println("VVV");
            return "-----------------------------------------";
//            throw new RuntimeException(sce.findWithinHorizon(".*", 0));
        }
        Scanner scn = new Scanner(inputStream);
        // looking for: ': XXXX-XXXX' using regex
        String res = scn.findWithinHorizon(": \\w+-\\w+", 0);
        return res.substring(2).toUpperCase().trim();
    }


    public static final String GetSerialNumber() {
        String sn = null;

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(new String[]{"wmic", "bios", "get", "serialnumber"});
        } catch (IOException e) {
            System.out.println("SSS");
            return "-----------------------------------------";
//            throw new RuntimeException(e);
        }

        OutputStream os = process.getOutputStream();
        InputStream is = process.getInputStream();

        try {
            os.close();
        } catch (IOException e) {
            return "-----------------------------------------";
//            throw new RuntimeException(e);
        }

        Scanner sc = new Scanner(is);
        try {
            while (sc.hasNext()) {
                String next = sc.next();
                if ("SerialNumber".equals(next)) {
                    sn = sc.next().trim();
                    break;
                }
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                return "-----------------------------------------";
//                throw new RuntimeException(e);
            }
        }

        if (sn == null) {
            return "-----------------------------------------";
//            throw new RuntimeException("Cannot find computer SN");
        }

        return sn;
    }


    public static final String GetNetwork() {
        String sn = null;

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(new String[]{"ping", "10.1.1.2"});
        } catch (IOException e) {
            return "-----------------------------------------";
//            throw new RuntimeException(e);
        }

        OutputStream os = process.getOutputStream();
        InputStream is = process.getInputStream();

        try {
            os.close();
        } catch (IOException e) {
            return "-----------------------------------------";
//            throw new RuntimeException(e);
        }

        Scanner sc = new Scanner(is);
        try {
            while (sc.hasNext()) {
                String next = sc.next();
                if (next.contains("TTL=")) {
                    sn = "Nanana";
                    break;
                }
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                return "-----------------------------------------";
//                throw new RuntimeException(e);
            }
        }

        if (sn == null) {
            return "-----------------------------------------";
//            throw new RuntimeException("Cannot find computer SN");
        }

        return sn;
    }

}
