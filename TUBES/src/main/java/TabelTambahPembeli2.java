
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HAKIM AL BAIHAQY
 */
public class TabelTambahPembeli2 extends javax.swing.JFrame {
        private DbConnection db = new DbConnection();
        private Connection con = db.getConnection();

        /**
         * Creates new form TabelTambahPembeli2
         */
        public TabelTambahPembeli2() {
                initComponents();
                addLoadMenuData();
                btnTambah.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                TablePembeliModel(evt);
                        }
                });
                cbPilihMakanan();
                try {
                        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/tubes_new", "root", "");
                } catch (SQLException ex) {
                        Logger.getLogger(TabelTambahPembeli2.class.getName()).log(Level.SEVERE, null, ex);
                }
                btnSelesai.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                TableStrukModel(evt);
                        }
                });

        }

        private void TableStrukModel(java.awt.event.ActionEvent evt) {
                DefaultTableModel dtl = (DefaultTableModel) tblStruk.getModel();
                dtl.setRowCount(0);

                if (evt != null) {
                        insertStrukData();
                }

                String alterTableQuery2 = "ALTER TABLE detail_transaksi ADD COLUMN IF NOT EXISTS total_detailTransaksi DECIMAL(10, 2) DEFAULT 0.00";
                try (PreparedStatement ps = con.prepareStatement(alterTableQuery2)) {
                        ps.executeUpdate();
                } catch (SQLException ex) {
                        Logger.getLogger(TabelTambahPembeli2.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Error ALTER TABLE: " + ex.getMessage());
                }

                String updateQuery2 = "UPDATE detail_transaksi dt" +
                                "JOIN (SELECT t.ID_Transaksi, SUM(ps.total_Pesanan) AS total_pesanan" +
                                "FROM transaksi t" +
                                "JOIN pesanan ps ON t.ID_Pesanan = ps.ID_Pesanan" +
                                "GROUP BY t.ID_Transaksi)" +
                                "AS temp ON dt.ID_Transaksi = temp.ID_Transaksi" +
                                "SET dt.total_detailTransaksi = temp.total_pesanan";
                try (PreparedStatement ps = con.prepareStatement(updateQuery2)) {
                        ps.executeUpdate();
                } catch (SQLException ex) {
                        Logger.getLogger(TabelTambahPembeli2.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Error ALTER TABLE: " + ex.getMessage());
                }

                String Query2 = "SELECT p.nama_Pembeli, t.tanggal_Transaksi, dt.total_detailTransaksi" +
                                "FROM pembeli p " +
                                "JOIN pesanan ps ON p.ID_Pembeli = ps.ID_Pembeli " +
                                "JOIN transaksi t ON ps.ID_Pesanan = t.ID_Pesanan " +
                                "JOIN detail_transaksi dt ON t.ID_Transaksi = dt.ID_Transaksi"; // Perbaikan query SQL

                try (PreparedStatement psSelect = con.prepareStatement(Query2);
                                ResultSet rs = psSelect.executeQuery()) {
                        while (rs.next()) {
                                Object[] row = new Object[3];
                                row[0] = rs.getString("nama_Produk"); // Mengambil nama_Produk sesuai dengan query
                                // Memformat tanggal dari ResultSet
                                java.sql.Date sqlDate = rs.getDate("tanggal_Transaksi");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String tanggal = sdf.format(sqlDate);
                                row[1] = tanggal;
                                row[2] = rs.getDouble("total_detailTransaksi"); // Mengambil total_detailTransaksi
                                                                                // sesuai dengan query
                                dtl.addRow(row);
                        }

                        StrukPembeli strukPembeli = new StrukPembeli();
                        strukPembeli.setVisible(true);

                } catch (SQLException e) {
                        e.printStackTrace();
                }

        }

        private void insertStrukData() {
                Connection connection = null;
                PreparedStatement selectPembeliStmt = null;
                PreparedStatement insertTransaksiStmt = null;
                PreparedStatement insertDetailTransaksiStmt = null;
                ResultSet pembeliRs = null;

                try {
                        // Establish a connection
                        connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/tubes new", "root", "");

                        // Select from pembeli table
                        String selectPembeliSql = "SELECT ID_Pembeli FROM pembeli WHERE nama_Pembeli = ?";
                        selectPembeliStmt = connection.prepareStatement(selectPembeliSql);
                        selectPembeliStmt.setString(1, "nama_Pembeli"); // Replace with actual name or parameter
                        pembeliRs = selectPembeliStmt.executeQuery();

                        int idPembeli = -1;
                        if (pembeliRs.next()) {
                                idPembeli = pembeliRs.getInt("ID_Pembeli");
                        }

                        // Insert into transaksi table
                        String insertTransaksiSql = "INSERT INTO transaksi (ID_Pesanan, tanggal_Transaksi) VALUES (?, ?)";
                        insertTransaksiStmt = connection.prepareStatement(insertTransaksiSql,
                                        Statement.RETURN_GENERATED_KEYS);
                        insertTransaksiStmt.setInt(1, idPembeli); // Assuming ID_Pesanan is ID_Pembeli; adjust as needed
                        insertTransaksiStmt.setDate(2, new java.sql.Date(new java.util.Date().getTime())); // Current
                                                                                                           // date
                        insertTransaksiStmt.executeUpdate();

                        // Get the generated key for ID_Transaksi
                        ResultSet transaksiKeys = insertTransaksiStmt.getGeneratedKeys();
                        int idTransaksi = -1;
                        if (transaksiKeys.next()) {
                                idTransaksi = transaksiKeys.getInt(1);
                        }

                        // Insert into detail_transaksi table
                        String insertDetailTransaksiSql = "INSERT INTO detail_transaksi (ID_Transaksi, total_detailTransaksi) "
                                        +
                                        "SELECT ?, SUM(jumlah_Pesanan) FROM pesanan";
                        insertDetailTransaksiStmt = connection.prepareStatement(insertDetailTransaksiSql);
                        insertDetailTransaksiStmt.setInt(1, idTransaksi);
                        insertDetailTransaksiStmt.executeUpdate();

                } catch (SQLException e) {
                        e.printStackTrace();
                } finally {
                        // Close resources
                        try {
                                if (pembeliRs != null)
                                        pembeliRs.close();
                                if (selectPembeliStmt != null)
                                        selectPembeliStmt.close();
                                if (insertTransaksiStmt != null)
                                        insertTransaksiStmt.close();
                                if (insertDetailTransaksiStmt != null)
                                        insertDetailTransaksiStmt.close();
                                if (connection != null)
                                        connection.close();
                        } catch (SQLException e) {
                                e.printStackTrace();
                        }
                }
        }

        private void cbPilihMakanan() {
                // Populate combo box with product names
                String selectProdukQuery = "SELECT nama_Produk FROM menu";
                try (PreparedStatement pt = con.prepareStatement(selectProdukQuery); ResultSet rs = pt.executeQuery()) {
                        cbPilihMakanan.removeAllItems();
                        while (rs.next()) {
                                String nama_Produk = rs.getString("nama_Produk");
                                cbPilihMakanan.addItem(nama_Produk);
                        }
                } catch (SQLException ex) {
                        System.out.println("Error SELECT Produk: " + ex.getMessage());
                        ex.printStackTrace(); // Better logging should be implemented
                }
        }

        private void TablePembeliModel(java.awt.event.ActionEvent evt) {
                DefaultTableModel mdl = (DefaultTableModel) tblPembeli.getModel();
                mdl.setRowCount(0); // Clear existing table rows

                // Panggil fungsi untuk menyimpan data pembeli jika ada aksi yang memicu
                // penyimpanan
                if (evt != null) {
                        insertPembeliData();
                }

                // Tambahkan kolom total_Pesanan jika belum ada
                String alterTableQuery1 = "ALTER TABLE pesanan ADD COLUMN IF NOT EXISTS total_Pesanan DECIMAL(10, 2)";
                try (PreparedStatement ps = con.prepareStatement(alterTableQuery1)) {
                        ps.executeUpdate();
                } catch (SQLException ex) {
                        Logger.getLogger(TabelTambahPembeli2.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Error ALTER TABLE: " + ex.getMessage());
                }

                // Update total_Pesanan based on jumlah_Pesanan * harga_Produk
                String updateQuery1 = "UPDATE pesanan PS " +
                                "JOIN menu M ON PS.ID_Menu = M.ID_Menu " +
                                "SET PS.total_Pesanan = PS.jumlah_Pesanan * M.harga_Produk";
                try (PreparedStatement psUpdate = con.prepareStatement(updateQuery1)) {
                        psUpdate.executeUpdate();
                } catch (SQLException ex) {
                        Logger.getLogger(TabelTambahPembeli2.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Error UPDATE: " + ex.getMessage());
                        ex.printStackTrace(); // Better logging should be implemented
                }

                // Fetch data for display
                String query1 = "SELECT p.nama_Pembeli, m.nama_Produk, ps.jumlah_Pesanan, ps.request_Pesanan, ps.total_Pesanan "
                                +
                                "FROM pembeli p " +
                                "JOIN pesanan ps ON p.ID_Pembeli = ps.ID_Pembeli " +
                                "JOIN menu m ON ps.ID_Menu = m.ID_Menu";
                try (PreparedStatement psSelect = con.prepareStatement(query1);
                                ResultSet rs = psSelect.executeQuery()) {
                        while (rs.next()) {
                                String namaPembeli = rs.getString("nama_Pembeli");
                                String namaProduk = rs.getString("nama_Produk");
                                int jumlahPesanan = rs.getInt("jumlah_Pesanan");
                                String requestPesanan = rs.getString("request_Pesanan");
                                double totalPesanan = rs.getDouble("total_Pesanan");

                                // Cek apakah data sudah ada di tabel
                                boolean isDuplicate = false;
                                for (int i = 0; i < mdl.getRowCount(); i++) {
                                        if (mdl.getValueAt(i, 0).toString().equals(namaPembeli) &&
                                                        mdl.getValueAt(i, 1).toString().equals(namaProduk) &&
                                                        (int) mdl.getValueAt(i, 2) == jumlahPesanan &&
                                                        mdl.getValueAt(i, 3).toString().equals(requestPesanan) &&
                                                        (double) mdl.getValueAt(i, 4) == totalPesanan) {
                                                isDuplicate = true;
                                                break;
                                        }
                                }

                                // Jika tidak duplikat, tambahkan ke tabel
                                if (!isDuplicate) {
                                        Object[] row = { namaPembeli, namaProduk, jumlahPesanan, requestPesanan,
                                                        totalPesanan };
                                        mdl.addRow(row);
                                }
                        }

                } catch (SQLException ex) {
                        Logger.getLogger(TabelTambahPembeli2.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Error SELECT: " + ex.getMessage());
                        ex.printStackTrace(); // Better logging should be implemented
                }
        }

        private void insertPembeliData() {
                String namaPembeli = tfNamaPembeli.getText().trim();
                String namaProduk = (String) cbPilihMakanan.getSelectedItem();
                int jumlahPesanan = Integer.parseInt(tfJumlahPesanan.getText().trim());
                String requestPesanan = taRequestPembeli.getText().trim();

                // Ambil harga produk dari database berdasarkan nama produk yang dipilih
                String selectHargaProdukQuery = "SELECT harga_Produk FROM menu WHERE nama_Produk = ?";
                double hargaProduk = 0.0;
                try (PreparedStatement ps = con.prepareStatement(selectHargaProdukQuery)) {
                        ps.setString(1, namaProduk);
                        try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                        hargaProduk = rs.getDouble("harga_Produk");
                                } else {
                                        Logger.getLogger(TabelTambahPembeli2.class.getName()).log(Level.SEVERE,
                                                        "Produk tidak ditemukan: " + namaProduk);
                                        System.out.println("Produk tidak ditemukan: " + namaProduk);
                                        return; // Keluar dari fungsi jika produk tidak ditemukan
                                }
                        }
                } catch (SQLException ex) {
                        Logger.getLogger(TabelTambahPembeli2.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Error SELECT harga_Produk: " + ex.getMessage());
                        ex.printStackTrace(); // Better logging should be implemented
                        return; // Keluar dari fungsi jika terjadi kesalahan
                }

                // Hitung total_Pesanan
                double totalPesanan = jumlahPesanan * hargaProduk;

                // Insert data ke dalam tabel pembeli dan pesanan
                String insertPembeliQuery = "INSERT INTO pembeli (nama_Pembeli) VALUES (?)";
                String insertPesananQuery = "INSERT INTO pesanan (ID_Pembeli, ID_Menu, jumlah_Pesanan, request_Pesanan, total_Pesanan) "
                                +
                                "VALUES (LAST_INSERT_ID(), (SELECT ID_Menu FROM menu WHERE nama_Produk = ?), ?, ?, ?)";
                try {
                        // Insert data pembeli
                        PreparedStatement pPembeli = con.prepareStatement(insertPembeliQuery);
                        pPembeli.setString(1, namaPembeli);
                        pPembeli.executeUpdate();
                        pPembeli.close();

                        // Insert data pesanan
                        PreparedStatement psPesanan = con.prepareStatement(insertPesananQuery);
                        psPesanan.setString(1, namaProduk);
                        psPesanan.setInt(2, jumlahPesanan);
                        psPesanan.setString(3, requestPesanan);
                        psPesanan.setDouble(4, totalPesanan);
                        psPesanan.executeUpdate();
                        psPesanan.close();

                        // Refresh tabel pembeli setelah insert
                        TablePembeliModel(null); // Panggil kembali fungsi TablePembeliModel untuk memperbarui tampilan
                } catch (SQLException ex) {
                        Logger.getLogger(TabelTambahPembeli2.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Error INSERT: " + ex.getMessage());
                        ex.printStackTrace(); // Better logging should be implemented
                }
        }

        private void btnKembaliToLobbyActionPerformed(java.awt.event.ActionEvent evt) {
                LobbyWarteg lobbyWarteg = new LobbyWarteg();
                lobbyWarteg.setVisible(true);
                this.dispose();
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

        /**
         * This method is called from within the constructor to initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is always
         * regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated
        // Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                tblStruk = new javax.swing.JTable();
                jPanel1 = new javax.swing.JPanel();
                jScrollPane1 = new javax.swing.JScrollPane();
                tblPembeli = new javax.swing.JTable();
                jbTabelPembeli = new javax.swing.JLabel();
                jScrollPane2 = new javax.swing.JScrollPane();
                tblMenu = new javax.swing.JTable();
                jbNamaPembeli = new javax.swing.JLabel();
                jbJumlahPesanan = new javax.swing.JLabel();
                jbRequestPembeli = new javax.swing.JLabel();
                tfJumlahPesanan = new javax.swing.JTextField();
                tfNamaPembeli = new javax.swing.JTextField();
                jScrollPane3 = new javax.swing.JScrollPane();
                taRequestPembeli = new javax.swing.JTextArea();
                jbPilihMakanan = new javax.swing.JLabel();
                cbPilihMakanan = new javax.swing.JComboBox<>();
                btnSelesai = new javax.swing.JButton();
                btnTambah = new javax.swing.JButton();
                btnKembaliToLobby = new javax.swing.JButton();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

                jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

                tblPembeli.setModel(new javax.swing.table.DefaultTableModel(
                                new Object[][] {
                                                { null, null, null, null, null },
                                                { null, null, null, null, null },
                                                { null, null, null, null, null },
                                                { null, null, null, null, null }
                                },
                                new String[] {
                                                "Nama Pembeli", "Produk", "Jumlah", "Request", "Total Pesanan"
                                }) {
                        Class[] types = new Class[] {
                                        java.lang.String.class, java.lang.String.class, java.lang.Double.class,
                                        java.lang.String.class,
                                        java.lang.Double.class
                        };

                        public Class getColumnClass(int columnIndex) {
                                return types[columnIndex];
                        }
                });
                jScrollPane1.setViewportView(tblPembeli);

                jbTabelPembeli.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
                jbTabelPembeli.setText("TABEL PEMBELI");

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
                jScrollPane2.setViewportView(tblMenu);

                jbNamaPembeli.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
                jbNamaPembeli.setText("Nama Pembeli      :");

                jbJumlahPesanan.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
                jbJumlahPesanan.setText("Jumlah               :");

                jbRequestPembeli.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
                jbRequestPembeli.setText("Request              :");

                taRequestPembeli.setColumns(20);
                taRequestPembeli.setRows(5);
                jScrollPane3.setViewportView(taRequestPembeli);

                jbPilihMakanan.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
                jbPilihMakanan.setText("Pilih Makanan       :");

                btnSelesai.setText("SELESAI");

                btnTambah.setText("TAMBAH");

                btnKembaliToLobby.setText("KEMBALI");
                btnKembaliToLobby.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                btnKembaliToLobbyActionPerformed(evt);
                        }
                });

                javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout
                                                                .createSequentialGroup()
                                                                .addContainerGap(454, Short.MAX_VALUE)
                                                                .addComponent(jScrollPane1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(20, 20, 20))
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(jPanel1Layout
                                                                                                .createSequentialGroup()
                                                                                                .addGap(125, 125, 125)
                                                                                                .addComponent(jbTabelPembeli))
                                                                                .addGroup(jPanel1Layout
                                                                                                .createSequentialGroup()
                                                                                                .addGap(28, 28, 28)
                                                                                                .addGroup(jPanel1Layout
                                                                                                                .createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                                false)
                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addComponent(jbJumlahPesanan)
                                                                                                                                .addPreferredGap(
                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                .addComponent(tfJumlahPesanan))
                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addComponent(jbNamaPembeli)
                                                                                                                                .addPreferredGap(
                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                .addComponent(tfNamaPembeli,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                231,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                                                jPanel1Layout.createSequentialGroup()
                                                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                                                .createParallelGroup(
                                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                                                .addComponent(jbRequestPembeli)
                                                                                                                                                                .addComponent(jbPilihMakanan)
                                                                                                                                                                .addComponent(btnSelesai,
                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                78,
                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                                                .createParallelGroup(
                                                                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                                                                .createSequentialGroup()
                                                                                                                                                                                .addPreferredGap(
                                                                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                                                                                .createParallelGroup(
                                                                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                                                                                .addComponent(
                                                                                                                                                                                                                jScrollPane3,
                                                                                                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                232,
                                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                                .addComponent(
                                                                                                                                                                                                                cbPilihMakanan,
                                                                                                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                232,
                                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                                                                .createSequentialGroup()
                                                                                                                                                                                .addGap(18, 18, 18)
                                                                                                                                                                                .addComponent(btnTambah,
                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                78,
                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                .addPreferredGap(
                                                                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                                                                .addComponent(
                                                                                                                                                                                                btnKembaliToLobby)))))))
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE))
                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                jPanel1Layout.createSequentialGroup()
                                                                                                .addContainerGap(453,
                                                                                                                Short.MAX_VALUE)
                                                                                                .addComponent(jScrollPane2,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                .addGap(21, 21, 21))));
                jPanel1Layout.setVerticalGroup(
                                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGap(24, 24, 24)
                                                                .addComponent(jbTabelPembeli)
                                                                .addGap(56, 56, 56)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(jbNamaPembeli)
                                                                                .addComponent(tfNamaPembeli,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(45, 45, 45)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(jbJumlahPesanan)
                                                                                .addComponent(tfJumlahPesanan,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(47, 47, 47)
                                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(jPanel1Layout
                                                                                                .createSequentialGroup()
                                                                                                .addComponent(jbRequestPembeli)
                                                                                                .addGroup(jPanel1Layout
                                                                                                                .createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addPreferredGap(
                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                                                53,
                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                .addComponent(jScrollPane1,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                283,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addGap(97, 97, 97)
                                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                                .createParallelGroup(
                                                                                                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                                                                .addComponent(jbPilihMakanan)
                                                                                                                                                .addComponent(cbPilihMakanan,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                                .addPreferredGap(
                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                .addGroup(jPanel1Layout
                                                                                                                                                .createParallelGroup(
                                                                                                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                                                                .addComponent(btnTambah)
                                                                                                                                                .addComponent(btnSelesai)
                                                                                                                                                .addComponent(btnKembaliToLobby))))
                                                                                                .addGap(25, 25, 25))
                                                                                .addGroup(jPanel1Layout
                                                                                                .createSequentialGroup()
                                                                                                .addComponent(jScrollPane3,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                .addContainerGap(
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE))))
                                                .addGroup(jPanel1Layout.createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                                                .addGap(34, 34, 34)
                                                                                .addComponent(jScrollPane2,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                247,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addContainerGap(327,
                                                                                                Short.MAX_VALUE))));

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout
                                                                .createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(jPanel1,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addContainerGap()));
                layout.setVerticalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(jPanel1,
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
                        java.util.logging.Logger.getLogger(TabelTambahPembeli2.class.getName()).log(
                                        java.util.logging.Level.SEVERE,
                                        null, ex);
                } catch (InstantiationException ex) {
                        java.util.logging.Logger.getLogger(TabelTambahPembeli2.class.getName()).log(
                                        java.util.logging.Level.SEVERE,
                                        null, ex);
                } catch (IllegalAccessException ex) {
                        java.util.logging.Logger.getLogger(TabelTambahPembeli2.class.getName()).log(
                                        java.util.logging.Level.SEVERE,
                                        null, ex);
                } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                        java.util.logging.Logger.getLogger(TabelTambahPembeli2.class.getName()).log(
                                        java.util.logging.Level.SEVERE,
                                        null, ex);
                }
                // </editor-fold>

                /* Create and display the form */
                java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {
                                new TabelTambahPembeli2().setVisible(true);
                        }
                });
        }

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton btnTambah;
        private javax.swing.JButton btnKembaliToLobby;
        private javax.swing.JButton btnSelesai;
        private javax.swing.JComboBox<String> cbPilihMakanan;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JScrollPane jScrollPane1;
        private javax.swing.JScrollPane jScrollPane2;
        private javax.swing.JScrollPane jScrollPane3;
        private javax.swing.JLabel jbJumlahPesanan;
        private javax.swing.JLabel jbNamaPembeli;
        private javax.swing.JLabel jbPilihMakanan;
        private javax.swing.JLabel jbRequestPembeli;
        private javax.swing.JLabel jbTabelPembeli;
        private javax.swing.JTextArea taRequestPembeli;
        private javax.swing.JTable tblMenu;
        private javax.swing.JTable tblPembeli;
        private javax.swing.JTextField tfJumlahPesanan;
        private javax.swing.JTextField tfNamaPembeli;
        private javax.swing.JTable tblStruk;
        // End of variables declaration//GEN-END:variables
}
