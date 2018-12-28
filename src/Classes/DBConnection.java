/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author CompuWorld
 */
public class DBConnection {

    public boolean check = false;
    public Connection conn;

    public Connection ConnectDB() {
        //jdbc 
        try {
//            Class.forName("com.mysql.jdbc.Driver");
            //create connection
            //conn = DriverManager.getConnection("jdbc:mysql://sql8.freemysqlhosting.net:3306/sql8120627?useUnicode=yes&characterEncoding=UTF-8", "sql8120627", "pPUVsMuFy3");
            conn= DriverManager.getConnection("jdbc:mysql://sql12.freemysqlhosting.net:3306/sql12271829?useSSL=false","sql12271829","mW33iQQxIb");
            check = true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot connect to database.");
            e.printStackTrace();
            check = false;
        }
        return null;
    }

    public void CloseConnection() {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
