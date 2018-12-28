/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UIForms;

import Classes.ImportantClass;
import UIPanels.ProductNameSearch;
import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Bendary
 */
public class Casher extends javax.swing.JFrame {

    private final ImportantClass IC = ImportantClass.getInstance();

    private ArrayList<Double> productPrice = new ArrayList<>();
    //This variable modify in table.
    private DefaultTableModel dtm;
    private double totalPrice = 0;
    private int billNum = 0;

    /**
     * Creates new form Casher
     */
    public Casher() {
        // Check Conection to DB
        if (!IC.dbc.check) {
            IC.dbc.ConnectDB();
        }

        initComponents();

//        WindowListener exitListener = null;
//        addWindowListener(prepareWindow(exitListener));
        casherTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));
        casherTable.getTableHeader().setAlignmentY(CENTER_ALIGNMENT);
        casherTable.setAutoCreateColumnsFromModel(false);
        dtm = (DefaultTableModel) casherTable.getModel();
        getAllProductData();
        getLastBillNum(0);
        //This statement to make the form in fullsize.
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

//    private WindowListener prepareWindow(WindowListener exitListener) {
//        exitListener = new WindowAdapter() {
//
//            @Override
//            public void windowClosing(WindowEvent e) {
//                Menu m = new Menu();
//                Casher.this.dispose();
//                m.setVisible(true);
//            }
//        };
//        return exitListener;
//    }
    private void getAllProductData() {
        clearTextFields();
        try {
            // Get all Product
            IC.pst = IC.dbc.conn.prepareStatement("select CONCAT(productName,' ',productType,' ',productSubType) as productName"
                    + ", productPrice "
                    + "from rest_cafe.product");
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

    private void getLastBillNum(int userId) {
        try {
            // Get all Product
            IC.pst = IC.dbc.conn.prepareStatement("select max(orderNum) from rest_cafe.order");
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
        jPanel8 = new javax.swing.JPanel();
        orderNumLabel = new javax.swing.JLabel();
        addBtn = new javax.swing.JButton();
        clearSelectionBtn = new javax.swing.JButton();
        searchNameBtn = new javax.swing.JButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(890, 548));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        productLabel.setText("اسم الصنف");

        countLabel.setText("العدد");

        countTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                countTxtActionPerformed(evt);
            }
        });

        jLabel1.setText("ملاحظات");

        notesTA.setColumns(20);
        notesTA.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        notesTA.setRows(5);
        jScrollPane2.setViewportView(notesTA);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(productNameCB, 0, 175, Short.MAX_VALUE)
                    .addComponent(countTxt))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(countLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(productLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(productNameCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(productLabel))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(countLabel)
                            .addComponent(countTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1)))
                .addContainerGap(24, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        orderNumLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        orderNumLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        orderNumLabel.setText("رقم الطلب : 0");

        addBtn.setText("اضافة صنف");
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        clearSelectionBtn.setText("الغاء الاختيار");
        clearSelectionBtn.setEnabled(false);
        clearSelectionBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearSelectionBtnActionPerformed(evt);
            }
        });

        searchNameBtn.setText("اختيار اسم صنف بالبحث");
        searchNameBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchNameBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(clearSelectionBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(143, 143, 143)
                .addComponent(searchNameBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(259, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                    .addContainerGap(680, Short.MAX_VALUE)
                    .addComponent(orderNumLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clearSelectionBtn)
                    .addComponent(searchNameBtn)
                    .addComponent(addBtn))
                .addContainerGap(10, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel8Layout.createSequentialGroup()
                    .addGap(3, 3, 3)
                    .addComponent(orderNumLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addGap(3, 3, 3)))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap(122, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 890, 180));

        casherTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "اسم الصنف", "العدد", "السعر", "المجموع", "التاريخ", "ملاحظات"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        casherTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        casherTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                casherTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(casherTable);
        if (casherTable.getColumnModel().getColumnCount() > 0) {
            casherTable.getColumnModel().getColumn(0).setResizable(false);
            casherTable.getColumnModel().getColumn(1).setResizable(false);
            casherTable.getColumnModel().getColumn(2).setResizable(false);
            casherTable.getColumnModel().getColumn(3).setResizable(false);
            casherTable.getColumnModel().getColumn(4).setResizable(false);
            casherTable.getColumnModel().getColumn(5).setResizable(false);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 890, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 188, 890, 230));

        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        updateBtn.setText("تعديل صنف");
        updateBtn.setEnabled(false);
        updateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });

        deleteBtn.setText("حذف صنف");
        deleteBtn.setEnabled(false);
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
                .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        totalLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        totalLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalLabel.setText("الاجمالي : 0.0");

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(142, Short.MAX_VALUE)
                .addComponent(totalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(submitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(324, Short.MAX_VALUE)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(totalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(28, 28, 28)
                    .addComponent(submitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(25, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 430, 890, 120));

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        if (!countTxt.getText().equals("")) {
            try {
                double price = Integer.parseInt(countTxt.getText()) * productPrice.get(productNameCB.getSelectedIndex());

                Object[] rowData = {productNameCB.getSelectedItem().toString(),
                    Integer.parseInt(countTxt.getText()),
                    productPrice.get(productNameCB.getSelectedIndex()),
                    price,
                    IC.getDate(),
                    notesTA.getText()};
                dtm.addRow(rowData);
                totalPrice += price;
                totalLabel.setText("الاجمالي : " + totalPrice);
                getAllProductData();
                clearTextFields();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "من فضلك ادخل قيمة عدديه");
            }
        } else {
            JOptionPane.showMessageDialog(null, "من فضلك املا خانة العدد");
        }
    }//GEN-LAST:event_addBtnActionPerformed

    private void casherTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_casherTableMouseClicked
        if (casherTable.getSelectedRow() != -1) {
            btnEnabled(true);
            productNameCB.setSelectedItem(casherTable.getValueAt(casherTable.getSelectedRow(), 0));
            countTxt.setText(casherTable.getValueAt(casherTable.getSelectedRow(), 1).toString());
            notesTA.setText(casherTable.getValueAt(casherTable.getSelectedRow(), 5).toString());
        }
    }//GEN-LAST:event_casherTableMouseClicked

    private void clearSelectionBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearSelectionBtnActionPerformed
        casherTable.clearSelection();
        clearTextFields();
        btnEnabled(false);
    }//GEN-LAST:event_clearSelectionBtnActionPerformed

    private void updateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
        totalPrice -= Double.parseDouble(casherTable.getValueAt(casherTable.getSelectedRow(), 3).toString());
        double price = Integer.parseInt(countTxt.getText()) * productPrice.get(productNameCB.getSelectedIndex());

        casherTable.setValueAt(productNameCB.getSelectedItem().toString(), casherTable.getSelectedRow(), 0);
        casherTable.setValueAt(countTxt.getText(), casherTable.getSelectedRow(), 1);
        casherTable.setValueAt(productPrice.get(productNameCB.getSelectedIndex()), casherTable.getSelectedRow(), 2);
        casherTable.setValueAt(price, casherTable.getSelectedRow(), 3);
        casherTable.setValueAt(IC.getDate(), casherTable.getSelectedRow(), 4);
        casherTable.setValueAt(notesTA.getText(), casherTable.getSelectedRow(), 5);

        totalPrice += price;
        totalLabel.setText("الاجمالي : " + totalPrice);
        clearSelectionBtn.doClick();
        getAllProductData();
    }//GEN-LAST:event_updateBtnActionPerformed

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        totalPrice -= Double.parseDouble(casherTable.getValueAt(casherTable.getSelectedRow(), 3).toString());
        totalLabel.setText("الاجمالي : " + totalPrice);

        dtm.removeRow(casherTable.getSelectedRow());
        getAllProductData();
    }//GEN-LAST:event_deleteBtnActionPerformed

    private void submitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitBtnActionPerformed
        if (casherTable.getRowCount() > 0) {
            int y = JOptionPane.showConfirmDialog(null,
                    "هل تريد تاكيد الفاتوره؟\nمن فضلك تاكد من العميل بان هذا كل شئ",
                    "رسالة تاكيد",
                    JOptionPane.YES_NO_OPTION);
            if (y == 0) {
                try {
                    for (int i = 0; i < casherTable.getRowCount(); i++) {
                        IC.pst = IC.dbc.conn.prepareStatement("insert into rest_cafe.order("
                                + "orderNum, orderProduct,"
                                + "orderCount, orderPrice,"
                                + "orderTotal, orderDate,"
                                + "orderNotes)"
                                + "values(?,?,?,?,?,?,?)");

                        IC.pst.setInt(1, billNum);
                        IC.pst.setString(2, casherTable.getValueAt(i, 0).toString());
                        IC.pst.setInt(3, Integer.parseInt(casherTable.getValueAt(i, 1).toString()));
                        IC.pst.setDouble(4, Double.parseDouble(casherTable.getValueAt(i, 2).toString()));
                        IC.pst.setDouble(5, Double.parseDouble(casherTable.getValueAt(i, 3).toString()));
                        IC.pst.setString(6, casherTable.getValueAt(i, 4).toString());
                        IC.pst.setString(7, casherTable.getValueAt(i, 5).toString());

                        IC.pst.execute();
                    }
                    dtm.setRowCount(0);
                    getLastBillNum(0);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "لايوجد اي بيانات مضافة في الجدول");
        }
    }//GEN-LAST:event_submitBtnActionPerformed

    private void countTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_countTxtActionPerformed
        addBtn.doClick();
    }//GEN-LAST:event_countTxtActionPerformed

    private void searchNameBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchNameBtnActionPerformed
        ProductNameSearch s = new ProductNameSearch();
        JOptionPane.showOptionDialog(null, s,
                "اختيار اسم بالبحث",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.DEFAULT_OPTION,
                null, new Object[]{}, null);
    }//GEN-LAST:event_searchNameBtnActionPerformed

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Casher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Casher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Casher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Casher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Casher().setVisible(true);
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
    private javax.swing.JLabel productLabel;
    private javax.swing.JComboBox<String> productNameCB;
    private javax.swing.JButton searchNameBtn;
    private javax.swing.JButton submitBtn;
    private javax.swing.JLabel totalLabel;
    private javax.swing.JButton updateBtn;
    // End of variables declaration//GEN-END:variables
}
