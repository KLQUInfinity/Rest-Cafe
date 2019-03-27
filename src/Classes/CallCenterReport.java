package Classes;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

public class CallCenterReport {

    Document document = new Document(PageSize.A4.rotate());
    private final ImportantClass IC = ImportantClass.getInstance();
    com.itextpdf.text.Font f = FontFactory.getFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, 10f);
    com.itextpdf.text.Font f1 = FontFactory.getFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, 13f);

    public CallCenterReport() {
        if (!IC.dbc.check) {
            IC.dbc.ConnectDB();
        }
    }

    //printTotalEmpReport
    public void printTotalEmpReport(String datee, String UsName) throws DocumentException, FileNotFoundException, SQLException {
        PdfPTable table = new PdfPTable(2);
        table.getDefaultCell().setMinimumHeight(16);
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        table.setWidthPercentage(50);
        table.getDefaultCell().setArabicOptions(1);
        ///orderNum
        PdfPCell orderNum = new PdfPCell(new Paragraph("اسم الصنف ", f1));
        orderNum.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(orderNum);
        ///ProductName
        PdfPCell ProductName = new PdfPCell(new Paragraph("اجمالي العدد", f1));
        ProductName.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(ProductName);
        String query = "SELECT Sum(o.orderCount) , o.orderProduct FROM `order` o  where o.orderProduct = o.orderProduct And orderDate LIKE '%" + datee + "%' AND userName Like'%" + UsName + "%' And orderKind='دليفري' group by o.orderProduct";
        IC.pst = IC.dbc.conn.prepareStatement(query);
        IC.rs = IC.pst.executeQuery();
        while (IC.rs.next()) {
            ///orderNum
            table.addCell(new Paragraph(IC.rs.getString("o.orderProduct"), f));
            ///ProductName
            table.addCell(new Paragraph(IC.rs.getString("Sum(o.orderCount)"), f));
        }
        document.add(table);
        document.add(new Paragraph(" ", f));
        document.add(new Paragraph(" ", f));
        table.flushContent();
        IC.pst = IC.dbc.conn.prepareStatement("SELECT Sum(o.orderTotal) FROM `order` o  where orderDate LIKE '%" + datee + "%' AND userName Like'%" + UsName + "%' And orderKind='دليفري'");
        IC.rs = IC.pst.executeQuery();
        if (IC.rs.next()) {
            ///orderNum
            table.addCell(new Paragraph("اجمالي متحصلات الموظف", f1));
            ///ProductName
            table.addCell(new Paragraph(IC.rs.getString("Sum(o.orderTotal)"), f1));
        }
        document.add(table);
        document.add(new Paragraph(" ", f));
        document.add(new Paragraph(" ", f));
        table.flushContent();
        IC.pst = IC.dbc.conn.prepareStatement("SELECT Sum(o.orderTotal) FROM `order` o ");
        IC.rs = IC.pst.executeQuery();
        if (IC.rs.next()) {
            ///orderNum
            table.addCell(new Paragraph("اجمالي المتحصلات ", f1));
            ///ProductName
            table.addCell(new Paragraph(IC.rs.getString("Sum(o.orderTotal)"), f1));
        }
        document.add(table);
        document.add(new Paragraph(" ", f));
        document.add(new Paragraph(" ", f));
        table.flushContent();
        IC.pst = IC.dbc.conn.prepareStatement("SELECT COUNT(DISTINCT orderNum) FROM `order`  where orderDate LIKE '%" + datee + "%' AND userName Like'%" + UsName + "%' And orderKind='دليفري'");
        IC.rs = IC.pst.executeQuery();
        if (IC.rs.next()) {
            ///orderNum
            table.addCell(new Paragraph("عدد الطلبات", f1));
            ///ProductName
            table.addCell(new Paragraph(IC.rs.getString("COUNT(DISTINCT orderNum)"), f1));
        }
        document.add(table);
    }

    //take away Bill
    public void printEmpReport(String datee, String UsName) throws DocumentException, FileNotFoundException, SQLException {
        
        PdfPTable table = new PdfPTable(12);
        table.getDefaultCell().setMinimumHeight(16);
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        table.setWidthPercentage(98);
        table.getDefaultCell().setArabicOptions(1);
        ///orderNum
        PdfPCell orderNum = new PdfPCell(new Paragraph("رقم الطلب ", f1));
        orderNum.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(orderNum);
        ///ProductName
        PdfPCell ProductName = new PdfPCell(new Paragraph("اسم الصنف", f1));
        ProductName.setColspan(3);
        ProductName.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(ProductName);
        ///Count
        PdfPCell Count = new PdfPCell(new Paragraph("العدد ", f1));
        Count.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(Count);
        ///Price
        PdfPCell Price = new PdfPCell(new Paragraph("السعر ", f1));
        Price.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(Price);
        ///total
        PdfPCell total = new PdfPCell(new Paragraph("الاجمالي ", f1));
        total.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(total);
        ///Date
        PdfPCell Date = new PdfPCell(new Paragraph("التاريخ", f1));
        Date.setHorizontalAlignment(Element.ALIGN_CENTER);
        Date.setColspan(2);
        table.addCell(Date);
        ///Type
        PdfPCell Type = new PdfPCell(new Paragraph("النوع ", f1));
        Type.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(Type);
        ///EmpName
        PdfPCell EmpName = new PdfPCell(new Paragraph("اسم الموظف", f1));
        EmpName.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(EmpName);
        ///orderType
        PdfPCell orderType = new PdfPCell(new Paragraph("نوع الطلب ", f1));
        orderType.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(orderType);
        String query = "SELECT * FROM `order` WHERE orderDate LIKE '%" + datee + "%' AND userName Like'%" + UsName + "%' And orderKind='دليفري' ";
        IC.pst = IC.dbc.conn.prepareStatement(query);
        IC.rs = IC.pst.executeQuery();
        while (IC.rs.next()) {
            ///orderNum
            table.addCell(new Paragraph(IC.rs.getString("orderNum"), f));
            ///ProductName
            PdfPCell ProductName1 = new PdfPCell(new Paragraph(IC.rs.getString("orderProduct"), f));
            ProductName1.setColspan(3);
            table.addCell(ProductName1);
            ///Count
            table.addCell(new Paragraph(IC.rs.getString("orderCount"), f));
            ///Price
            table.addCell(new Paragraph(IC.rs.getString("orderPrice"), f));
            ///total
            table.addCell(new Paragraph(IC.rs.getString("orderTotal"), f));
            ///Date
            PdfPCell Date1 = new PdfPCell(new Paragraph(IC.rs.getString("orderDate"), f));
            Date1.setColspan(2);
            table.addCell(Date1);
            ///Type
            table.addCell(new Paragraph(IC.rs.getString("orderType"), f));
            ///EmpName
            table.addCell(new Paragraph(IC.rs.getString("userName"), f));
            ///orderType
            table.addCell(new Paragraph(IC.rs.getString("orderKind"), f));
        }
        document.add(table);
        
    }

   public void totalReport(String date,String empName,String Path) throws DocumentException, FileNotFoundException, SQLException {
        PdfWriter.getInstance(document, new FileOutputStream(Path+".pdf"));
        document.setMargins(8, 8, 10, 10);
        document.open();
        printEmpReport(date, empName);
        document.newPage();
        printTotalEmpReport(date, empName);
        
        document.close();
    }
//print pdf Auto print the bill file
    public void pdfPrint(String Path) {
        FileInputStream psStream = null;
        try {
            psStream = new FileInputStream(Path);
        } catch (FileNotFoundException ffne) {
            ffne.printStackTrace();
        }
        if (psStream == null) {
            return;
        }
        DocFlavor psInFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
        Doc myDoc = new SimpleDoc(psStream, psInFormat, null);
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PrintService services = PrintServiceLookup.lookupDefaultPrintService();

        DocPrintJob job = services.createPrintJob();
        try {
            job.print(myDoc, aset);
        } catch (Exception pe) {
            pe.printStackTrace();
        }
    }

    public static void main(String[] args) throws DocumentException, FileNotFoundException, SQLException {
    CallCenterReport c=new CallCenterReport();
    c.totalReport("", "", "C://Users//Adel Amer//Desktop//ffe");
    }
}
