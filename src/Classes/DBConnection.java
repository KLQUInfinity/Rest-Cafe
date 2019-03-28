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
        try {///jdbc:mysql://aws-us-east-1-portal.4.dblayer.com:32967/?user=admin/?pass=HMWRRFFRFGTGWYWR 
//            Class.forName("com.mysql.jdbc.Driver");
            //create connection
            //            conn= DriverManager.getConnection("jdbc:mysql://sql2.freemysqlhosting.net:3306/sql2283641?useUnicode=yes&characterEncoding=UTF-8","sql2283641","bL8!yY6!");
            conn= DriverManager.getConnection("jdbc:mysql://aws-us-east-1-portal.4.dblayer.com:32967/rest_cafe?useUnicode=yes&characterEncoding=UTF-8","admin","HMWRRFFRFGTGWYWR");
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
