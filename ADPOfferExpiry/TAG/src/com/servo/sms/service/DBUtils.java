/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.servo.sms.service;

import com.google.gson.Gson;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author BAN112986
 */
public class DBUtils {
 

    public static Connection getConnection() {
        try {
            Context initContext = new InitialContext();
            DataSource datasource = (DataSource) initContext.lookup("oracleJNDI");
            return datasource.getConnection();
//            return getConnectionJdbc();
        } catch (Exception e) {
            //Logger.printOut(Logger.getExcpStackTrace(e));
        }
        return null;
    }

    public static Connection getConnectionJdbc() {
        try {
//UAT Only 
//step1 load the driver class  
            Class.forName("oracle.jdbc.driver.OracleDriver");

//step2 create  the connection object  
            Connection con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@192.168.1.234:1521/orcl", "C##I_STREAMS", "system123#");
//                    "jdbc:oracle:thin:@10.78.10.191:9013:CLOSUAT", "closuat", "CLOSUAT_123");

            System.out.println("Connection is " + con);
            return con;
        } catch (Exception e) {
            System.out.println("exception in connection ");
            e.printStackTrace();
            //Logger.printOut(Logger.getExcpStackTrace(e));
        }
        return null;
    }

    public static void closeConnection(Connection con) {

        try {
            if (con != null) {
                con.close();
                con = null;
            }
        } catch (Exception e) {
        }

    }

    public static void closePreparedStatement(PreparedStatement pstmt) {

        try {
            if (pstmt != null) {
                pstmt.close();
                pstmt = null;
            }
        } catch (Exception e) {
        }

    }

    public static void closeResultSet(ResultSet rs) {

        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (Exception e) {
        }

    }


}
