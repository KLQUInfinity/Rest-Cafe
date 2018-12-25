/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Classes.DBConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Bendary
 */
public class ImportantClass {

    public ImportantClass() {
        
    }

    // Singlton
    private static ImportantClass instance;

    public static ImportantClass getInstance() {
        if (instance == null) {
            instance = new ImportantClass();
        }
        return instance;
    }

    // Variables
    // This object from DBConnection to make a connection to the database and close it.
    public DBConnection dbc = new DBConnection();
    // This variable store the sql statement.
    public PreparedStatement pst = null;
    // This variable store the result of any sql statement from the database.
    public ResultSet rs = null;
    // This variable store the jobTitle of user.
    public String jobTitle = "";
}
