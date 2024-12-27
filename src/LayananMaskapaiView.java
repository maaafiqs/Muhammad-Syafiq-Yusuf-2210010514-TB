
import com.toedter.calendar.JDateChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author ASUS
 */
public class LayananMaskapaiView extends javax.swing.JFrame {
    
    private Connection conn; // Koneksi ke database
    private DefaultTableModel tableModel;
    
 
    /**
     * Creates new form LayananMaskapaiView
     */
    public LayananMaskapaiView() {
        initComponents();
        connectToDatabase();
        loadDataToTable();
        addTambahButtonListener();
        addCalculatePriceListener();
        addTableClickListener();
        addEditButtonListener();
        addDeleteButtonListener();
        addPrintButtonListener();
        addSearchListener();
        
        
    }
    
    private void connectToDatabase() {
        try {
            // Koneksi ke database MySQL
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/penjualan_tiket_pesawat", "root", "");
           
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Koneksi ke database gagal: " + e.getMessage());
        }
    }
    
    private void loadDataToTable() {
        tableModel = (DefaultTableModel) tblBagasi.getModel();
        tableModel.setRowCount(0);

        String query = "SELECT * FROM bagasi";
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("nama_penumpang"),
                    rs.getString("kelas"),
                    rs.getString("maskapai"),
                    rs.getDate("tgl_transaksi"),
                    rs.getDouble("berat_bawaan"),
                    rs.getString("layanan_bagasi"),
                    rs.getDouble("harga_bagasi"),
                    rs.getString("kode_promo")
                };
                tableModel.addRow(row);
            }

            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat memuat data: " + e.getMessage());
        }
    }
    
    private void addTambahButtonListener() {
        btnTambah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String namaPenumpang = txtPenumpang.getText().trim();
                String kelas = (String) cbbKelas.getSelectedItem();
                String maskapai = (String) cbbMaskapai.getSelectedItem();
                Date tanggalTransaksiDate = tglTransaksi.getDate();
                String beratBawaanStr = txtBeratBawaan.getText().trim();
                String layananBagasi = (String) cbbLayananBagasi.getSelectedItem();
                String hargaBagasiStr = txtHargaBagasi.getText().trim();
                String kodePromo = txtKodePromo.getText().trim();

                // Validasi input
                if (namaPenumpang.isEmpty() || tanggalTransaksiDate == null || beratBawaanStr.isEmpty() || hargaBagasiStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Semua data wajib diisi.", "Input Kosong", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double beratBawaan;
                double hargaBagasi;
                try {
                    beratBawaan = Double.parseDouble(beratBawaanStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Berat bawaan harus berupa angka.", "Input Salah", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    hargaBagasi = Double.parseDouble(hargaBagasiStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Harga bagasi harus berupa angka.", "Input Salah", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Format tanggal ke java.sql.Date
                java.sql.Date tanggalTransaksi = new java.sql.Date(tanggalTransaksiDate.getTime());

                // Menambahkan data ke database
                addDataToDatabase(namaPenumpang, kelas, maskapai, tanggalTransaksi, beratBawaan, layananBagasi, hargaBagasi, kodePromo);

                // Tambahkan data ke JTable
                Object[] row = {
                    null, // ID akan diisi otomatis oleh database
                    namaPenumpang,
                    kelas,
                    maskapai,
                    tanggalTransaksi,
                    beratBawaan,
                    layananBagasi,
                    hargaBagasi,
                    kodePromo
                };
                tableModel.addRow(row);

                JOptionPane.showMessageDialog(null, "Data Bagasi berhasil ditambahkan.");

                
            }
        });
    }
    
    //Menambahkan inputan data kedalam database
    private void addDataToDatabase(String namaPenumpang, String kelas, String maskapai, java.sql.Date tanggalTransaksi,
                                   double beratBawaan, String layananBagasi, double hargaBagasi, String kodePromo) {
        String query = "INSERT INTO bagasi (nama_penumpang, kelas, maskapai, tgl_transaksi, berat_bawaan, layanan_bagasi, harga_bagasi, kode_promo) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, namaPenumpang);
            pst.setString(2, kelas);
            pst.setString(3, maskapai);
            pst.setDate(4, tanggalTransaksi);
            pst.setDouble(5, beratBawaan);
            pst.setString(6, layananBagasi);
            pst.setDouble(7, hargaBagasi);
            pst.setString(8, kodePromo.isEmpty() ? null : kodePromo); // Set NULL jika kodePromo kosong

            pst.executeUpdate();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saat menambahkan data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //Perhitungan harga
    private void addCalculatePriceListener() {
    cbbLayananBagasi.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isResetting) {
            return; 
        }

        String layanan = (String) cbbLayananBagasi.getSelectedItem();
        String beratBawaanStr = txtBeratBawaan.getText().trim();
        String kodePromo = txtKodePromo.getText().trim();
        double hargaPerKg = 0;
        double beratBawaan = 0;
        double diskon = 0;

        try {
            beratBawaan = Double.parseDouble(beratBawaanStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Berat bawaan harus berupa angka.", "Input Salah", JOptionPane.ERROR_MESSAGE);
            txtHargaBagasi.setText("");
            return;
        }

        switch (layanan) {
            case "Bronze":
                hargaPerKg = 10000;
                break;
            case "Silver":
                hargaPerKg = 15000;
                break;
            case "Gold":
                hargaPerKg = 20000;
                break;
            default:
                JOptionPane.showMessageDialog(null, "Pilih layanan yang valid.", "Layanan Tidak Valid", JOptionPane.WARNING_MESSAGE);
                return;
        }

        double totalHarga = beratBawaan * hargaPerKg;

        if (!kodePromo.isEmpty()) {
            switch (kodePromo.toUpperCase()) {
                case "PROMO10":
                    diskon = totalHarga * 0.10;
                    break;
                case "PROMO20":
                    diskon = totalHarga * 0.20;
                    break;
                case "PROMO30":
                    diskon = totalHarga * 0.30;
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Kode promo tidak valid.", "Kode Promo Salah", JOptionPane.WARNING_MESSAGE);
                    diskon = 0;
                    break;
            }
        }

        totalHarga -= diskon;
        txtHargaBagasi.setText(String.valueOf(totalHarga));
    }
});
}
    
