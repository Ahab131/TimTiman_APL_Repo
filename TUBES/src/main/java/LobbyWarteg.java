import java.sql.Connection;

public class LobbyWarteg extends javax.swing.JFrame {
    private DbConnection db = new DbConnection();
    private Connection con = db.getConnection();
    private boolean isHeadless = false;


    public LobbyWarteg(boolean headlessMode) {
        isHeadless = headlessMode;
        if (!isHeadless) {
            initComponents();
        }
    }

    public boolean testPenjualAction() {
        try {
            if (!isHeadless) {
                btnPenjualActionPerformed(
                    new java.awt.event.ActionEvent(this, 0, "test"));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean testPembeliAction() {
        try {
            if (!isHeadless) {
                btnPembeliActionPerformed(
                    new java.awt.event.ActionEvent(this, 0, "test"));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public LobbyWarteg() {
        this(false);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        pnlLobbyWarteg = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnPenjual = new javax.swing.JButton();
        btnPembeli = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlLobbyWarteg.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("LOBBY WARTEG");

        btnPenjual.setText("PENJUAL");
        btnPenjual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPenjualActionPerformed(evt);
            }
        });

        btnPembeli.setText("PEMBELI");
        btnPembeli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPembeliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlLobbyWartegLayout = new javax.swing.GroupLayout(pnlLobbyWarteg);
        pnlLobbyWarteg.setLayout(pnlLobbyWartegLayout);
        pnlLobbyWartegLayout.setHorizontalGroup(
                pnlLobbyWartegLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlLobbyWartegLayout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addComponent(btnPenjual)
                                .addGap(18, 18, 18)
                                .addComponent(btnPembeli)
                                .addContainerGap(80, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                pnlLobbyWartegLayout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jLabel1)
                                        .addGap(94, 94, 94)));
        pnlLobbyWartegLayout.setVerticalGroup(
                pnlLobbyWartegLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlLobbyWartegLayout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(jLabel1)
                                .addGap(40, 40, 40)
                                .addGroup(pnlLobbyWartegLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnPenjual)
                                        .addComponent(btnPembeli))
                                .addContainerGap(50, Short.MAX_VALUE)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(pnlLobbyWarteg, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(pnlLobbyWarteg, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        pack();
    }

    // Method to handle btnPembeli click
    private void btnPembeliActionPerformed(java.awt.event.ActionEvent evt) {
        TabelTambahPembeli2 tabeltambahPembeli = new TabelTambahPembeli2();
        tabeltambahPembeli.setVisible(true);
        this.dispose();
    }

    // Method to handle btnPenjual click
    private void btnPenjualActionPerformed(java.awt.event.ActionEvent evt) {
        LoginPenjual loginPenjual = new LoginPenjual();
        loginPenjual.setVisible(true);
        this.dispose(); // Optionally close the LobbyWarteg window
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(LobbyWarteg.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LobbyWarteg().setVisible(true);
            }
        });
    }

    // Variables declaration
    private javax.swing.JButton btnPembeli;
    private javax.swing.JButton btnPenjual;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel pnlLobbyWarteg;
}
