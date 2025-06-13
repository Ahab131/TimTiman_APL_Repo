/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author HAKIM AL BAIHAQY
 */
public class StrukPembeli extends javax.swing.JFrame {
        private DbConnection db = new DbConnection();
        private Connection con = db.getConnection();

        public StrukPembeli() {
                initComponents();
                btnBatal.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                addBatalPembelian();
                        }
                });
                btnCetak.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                addCetakPembelian();
                        }
                });
        }

        private void addBatalPembelian() {
                DefaultTableModel modelStruk = (DefaultTableModel) tblStruk.getModel();
                modelStruk.setRowCount(0);

                try {
                        con.setAutoCommit(false); // Mulai transaksi

                        // Menghapus semua detail transaksi dari tabel detail_transaksi
                        String deleteDetailTransaksiSQL = "DELETE FROM detail_transaksi";
                        try (PreparedStatement psDeleteDetail = con.prepareStatement(deleteDetailTransaksiSQL)) {
                                psDeleteDetail.executeUpdate();
                        }

                        // Menghapus semua transaksi dari tabel transaksi
                        String deleteTransaksiSQL = "DELETE FROM transaksi";
                        try (PreparedStatement psDeleteTransaksi = con.prepareStatement(deleteTransaksiSQL)) {
                                psDeleteTransaksi.executeUpdate();
                        }

                        String deletePesananSQL = "DELETE FROM pesanan";
                        try (PreparedStatement psDeletePesanan = con.prepareStatement(deletePesananSQL)) {
                                psDeletePesanan.executeUpdate();
                        }

                        String deletePembeliSQL = "DELETE FROM pembeli";
                        try (PreparedStatement psDeletePembeli = con.prepareStatement(deletePembeliSQL)) {
                                psDeletePembeli.executeUpdate();
                        }

                        con.commit(); // Commit transaksi jika berhasil
                        JOptionPane.showMessageDialog(null, "Pembelian berhasil dibatalkan.");

                        TabelTambahPembeli2 tabelTambahPembeli2 = new TabelTambahPembeli2();
                        tabelTambahPembeli2.setVisible(true);

                } catch (SQLException e) {
                        try {
                                con.rollback(); // Rollback transaksi jika terjadi kesalahan
                        } catch (SQLException rollbackEx) {
                                rollbackEx.printStackTrace();
                        }
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat membatalkan pembelian.");
                } finally {
                        try {
                                con.setAutoCommit(true); // Kembalikan otomatisasi commit ke nilai awal
                        } catch (SQLException ex) {
                                ex.printStackTrace();
                        }
                }
        }

        private void addCetakPembelian() {
                // Mengambil model tabel
                DefaultTableModel modelStruk = (DefaultTableModel) tblStruk.getModel();

                // Mendapatkan jumlah baris dan kolom dari model tabel
                int rowCount = modelStruk.getRowCount();
                int colCount = modelStruk.getColumnCount();

                // Membuat BufferedImage untuk menyimpan gambar tabel
                BufferedImage tableImage = new BufferedImage(tblStruk.getWidth(), tblStruk.getHeight(),
                                BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = tableImage.createGraphics();

                // Menggambar tabel ke dalam BufferedImage
                tblStruk.print(graphics2D);

                // Menutup graphics context
                graphics2D.dispose();

                // Menyimpan BufferedImage ke dalam file JPG
                String filePath = "C:\\Users\\HAKIM AL BAIHAQY\\Documents\\Data Pembeli\\tblStruk.jpg";
                File imageFile = new File(filePath);
                try {
                        ImageIO.write(tableImage, "jpg", imageFile);
                        System.out.println("File berhasil disimpan di: " + filePath);

                        // Reset tabel (tblStruk)
                        modelStruk.setRowCount(0); // Mengosongkan semua baris dari tabel
                        TabelTambahPembeli2 tabelTambahPembeli2 = new TabelTambahPembeli2();
                        tabelTambahPembeli2.setVisible(true);
                        JOptionPane.showMessageDialog(null, "Terima Kasih Telah Melakukan Pemesanan");

                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        /**
         * This method is called from within the constructor to initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is always
         * regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated
        // Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                pnlStrukPembeli = new javax.swing.JPanel();
                jbStrukPembeli = new javax.swing.JLabel();
                jScrollPane1 = new javax.swing.JScrollPane();
                tblStruk = new javax.swing.JTable();
                btnCetak = new javax.swing.JButton();
                btnBatal = new javax.swing.JButton();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

                pnlStrukPembeli.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

                jbStrukPembeli.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
                jbStrukPembeli.setText("STRUK PEMBELIAN");

                tblStruk.setModel(new javax.swing.table.DefaultTableModel(
                                new Object[][] {
                                                { null, null, null },
                                                { null, null, null },
                                                { null, null, null },
                                                { null, null, null }
                                },
                                new String[] {
                                                "Nama Pembeli", "Tanggal", "Total"
                                }) {
                        Class[] types = new Class[] {
                                        java.lang.String.class, java.lang.String.class, java.lang.Double.class
                        };

                        public Class getColumnClass(int columnIndex) {
                                return types[columnIndex];
                        }
                });
                jScrollPane1.setViewportView(tblStruk);

                btnCetak.setText("CETAK");

                btnBatal.setText("BATAL");

                javax.swing.GroupLayout pnlStrukPembeliLayout = new javax.swing.GroupLayout(pnlStrukPembeli);
                pnlStrukPembeli.setLayout(pnlStrukPembeliLayout);
                pnlStrukPembeliLayout.setHorizontalGroup(
                                pnlStrukPembeliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                pnlStrukPembeliLayout.createSequentialGroup()
                                                                                .addContainerGap(40, Short.MAX_VALUE)
                                                                                .addComponent(jScrollPane1,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                355,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addContainerGap(40, Short.MAX_VALUE))
                                                .addGroup(pnlStrukPembeliLayout.createSequentialGroup()
                                                                .addGap(101, 101, 101)
                                                                .addComponent(jbStrukPembeli)
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE))
                                                .addGroup(pnlStrukPembeliLayout.createSequentialGroup()
                                                                .addGap(113, 113, 113)
                                                                .addComponent(btnCetak)
                                                                .addGap(65, 65, 65)
                                                                .addComponent(btnBatal)
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)));
                pnlStrukPembeliLayout.setVerticalGroup(
                                pnlStrukPembeliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(pnlStrukPembeliLayout.createSequentialGroup()
                                                                .addGap(23, 23, 23)
                                                                .addComponent(jbStrukPembeli)
                                                                .addGap(39, 39, 39)
                                                                .addComponent(jScrollPane1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                296,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                172,
                                                                                Short.MAX_VALUE)
                                                                .addGroup(pnlStrukPembeliLayout
                                                                                .createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(btnCetak)
                                                                                .addComponent(btnBatal))
                                                                .addGap(28, 28, 28)));

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(pnlStrukPembeli,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addContainerGap()));
                layout.setVerticalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(pnlStrukPembeli,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addContainerGap()));

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
                        java.util.logging.Logger.getLogger(StrukPembeli.class.getName()).log(
                                        java.util.logging.Level.SEVERE, null,
                                        ex);
                } catch (InstantiationException ex) {
                        java.util.logging.Logger.getLogger(StrukPembeli.class.getName()).log(
                                        java.util.logging.Level.SEVERE, null,
                                        ex);
                } catch (IllegalAccessException ex) {
                        java.util.logging.Logger.getLogger(StrukPembeli.class.getName()).log(
                                        java.util.logging.Level.SEVERE, null,
                                        ex);
                } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                        java.util.logging.Logger.getLogger(StrukPembeli.class.getName()).log(
                                        java.util.logging.Level.SEVERE, null,
                                        ex);
                }
                // </editor-fold>

                /* Create and display the form */
                java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {
                                new StrukPembeli().setVisible(true);
                        }
                });
        }

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton btnBatal;
        private javax.swing.JButton btnCetak;
        private javax.swing.JScrollPane jScrollPane1;
        private javax.swing.JLabel jbStrukPembeli;
        private javax.swing.JPanel pnlStrukPembeli;
        private javax.swing.JTable tblStruk;
        // End of variables declaration//GEN-END:variables
}
