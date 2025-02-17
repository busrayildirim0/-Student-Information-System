package programmingproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.util.List;
import java.sql.*;

public class AttendanceManagementScreen extends JFrame {

    DefaultTableModel attendanceModel;

    public AttendanceManagementScreen() {
        setTitle("Attendance Management");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        attendanceModel = new DefaultTableModel(new String[]{"Student ID", "Date"}, 0);
        JTable attendanceTable = new JTable(attendanceModel);
        JScrollPane attendanceScrollPane = new JScrollPane(attendanceTable);
        attendanceScrollPane.setBounds(20, 30, 440, 150);
        add(attendanceScrollPane);

        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setBounds(20, 200, 100, 30);
        JTextField idField = new JTextField();
        idField.setBounds(120, 200, 150, 30);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(20, 250, 100, 30);

        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setBounds(120, 250, 150, 30);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        JButton btnAddAttendance = new JButton("Add Attendance");
        btnAddAttendance.setBounds(20, 300, 150, 30);

        JButton btnDeleteAttendance = new JButton("Delete Attendance");
        btnDeleteAttendance.setBounds(180, 300, 150, 30);

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(20, 0, 100, 20);
        btnBack.addActionListener(e -> {
            dispose();
            new TeacherMainPanel();
        });

        btnAddAttendance.addActionListener(e -> {
            String studentIDText = idField.getText().trim();
            Date selectedDate = (Date) dateSpinner.getValue();
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

            if (!studentIDText.matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "Student ID must be numeric!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate if the selected date is not in the future
            Date currentDate = new Date();
            if (selectedDate.after(currentDate)) {
                JOptionPane.showMessageDialog(null, "Attendance cannot be added for future dates!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int studentID = Integer.parseInt(studentIDText);

            try {
                // Validate student ID and add attendance
                DatabaseHelper.addAttendance(studentID, date);
                attendanceModel.addRow(new Object[]{studentID, date});
                JOptionPane.showMessageDialog(null, "Attendance added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                if ("SQLITE_BUSY".equals(ex.getSQLState())) {
                    JOptionPane.showMessageDialog(null, "Database is busy. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error adding attendance: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDeleteAttendance.addActionListener(e -> {
            int selectedRow = attendanceTable.getSelectedRow();
            if (selectedRow != -1) {
                // Get the student ID and date from the selected row
                int studentID = (int) attendanceModel.getValueAt(selectedRow, 0);
                String date = (String) attendanceModel.getValueAt(selectedRow, 1);

                try (Connection conn = DatabaseHelper.connect()) {
                    // Delete the attendance record from the database
                    String deleteSql = "DELETE FROM attendance WHERE student_id = ? AND date = ?";
                    PreparedStatement pstmt = conn.prepareStatement(deleteSql);
                    pstmt.setInt(1, studentID);
                    pstmt.setString(2, date);

                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        // Remove the record from the table
                        attendanceModel.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(null, "Attendance record deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to delete the attendance record.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An error occurred while deleting attendance.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select an attendance record to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(idLabel);
        add(idField);
        add(dateLabel);
        add(dateSpinner);
        add(btnAddAttendance);
        add(btnDeleteAttendance);
        add(btnBack);

        loadAttendanceData();

        setVisible(true);
    }

    private void loadAttendanceData() {
        List<Object[]> attendances = DatabaseHelper.getAllAttendances();

        attendanceModel.setRowCount(0);

        for (Object[] attendance : attendances) {
            attendanceModel.addRow(attendance);
        }
    }

    public static void main(String[] args) {
        new AttendanceManagementScreen();
    }
}
