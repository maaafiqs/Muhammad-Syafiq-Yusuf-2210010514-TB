import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.sql.*;
import java.text.SimpleDateFormat;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */


/**
 *
 * @author ASUS
 */
public class ManajemenDataView extends javax.swing.JFrame {
    

     private Connection conn;
     private DefaultTableModel tableModel;
     private int selectedId = -1;
     
     
    
    
    
  
    /**
     * Creates new form ManajemenDataView
     */
    public ManajemenDataView() {
        initComponents();
        
        //Koneksi ke database
         try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/penjualan_tiket_pesawat", "root", "");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Koneksi ke database gagal: " + e.getMessage());
            System.exit(0);
        }
         
        loadData(); //Memuat data dari database
        
         btnTambah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tambahData();
            }
        });
         
         btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 hapusData();
            }
        });
         

      tblData.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblData.getSelectedRow();
                if (row != -1) {
                    // Pastikan index kolom ID sama dengan definisi di DefaultTableModel
                    selectedId = (int) tblData.getValueAt(row, 0);
                    
                    String namaPenumpang  = (String) tblData.getValueAt(row, 1);
                    String kelas          = (String) tblData.getValueAt(row, 2);
                    String maskapai       = (String) tblData.getValueAt(row, 3);
                    String layananBagasi  = (String) tblData.getValueAt(row, 4);
                    double hargaTiket     = (double) tblData.getValueAt(row, 5);
                    
                    // Tanggal di DB tersimpan sebagai java.sql.Date (cast ke java.util.Date)
                    Date tanggalTransaksi = (Date) tblData.getValueAt(row, 6);
                    Date tanggalPesan     = (Date) tblData.getValueAt(row, 7);
                    Date tanggalBerangkat = (Date) tblData.getValueAt(row, 8);
                    
                    // Tampilkan ke Input Fields
                    txtNamaPenumpang.setText(namaPenumpang);
                    cbbKelas.setSelectedItem(kelas);
                    cbbMaskapai.setSelectedItem(maskapai);
                    cbbLayananBagasi.setSelectedItem(layananBagasi);
                    txtHargaTiket.setText(String.valueOf(hargaTiket));
                    
                    tglTransaksi.setDate(tanggalTransaksi);
                    tglPesan.setDate(tanggalPesan);
                    tglKeberangkatan.setDate(tanggalBerangkat);
                }
            }
        });
    
    btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editData();
            }
        });
    
    btnHapus.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        hapusData();
    }
});
    
    txtCari.addKeyListener(new KeyAdapter() {
    @Override
    public void keyReleased(KeyEvent e) {
        cariData();
    }
});
    
    btnCetak.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        cetakLaporan();
    }
});
    
    btnUlang.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Kosongkan form input
        txtNamaPenumpang.setText("");
        txtHargaTiket.setText("");

        // Set combo boxes ke indeks awal (atau sesuai pilihan default)
        cbbKelas.setSelectedIndex(0);
        cbbMaskapai.setSelectedIndex(0);
        cbbLayananBagasi.setSelectedIndex(0);

        // Reset JDateChooser jadi null (tidak ada tanggal terpilih)
        tglTransaksi.setDate(null);
        tglPesan.setDate(null);
        tglKeberangkatan.setDate(null);

        // Kembalikan selectedId ke -1 (menandakan tidak ada baris yang dipilih)
        selectedId = -1;
    }
});


    


        
        
    }
    
     private void loadData() {
        try {
            tableModel = (DefaultTableModel) tblData.getModel();
            tableModel.setRowCount(0);
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM manajemen_data");

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nama_penumpang"),
                        rs.getString("kelas"),
                        rs.getString("maskapai"),
                        rs.getString("layanan_bagasi"),
                        rs.getDouble("harga_tiket"),
                        rs.getDate("tanggal_transaksi"),
                        rs.getDate("tanggal_pesan"),
                        rs.getDate("tanggal_keberangkatan")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        }
    }
     
     private void cetakLaporan() {
    // 1. Buat file chooser untuk memilih lokasi penyimpanan
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Simpan Laporan TXT"); 
    
    // 2. Tampilkan dialog dan cek aksi user
    int userSelection = fileChooser.showSaveDialog(this);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        // 3. Dapatkan file tujuan
        java.io.File fileToSave = fileChooser.getSelectedFile();
        
        // 4. Lakukan penulisan file
        try (FileWriter writer = new FileWriter(fileToSave, false)) {
            
            // -- Opsi: Tuliskan Judul Laporan --
            writer.write("=== Laporan Penjualan Tiket Pesawat ===\n\n");
            
            // 4a. Tulis nama kolom (header) terlebih dahulu
            int columnCount = tblData.getColumnCount();
            for (int col = 0; col < columnCount; col++) {
                writer.write(tblData.getColumnName(col));
                if (col < columnCount - 1) {
                    writer.write("\t"); // pemisah antar-kolom
                }
            }
            writer.write("\n"); // pindah baris setelah header
            
            // 4b. Tulis isi data baris per baris
            int rowCount = tblData.getRowCount();
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    Object cellValue = tblData.getValueAt(row, col);
                    if (cellValue != null) {
                        writer.write(cellValue.toString());
                    } else {
                        writer.write(""); // jika null, tulis kosong
                    }
                    if (col < columnCount - 1) {
                        writer.write("\t"); // tab antar-kolom
                    }
                }
                writer.write("\n"); // pindah baris setelah tiap row
            }
            
            // 4c. (Opsional) Tambahkan footer atau summary
            writer.write("\n=== Akhir Laporan ===\n");
            
            JOptionPane.showMessageDialog(this, 
                    "Laporan berhasil disimpan di:\n" + fileToSave.getAbsolutePath());
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                    "Gagal mencetak laporan: " + ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
     
     private void cariData() {
    // Ambil teks yang diinput di txtCari
    String keyword = txtCari.getText().trim(); 

    // Bersihkan tabel sebelum menampilkan hasil pencarian
    tableModel = (DefaultTableModel) tblData.getModel();
    tableModel.setRowCount(0);

    // Buat query SELECT dengan parameter nama
    try {
        String sql = "SELECT * FROM manajemen_data "
                   + "WHERE nama_penumpang LIKE ?";
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
       
        pstmt.setString(1, "%" + keyword + "%");

        ResultSet rs = pstmt.executeQuery();
        
        // Tampilkan data ke JTable
        while (rs.next()) {
            tableModel.addRow(new Object[] {
                rs.getInt("id"),
                rs.getString("nama_penumpang"),
                rs.getString("kelas"),
                rs.getString("maskapai"),
                rs.getString("layanan_bagasi"),
                rs.getDouble("harga_tiket"),
                rs.getDate("tanggal_transaksi"),
                rs.getDate("tanggal_pesan"),
                rs.getDate("tanggal_keberangkatan")
            });
        }
        
        rs.close();
        pstmt.close();
        
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, 
            "Gagal mencari data: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
     //Menambah data ke database
     private void tambahData() {
        try {
            String sql = "INSERT INTO manajemen_data (nama_penumpang, kelas, maskapai, layanan_bagasi, harga_tiket, tanggal_transaksi, tanggal_pesan, tanggal_keberangkatan) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtNamaPenumpang.getText());
            pstmt.setString(2, cbbKelas.getSelectedItem().toString());
            pstmt.setString(3, cbbMaskapai.getSelectedItem().toString());
            pstmt.setString(4, cbbLayananBagasi.getSelectedItem().toString());
            pstmt.setDouble(5, Double.parseDouble(txtHargaTiket.getText()));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(6, sdf.format(tglTransaksi.getDate()));
            pstmt.setString(7, sdf.format(tglPesan.getDate()));
            pstmt.setString(8, sdf.format(tglKeberangkatan.getDate()));

            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
            loadData(); // Refresh data di tabel
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data: " + e.getMessage());
        }
    }
     
  //Menghapus data dari database   
private void hapusData() {
    //Cek apakah ada baris yang dipilih
    int selectedRow = tblData.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, 
            "Data Diperbaharui!",
            "Berhasil",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // 2. Ambil 'id' dari kolom pertama (index = 0)
    int id = (int) tblData.getValueAt(selectedRow, 0);

    // 3. Tampilkan konfirmasi
    int confirm = JOptionPane.showConfirmDialog(this,
        "Apakah Anda yakin ingin menghapus data ini?",
        "Konfirmasi Hapus",
        JOptionPane.YES_NO_OPTION
    );
    if (confirm != JOptionPane.YES_OPTION) {
        return; // Batalkan penghapusan
    }

    // 4. Eksekusi query DELETE
    try {
        String sql = "DELETE FROM manajemen_data WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);

        int affectedRows = pstmt.executeUpdate();
        pstmt.close();

        if (affectedRows > 0) {
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
        } else {
            JOptionPane.showMessageDialog(this,
                "Data tidak ditemukan di database!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        // 5. Refresh tabel
        loadData();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Gagal menghapus data: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

// Edit data dari database
private void editData() {
        // Pastikan user telah memilih baris (selectedId != -1)
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Pilih data yang ingin di-edit terlebih dahulu!");
            return;
        }
        
        try {
            // Ambil data terbaru dari input fields
            String namaPenumpang = txtNamaPenumpang.getText();
            String kelas = cbbKelas.getSelectedItem().toString();
            String maskapai = cbbMaskapai.getSelectedItem().toString();
            String layananBagasi = cbbLayananBagasi.getSelectedItem().toString();
            double hargaTiket = Double.parseDouble(txtHargaTiket.getText());
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tglTrans = sdf.format(tglTransaksi.getDate());
            String tglPs   = sdf.format(tglPesan.getDate());
            String tglBrk  = sdf.format(tglKeberangkatan.getDate());
            
            // Buat query UPDATE
            String sql = "UPDATE manajemen_data "
                       + "SET nama_penumpang=?, kelas=?, maskapai=?, layanan_bagasi=?, "
                       + "    harga_tiket=?, tanggal_transaksi=?, tanggal_pesan=?, tanggal_keberangkatan=? "
                       + "WHERE id=?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, namaPenumpang);
            pstmt.setString(2, kelas);
            pstmt.setString(3, maskapai);
            pstmt.setString(4, layananBagasi);
            pstmt.setDouble(5, hargaTiket);
            pstmt.setString(6, tglTrans);
            pstmt.setString(7, tglPs);
            pstmt.setString(8, tglBrk);
            pstmt.setInt(9, selectedId);
            
            int updatedRows = pstmt.executeUpdate();
            pstmt.close();
            
            if (updatedRows > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil di-update!");
                loadData();
                
                // Reset selectedId supaya tidak tertinggal
                selectedId = -1;
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Data gagal di-update atau tidak ditemukan!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                    "Error saat mengupdate data: " + ex.getMessage());
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tblData = new javax.swing.JTable();
        btnKembali = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNamaPenumpang = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtHargaTiket = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cbbMaskapai = new javax.swing.JComboBox<>();
        cbbKelas = new javax.swing.JComboBox<>();
        cbbLayananBagasi = new javax.swing.JComboBox<>();
        tglTransaksi = new com.toedter.calendar.JDateChooser();
        tglPesan = new com.toedter.calendar.JDateChooser();
        tglKeberangkatan = new com.toedter.calendar.JDateChooser();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnUlang = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnCetak = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 153, 255));

        jPanel2.setBackground(new java.awt.Color(0, 204, 204));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("MANAJEMEN DATA");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(415, 415, 415))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(39, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(31, 31, 31))
        );

        tblData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No", "Nama Penumpang", "Kelas", "Maskapai", "Layanan Bagasi", "Harga Tiket", "Tanggal Transaksi", "Tanggal Pesan", "Tanggal Keberangkatan"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblData);
        if (tblData.getColumnModel().getColumnCount() > 0) {
            tblData.getColumnModel().getColumn(0).setResizable(false);
        }

        btnKembali.setText("Kembali ke Menu Utama");
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });

        jLabel2.setText("Cari data berdasarkan nama penumpang");

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Nama Penumpang");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Kelas");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Maskapai");

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Layanan Bagasi");

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Harga Tiket");

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Tanggal Transaksi");

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Tanggal Pesan");

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Tanggal Keberangkatan");

        cbbMaskapai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lion Air", "Garuda Indonesia", "Citilink", "Singapore Airlines", "Air Asia", "Qatar Airways" }));

        cbbKelas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ekonomi", "Bisnis" }));

        cbbLayananBagasi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bronze", "Silver", "Gold" }));

        btnTambah.setBackground(new java.awt.Color(51, 255, 0));
        btnTambah.setText("Tambah");

        btnEdit.setBackground(new java.awt.Color(255, 255, 51));
        btnEdit.setText("Edit");

        btnUlang.setBackground(new java.awt.Color(153, 153, 153));
        btnUlang.setText("Ulang");

        btnHapus.setBackground(new java.awt.Color(255, 0, 0));
        btnHapus.setText("Hapus");

        btnCetak.setText("Cetak Laporan");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(45, 45, 45)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cbbMaskapai, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(cbbKelas, javax.swing.GroupLayout.Alignment.LEADING, 0, 207, Short.MAX_VALUE)
                                                .addComponent(txtNamaPenumpang, javax.swing.GroupLayout.Alignment.LEADING)))
                                        .addGap(66, 66, 66)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(45, 45, 45))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(cbbLayananBagasi, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tglKeberangkatan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tglPesan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                                    .addComponent(txtHargaTiket, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                                    .addComponent(tglTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                                .addComponent(btnTambah)
                                .addGap(18, 18, 18)
                                .addComponent(btnEdit)
                                .addGap(18, 18, 18)
                                .addComponent(btnUlang)
                                .addGap(18, 18, 18)
                                .addComponent(btnHapus)
                                .addGap(61, 61, 61)
                                .addComponent(btnCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 154, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtNamaPenumpang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(txtHargaTiket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(jLabel8)
                                .addComponent(cbbKelas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tglTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(jLabel9)
                                .addComponent(cbbMaskapai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tglPesan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel10)
                            .addComponent(cbbLayananBagasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(tglKeberangkatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah)
                    .addComponent(btnEdit)
                    .addComponent(btnUlang)
                    .addComponent(btnHapus)
                    .addComponent(btnCetak)
                    .addComponent(btnKembali))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
    }// </editor-fold>//GEN-END:initComponents

    //Kembali ke menu utama
    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
       PenjualanTiketView mainForm = new PenjualanTiketView();
        
        // Make the main form visible
        mainForm.setVisible(true);
        
        // Close the current form
        this.dispose();
    }//GEN-LAST:event_btnKembaliActionPerformed


    
    
    
    
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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ManajemenDataView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManajemenDataView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManajemenDataView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManajemenDataView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManajemenDataView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnUlang;
    private javax.swing.JComboBox<String> cbbKelas;
    private javax.swing.JComboBox<String> cbbLayananBagasi;
    private javax.swing.JComboBox<String> cbbMaskapai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblData;
    private com.toedter.calendar.JDateChooser tglKeberangkatan;
    private com.toedter.calendar.JDateChooser tglPesan;
    private com.toedter.calendar.JDateChooser tglTransaksi;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtHargaTiket;
    private javax.swing.JTextField txtNamaPenumpang;
    // End of variables declaration//GEN-END:variables



}
