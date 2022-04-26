/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.servo.sms.service;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Execute;
import javax.annotation.TimerService;

/**
 *
 * @author Servosys
 */
@TimerService
public class KVBSmsService {

    @Execute
    public void KVBSMS(Connection conn) {
        String status = getSMSData(conn);
        System.out.println("ststus:::::::::" + status);
    }

    public String getSMSData(Connection con) {
        String query = "";
        PreparedStatement pstmt = null;

        ResultSet rs = null;
        String DataString = "";
        String output;
        try {

            query = "SELECT * FROM SRV_RU_SENDSMS_KVB WHERE STATUS is null";
            pstmt = con.prepareCall(query);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String MOBILENO = rs.getString("MOBILENO");
                String MESSAGE = rs.getString("MESSAGE");
                System.out.println("MOBILENO::::::::" + MOBILENO + "    MESSAGE:::::::" + MESSAGE);
//                URL url = new URL(" http://172.22.1.13:80/SENDSMS_HTTP/SendSMS_Http");
//                URL url = new URL(" http://14.142.112.3:80/SENDSMS_HTTP/SendSMS_Http");www.kvbbankonline.com
                URL url = new URL("https://172.21.1.44:443/SENDSMS_HTTP/SendSMS_Http");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                String data = "Userid=SENDSMS_SERV&Password=SENDSMS_SERV&message=" + MESSAGE + "&mobno=" + MOBILENO;
                os.write(data.getBytes());
                os.flush();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (connection.getInputStream())));
                while ((output = br.readLine()) != null) {
                    DataString += output;
                }
                System.out.println(" DataString---> " + DataString.substring(0, 6));
                if (DataString.equalsIgnoreCase("success")) {
                    UpdateSmsData(con, MOBILENO, MESSAGE, "Y", DataString);
                } else if (DataString.substring(0, 6).equalsIgnoreCase("success")) {
                    UpdateSmsData(con, MOBILENO, MESSAGE, "Y", DataString);
                } else {
                    UpdateSmsData(con, MOBILENO, MESSAGE, "N", DataString);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            Logger.getLogger(KVBSmsService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KVBSmsService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
        return DataString;

    }

    public void UpdateSmsData(Connection conn, String number, String Message, String status, String error) {
        PreparedStatement pstmt;

        String query = "UPDATE SRV_RU_SENDSMS_KVB SET STATUS=?,ERROR=? WHERE MOBILENO=? AND MESSAGE=?";
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, status);
            pstmt.setString(2, error);
            pstmt.setString(3, number);
            pstmt.setString(4, Message);
            System.out.println(pstmt.executeUpdate());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void InsertSmsDataHistory(Connection conn, String number, String Message) {
//        PreparedStatement pstmt;
//        String query = "INSERT INTO SRV_RU_SMSSERVICEHISTORY_KVB(MOBILENO,MESSAGE) VALUES(?,?) ";
//        try {
//            pstmt = conn.prepareStatement(query);
//            pstmt.setString(1, number);
//            pstmt.setString(2, Message);
//            System.out.println(pstmt.execute());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
