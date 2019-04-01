/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UIForms;

import Classes.BillingData;
import Classes.Bills_Printing_Reports;
import Classes.ImportantClass;
import com.itextpdf.text.DocumentException;
import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.print.PrinterException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Bendary
 */
public class Metre extends javax.swing.JFrame {

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd    HH:mm:ss");
    Date date = new Date();
    private final ImportantClass IC = ImportantClass.getInstance();
    public ArrayList<Double> productPrice = new ArrayList<>();
    Bills_Printing_Reports BPR = new Bills_Printing_Reports();

    //This variable modify in table.
    private DefaultTableModel dtm;
    private double totalPrice = 0;
    private int billNum = 0;
    private String titl = "دليفري";
    private boolean kitchin1 = false;
    private boolean kitchin2 = false;
    private boolean kitchin3 = false;
    private final float delaveryValue = 3;
    private boolean Cafe;

    /**
     * Creates new form Casher
     */
    public Metre() {
        // Check Conection to DB
        if (!IC.dbc.check) {
            IC.dbc.ConnectDB();
        }
        initComponents();
        if (!IC.jobTitle.equals("Delivery")) {
            productLabel1.setVisible(false);
            delverNameCB.setVisible(false);
            clientName.setVisible(false);
            address.setVisible(false);
            phone.setVisible(false);
            countLabel1.setVisible(false);
            countLabel2.setVisible(false);
            countLabel3.setVisible(false);
        } else {
            tabNum.setVisible(false);
            jLabel2.setVisible(false);
            totalPrice = delaveryValue;
            totalLabel.setText("الاجمالي : " + totalPrice);
        }
        WindowListener exitListener = null;
        addWindowListener(prepareWindow(exitListener));
        IC.dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        CheckDate();

        casherTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));
        casherTable.getTableHeader().setAlignmentY(CENTER_ALIGNMENT);
        DefaultTableCellRenderer c = new DefaultTableCellRenderer();
        c.setHorizontalAlignment(JLabel.CENTER);
        c.setFont(new Font("Tahoma", Font.PLAIN, 13));
        casherTable.setDefaultRenderer(Object.class, c);
        casherTable.setAutoCreateColumnsFromModel(false);
        dtm = (DefaultTableModel) casherTable.getModel();

        category();
        getAllProductData();
        getLastBillNum();
        SelectDelvery();
    }

    public void SelectDelvery() {
        try {
            IC.pst = IC.dbc.conn.prepareStatement("SELECT userName FROM rest_cafe.user where userType ='Deliver'");
            IC.rs = IC.pst.executeQuery();
            while (IC.rs.next()) {
                delverNameCB.addItem(IC.rs.getString("userName"));
            }
        } catch (SQLException e) {
        }
    }

    private WindowListener prepareWindow(WindowListener exitListener) {
        exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (casherTable.getRowCount() == 0) {
                    Home m = new Home();
                    Metre.this.dispose();
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
            IC.pst = IC.dbc.conn.prepareStatement("select CONCAT(productSubType,' ',productName) as productName"
                    + ", productPrice "
                    + " from rest_cafe.product"
                    + " where productType ='" + productTypeCB.getSelectedItem().toString() + "'"
                    + "AND  productSubType ='" + categoryCB.getSelectedItem().toString() + "'");
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

    private void category() {
        try {
            categoryCB.removeAllItems();
            // Get all Product
            IC.pst = IC.dbc.conn.prepareStatement("select DISTINCT productSubType from rest_cafe.product "
                    + "WHERE productType =?");
            IC.pst.setString(1, productTypeCB.getSelectedItem().toString());
            IC.rs = IC.pst.executeQuery();
            ArrayList<String> productCategory = new ArrayList<>();
            while (IC.rs.next()) {
                String s = IC.rs.getString(1);
                productCategory.add(s);
            }
            categoryCB.setModel(new DefaultComboBoxModel(productCategory.toArray()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void CheckDate() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (IC.dayOfYear != Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                    IC.dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                    JOptionPane.showMessageDialog(null, "يوم جديد");
                    getLastBillNum();
                }
            }
        });
        timer.start();
    }

    private void getLastBillNum() {
        try {
            // Get all Product
            IC.pst = IC.dbc.conn.prepareStatement("select max(orderNum) from rest_cafe.order "
                    + "where orderDate=?");
            IC.pst.setString(1, IC.getDateOnly());
            IC.rs = IC.pst.executeQuery();
            if (IC.rs.next()) {
                billNum = IC.rs.getInt("max(orderNum)") + 1;
                orderNumLabel.setText("رقم الطلب : " + billNum);
            }
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
        productLabel1 = new javax.swing.JLabel();
        delverNameCB = new javax.swing.JComboBox<>();
        countLabel1 = new javax.swing.JLabel();
        clientName = new javax.swing.JTextField();
        countLabel2 = new javax.swing.JLabel();
        address = new javax.swing.JTextField();
        phone = new javax.swing.JTextField();
        countLabel3 = new javax.swing.JLabel();
        categoryCB = new javax.swing.JComboBox<>();
        productLabel2 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tabNum = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        casherTable = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        orderNumLabel = new javax.swing.JLabel();
        addBtn = new javax.swing.JButton();
        clearSelectionBtn = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        updateBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        deleteBtn1 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        totalLabel = new javax.swing.JLabel();
        submitBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("ميتر");
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
        productNameCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                productNameCBItemStateChanged(evt);
            }
        });

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
        productTypeCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "شرقي", "غربي", "كريب و بيتزا", "الاضافات", "الكافيه" }));
        productTypeCB.setPreferredSize(new java.awt.Dimension(175, 26));
        productTypeCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                productTypeCBItemStateChanged(evt);
            }
        });

        productLabel1.setBackground(new java.awt.Color(204, 204, 204));
        productLabel1.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        productLabel1.setForeground(new java.awt.Color(255, 0, 0));
        productLabel1.setText("اسم الطيار");

        delverNameCB.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        delverNameCB.setForeground(new java.awt.Color(255, 0, 0));
        delverNameCB.setPreferredSize(new java.awt.Dimension(175, 26));

        countLabel1.setBackground(new java.awt.Color(204, 204, 204));
        countLabel1.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        countLabel1.setForeground(new java.awt.Color(255, 0, 0));
        countLabel1.setText("اسم العميل");

        clientName.setForeground(new java.awt.Color(255, 0, 0));
        clientName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientNameActionPerformed(evt);
            }
        });

        countLabel2.setBackground(new java.awt.Color(204, 204, 204));
        countLabel2.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        countLabel2.setForeground(new java.awt.Color(255, 0, 0));
        countLabel2.setText("العنوان");

        address.setForeground(new java.awt.Color(255, 0, 0));
        address.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addressActionPerformed(evt);
            }
        });

        phone.setForeground(new java.awt.Color(255, 0, 0));
        phone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phoneActionPerformed(evt);
            }
        });

        countLabel3.setBackground(new java.awt.Color(204, 204, 204));
        countLabel3.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        countLabel3.setForeground(new java.awt.Color(255, 0, 0));
        countLabel3.setText("رقم الموبيل");

        categoryCB.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        categoryCB.setForeground(new java.awt.Color(255, 0, 0));
        categoryCB.setPreferredSize(new java.awt.Dimension(175, 26));
        categoryCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                categoryCBItemStateChanged(evt);
            }
        });

        productLabel2.setBackground(new java.awt.Color(204, 204, 204));
        productLabel2.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        productLabel2.setForeground(new java.awt.Color(255, 0, 0));
        productLabel2.setText("التصنيف");

        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("رقم الترابيزه");

        tabNum.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        tabNum.setForeground(new java.awt.Color(255, 0, 0));
        tabNum.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(tabNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)))
                .addGap(54, 54, 54)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(productNameCB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(countTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(phone, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(countLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(delverNameCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(productLabel1))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGap(71, 71, 71)
                                        .addComponent(clientName, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(countLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                        .addComponent(countLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(categoryCB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                        .addComponent(productLabel)
                                        .addGap(18, 18, 18)
                                        .addComponent(productTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(productLabel2)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(productTypeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)))))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(address, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(countLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(productNameCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(productLabel)
                            .addComponent(productTypeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(productTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(categoryCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(productLabel2))
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(countLabel)
                                .addComponent(countTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(productLabel1)
                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(countLabel3)
                                        .addComponent(phone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(delverNameCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(countLabel1)
                        .addComponent(clientName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(countLabel2)
                        .addComponent(address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(tabNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                .addGap(97, 97, 97))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 890, 170));

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
                .addGap(18, 18, 18)
                .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 300, Short.MAX_VALUE)
                .addComponent(orderNumLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(orderNumLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(clearSelectionBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                        .addComponent(addBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 170, 890, 250));

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

        deleteBtn1.setBackground(new java.awt.Color(255, 0, 0));
        deleteBtn1.setFont(new java.awt.Font("Times New Roman", 1, 20)); // NOI18N
        deleteBtn1.setForeground(new java.awt.Color(255, 255, 255));
        deleteBtn1.setText("حذف الطلب");
        deleteBtn1.setPreferredSize(new java.awt.Dimension(150, 78));
        deleteBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(deleteBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(submitBtn)
                .addGap(39, 39, 39)
                .addComponent(totalLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(totalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(15, Short.MAX_VALUE))
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
                        IC.getDateOnly(),
                        price,
                        productPrice.get(productNameCB.getSelectedIndex()),
                        Integer.parseInt(countTxt.getText()),
                        productNameCB.getSelectedItem().toString()};
                    dtm.addRow(rowData);
                    totalPrice += price;
                    totalLabel.setText("الاجمالي : " + totalPrice);
                    getAllProductData();
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
                casherTable.setValueAt(IC.getAllDate(), casherTable.getSelectedRow(), 2);
                casherTable.setValueAt(notesTA.getText(), casherTable.getSelectedRow(), 1);
                casherTable.setValueAt(productTypeCB.getSelectedItem().toString(), casherTable.getSelectedRow(), 0);

                totalPrice += price;
                totalLabel.setText("الاجمالي : " + totalPrice);
                clearSelectionBtn.doClick();
                getAllProductData();
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
                    submitBtn.setEnabled(false);
                    for (int i = 0; i < casherTable.getRowCount(); i++) {
                        IC.pst = IC.dbc.conn.prepareStatement("insert into rest_cafe.order("
                                + "orderNum, orderProduct,"
                                + "orderCount, orderPrice,"
                                + "orderTotal, orderDate,"
                                + "orderNotes, userName,"
                                + "orderType,orderKind,"
                                + "delivery, clientName,"
                                + "clientPhone,clientAddress,"
                                + "tabNum)"
                                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                        IC.pst.setInt(1, billNum);
                        IC.pst.setString(2, casherTable.getValueAt(i, 6).toString());
                        IC.pst.setInt(3, Integer.parseInt(casherTable.getValueAt(i, 5).toString()));
                        IC.pst.setDouble(4, Double.parseDouble(casherTable.getValueAt(i, 4).toString()));
                        IC.pst.setDouble(5, Double.parseDouble(casherTable.getValueAt(i, 3).toString()));
                        IC.pst.setString(6, casherTable.getValueAt(i, 2).toString());
                        IC.pst.setString(7, casherTable.getValueAt(i, 1).toString());
                        IC.pst.setString(8, IC.userName);
                        IC.pst.setString(9, casherTable.getValueAt(i, 0).toString());
                        if (IC.jobTitle.equals("Delivery")) {
                            titl = "دليفري";
                        } else if (IC.jobTitle.equals("Metre")) {
                            titl = "سفرة";
                        }
                        IC.pst.setString(10, titl);
                        if (!IC.jobTitle.equals("Delivery")) {
                            IC.pst.setString(11, " ");
                            IC.pst.setString(12, " ");
                            IC.pst.setString(13, " ");
                            IC.pst.setString(14, " ");
                            IC.pst.setString(15, tabNum.getSelectedItem().toString());
                        } else {
                            IC.pst.setString(11, delverNameCB.getSelectedItem().toString());
                            IC.pst.setString(12, clientName.getText());
                            IC.pst.setString(13, phone.getText());
                            IC.pst.setString(14, address.getText());
                            IC.pst.setString(15, " ");
                        }
                        //add order data to be printed 
                        BillingData dd = new BillingData();
                        dd.setProductCount(casherTable.getValueAt(i, 5).toString());
                        dd.setProductName(casherTable.getValueAt(i, 6).toString());
                        dd.setProductPrice(casherTable.getValueAt(i, 4).toString());
                        dd.setProductTotal(casherTable.getValueAt(i, 3).toString());
                        dd.setProductKitchen(casherTable.getValueAt(i, 0).toString());
                        dd.setBillNum(billNum + "");
                        IC.list.add(dd);

                        IC.pst.execute();

                        if (casherTable.getValueAt(i, 0).toString().equals("فرعي")) {
                            kitchin1 = true;
                        }

                        if (casherTable.getValueAt(i, 0).toString().equals("شرقي")) {
                            kitchin2 = true;
                        }

                        if (casherTable.getValueAt(i, 0).toString().equals("غربي")) {
                            kitchin3 = true;
                        }
                        if (casherTable.getValueAt(i, 0).toString().equals("الكافيه")) {
                            Cafe = true;
                        }
                    }

                    // Print method
                    if (kitchin1 == true) {
                        if (titl.equals("دليفري")) {
                            BPR.printBillKitchen1(billNum, titl, notesTA.getText(), delverNameCB.getSelectedItem().toString(), "");
                        } else {
                            BPR.printBillKitchen1(billNum, titl, notesTA.getText(), "", tabNum.getSelectedItem().toString());
                        }
                    }
                    if (kitchin2 == true) {
                        if (titl.equals("دليفري")) {
                            BPR.printBillKitchen2(billNum, titl, notesTA.getText(), delverNameCB.getSelectedItem().toString(), "");
                        } else {
                            BPR.printBillKitchen2(billNum, titl, notesTA.getText(), "", tabNum.getSelectedItem().toString());
                        }
                    }
                    if (kitchin3 == true) {
                        if (titl.equals("دليفري")) {
                            BPR.printBillKitchen3(billNum, titl, notesTA.getText(), delverNameCB.getSelectedItem().toString(), "");
                        } else {
                            BPR.printBillKitchen3(billNum, titl, notesTA.getText(), "", tabNum.getSelectedItem().toString());
                        }
                    }
                    if (titl.equals("دليفري")) {
                        BPR.printBill(billNum, totalPrice, "", "", titl, delverNameCB.getSelectedItem().toString(), clientName.getText(), phone.getText(), address.getText(), "");
                    } else {
                        BPR.printBill(billNum, totalPrice, "", "", titl, "", "", "", "", tabNum.getSelectedItem().toString());
                    }
                    if (Cafe == true) {
                        if (titl.equals("دليفري")) {
                            BPR.printBillCafe(billNum, titl, notesTA.getText(), delverNameCB.getSelectedItem().toString(), "");
                        } else {
                            BPR.printBillCafe(billNum, titl, notesTA.getText(), "", tabNum.getSelectedItem().toString());
                        }
                    }
                    if (titl.equals("دليفري")) {
                        //client
                        BPR.pdfPrint("client.pdf", "POS-80bu");
                        //backup
                        BPR.pdfPrint("client.pdf", "POS-80dv");
                    } else {
//                    client
                        BPR.pdfPrint("client.pdf", "Xprinter XP-370B");
                        //backup
                        BPR.pdfPrint("client.pdf", "POS-80bu");
                    }

                    if (kitchin1 == true) {
//                        BPR.pdfPrint("kitchen1.pdf", "POS-80pz on Pc1");
                        BPR.pdfPrint("kitchen1.pdf", "POS-80pz");
                    }
                    if (kitchin2 == true) {
//                        BPR.pdfPrint("kitchen2.pdf", "POS-80ss on PC3");
                        BPR.pdfPrint("kitchen2.pdf", "POS-80ss");
                    }
                    if (kitchin3 == true) {
//                        BPR.pdfPrint("kitchen3.pdf", "POS-80gh on PC1");
                        BPR.pdfPrint("kitchen3.pdf", "POS-80gh");
                    }
                    if (Cafe == true) {
                        if (titl.equals("دليفري")) {
                            BPR.pdfPrint("Cafe.pdf", "POS-80dv");
                        } else {
                        }
                        BPR.pdfPrint("Cafe.pdf", "Xprinter XP-370B");
                    }
                    // Rest all varibles
                    dtm.setRowCount(0);
                    getLastBillNum();
                    if (IC.jobTitle.equals("Delivery")) {
                        totalPrice = delaveryValue;
                    } else {
                        totalPrice = 0;
                    }
                    totalLabel.setText("الاجمالي : " + totalPrice);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (DocumentException ex) {
                    Logger.getLogger(Cashier.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Cashier.class.getName()).log(Level.SEVERE, null, ex);
                } catch (PrinterException ex) {
                    Logger.getLogger(Metre.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Metre.class.getName()).log(Level.SEVERE, null, ex);
                } catch (PrintException ex) {
                    Logger.getLogger(Metre.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "لايوجد اي بيانات مضافة في الجدول");
        }
        kitchin1 = false;
        kitchin2 = false;
        kitchin3 = false;
        Cafe = false;
        IC.list.clear();
        clientName.setText("");
        phone.setText("");
        address.setText("");
        submitBtn.setEnabled(true);
    }//GEN-LAST:event_submitBtnActionPerformed

    private void countTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_countTxtActionPerformed
        addBtn.doClick();
    }//GEN-LAST:event_countTxtActionPerformed

    private void productTypeCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_productTypeCBItemStateChanged
        category();
    }//GEN-LAST:event_productTypeCBItemStateChanged

    private void clientNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clientNameActionPerformed

    private void addressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addressActionPerformed

    private void phoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_phoneActionPerformed

    private void casherTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_casherTableMouseClicked
        if (casherTable.getSelectedRow() != -1) {
            btnEnabled(true);
            productNameCB.setSelectedItem(casherTable.getValueAt(casherTable.getSelectedRow(), 6));
            productTypeCB.setSelectedItem(casherTable.getValueAt(casherTable.getSelectedRow(), 0));
            countTxt.setText(casherTable.getValueAt(casherTable.getSelectedRow(), 5).toString());
            notesTA.setText(casherTable.getValueAt(casherTable.getSelectedRow(), 1).toString());
        }
    }//GEN-LAST:event_casherTableMouseClicked

    private void categoryCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_categoryCBItemStateChanged
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
            @Override
            public void run() {
                getAllProductData();
            }
        },
                500
        );
    }//GEN-LAST:event_categoryCBItemStateChanged

    private void productNameCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_productNameCBItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_productNameCBItemStateChanged

    private void deleteBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtn1ActionPerformed
        dtm.setRowCount(0);
        if (IC.jobTitle.equals("Delivery")) {
            totalPrice = delaveryValue;
        } else {
            totalPrice = 0;
        }
        totalLabel.setText("الاجمالي : " + totalPrice);
    }//GEN-LAST:event_deleteBtn1ActionPerformed

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
            java.util.logging.Logger.getLogger(Metre.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Metre.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Metre.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Metre.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Metre().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JTextField address;
    private javax.swing.JTable casherTable;
    private javax.swing.JComboBox<String> categoryCB;
    private javax.swing.JButton clearSelectionBtn;
    private javax.swing.JTextField clientName;
    private javax.swing.JLabel countLabel;
    private javax.swing.JLabel countLabel1;
    private javax.swing.JLabel countLabel2;
    private javax.swing.JLabel countLabel3;
    private javax.swing.JTextField countTxt;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JButton deleteBtn1;
    private javax.swing.JComboBox<String> delverNameCB;
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
    private javax.swing.JTextField phone;
    private javax.swing.JLabel productLabel;
    private javax.swing.JLabel productLabel1;
    private javax.swing.JLabel productLabel2;
    private javax.swing.JComboBox<String> productNameCB;
    private javax.swing.JComboBox<String> productTypeCB;
    private javax.swing.JLabel productTypeLabel;
    private javax.swing.JButton submitBtn;
    private javax.swing.JComboBox<String> tabNum;
    private javax.swing.JLabel totalLabel;
    private javax.swing.JButton updateBtn;
    // End of variables declaration//GEN-END:variables
}
