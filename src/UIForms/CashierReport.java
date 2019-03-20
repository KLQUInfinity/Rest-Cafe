/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UIForms;

import Classes.ExcelReport;
import Classes.ImportantClass;
import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Bendary
 */
public class CashierReport extends javax.swing.JFrame {

    private final ImportantClass IC = ImportantClass.getInstance();
    private ArrayList<Integer> orderNums = new ArrayList<>();
    private int billIndex = 0;
    private double billTotal = 0;
    private double allbillTotal = 0;
    private double allTotals = 0;
    private String file;
    ExcelReport e=new ExcelReport();
    /**
     * Creates new form OrderReport
     */
    public CashierReport() {
        // Check Conection to DB
        if (!IC.dbc.check) {
            IC.dbc.ConnectDB();
        }

        initComponents();

        WindowListener exitListener = null;
        addWindowListener(prepareWindow(exitListener));

        cashierReportTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));
        cashierReportTable.getTableHeader().setAlignmentY(CENTER_ALIGNMENT);
        DefaultTableCellRenderer c = new DefaultTableCellRenderer();
        c.setHorizontalAlignment(JLabel.CENTER);
        c.setFont(new Font("Tahoma", Font.PLAIN, 13));
        cashierReportTable.setDefaultRenderer(Object.class, c);
        cashierReportTable.setAutoCreateColumnsFromModel(false);

        getAllUsers();
        getAllBillNums();
    }

    private WindowListener prepareWindow(WindowListener exitListener) {
        exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                Home m = new Home();
                CashierReport.this.dispose();
                m.setVisible(true);
            }
        };
        return exitListener;
    }

    private void getAllUsers() {
        try {
            // Get all users
            IC.pst = IC.dbc.conn.prepareStatement("select userName"
                    + " from sql2283641.user"
                    + " where userType = 'Cashier' or userType = 'admin'");
            IC.rs = IC.pst.executeQuery();
            ArrayList<String> users = new ArrayList<>();
            while (IC.rs.next()) {
                users.add(IC.rs.getString("userName"));
            }
            employeeNameCB.setModel(new DefaultComboBoxModel(users.toArray()));
            System.out.println("aaa");
            getAllTotals();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void getAllBillNums() {
        if (employeeNameCB.getSelectedItem() != null) {
            try {
                // Rest variables
                ((DefaultTableModel) cashierReportTable.getModel()).setRowCount(0);

                // Get all bill total
                getAllBillTotalForOneUser();

                // Get all bill numbers
                IC.pst = IC.dbc.conn.prepareStatement("select DISTINCT orderNum"
                        + " from sql2283641.order"
                        + " where userName = ?");
                IC.pst.setString(1, employeeNameCB.getSelectedItem().toString());
                IC.rs = IC.pst.executeQuery();
                orderNums.clear();
                while (IC.rs.next()) {
                    orderNums.add(IC.rs.getInt("orderNum"));
                }

                // Get First Bill
                if (orderNums.size() > 0) {
                    billIndex = 0;
                    selectionBillHandel();
                    getBill(orderNums.get(billIndex));
                } else {
                    billIndex = -1;
                    selectionBillHandel();
                    billTotal = 0;
                    tableTotalLabel.setText("اجمالي الفاتورة : " + billTotal);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void getAllBillTotalForOneUser() {
        try {
            allbillTotal = 0;
            IC.pst = IC.dbc.conn.prepareStatement("select sum(orderTotal)"
                    + " from sql2283641.order"
                    + " where userName = ?");
            IC.pst.setString(1, employeeNameCB.getSelectedItem().toString());
            IC.rs = IC.pst.executeQuery();
            if (IC.rs.next()) {
                allbillTotal = IC.rs.getDouble("sum(orderTotal)");
                AllEmployeeTotalLabel.setText("اجمالي كل الفواتير : " + allbillTotal);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void getAllTotals() {
        try {
            allTotals = 0;
            IC.pst = IC.dbc.conn.prepareStatement("select sum(orderTotal)"
                    + " from sql2283641.order");
            IC.rs = IC.pst.executeQuery();
            if (IC.rs.next()) {
                allTotals = IC.rs.getDouble("sum(orderTotal)");
                allTotalsLabel.setText("الاجمالي لكل الموظفين : " + allTotals);
            }
            System.out.println("aaaaaaa");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void getTotalOftheCurrnetTable() {
        try {
            billTotal = 0;
            IC.pst = IC.dbc.conn.prepareStatement("select sum(orderTotal)"
                    + " from sql2283641.order"
                    + " where orderNum = ?");
            IC.pst.setInt(1, orderNums.get(billIndex));
            IC.rs = IC.pst.executeQuery();
            if (IC.rs.next()) {
                billTotal = IC.rs.getDouble("sum(orderTotal)");
                tableTotalLabel.setText("اجمالي الفاتورة : " + billTotal);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void getBill(int billNum) {
        try {
            // Get Bill by bill number
            IC.pst = IC.dbc.conn.prepareStatement("select orderNotes, orderDate,"
                    + " orderTotal, orderPrice,"
                    + " orderCount, orderProduct"
                    + " from sql2283641.order"
                    + " where orderNum = ?");
            IC.pst.setInt(1, billNum);
            IC.rs = IC.pst.executeQuery();
            cashierReportTable.setModel(DbUtils.resultSetToTableModel(IC.rs));

            getTotalOftheCurrnetTable();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void selectionBillHandel() {
        orderNumLabel.setVisible(true);
        if (billIndex > 0 && billIndex < orderNums.size() - 1) {
            nextBtn.setEnabled(true);
            lastBtn.setEnabled(true);
            prevBtn.setEnabled(true);
            firstBtn.setEnabled(true);
        } else if (billIndex == 0) {
            nextBtn.setEnabled(true);
            lastBtn.setEnabled(true);
            prevBtn.setEnabled(false);
            firstBtn.setEnabled(false);
        } else if (billIndex == orderNums.size() - 1 && billIndex != -1) {
            nextBtn.setEnabled(false);
            lastBtn.setEnabled(false);
            prevBtn.setEnabled(true);
            firstBtn.setEnabled(true);
        } else if (billIndex < 0) {
            orderNumLabel.setVisible(false);
            nextBtn.setEnabled(false);
            lastBtn.setEnabled(false);
            prevBtn.setEnabled(false);
            firstBtn.setEnabled(false);
        }
        if (orderNumLabel.isVisible()) {
            orderNumLabel.setText(orderNums.get(billIndex) + "");
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
        jLabel1 = new javax.swing.JLabel();
        employeeNameCB = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        firstBtn = new javax.swing.JButton();
        prevBtn = new javax.swing.JButton();
        nextBtn = new javax.swing.JButton();
        lastBtn = new javax.swing.JButton();
        orderNumLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        cashierReportTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        tableTotalLabel = new javax.swing.JLabel();
        AllEmployeeTotalLabel = new javax.swing.JLabel();
        allTotalsLabel = new javax.swing.JLabel();
        refreshBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("تقارير الطلبات");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setPreferredSize(new java.awt.Dimension(940, 88));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("اسم الموظف");
        jLabel1.setPreferredSize(new java.awt.Dimension(101, 43));

        employeeNameCB.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        employeeNameCB.setForeground(new java.awt.Color(255, 0, 0));
        employeeNameCB.setPreferredSize(new java.awt.Dimension(65, 43));
        employeeNameCB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                employeeNameCBItemStateChanged(evt);
            }
        });

        jPanel6.setBackground(new java.awt.Color(187, 187, 187));
        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        firstBtn.setBackground(new java.awt.Color(255, 0, 0));
        firstBtn.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        firstBtn.setForeground(new java.awt.Color(255, 255, 255));
        firstBtn.setText("<<");
        firstBtn.setToolTipText("First");
        firstBtn.setEnabled(false);
        firstBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstBtnActionPerformed(evt);
            }
        });

        prevBtn.setBackground(new java.awt.Color(255, 0, 0));
        prevBtn.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        prevBtn.setForeground(new java.awt.Color(255, 255, 255));
        prevBtn.setText("<");
        prevBtn.setToolTipText("Previous");
        prevBtn.setEnabled(false);
        prevBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevBtnActionPerformed(evt);
            }
        });

        nextBtn.setBackground(new java.awt.Color(255, 0, 0));
        nextBtn.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        nextBtn.setForeground(new java.awt.Color(255, 255, 255));
        nextBtn.setText(">");
        nextBtn.setToolTipText("Next");
        nextBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextBtnActionPerformed(evt);
            }
        });

        lastBtn.setBackground(new java.awt.Color(255, 0, 0));
        lastBtn.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        lastBtn.setForeground(new java.awt.Color(255, 255, 255));
        lastBtn.setText(">>");
        lastBtn.setToolTipText("Last");
        lastBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastBtnActionPerformed(evt);
            }
        });

        orderNumLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        orderNumLabel.setForeground(new java.awt.Color(255, 0, 0));
        orderNumLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        orderNumLabel.setText("0");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(firstBtn)
                .addGap(18, 18, 18)
                .addComponent(prevBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 111, Short.MAX_VALUE)
                .addComponent(nextBtn)
                .addGap(18, 18, 18)
                .addComponent(lastBtn)
                .addGap(18, 18, 18))
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                    .addContainerGap(137, Short.MAX_VALUE)
                    .addComponent(orderNumLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(140, Short.MAX_VALUE)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lastBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .addComponent(nextBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(prevBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(firstBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(orderNumLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jButton1.setBackground(new java.awt.Color(255, 0, 51));
        jButton1.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("حفظ ملف اكسيل");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                .addComponent(employeeNameCB, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(employeeNameCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(940, 388));

        cashierReportTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ملاحظات", "التاريخ", "المجموع", "السعر", "العدد", "اسم الصنف"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        cashierReportTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        cashierReportTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(cashierReportTable);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 928, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 359, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setOpaque(false);

        jPanel7.setBackground(new java.awt.Color(187, 187, 187));
        jPanel7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        tableTotalLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        tableTotalLabel.setForeground(new java.awt.Color(255, 0, 0));
        tableTotalLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        tableTotalLabel.setText("اجمالي الفاتورة : 0.0");
        tableTotalLabel.setPreferredSize(new java.awt.Dimension(198, 43));

        AllEmployeeTotalLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        AllEmployeeTotalLabel.setForeground(new java.awt.Color(255, 0, 0));
        AllEmployeeTotalLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        AllEmployeeTotalLabel.setText("اجمالي كل الفواتير : 0.0");
        AllEmployeeTotalLabel.setPreferredSize(new java.awt.Dimension(198, 43));

        allTotalsLabel.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        allTotalsLabel.setForeground(new java.awt.Color(255, 0, 0));
        allTotalsLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        allTotalsLabel.setText("الاجمالي لكل الموظفين : 0.0");
        allTotalsLabel.setPreferredSize(new java.awt.Dimension(258, 43));

        refreshBtn.setBackground(new java.awt.Color(255, 0, 0));
        refreshBtn.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        refreshBtn.setForeground(new java.awt.Color(255, 255, 255));
        refreshBtn.setText("Refresh");
        refreshBtn.setPreferredSize(new java.awt.Dimension(158, 45));
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(444, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(allTotalsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(tableTotalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(AllEmployeeTotalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableTotalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(allTotalsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(0, 2, Short.MAX_VALUE)
                        .addComponent(AllEmployeeTotalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(refreshBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 154, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 956, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 956, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 944, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 618, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void employeeNameCBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_employeeNameCBItemStateChanged
        getAllBillNums();
    }//GEN-LAST:event_employeeNameCBItemStateChanged

    private void nextBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextBtnActionPerformed
        if (billIndex < orderNums.size() - 1) {
            billIndex++;
            selectionBillHandel();
            getBill(orderNums.get(billIndex));
        }
    }//GEN-LAST:event_nextBtnActionPerformed

    private void lastBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastBtnActionPerformed
        billIndex = orderNums.size() - 1;
        selectionBillHandel();
        getBill(orderNums.get(billIndex));
    }//GEN-LAST:event_lastBtnActionPerformed

    private void prevBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevBtnActionPerformed
        if (billIndex > 0) {
            billIndex--;
            selectionBillHandel();
            getBill(orderNums.get(billIndex));
        }
    }//GEN-LAST:event_prevBtnActionPerformed

    private void firstBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstBtnActionPerformed
        billIndex = 0;
        selectionBillHandel();
        getBill(orderNums.get(billIndex));
    }//GEN-LAST:event_firstBtnActionPerformed

    private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtnActionPerformed
        getAllUsers();
        getAllBillNums();
    }//GEN-LAST:event_refreshBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            JFileChooser jfc = new JFileChooser();
            jfc.showOpenDialog(null);
            File f = jfc.getSelectedFile();
            String path = f.getAbsolutePath();
            file = path;
            e.writeData(file,IC.userName);
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(CashierReport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CashierReport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CashierReport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CashierReport.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CashierReport().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AllEmployeeTotalLabel;
    private javax.swing.JLabel allTotalsLabel;
    private javax.swing.JTable cashierReportTable;
    private javax.swing.JComboBox<String> employeeNameCB;
    private javax.swing.JButton firstBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton lastBtn;
    private javax.swing.JButton nextBtn;
    private javax.swing.JLabel orderNumLabel;
    private javax.swing.JButton prevBtn;
    private javax.swing.JButton refreshBtn;
    private javax.swing.JLabel tableTotalLabel;
    // End of variables declaration//GEN-END:variables
}
