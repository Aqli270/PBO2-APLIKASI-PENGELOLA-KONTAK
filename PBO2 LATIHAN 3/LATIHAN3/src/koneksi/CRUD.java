/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CRUD {
    // Pastikan koneksi global ini hanya dibuka sekali
    public static Connection koneksiDB;

    public static Connection koneksi() {
        try {
            if (koneksiDB == null || koneksiDB.isClosed()) {
                // Ubah URL, user, dan password sesuai pengaturan Anda
                String url = "jdbc:mysql://localhost:3306/contacts.db"; // Contoh URL
                String user = "root";
                String password = "";
                koneksiDB = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
        return koneksiDB;
    }
}