//Memungkinkan data yang dipilih agar bisa tampil di inputan  
private void addTableClickListener() {
    tblBagasi.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            int selectedRow = tblBagasi.getSelectedRow();
            if (selectedRow != -1) {
                // Ambil data dari baris yang dipilih dan tampilkan di input field
                txtPenumpang.setText(tblBagasi.getValueAt(selectedRow, 1).toString());
                cbbKelas.setSelectedItem(tblBagasi.getValueAt(selectedRow, 2).toString());
                cbbMaskapai.setSelectedItem(tblBagasi.getValueAt(selectedRow, 3).toString());
                tglTransaksi.setDate((java.util.Date) tblBagasi.getValueAt(selectedRow, 4));
                txtBeratBawaan.setText(tblBagasi.getValueAt(selectedRow, 5).toString());
                cbbLayananBagasi.setSelectedItem(tblBagasi.getValueAt(selectedRow, 6).toString());
                txtHargaBagasi.setText(tblBagasi.getValueAt(selectedRow, 7).toString());
                txtKodePromo.setText(tblBagasi.getValueAt(selectedRow, 8) != null ? tblBagasi.getValueAt(selectedRow, 8).toString() : "");
            }
        }
    });
}

//Edit data dari database
private void addEditButtonListener() {
    btnEdit.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = tblBagasi.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Pilih baris yang akan diedit.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Ambil data dari input field
            String namaPenumpang = txtPenumpang.getText().trim();
            String kelas = (String) cbbKelas.getSelectedItem();
            String maskapai = (String) cbbMaskapai.getSelectedItem();
            java.util.Date tanggalTransaksiDate = tglTransaksi.getDate();
            String beratBawaanStr = txtBeratBawaan.getText().trim();
            String layananBagasi = (String) cbbLayananBagasi.getSelectedItem();
            String hargaBagasiStr = txtHargaBagasi.getText().trim();
            String kodePromo = txtKodePromo.getText().trim();

            // Validasi input
            if (namaPenumpang.isEmpty() || tanggalTransaksiDate == null || beratBawaanStr.isEmpty() || hargaBagasiStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua data wajib diisi.", "Input Kosong", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double beratBawaan;
            double hargaBagasi;
            try {
                beratBawaan = Double.parseDouble(beratBawaanStr);
                hargaBagasi = Double.parseDouble(hargaBagasiStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Berat bawaan dan harga bagasi harus berupa angka.", "Input Salah", JOptionPane.ERROR_MESSAGE);
                return;
            }

            java.sql.Date tanggalTransaksi = new java.sql.Date(tanggalTransaksiDate.getTime());

            // Perbarui data di tabel
            tblBagasi.setValueAt(namaPenumpang, selectedRow, 1);
            tblBagasi.setValueAt(kelas, selectedRow, 2);
            tblBagasi.setValueAt(maskapai, selectedRow, 3);
            tblBagasi.setValueAt(tanggalTransaksi, selectedRow, 4);
            tblBagasi.setValueAt(beratBawaan, selectedRow, 5);
            tblBagasi.setValueAt(layananBagasi, selectedRow, 6);
            tblBagasi.setValueAt(hargaBagasi, selectedRow, 7);
            tblBagasi.setValueAt(kodePromo, selectedRow, 8);

            JOptionPane.showMessageDialog(null, "Data berhasil diperbarui.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    });
}

private boolean isResetting = false;


//Mereset semua inputan    
private void resetForm() {
    isResetting = true; // Abaikan validasi selama reset
    txtPenumpang.setText("");
    cbbKelas.setSelectedIndex(0);
    cbbMaskapai.setSelectedIndex(0);
    try {
        tglTransaksi.setDate(null); // Kosongkan tanggal
    } catch (IllegalArgumentException ex) {
        tglTransaksi.setDate(new java.util.Date()); // Setel ke tanggal sekarang jika null tidak didukung
    }
    txtBeratBawaan.setText("");
    cbbLayananBagasi.setSelectedIndex(0);
    txtHargaBagasi.setText("");
    txtKodePromo.setText("");
    isResetting = false; // Kembalikan validasi setelah reset selesai
}

//Hapus data dari database
private void addDeleteButtonListener() {
    btnHapus.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = tblBagasi.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Pilih baris yang akan dihapus.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Ambil ID dari baris yang dipilih
            int id = (int) tblBagasi.getValueAt(selectedRow, 0);

            // Hapus data dari database
            try {
                String query = "DELETE FROM bagasi WHERE id = ?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setInt(1, id);
                pst.executeUpdate();
                pst.close();

                // Hapus baris dari tabel
                ((DefaultTableModel) tblBagasi.getModel()).removeRow(selectedRow);

                JOptionPane.showMessageDialog(null, "Data berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error saat menghapus data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });
}

//Mencetak Laporan
private void addPrintButtonListener() {
    btnCetak.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = tblBagasi.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Pilih baris yang akan dicetak.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Ambil data dari baris yang dipilih
                int id = (int) tblBagasi.getValueAt(selectedRow, 0);
                String namaPenumpang = tblBagasi.getValueAt(selectedRow, 1).toString();
                String kelas = tblBagasi.getValueAt(selectedRow, 2).toString();
                String maskapai = tblBagasi.getValueAt(selectedRow, 3).toString();
                String tanggalTransaksi = tblBagasi.getValueAt(selectedRow, 4).toString();
                String beratBawaan = tblBagasi.getValueAt(selectedRow, 5).toString();
                String layananBagasi = tblBagasi.getValueAt(selectedRow, 6).toString();
                String hargaBagasi = tblBagasi.getValueAt(selectedRow, 7).toString();
                String kodePromo = tblBagasi.getValueAt(selectedRow, 8) != null ? tblBagasi.getValueAt(selectedRow, 8).toString() : "-";

                // Buat isi struk
                StringBuilder struk = new StringBuilder();
                struk.append("=== STRUK LAYANAN BAGASI ===\n");
                struk.append("ID: ").append(id).append("\n");
                struk.append("Nama Penumpang: ").append(namaPenumpang).append("\n");
                struk.append("Kelas: ").append(kelas).append("\n");
                struk.append("Maskapai: ").append(maskapai).append("\n");
                struk.append("Tanggal Transaksi: ").append(tanggalTransaksi).append("\n");
                struk.append("Berat Bawaan: ").append(beratBawaan).append(" kg\n");
                struk.append("Layanan Bagasi: ").append(layananBagasi).append("\n");
                struk.append("Harga Bagasi: Rp").append(hargaBagasi).append("\n");
                struk.append("Kode Promo: ").append(kodePromo).append("\n");
                struk.append("============================\n");

                // Simpan struk ke file txt
                java.io.File file = new java.io.File("struk_bagasi_" + id + ".txt");
                try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                    writer.write(struk.toString());
                }

                JOptionPane.showMessageDialog(null, "Struk berhasil dicetak ke file: " + file.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error saat mencetak struk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });
}


private void addSearchListener() {
    txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyReleased(java.awt.event.KeyEvent evt) {
            String searchText = txtCari.getText().trim().toLowerCase();
            DefaultTableModel model = (DefaultTableModel) tblBagasi.getModel();
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            tblBagasi.setRowSorter(sorter);

            if (searchText.isEmpty()) {
                sorter.setRowFilter(null); // Tampilkan semua data jika input pencarian kosong
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 1)); // Cari berdasarkan kolom nama (index 1)
            }
        }
    });
}





    
    
    



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSpinField1 = new com.toedter.components.JSpinField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtBeratBawaan = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cbbKelas = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cbbMaskapai = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtPenumpang = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cbbLayananBagasi = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        txtHargaBagasi = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnUlang = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnCetak = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBagasi = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        txtKodePromo = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        tglTransaksi = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        btnKembali = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 102, 153));

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Nama Penumpang");

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Kelas");

        cbbKelas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ekonomi", "Bisnis" }));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Maskapai");

        cbbMaskapai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lion Air", "Garuda Indonesia", "Citilink", "Singapore Airlines", "Air Asia", "Qatar Airways" }));

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Berat Bawaan (Kg)");

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Layanan Bagasi");

        cbbLayananBagasi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bronze", "Silver", "Gold" }));

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Harga Bagasi");

        txtHargaBagasi.setEditable(false);
        txtHargaBagasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHargaBagasiActionPerformed(evt);
            }
        });

        btnTambah.setBackground(new java.awt.Color(51, 204, 0));
        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnEdit.setBackground(new java.awt.Color(255, 255, 0));
        btnEdit.setText("Edit");

        btnUlang.setBackground(new java.awt.Color(102, 255, 255));
        btnUlang.setText("Ulang");
        btnUlang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUlangActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(204, 0, 51));
        btnHapus.setText("Hapus");

        btnCetak.setBackground(new java.awt.Color(0, 204, 204));
        btnCetak.setText("Cetak Struk");
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });

        tblBagasi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "id", "Nama Penumpang", "Kelas", "Maskapai", "Tanggal Transaksi", "Berat Bawaan (Kg)", "Layanan Bagasi", "Harga Bagasi", "Kode Promo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblBagasi);

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Kode Promo");

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Tanggal Transaksi");

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Cari bagasi berdasarkan nama penumpang");

        btnKembali.setText("Kembali ke Halaman Utama");
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(37, 37, 37)
                            .addComponent(txtPenumpang))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(37, 37, 37)
                            .addComponent(cbbKelas, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(37, 37, 37)
                            .addComponent(cbbMaskapai, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(37, 37, 37)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtKodePromo)
                                .addComponent(cbbLayananBagasi, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtHargaBagasi)
                                .addComponent(txtBeratBawaan)
                                .addComponent(tglTransaksi, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btnTambah)
                            .addGap(18, 18, 18)
                            .addComponent(btnEdit)
                            .addGap(18, 18, 18)
                            .addComponent(btnUlang)
                            .addGap(18, 18, 18)
                            .addComponent(btnHapus)
                            .addGap(34, 34, 34)
                            .addComponent(btnCetak)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(136, 136, 136)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCari, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtPenumpang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbbKelas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbbMaskapai, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tglTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel8))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBeratBawaan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbbLayananBagasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtHargaBagasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtKodePromo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah)
                    .addComponent(btnEdit)
                    .addComponent(btnUlang)
                    .addComponent(btnHapus)
                    .addComponent(btnCetak))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnKembali)
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(0, 51, 51));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("FORM LAYANAN BAGASI");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(289, 289, 289)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(44, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(36, 36, 36))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCetakActionPerformed

    private void txtHargaBagasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHargaBagasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaBagasiActionPerformed

    private void btnUlangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUlangActionPerformed
        resetForm();
    }//GEN-LAST:event_btnUlangActionPerformed

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
            java.util.logging.Logger.getLogger(LayananMaskapaiView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LayananMaskapaiView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LayananMaskapaiView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LayananMaskapaiView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LayananMaskapaiView().setVisible(true);
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
    private com.toedter.components.JSpinField jSpinField1;
    private javax.swing.JTable tblBagasi;
    private com.toedter.calendar.JDateChooser tglTransaksi;
    private javax.swing.JTextField txtBeratBawaan;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtHargaBagasi;
    private javax.swing.JTextField txtKodePromo;
    private javax.swing.JTextField txtPenumpang;
    // End of variables declaration//GEN-END:variables

     


}
