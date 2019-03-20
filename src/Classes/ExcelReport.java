/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelReport {

    private final ImportantClass IC = ImportantClass.getInstance();
    ///write data to excel sheet
    public void writeData(String path) throws IOException, WriteException, SQLException {
        ///////////
        WritableWorkbook workbook = Workbook.createWorkbook(new File(path + ".xls"));
        WritableSheet s = workbook.createSheet("1", 0);
        /////////////
        Label L1 = new Label(0, 0, "رقم الطلب");
        s.addCell(L1);
        ///////////
        L1 = new Label(1, 0, "اسم الصنف");
        s.addCell(L1);
        ///////////
        L1 = new Label(2, 0, "العدد");
        s.addCell(L1);
        ///////////
        L1 = new Label(3, 0, "سعر القطعه");
        s.addCell(L1);
        ///////////
        L1 = new Label(4, 0, "الاجمالي");
        s.addCell(L1);
        ///////////
        L1 = new Label(5, 0, "التاريخ");
        s.addCell(L1);
        ///////////
        L1 = new Label(6, 0, "نوع الطلب");
        s.addCell(L1);
        ///////////
        L1 = new Label(7, 0, "الموظف");
        s.addCell(L1);
        ///////////
        int rw = 1;
        ///////////
        String query = "SELECT * FROM sql2283641.`order`;";
        IC.pst = IC.dbc.conn.prepareStatement(query);
        IC.rs = IC.pst .executeQuery();
        ///////////
        while (IC.rs.next()) {
            L1 = new Label(0, rw, IC.rs.getString(2));
            s.addCell(L1);
            L1 = new Label(1, rw, IC.rs.getString(3));
            s.addCell(L1);
            L1 = new Label(2, rw, IC.rs.getString(4));
            s.addCell(L1);
            L1 = new Label(3, rw, IC.rs.getString(5));
            s.addCell(L1);
            L1 = new Label(4, rw, IC.rs.getString(6));
            s.addCell(L1);
            L1 = new Label(5, rw, IC.rs.getString(7));
            s.addCell(L1);
            L1 = new Label(6, rw, IC.rs.getString(9));
            s.addCell(L1);
            L1 = new Label(7, rw, IC.rs.getString(10));
            s.addCell(L1);
            rw++;
        }
        ///////////
        workbook.write();
        workbook.close();
    }

}
