import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author HAKIM AL BAIHAQY
 */
public class TabelTambahMenu extends javax.swing.JFrame {
        private DbConnection db = new DbConnection();
        private Connection con = db.getConnection();

        /**
         * Creates new form TabelTambahMenu
         */
        public TabelTambahMenu() {
                initComponents();
                addSubmitButtonListener();
                addResetButtonListener();
                addLoadMenuData();
                addBackButtonListener();
                addEditMenuData();
                tblMenu.getSelectionModel().addListSelectionListener(e -> {
                        int selectedRow = tblMenu.getSelectedRow();
                        if (selectedRow >= 0) {
                                tfProduk.setText(tblMenu.getValueAt(selectedRow, 0).toString());
                                tfHarga.setText(tblMenu.getValueAt(selectedRow, 1).toString());
                                cbKeterangan.setSelectedItem(tblMenu.getValueAt(selectedRow, 2).toString());
                        }
                });

        }

        private void addEditMenuData() {
                btnSubmit.addActionListener(e -> {
                        try {
                                String produk = tfProduk.getText();
                                double harga = Double.parseDouble(tfHarga.getText());
                                String keteranganText = cbKeterangan.getSelectedItem().toString();

                                // Validasi input
                                if (produk.isEmpty() || tfHarga.getText().isEmpty() || keteranganText.isEmpty()) {
                                        JOptionPane.showMessageDialog(null, "Harap isi semua field.");
                                        return;
                                }

                                // Periksa apakah produk sudah ada di database
                                if (isProductExists(produk)) {
                                        // Update data jika produk sudah ada
                                        updateDataInMenu(produk, harga, keteranganText);
                                } else {
                                        // Tambahkan data baru jika produk belum ada
                                        insertDataToMenu(produk, harga, keteranganText);
                                }

                                // Perbarui tabel GUI
                                updateTableModel();

                                // Bersihkan field input
                                tfProduk.setText("");
                                tfHarga.setText("");
                                cbKeterangan.setSelectedIndex(0);

                                // Tampilkan pesan konfirmasi
                                JOptionPane.showMessageDialog(null, "Data berhasil disimpan!");
                        } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null, "Harga harus berupa angka.");
                        }
                });
        }

        private void addBackButtonListener() {
                btnKembali2.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                btnKembali2ActionPerformed(e);
                        }
                });
        }

        private void btnKembali2ActionPerformed(java.awt.event.ActionEvent evt) {
                LoginPenjual loginPenjual = new LoginPenjual();
                loginPenjual.setVisible(true);
                this.dispose();
        }

        private void addResetButtonListener() {
                btnReset.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                resetAll();
                        }
                });
        }

        private void addLoadMenuData() {
                DefaultTableModel model = (DefaultTableModel) tblMenu.getModel();
                model.setRowCount(0); // Clear the table

                String query = "SELECT nama_Produk, harga_Produk, keterangan_Produk FROM menu";
                try (PreparedStatement ps = con.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                                Object[] row = new Object[3]; // Adjusted for 3 columns
                                row[0] = rs.getString("nama_Produk");
                                row[1] = rs.getDouble("harga_Produk");
                                row[2] = rs.getBoolean("keterangan_Produk") ? "Tersedia" : "Tidak Tersedia";
                                model.addRow(row);
                        }
                } catch (SQLException ex) {
                        System.out.println("Error: " + ex.getMessage());
                }
        }

        private void resetAll() {
                try {
                        // Reset combo boxes and text fields
                        tfProduk.setText("");
                        tfHarga.setText("");
                        cbKeterangan.setSelectedIndex(0);

                        // Clear the table model
                        DefaultTableModel model = (DefaultTableModel) tblMenu.getModel();
                        model.setRowCount(0); // Remove all rows from the table

                        // Reset menu table
                        String resetMenuQuery = "DELETE FROM menu";
                        con.setAutoCommit(false); // Start transaction

                        try (PreparedStatement resetMenuStatement = con.prepareStatement(resetMenuQuery)) {
                                resetMenuStatement.executeUpdate();
                        }

                        con.commit(); // Commit transaction

                        // Reload the table data
                        addLoadMenuData();

                        // Show a message indicating a successful reset
                        JOptionPane.showMessageDialog(null, "Database reset successfully!");
                } catch (SQLException ex) {
                        try {
                                con.rollback(); // Rollback transaction in case of error
                                JOptionPane.showMessageDialog(null, "Error: Database reset failed.");
                        } catch (SQLException rollbackEx) {
                                System.out.println("Error: " + rollbackEx.getMessage());
                        }
                        System.out.println("Error: " + ex.getMessage());
                } finally {
                        try {
                                con.setAutoCommit(true); // Ensure auto-commit is re-enabled
                        } catch (SQLException e) {
                                System.out.println("Error: " + e.getMessage());
                        }
                }
        }

        private void addSubmitButtonListener() {
                btnSubmit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                try {
                                        String produk = tfProduk.getText();
                                        double harga = Double.parseDouble(tfHarga.getText());
                                        String keteranganText = (String) cbKeterangan.getSelectedItem();

                                        // Validasi input (opsional)
                                        if (produk.isEmpty() || tfHarga.getText().isEmpty()
                                                        || keteranganText.isEmpty()) {
                                                JOptionPane.showMessageDialog(null, "Harap isi semua field.");
                                                return;
                                        }

                                        // Masukkan data ke dalam tabel menu
                                        insertDataToMenu(produk, harga, keteranganText);

                                        // Perbarui tabel dengan data baru
                                        DefaultTableModel model = (DefaultTableModel) tblMenu.getModel();
                                        model.addRow(new Object[] { null, produk, harga, keteranganText });

                                        // Bersihkan field input
                                        tfProduk.setText("");
                                        tfHarga.setText("");
                                        cbKeterangan.setSelectedIndex(0);

                                        // Tampilkan pesan konfirmasi
                                        JOptionPane.showMessageDialog(null, "Data berhasil disimpan!");
                                } catch (NumberFormatException ex) {
                                        
                                }
                        }
                });
        }

        private void insertDataToMenu(String produk, double harga, String keteranganText) {
                try {
                        String query = "INSERT INTO menu (nama_Produk, harga_Produk, keterangan_Produk) VALUES (?, ?, ?)";
                        PreparedStatement ps = con.prepareStatement(query);
                        ps.setString(1, produk);
                        ps.setDouble(2, harga);
                        ps.setBoolean(3, "Tersedia".equals(keteranganText));
                        ps.executeUpdate();
                        System.out.println("Data menu berhasil ditambahkan.");
                } catch (SQLException ex) {
                        System.out.println("Error: " + ex.getMessage());
                }
        }

        private void updateDataInMenu(String produk, double harga, String keteranganText) {
                try {
                        String query = "UPDATE menu SET harga_Produk = ?, keterangan_Produk = ? WHERE nama_Produk = ?";
                        PreparedStatement ps = con.prepareStatement(query);
                        ps.setDouble(1, harga);
                        ps.setBoolean(2, "Tersedia".equals(keteranganText));
                        ps.setString(3, produk);
                        int rowsAffected = ps.executeUpdate();
                        if (rowsAffected > 0) {
                                System.out.println("Data menu berhasil diupdate.");
                        } else {
                                System.out.println("Data menu dengan nama produk '" + produk + "' tidak ditemukan.");
                        }
                } catch (SQLException ex) {
                        System.out.println("Error: " + ex.getMessage());
                }
        }

        private boolean isProductExists(String produk) {
                String query = "SELECT COUNT(*) AS count FROM menu WHERE nama_Produk = ?";
                try (PreparedStatement ps = con.prepareStatement(query)) {
                        ps.setString(1, produk);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                                int count = rs.getInt("count");
                                return count > 0;
                        }
                } catch (SQLException ex) {
                        System.out.println("Error: " + ex.getMessage());
                }
                return false;
        }

        private void updateTableModel() {
                DefaultTableModel model = (DefaultTableModel) tblMenu.getModel();
                model.setRowCount(0); // Hapus semua baris

                String query = "SELECT nama_Produk, harga_Produk, keterangan_Produk FROM menu";
                try (PreparedStatement ps = con.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                                Object[] row = new Object[3]; // Disesuaikan untuk 3 kolom
                                row[0] = rs.getString("nama_Produk");
                                row[1] = rs.getDouble("harga_Produk");
                                row[2] = rs.getBoolean("keterangan_Produk") ? "Tersedia" : "Tidak Tersedia";
                                model.addRow(row);
                        }
                } catch (SQLException ex) {
                        System.out.println("Error: " + ex.getMessage());
                }
        }

        /**
         * This method is called from within the constructor to initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is always
         * regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
        // Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                pnlTabelTambahMenu = new javax.swing.JPanel();
                jLabel1 = new javax.swing.JLabel();
                jScrollPane1 = new javax.swing.JScrollPane();
                tblMenu = new javax.swing.JTable();
                jLabel3 = new javax.swing.JLabel();
                jLabel5 = new javax.swing.JLabel();
                jLabel6 = new javax.swing.JLabel();
                tfProduk = new javax.swing.JTextField();
                tfHarga = new javax.swing.JTextField();
                btnSubmit = new javax.swing.JButton();
                btnReset = new javax.swing.JButton();
                btnKembali2 = new javax.swing.JButton();
                cbKeterangan = new javax.swing.JComboBox<>();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

                pnlTabelTambahMenu
                                .setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

                jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
                jLabel1.setText("TABEL MENU");

                tblMenu.setModel(new javax.swing.table.DefaultTableModel(
                                new Object[][] {
                                                { null, null, null },
                                                { null, null, null },
                                                { null, null, null },
                                                { null, null, null }
                                },
                                new String[] {
                                                "Produk", "Harga", "Keterangan"
                                }) {
                        Class[] types = new Class[] {
                                        java.lang.String.class, java.lang.Double.class, java.lang.String.class
                        };

                        public Class getColumnClass(int columnIndex) {
                                return types[columnIndex];
                        }
                });
                jScrollPane1.setViewportView(tblMenu);

                jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
                jLabel3.setText("Produk      :");

                jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
                jLabel5.setText("Harga        :");

                jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
                jLabel6.setText("Keterangan :");

                btnSubmit.setText("SUBMIT");

                btnReset.setText("RESET");

                btnKembali2.setText("KEMBALI");

                cbKeterangan.setModel(
                                new javax.swing.DefaultComboBoxModel<>(new String[] { "Tersedia", "Tidak Tersedia" }));

                javax.swing.GroupLayout pnlTabelTambahMenuLayout = new javax.swing.GroupLayout(pnlTabelTambahMenu);
                pnlTabelTambahMenu.setLayout(pnlTabelTambahMenuLayout);
                pnlTabelTambahMenuLayout.setHorizontalGroup(
                                pnlTabelTambahMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(pnlTabelTambahMenuLayout.createSequentialGroup()
                                                                .addGroup(pnlTabelTambahMenuLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                .createSequentialGroup()
                                                                                                .addGap(151, 151, 151)
                                                                                                .addComponent(jLabel1))
                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                .createSequentialGroup()
                                                                                                .addGap(40, 40, 40)
                                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                                .createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                                                                .createParallelGroup(
                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                                .addComponent(jLabel3)
                                                                                                                                                .addComponent(jLabel5))
                                                                                                                                .addPreferredGap(
                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                                                                .createParallelGroup(
                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                                                                false)
                                                                                                                                                .addComponent(tfProduk)
                                                                                                                                                .addComponent(tfHarga,
                                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                242,
                                                                                                                                                                Short.MAX_VALUE)))
                                                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                                                                .createParallelGroup(
                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                                                                                .createSequentialGroup()
                                                                                                                                                                .addComponent(jLabel6)
                                                                                                                                                                .addPreferredGap(
                                                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                                                .addComponent(cbKeterangan,
                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                                                                                .createSequentialGroup()
                                                                                                                                                                .addComponent(btnSubmit)
                                                                                                                                                                .addGap(63, 63, 63)
                                                                                                                                                                .addComponent(btnReset)))
                                                                                                                                .addPreferredGap(
                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                                                69,
                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                .addComponent(btnKembali2)))))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                51, Short.MAX_VALUE)
                                                                .addComponent(jScrollPane1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(40, 40, 40)));
                pnlTabelTambahMenuLayout.setVerticalGroup(
                                pnlTabelTambahMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(pnlTabelTambahMenuLayout.createSequentialGroup()
                                                                .addGap(55, 55, 55)
                                                                .addGroup(pnlTabelTambahMenuLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                false)
                                                                                .addComponent(jScrollPane1,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                .createSequentialGroup()
                                                                                                .addComponent(jLabel1)
                                                                                                .addGap(80, 80, 80)
                                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                                .createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                                .addComponent(jLabel3)
                                                                                                                .addComponent(tfProduk,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                .addGap(18, 18, 18)
                                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                                .createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                                .addComponent(jLabel5)
                                                                                                                .addComponent(tfHarga,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                .addGap(18, 18, 18)
                                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                                .createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                                .addComponent(jLabel6)
                                                                                                                .addComponent(cbKeterangan,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                .addPreferredGap(
                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE)
                                                                                                .addGroup(pnlTabelTambahMenuLayout
                                                                                                                .createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                                .addComponent(btnSubmit)
                                                                                                                .addComponent(btnReset)
                                                                                                                .addComponent(btnKembali2))))
                                                                .addContainerGap(85, Short.MAX_VALUE)));

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(pnlTabelTambahMenu,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)));
                layout.setVerticalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(pnlTabelTambahMenu,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)));

                pack();
        }// </editor-fold>//GEN-END:initComponents

        /**
         * @param args the command line arguments
         */
        public static void main(String args[]) {
                /* Set the Nimbus look and feel */
                // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
                // (optional) ">
                /*
                 * If Nimbus (introduced in Java SE 6) is not available, stay with the default
                 * look and feel.
                 * For details see
                 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
                 */
                try {
                        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                                        .getInstalledLookAndFeels()) {
                                if ("Nimbus".equals(info.getName())) {
                                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                                        break;
                                }
                        }
                } catch (ClassNotFoundException ex) {
                        java.util.logging.Logger.getLogger(TabelTambahMenu.class.getName()).log(
                                        java.util.logging.Level.SEVERE,
                                        null, ex);
                } catch (InstantiationException ex) {
                        java.util.logging.Logger.getLogger(TabelTambahMenu.class.getName()).log(
                                        java.util.logging.Level.SEVERE,
                                        null, ex);
                } catch (IllegalAccessException ex) {
                        java.util.logging.Logger.getLogger(TabelTambahMenu.class.getName()).log(
                                        java.util.logging.Level.SEVERE,
                                        null, ex);
                } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                        java.util.logging.Logger.getLogger(TabelTambahMenu.class.getName()).log(
                                        java.util.logging.Level.SEVERE,
                                        null, ex);
                }
                // </editor-fold>

                /* Create and display the form */
                java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {
                                new TabelTambahMenu().setVisible(true);
                        }
                });
        }

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton btnKembali2;
        private javax.swing.JButton btnReset;
        private javax.swing.JButton btnSubmit;
        private javax.swing.JComboBox<String> cbKeterangan;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel5;
        private javax.swing.JLabel jLabel6;
        private javax.swing.JScrollPane jScrollPane1;
        private javax.swing.JPanel pnlTabelTambahMenu;
        private javax.swing.JTable tblMenu;
        private javax.swing.JTextField tfHarga;
        private javax.swing.JTextField tfProduk;
        // End of variables declaration//GEN-END:variables
}
