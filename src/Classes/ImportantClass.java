/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Bendary
 */
public class ImportantClass {

    public ImportantClass() {

    }

    // <editor-fold defaultstate="collapsed" desc="Singlton">
    private static ImportantClass instance;

    public static ImportantClass getInstance() {
        if (instance == null) {
            instance = new ImportantClass();
        }
        return instance;
    }
// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Variables">

    // This object from DBConnection to make a connection to the database and close it.
    public DBConnection dbc = new DBConnection();
    // This variable store the sql statement.
    public PreparedStatement pst = null;
    // This variable store the result of any sql statement from the database.
    public ResultSet rs = null;
    // This variable store the jobTitle of user.
    public String jobTitle = "";
    // This variable store the user name from the login session
    public String userName = "admin";
    // </editor-fold>

    public String getDate() {
        Date date = new Date();
        final DateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy;HH:mm:ss");
        return dateformat.format(date);
    }
}
