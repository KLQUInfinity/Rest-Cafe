/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UIForms;

import Classes.BillingData;
import Classes.ImportantClass;
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
import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Bendary
 */
public class Cashier extends javax.swing.JFrame {

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd    HH:mm:ss");
    Date date = new Date();
    private final ImportantClass IC = ImportantClass.getInstance();
    BillingData dd = new BillingData();
    public ArrayList<Double> productPrice = new ArrayList<>();

    //This variable modify in table.
    private DefaultTableModel dtm;
    private double totalPrice = 0;
    private int billNum = 0;
    private double paid = 0, change = 0;

    /**
     * Creates new form Casher
     */
    public Cashier() {
        // Check Conection to DB
        if (!IC.dbc.check) {
            IC.dbc.ConnectDB();
        }

        initComponents();

        WindowListener exitListener = null;
        addWindowListener(prepareWindow(exitListener));

        casherTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));
        casherTable.getTableHeader().setAlignmentY(CENTER_ALIGNMENT);
        DefaultTableCellRenderer c = new DefaultTableCellRenderer();
        c.setHorizontalAlignment(JLabel.CENTER);
        c.setFont(new Font("Tahoma", Font.PLAIN, 13));
        casherTable.setDefaultRenderer(Object.class, c);
        casherTable.setAutoCreateColumnsFromModel(false);
        dtm = (DefaultTableModel) casherTable.getModel();

        getAllProductData();
        getLastBillNum();
    }

    private WindowListener prepareWindow(WindowListener exitListener) {
        exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (casherTable.getRowCount() == 0) {
                    Home m = new Home();
                    Cashier.this.dispose();
                    m.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "من فضلك انهي الطلب اولا");
                }
            }
        };
        return exitListener;
    }

    private void getAllProductData() {
        clearTextFields();
        try {
            // Get all Product
            IC.pst = IC.dbc.conn.prepareStatement("select CONCAT(productName,' ',productType,' ',productSubType) as productName"
                    + ", productPrice "
                    + " from sql2283641.product"
                    + " where productType=?");
            IC.pst.setString(1, productTypeCB.getSelectedItem().toString());
            IC.rs = IC.pst.executeQuery();
            ArrayList<String> products = new ArrayList<>();
            while (IC.rs.next()) {
                products.add(IC.rs.getString("productName"));
                productPrice.add(IC.rs.getDouble("productPrice"));
            }
            productNameCB.setModel(new DefaultComboBoxModel(products.toArray()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void getLastBillNum() {
        try {
            // Get all Product
            IC.pst = IC.dbc.conn.prepareStatement("select max(orderNum) from sql2283641.order");
            IC.rs = IC.pst.executeQuery();
            if (IC.rs.next()) {
                billNum = IC.rs.getInt("max(orderNum)") + 1;
                orderNumLabel.setText("رقم الطلب : " + billNum);
            }

            totalPrice = 0;
            totalLabel.setText("الاجمالي : " + totalPrice);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearTextFields() {
        countTxt.setText("");
        notesTA.setText("");
        productPrice.clear();
    }

    private void btnEnabled(boolean check) {
        updateBtn.setEnabled(check);
        deleteBtn.setEnabled(check);
        addBtn.setEnabled(!check);
        clearSelectionBtn.setEnabled(check);
    }

    //print pdf
    private void pdfPrint() {
        FileInputStream psStream = null;
        try {
            psStream = new FileInputStream("ss.pdf");
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

    private void printBill() throws DocumentException, FileNotFoundException {
        Document document = new Document(PageSize.A7);
        com.itextpdf.text.Font f = FontFactory.getFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, 10f);
        com.itextpdf.text.Font f1 = FontFactory.getFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, 13f);
        PdfWriter.getInstance(document, new FileOutputStream("ss.pdf"));
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
        PdfPCell c3 = new PdfPCell(new Paragraph(df.format(date).toString(), f));
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
        PdfPCell ce = new PdfPCell(new Paragraph("المدفوع:" + paidTxt.getText(), f));
        ce.setColspan(3);
        ce.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(ce);
        PdfPCell ce1 = new PdfPCell(new Paragraph(totalChangeLabel.getText(), f));
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

        document.add(table);
        document.newPage();
        document.add(table);
        document.newPage();
        document.add(table);

        document.close();
    }

    private void calculateTotalChange() {
        try {
            if (!paidTxt.getText().equals("")) {
                paid = Double.parseDouble(paidTxt.getText());
            }
            if (paid >= 0) {
                change = paid - totalPrice;
                totalChangeLabel.setText("الباقي : " + change);
            } else {
                JOptionPane.showMessageDialog(null, "من فضلك ادخل قيمة اكبر من الصفر");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "من فضلك ادخل قيمة عدديه");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        productLabel = new javax.swing.JLabel();
        productNameCB = new javax.swing.JComboBox<>();
        countLabel = new javax.swing.JLabel();
        countTxt = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        notesTA = new javax.swing.JTextArea();
        productTypeLabel = new javax.swing.JLabel();
        productTypeCB = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        paidTxt = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        orderNumLabel = new javax.swing.JLabel();
        addBtn = new javax.swing.JButton();
        clearSelectionBtn = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        casherTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        updateBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        totalLabel = new javax.swing.JLabel();
        submitBtn = new javax.swing.JButton();
        totalChangeLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("كاشير");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(890, 548));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        productLabel.setBackground(new java.awt.Color(204, 204, 204));
        productLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        productLabel.setForeground(new java.awt.Color(255, 0, 0));
        productLabel.setText("اسم الصنف");

        productNameCB.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        productNameCB.setForeground(new java.awt.Color(255, 0, 0));
        productNameCB.setPreferredSize(new java.awt.Dimension(175, 26));

        countLabel.setBackground(new java.awt.Color(204, 204, 204));
        countLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        countLabel.setForeground(new java.awt.Color(255, 0, 0));
        countLabel.setText("العدد");

        countTxt.setForeground(new java.awt.Color(255, 0, 0));
        countTxt.setText("0");
        countTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                countTxtActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("ملاحظات");

        notesTA.setColumns(20);
        notesTA.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        notesTA.setRows(5);
        jScrollPane2.setViewportView(notesTA);

        productTypeLabel.setBackground(new java.awt.Color(204, 204, 204));
        productTypeLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        productTypeLabel.setForeground(new java.awt.Color(255, 0, 0));
        productTypeLabel.setText("نوع الصنف");
        productTypeLabel.setPreferredSize(new java.awt.Dimension(69, 17));

        productTypeCB.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        productTypeCB.setForeground(new java.awt.Color(255, 0, 0));
        productTypeCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "الشاورما", "البيتزا", "المشويات", "الشرقي" }));
        productTypeCB.setPreferredSize(new java.awt.Dimension(175, 26));
        productTypeCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                productTypeCBItemStateChanged(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("المدفوع");
        jLabel2.setPreferredSize(new java.awt.Dimension(69, 17));

        paidTxt.setForeground(new java.awt.Color(255, 0, 0));
        paidTxt.setText("0.0");
        paidTxt.setPreferredSize(new java.awt.Dimension(187, 24));
        paidTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                paidTxtCaretUpdate(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(productNameCB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(countTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(countLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(paidTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(productLabel)
                        .addGap(18, 18, 18)
                        .addComponent(productTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(productTypeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(productNameCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(productLabel)
                    .addComponent(productTypeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(productTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(countLabel)
                    .addComponent(countTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(paidTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(187, 187, 187));
        jPanel8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        orderNumLabel.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        orderNumLabel.setForeground(new java.awt.Color(255, 0, 0));
        orderNumLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        orderNumLabel.setText("رقم الطلب : 0");
        orderNumLabel.setPreferredSize(new java.awt.Dimension(188, 43));

        addBtn.setBackground(new java.awt.Color(255, 0, 0));
        addBtn.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        addBtn.setForeground(new java.awt.Color(255, 255, 255));
        addBtn.setText("اضافة صنف");
        addBtn.setPreferredSize(new java.awt.Dimension(166, 40));
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        clearSelectionBtn.setBackground(new java.awt.Color(255, 0, 0));
        clearSelectionBtn.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        clearSelectionBtn.setForeground(new java.awt.Color(255, 255, 255));
        clearSelectionBtn.setText("الغاء الاختيار");
        clearSelectionBtn.setEnabled(false);
        clearSelectionBtn.setPreferredSize(new java.awt.Dimension(166, 40));
        clearSelectionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearSelectionBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(clearSelectionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(244, 244, 244)
                .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addComponent(orderNumLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(addBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(orderNumLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clearSelectionBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 890, 180));

        casherTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "نوع الصنف", "ملاحظات", "التاريخ", "المجموع", "السعر", "العدد", "اسم الصنف"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        casherTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        casherTable.getTableHeader().setReorderingAllowed(false);
        casherTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                casherTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(casherTable);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 890, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 188, 890, 220));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(187, 187, 187));
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        updateBtn.setBackground(new java.awt.Color(255, 0, 0));
        updateBtn.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        updateBtn.setForeground(new java.awt.Color(255, 255, 255));
        updateBtn.setText("تعديل صنف");
        updateBtn.setEnabled(false);
        updateBtn.setPreferredSize(new java.awt.Dimension(150, 78));
        updateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });

        deleteBtn.setBackground(new java.awt.Color(255, 0, 0));
        deleteBtn.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        deleteBtn.setForeground(new java.awt.Color(255, 255, 255));
        deleteBtn.setText("حذف صنف");
        deleteBtn.setEnabled(false);
        deleteBtn.setPreferredSize(new java.awt.Dimension(150, 78));
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(187, 187, 187));
        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        totalLabel.setBackground(new java.awt.Color(255, 255, 255));
        totalLabel.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        totalLabel.setForeground(new java.awt.Color(255, 0, 0));
        totalLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalLabel.setText("الاجمالي : 0.0");
        totalLabel.setPreferredSize(new java.awt.Dimension(266, 28));

        submitBtn.setBackground(new java.awt.Color(255, 0, 0));
        submitBtn.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        submitBtn.setForeground(new java.awt.Color(255, 255, 255));
        submitBtn.setText("تنفيذ الطلب");
        submitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitBtnActionPerformed(evt);
            }
        });

        totalChangeLabel.setBackground(new java.awt.Color(255, 255, 255));
        totalChangeLabel.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        totalChangeLabel.setForeground(new java.awt.Color(255, 0, 0));
        totalChangeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalChangeLabel.setText("الباقي : 0.0");
        totalChangeLabel.setPreferredSize(new java.awt.Dimension(266, 28));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(submitBtn)
                .addGap(39, 39, 39)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(totalChangeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(submitBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(totalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalChangeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 420, 890, 130));

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        if (!countTxt.getText().equals("")) {
            try {
                int count = Integer.parseInt(countTxt.getText());
                if (count > 0) {
                    double price = count * productPrice.get(productNameCB.getSelectedIndex());

                    Object[] rowData = {productTypeCB.getSelectedItem().toString(),
                        notesTA.getText(),
                        IC.getDate(),
                        price,
                        productPrice.get(productNameCB.getSelectedIndex()),
                        Integer.parseInt(countTxt.getText()),
                        productNameCB.getSelectedItem().toString()};
                    dtm.addRow(rowData);
                    totalPrice += price;
                    totalLabel.setText("الاجمالي : " + totalPrice);
                    getAllProductData();
                    calculateTotalChange();
                } else {
                    JOptionPane.showMessageDialog(null, "من فضلك ادخل قيمة اكبر من الصفر");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "من فضلك ادخل قيمة عدديه");
            }
        } else {
            JOptionPane.showMessageDialog(null, "من فضلك املا خانة العدد");
        }
        getLastBillNum();
    }//GEN-LAST:event_addBtnActionPerformed

    private void casherTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_casherTableMouseClicked
        if (casherTable.getSelectedRow() != -1) {
            btnEnabled(true);
            productNameCB.setSelectedItem(casherTable.getValueAt(casherTable.getSelectedRow(), 6));
            productTypeCB.setSelectedItem(casherTable.getValueAt(casherTable.getSelectedRow(), 0));
            countTxt.setText(casherTable.getValueAt(casherTable.getSelectedRow(), 5).toString());
            notesTA.setText(casherTable.getValueAt(casherTable.getSelectedRow(), 1).toString());
        }
    }//GEN-LAST:event_casherTableMouseClicked

    private void clearSelectionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearSelectionBtnActionPerformed
        casherTable.clearSelection();
        clearTextFields();
        btnEnabled(false);
    }//GEN-LAST:event_clearSelectionBtnActionPerformed

    private void updateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
        try {
            int count = Integer.parseInt(countTxt.getText());
            if (count > 0) {
                totalPrice -= Double.parseDouble(casherTable.getValueAt(casherTable.getSelectedRow(), 3).toString());
                double price = count * productPrice.get(productNameCB.getSelectedIndex());

                casherTable.setValueAt(productNameCB.getSelectedItem().toString(), casherTable.getSelectedRow(), 6);
                casherTable.setValueAt(countTxt.getText(), casherTable.getSelectedRow(), 5);
                casherTable.setValueAt(productPrice.get(productNameCB.getSelectedIndex()), casherTable.getSelectedRow(), 4);
                casherTable.setValueAt(price, casherTable.getSelectedRow(), 3);
                casherTable.setValueAt(IC.getDate(), casherTable.getSelectedRow(), 2);
                casherTable.setValueAt(notesTA.getText(), casherTable.getSelectedRow(), 1);
                casherTable.setValueAt(productTypeCB.getSelectedItem().toString(), casherTable.getSelectedRow(), 0);

                totalPrice += price;
                totalLabel.setText("الاجمالي : " + totalPrice);
                clearSelectionBtn.doClick();
                getAllProductData();
                calculateTotalChange();
            } else {
                JOptionPane.showMessageDialog(null, "من فضلك ادخل قيمة اكبر من الصفر");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "من فضلك ادخل قيمة عدديه");
        }
        getLastBillNum();
    }//GEN-LAST:event_updateBtnActionPerformed

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        totalPrice -= Double.parseDouble(casherTable.getValueAt(casherTable.getSelectedRow(), 3).toString());
        totalLabel.setText("الاجمالي : " + totalPrice);

        dtm.removeRow(casherTable.getSelectedRow());
        clearSelectionBtn.doClick();
        getAllProductData();
        calculateTotalChange();
        getLastBillNum();
    }//GEN-LAST:event_deleteBtnActionPerformed

    private void submitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitBtnActionPerformed
        getLastBillNum();
        if (casherTable.getRowCount() > 0) {
            int y = JOptionPane.showConfirmDialog(null,
                    "هل تريد تاكيد الفاتوره؟\nمن فضلك تاكد من العميل بان هذا كل شئ",
                    "رسالة تاكيد",
                    JOptionPane.YES_NO_OPTION);
            if (y == 0) {
                try {
                    for (int i = 0; i < casherTable.getRowCount(); i++) {
                        IC.pst = IC.dbc.conn.prepareStatement("insert into sql2283641.order("
                                + "orderNum, orderProduct,"
                                + "orderCount, orderPrice,"
                                + "orderTotal, orderDate,"
                                + "orderNotes, userName,"
                                + "orderType)"
                                + "values(?,?,?,?,?,?,?,?,?)");

                        IC.pst.setInt(1, billNum);
                        IC.pst.setString(2, casherTable.getValueAt(i, 6).toString());
                        IC.pst.setInt(3, Integer.parseInt(casherTable.getValueAt(i, 5).toString()));
                        IC.pst.setDouble(4, Double.parseDouble(casherTable.getValueAt(i, 4).toString()));
                        IC.pst.setDouble(5, Double.parseDouble(casherTable.getValueAt(i, 3).toString()));
                        IC.pst.setString(6, casherTable.getValueAt(i, 2).toString());
                        IC.pst.setString(7, casherTable.getValueAt(i, 1).toString());
                        IC.pst.setString(8, IC.userName);
                        IC.pst.setString(9, casherTable.getValueAt(i, 0).toString());

                        //add order data to be printed 
                        dd.setProductCount(casherTable.getValueAt(i, 5).toString());
                        dd.setProductName(casherTable.getValueAt(i, 6).toString());
                        dd.setProductPrice(casherTable.getValueAt(i, 4).toString());
                        dd.setProductTotal(casherTable.getValueAt(i, 3).toString());
                        dd.setBillNum(billNum + "");
                        IC.list.add(dd);

                        IC.pst.execute();
                    }

                    // Print method
                    printBill();
                    pdfPrint();
                    // Rest all varibles
                    dtm.setRowCount(0);
                    getLastBillNum();

                    paid = 0;
                    change = 0;
                    paidTxt.setText("0.0");
                    totalChangeLabel.setText("الباقي : " + change);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (DocumentException ex) {
                    Logger.getLogger(Cashier.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Cashier.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "لايوجد اي بيانات مضافة في الجدول");
        }
    }//GEN-LAST:event_submitBtnActionPerformed

    private void countTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_countTxtActionPerformed
        addBtn.doClick();
    }//GEN-LAST:event_countTxtActionPerformed

    private void productTypeCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_productTypeCBItemStateChanged
        getAllProductData();
    }//GEN-LAST:event_productTypeCBItemStateChanged

    private void paidTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_paidTxtCaretUpdate
        calculateTotalChange();
    }//GEN-LAST:event_paidTxtCaretUpdate

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Cashier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cashier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cashier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cashier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Cashier().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JTable casherTable;
    private javax.swing.JButton clearSelectionBtn;
    private javax.swing.JLabel countLabel;
    private javax.swing.JTextField countTxt;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea notesTA;
    private javax.swing.JLabel orderNumLabel;
    private javax.swing.JTextField paidTxt;
    private javax.swing.JLabel productLabel;
    private javax.swing.JComboBox<String> productNameCB;
    private javax.swing.JComboBox<String> productTypeCB;
    private javax.swing.JLabel productTypeLabel;
    private javax.swing.JButton submitBtn;
    private javax.swing.JLabel totalChangeLabel;
    private javax.swing.JLabel totalLabel;
    private javax.swing.JButton updateBtn;
    // End of variables declaration//GEN-END:variables
}
