/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author ASUS
 */
public class DatabaseKoneksi {
   // URL database, username, dan password
    private static final String URL = "jdbc:mysql://localhost:3306/penjualan_tiket_pesawat";  // Ganti dengan URL database sesuai dengan konfigurasi
    private static final String USER = "root";  // Ganti dengan username MySQL kamu
    private static final String PASSWORD = "";  // Ganti dengan password MySQL kamu jika ada

    private static Connection connection;

    // Method untuk mendapatkan koneksi ke database
    public static Connection getKoneksi() {
        if (connection == null) {
            try {
                // Memuat driver MySQL JDBC
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Membuat koneksi ke database
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

                System.out.println("Koneksi ke database berhasil!");
            } catch (ClassNotFoundException e) {
                System.err.println("Driver JDBC tidak ditemukan: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Gagal membuat koneksi: " + e.getMessage());
            }
        }
        return connection;
    }

    // Method untuk menutup koneksi database
    public static void closeKoneksi() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("Gagal menutup koneksi: " + e.getMessage());
        }
    }
}
