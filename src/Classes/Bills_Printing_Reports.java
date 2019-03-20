/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import com.itextpdf.text.BaseColor;
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
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

public class Bills_Printing_Reports {

    private final ImportantClass IC = ImportantClass.getInstance();
    Document document = new Document(PageSize.A7);
    com.itextpdf.text.Font f = FontFactory.getFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, 10f);
    com.itextpdf.text.Font f1 = FontFactory.getFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, 13f);

    //print pdf Auto print the bill file
    public void pdfPrint() {
        FileInputStream psStream = null;
        try {
            psStream = new FileInputStream("client.pdf");
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

    //take away Bill
    public void printBill(int billNum, double totalPrice, String paidTxt, String totalChangeLabel) throws DocumentException, FileNotFoundException {
        PdfWriter.getInstance(document, new FileOutputStream("client.pdf"));
        document.setMargins(8, 8, 10, 10);
        document.open();
        PdfPTable table = new PdfPTable(6);
        table.getDefaultCell().setMinimumHeight(12);
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        table.setWidthPercentage(98);
        table.getDefaultCell().setArabicOptions(1);
        ///head
        PdfPCell cell0 = new PdfPCell(new Paragraph("مطعم وكافيه ", f1));
        cell0.setColspan(6);
        cell0.setPaddingBottom(4f);
        cell0.setBorderColorTop(BaseColor.BLACK);
        cell0.setBorderColorBottom(BaseColor.BLACK);
        cell0.setBorderWidthLeft(0);
        cell0.setBorderWidthRight(0);
        cell0.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell0);
        ///bill Num
        PdfPCell c = new PdfPCell(new Paragraph("رقم الطلب :", f));
        c.setColspan(2);
        c.setBorder(0);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c);
        PdfPCell c1 = new PdfPCell(new Paragraph(String.valueOf(billNum), f));
        c1.setColspan(2);
        c1.setBorder(0);
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        PdfPCell c0 = new PdfPCell(new Paragraph("", f));
        c0.setColspan(2);
        c0.setBorder(0);
        table.addCell(c0);
        //date 
        PdfPCell c2 = new PdfPCell(new Paragraph("التاريخ:", f));
        c2.setColspan(2);
        c2.setBorder(0);
        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c2);
        PdfPCell c3 = new PdfPCell(new Paragraph(IC.getDate(), f));
        c3.setColspan(4);
        c3.setBorder(0);
        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c3);
        ///bill body
        PdfPCell c00 = new PdfPCell(new Paragraph("", f));
        c00.setColspan(6);
        c00.setBorder(0);
        table.addCell(c00);
        table.addCell(c00);
        PdfPCell c4 = new PdfPCell(new Paragraph("الصنف", f));
        c4.setColspan(3);
        c4.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c4);
        PdfPCell c5 = new PdfPCell(new Paragraph("العدد", f));
        c5.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c5);
        PdfPCell c6 = new PdfPCell(new Paragraph("السعر", f));
        c6.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c6);
        PdfPCell c7 = new PdfPCell(new Paragraph("الاجمالي", f));
        c7.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c7);
        BillingData bd = new BillingData();
        for (int q = 0; q < IC.list.size(); q++) {
            bd = IC.list.get(q);
            PdfPCell c40 = new PdfPCell(new Paragraph(bd.getProductName(), f));
            c40.setColspan(3);
            table.addCell(c40);
            PdfPCell c50 = new PdfPCell(new Paragraph(bd.getProductCount(), f));
            c50.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c50);
            PdfPCell c60 = new PdfPCell(new Paragraph(bd.getProductPrice(), f));
            c60.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c60);
            PdfPCell c70 = new PdfPCell(new Paragraph(bd.getProductTotal(), f));
            c70.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c70);

        }
        table.addCell(c00);
        table.addCell(c00);
        ///total
        PdfPCell cel = new PdfPCell(new Paragraph("المجموع: ", f));
        cel.setColspan(2);
        cel.setBorderWidthLeft(0);
        cel.setBorderWidthRight(0);
        cel.setPaddingBottom(4f);
        cel.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cel);
        PdfPCell cl = new PdfPCell(new Paragraph(String.valueOf(totalPrice), f));
        cl.setColspan(4);
        cl.setBorderWidthLeft(0);
        cl.setBorderWidthRight(0);
        cl.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cl);
        table.addCell(c00);
        table.addCell(c00);
        //الباقي 
        PdfPCell ce = new PdfPCell(new Paragraph("المدفوع:" + paidTxt, f));
        ce.setColspan(3);
        ce.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(ce);
        PdfPCell ce1 = new PdfPCell(new Paragraph(totalChangeLabel, f));
        ce1.setColspan(3);
        ce1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(ce1);
        //casher name
        table.addCell(c00);
        PdfPCell c8 = new PdfPCell(new Paragraph("موظف الكاشير:", f));
        c8.setColspan(3);
        c8.setPadding(5f);
        c8.setHorizontalAlignment(Element.ALIGN_CENTER);
        c8.setBorder(0);
        table.addCell(c8);
        PdfPCell c9 = new PdfPCell(new Paragraph(IC.userName, f));
        c9.setColspan(3);
        c9.setBorder(0);
        table.addCell(c9);
        ///footer
        table.addCell(c00);
        PdfPCell cel0 = new PdfPCell(new Paragraph("شكرا  لزيارتكم", f1));
        cel0.setColspan(6);
        cel0.setPaddingBottom(4f);
        cel0.setBorderColorTop(BaseColor.BLACK);
        cel0.setBorderColorBottom(BaseColor.BLACK);
        cel0.setBorderWidthLeft(0);
        cel0.setBorderWidthRight(0);
        cel0.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cel0);
        ///////////////
        document.add(table);
        document.newPage();
        document.add(table);
        document.newPage();
        document.add(table);
        document.close();
    }

    //take away Bill
    public void printBillKitchen(int billNum) throws DocumentException, FileNotFoundException {
        PdfWriter.getInstance(document, new FileOutputStream("kitchen.pdf"));
        document.setMargins(8, 8, 10, 10);
        document.open();
        PdfPTable table = new PdfPTable(3);
        table.getDefaultCell().setMinimumHeight(12);
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        table.setWidthPercentage(98);
        table.getDefaultCell().setArabicOptions(1);
        ///head
        PdfPCell cell0 = new PdfPCell(new Paragraph("مطعم وكافيه ", f1));
        cell0.setColspan(3);
        cell0.setPaddingBottom(4f);
        cell0.setBorderColorTop(BaseColor.BLACK);
        cell0.setBorderColorBottom(BaseColor.BLACK);
        cell0.setBorderWidthLeft(0);
        cell0.setBorderWidthRight(0);
        cell0.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell0);
        ///bill Num
        PdfPCell c = new PdfPCell(new Paragraph("رقم الطلب :", f));
        c.setBorder(0);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c);
        PdfPCell c1 = new PdfPCell(new Paragraph(String.valueOf(billNum), f));
        c1.setBorder(0);
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        PdfPCell c00 = new PdfPCell(new Paragraph("", f));
        c00.setBorder(0);
        table.addCell(c00);
        //date 
        PdfPCell c2 = new PdfPCell(new Paragraph("التاريخ:", f));
        c2.setBorder(0);
        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c2);
        PdfPCell c3 = new PdfPCell(new Paragraph(IC.getDate(), f));
        c3.setBorder(0);
        c3.setColspan(2);
        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c3);
        ///bill body
        table.addCell(c00);
        table.addCell(c00);
        table.addCell(c00);
        PdfPCell c4 = new PdfPCell(new Paragraph("الصنف", f));
        c4.setHorizontalAlignment(Element.ALIGN_CENTER);
        c4.setColspan(2);
        table.addCell(c4);
        PdfPCell c5 = new PdfPCell(new Paragraph("العدد", f));
        c5.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c5);
        BillingData bd = new BillingData();
        for (int q = 0; q < IC.list.size(); q++) {
            bd = IC.list.get(q);
            PdfPCell c40 = new PdfPCell(new Paragraph(bd.getProductName(), f));
            c40.setColspan(2);
            table.addCell(c40);
            PdfPCell c50 = new PdfPCell(new Paragraph(bd.getProductCount(), f));
            c50.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c50);
        }
        table.addCell(c00);
        table.addCell(c00);
        //casher name
        table.addCell(c00);
        PdfPCell c8 = new PdfPCell(new Paragraph("موظف الكاشير:", f));
        c8.setPadding(5f);
        c8.setColspan(2);
        c8.setHorizontalAlignment(Element.ALIGN_CENTER);
        c8.setBorder(0);
        table.addCell(c8);
        PdfPCell c9 = new PdfPCell(new Paragraph(IC.userName, f));
        c9.setBorder(0);
        table.addCell(c9);
        ///footer
        table.addCell(c00);
        document.add(table);
        document.close();
    }
}