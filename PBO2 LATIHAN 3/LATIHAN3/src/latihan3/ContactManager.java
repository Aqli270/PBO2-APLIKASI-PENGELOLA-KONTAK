/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package latihan3;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;
import java.io.*;
import koneksi.CRUD;

public class ContactManager extends JFrame {
    private JTextField nameField, phoneField;
    private JComboBox<String> categoryComboBox;
    private JTable contactTable;
    private DefaultTableModel tableModel;

    public static void main(String[] args) {
        new ContactManager().setVisible(true);
    }

    public ContactManager() {
        setTitle("Aplikasi Pengelolaan Kontak");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create GUI components
        nameField = new JTextField(20);
        phoneField = new JTextField(20);
        categoryComboBox = new JComboBox<>(new String[]{"Keluarga", "Teman", "Kerja"});
        JButton addButton = new JButton("Tambah");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Hapus");
        JButton searchButton = new JButton("Cari");
        JButton exportButton = new JButton("Ekspor CSV");
        JButton importButton = new JButton("Impor CSV");
        
        // JTable setup
        String[] columns = {"ID", "Nama", "Nomor Telepon", "Kategori"};
        tableModel = new DefaultTableModel(columns, 0);
        contactTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(contactTable);

        // Layout setup
        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Nama:"));
        panel.add(nameField);
        panel.add(new JLabel("Nomor Telepon:"));
        panel.add(phoneField);
        panel.add(new JLabel("Kategori:"));
        panel.add(categoryComboBox);
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(searchButton);
        panel.add(exportButton);
        panel.add(importButton);

        // Add components to frame
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Event listeners
        addButton.addActionListener(e -> addContact());
        editButton.addActionListener(e -> editContact());
        deleteButton.addActionListener(e -> deleteContact());
        searchButton.addActionListener(e -> searchContact());
        exportButton.addActionListener(e -> exportToCSV());
        importButton.addActionListener(e -> importFromCSV());

        // Load contacts from database
        loadContacts();
    }

    // Load contacts into JTable
    public void loadContacts() {
        String sql = "SELECT * FROM contacts"; // Mengambil data dari tabel contacts
        try (Connection conn = CRUD.koneksi(); // Menggunakan koneksi dari class CRUD
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            tableModel.setRowCount(0); // Clear existing rows
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"),
                    rs.getString("phone"), rs.getString("category")
                });
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Add new contact to database
    public void addContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String category = (String) categoryComboBox.getSelectedItem();
        if (validatePhoneNumber(phone)) {
            String sql = "INSERT INTO contacts(name, phone, category) VALUES(?, ?, ?)";
            try (Connection conn = CRUD.koneksi(); // Menggunakan koneksi dari class CRUD
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, phone);
                pstmt.setString(3, category);
                pstmt.executeUpdate();
                loadContacts();
                clearFields();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nomor telepon tidak valid!");
        }
    }

    // Edit existing contact
    public void editContact() {
        int row = contactTable.getSelectedRow();
        if (row != -1) {
            int id = (int) contactTable.getValueAt(row, 0);
            String name = nameField.getText();
            String phone = phoneField.getText();
            String category = (String) categoryComboBox.getSelectedItem();
            if (validatePhoneNumber(phone)) {
                String sql = "UPDATE contacts SET name = ?, phone = ?, category = ? WHERE id = ?";
                try (Connection conn = CRUD.koneksi(); // Menggunakan koneksi dari class CRUD
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, phone);
                    pstmt.setString(3, category);
                    pstmt.setInt(4, id);
                    pstmt.executeUpdate();
                    loadContacts();
                    clearFields();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Nomor telepon tidak valid!");
            }
        }
    }

    // Delete selected contact
    public void deleteContact() {
        int row = contactTable.getSelectedRow();
        if (row != -1) {
            int id = (int) contactTable.getValueAt(row, 0);
            String sql = "DELETE FROM contacts WHERE id = ?";
            try (Connection conn = CRUD.koneksi(); // Menggunakan koneksi dari class CRUD
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                loadContacts();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Search contact
    public void searchContact() {
        String searchText = nameField.getText();
        String sql = "SELECT * FROM contacts WHERE name LIKE ?";
        try (Connection conn = CRUD.koneksi(); // Menggunakan koneksi dari class CRUD
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchText + "%");
            ResultSet rs = pstmt.executeQuery();
            tableModel.setRowCount(0); // Clear existing rows
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"),
                    rs.getString("phone"), rs.getString("category")
                });
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Validate phone number
    public boolean validatePhoneNumber(String phone) {
        return phone.matches("\\d{10,15}"); // Validates 10-15 digit number
    }

    // Clear text fields
    public void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        categoryComboBox.setSelectedIndex(0);
    }

    // Export contacts to CSV
    public void exportToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:/latihan 3"))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String name = (String) tableModel.getValueAt(i, 1);
                String phone = (String) tableModel.getValueAt(i, 2);
                String category = (String) tableModel.getValueAt(i, 3);
                writer.write(name + "," + phone + "," + category);
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Daftar kontak telah diekspor ke CSV.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // Import contacts from CSV
    public void importFromCSV() {
    // Membuat JFileChooser untuk memilih file CSV
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Pilih File CSV untuk Diimpor");

    // Menampilkan dialog pemilihan file
    int result = fileChooser.showOpenDialog(this);
    
    // Jika pengguna memilih file, proses impor
    if (result == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            
            // Membaca dan memproses setiap baris CSV
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                
                // Menambahkan kontak ke database
                addContactFromCSV(data[0], data[1], data[2]);
            }
            
            JOptionPane.showMessageDialog(this, "Kontak berhasil diimpor.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengimpor file CSV: " + e.getMessage());
        }
    }
}


    // Add contact from CSV data
    public void addContactFromCSV(String name, String phone, String category) {
        String sql = "INSERT INTO contacts(name, phone, category) VALUES(?, ?, ?)";
        try (Connection conn = CRUD.koneksi(); // Menggunakan koneksi dari class CRUD
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, category);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
