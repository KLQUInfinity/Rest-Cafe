/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UIForms;

import Classes.ImportantClass;
import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author sayed
 */
public class OrderPreview extends javax.swing.JFrame {

    private final ImportantClass IC = ImportantClass.getInstance();

    /**
     * Creates new form OrderPreview
     */
    private ArrayList<JTable> tables = new ArrayList<>();
    private ArrayList<Integer> orderNum = new ArrayList<>();
    private ArrayList<Boolean> tabCreated = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> id = new ArrayList<>();

    public OrderPreview() {
        // Check Conection to DB
        if (!IC.dbc.check) {
            IC.dbc.ConnectDB();
        }

        initComponents();

        WindowListener exitListener = null;
        addWindowListener(prepareWindow(exitListener));

        getAllUnfinishedOrder();
    }

    private WindowListener prepareWindow(WindowListener exitListener) {
        exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                Home m = new Home();
                OrderPreview.this.dispose();
                m.setVisible(true);
            }
        };
        return exitListener;
    }

    private void getAllUnfinishedOrder() {
        try {
            IC.pst = IC.dbc.conn.prepareStatement("SELECT DISTINCT orderNum "
                    + "FROM sql2283641.order "
                    + "where orderDone=0");
            IC.rs = IC.pst.executeQuery();

            while (IC.rs.next()) {
                int x = IC.rs.getInt("orderNum");
                if (!orderNum.contains(x)) {
                    orderNum.add(x);
                    tabCreated.add(false);
                }
            }

            for (int i = 0; i < orderNum.size(); i++) {
                if (!tabCreated.get(i)) {
                    createNewTab(orderNum.get(i));

                    // Get all id of the order
                    IC.pst = IC.dbc.conn.prepareStatement("SELECT id FROM sql2283641.order"
                            + " where orderNum=?");
                    IC.pst.setInt(1, orderNum.get(i));
                    IC.rs = IC.pst.executeQuery();
                    ArrayList<Integer> orderId = new ArrayList<>();
                    while (IC.rs.next()) {
                        orderId.add(IC.rs.getInt("id"));
                    }
                    id.add(orderId);

                    // Get all data of the order
                    IC.pst = IC.dbc.conn.prepareStatement("SELECT orderNotes,orderCount,orderProduct "
                            + " FROM sql2283641.order"
                            + " where orderNum=?");
                    IC.pst.setInt(1, orderNum.get(i));
                    IC.rs = IC.pst.executeQuery();
                    tables.get(i).setModel(DbUtils.resultSetToTableModel(IC.rs));
                    tabCreated.set(i, true);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // This method for create new tab and it's children
    private void createNewTab(Integer tabIndex) {
        JScrollPane scrollPane = new JScrollPane();

        JTable table = new JTable();

        table.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ملاحظات", "العدد", "اسم الصنف"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        table.getTableHeader().setReorderingAllowed(false);

        scrollPane.setViewportView(table);
        Tabs.addTab("طلب رقم " + tabIndex.toString(), scrollPane);

        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));
        table.getTableHeader().setAlignmentY(CENTER_ALIGNMENT);
        DefaultTableCellRenderer c = new DefaultTableCellRenderer();
        c.setHorizontalAlignment(JLabel.CENTER);
        c.setFont(new Font("Tahoma", Font.PLAIN, 13));
        table.setDefaultRenderer(Object.class, c);
        table.setAutoCreateColumnsFromModel(false);

        tables.add(table);
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
        noOrderLabel = new javax.swing.JLabel();
        Tabs = new javax.swing.JTabbedPane();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        orderNumLabel = new javax.swing.JLabel();
        orderDoneBtn = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        showNewOrderBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("عرض الطلبات");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        noOrderLabel.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        noOrderLabel.setForeground(new java.awt.Color(255, 0, 0));
        noOrderLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        noOrderLabel.setText("لا يوجد طلبات اضغط علي (اظهر طلبات جديده) لاظهار اجدد الطلبات");

        Tabs.setBackground(new java.awt.Color(255, 255, 255));
        Tabs.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Tabs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TabsStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabs, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 894, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(noOrderLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 882, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(noOrderLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jPanel4.setBackground(new java.awt.Color(187, 187, 187));
        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        orderNumLabel.setBackground(new java.awt.Color(255, 255, 255));
        orderNumLabel.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        orderNumLabel.setForeground(new java.awt.Color(255, 0, 0));
        orderNumLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        orderNumLabel.setText("رقم الطلب : 0");

        orderDoneBtn.setBackground(new java.awt.Color(255, 0, 0));
        orderDoneBtn.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        orderDoneBtn.setForeground(new java.awt.Color(255, 255, 255));
        orderDoneBtn.setText("تم تجهيز الطلب");
        orderDoneBtn.setPreferredSize(new java.awt.Dimension(150, 45));
        orderDoneBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderDoneBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 397, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(orderDoneBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(orderNumLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 102, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(21, 21, 21)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(orderNumLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(orderDoneBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(24, Short.MAX_VALUE)))
        );

        jPanel5.setBackground(new java.awt.Color(187, 187, 187));
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        showNewOrderBtn.setBackground(new java.awt.Color(255, 0, 0));
        showNewOrderBtn.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        showNewOrderBtn.setForeground(new java.awt.Color(255, 255, 255));
        showNewOrderBtn.setText("اظهر طلبات جديدة");
        showNewOrderBtn.setPreferredSize(new java.awt.Dimension(150, 45));
        showNewOrderBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showNewOrderBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 462, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                    .addContainerGap(156, Short.MAX_VALUE)
                    .addComponent(showNewOrderBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(156, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 104, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                    .addContainerGap(20, Short.MAX_VALUE)
                    .addComponent(showNewOrderBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(29, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 890, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(483, Short.MAX_VALUE)))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                    .addContainerGap(418, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 890, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 126, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addGap(0, 430, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(429, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(121, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void TabsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TabsStateChanged
        if (Tabs.getTabCount() > 0 && Tabs.getSelectedIndex() != -1) {
            noOrderLabel.setVisible(false);
            orderNumLabel.setVisible(true);
            orderNumLabel.setText("رقم الطلب : " + orderNum.get(Tabs.getSelectedIndex()));
        } else if (Tabs.getTabCount() == 0) {
            noOrderLabel.setVisible(true);
            orderNumLabel.setVisible(false);
        }
    }//GEN-LAST:event_TabsStateChanged

    private void showNewOrderBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showNewOrderBtnActionPerformed
        getAllUnfinishedOrder();
    }//GEN-LAST:event_showNewOrderBtnActionPerformed

    private void orderDoneBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderDoneBtnActionPerformed
        if (Tabs.getTabCount() > 0) {
            try {
                for (int i = 0; i < id.get(Tabs.getSelectedIndex()).size(); i++) {
                    IC.pst = IC.dbc.conn.prepareStatement("update sql2283641.order"
                            + " set orderDone=1"
                            + " where id=?");
                    IC.pst.setInt(1, id.get(Tabs.getSelectedIndex()).get(i));
                    IC.pst.execute();
                }
                id.remove(Tabs.getSelectedIndex());
                orderNum.remove(Tabs.getSelectedIndex());
                tables.remove(Tabs.getSelectedIndex());
                tabCreated.remove(Tabs.getSelectedIndex());
                Tabs.remove(Tabs.getSelectedIndex());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "لا يوجد طلبات لتنفذيها");
        }
    }//GEN-LAST:event_orderDoneBtnActionPerformed

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
            java.util.logging.Logger.getLogger(OrderPreview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OrderPreview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OrderPreview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrderPreview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OrderPreview().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane Tabs;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel noOrderLabel;
    private javax.swing.JButton orderDoneBtn;
    private javax.swing.JLabel orderNumLabel;
    private javax.swing.JButton showNewOrderBtn;
    // End of variables declaration//GEN-END:variables
}
