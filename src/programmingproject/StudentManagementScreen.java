package programmingproject;

import java.sql.*;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class StudentManagementScreen extends JFrame {

    public StudentManagementScreen() {
        DatabaseHelper.createCourseTable();
        setTitle("Student Management");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        DefaultTableModel studentModel = new DefaultTableModel(new String[]{"ID", "Name", "Surname", "Class"}, 0);
        JTable studentTable = new JTable(studentModel);
        JScrollPane studentScrollPane = new JScrollPane(studentTable);
        studentScrollPane.setBounds(20, 30, 500, 200);

        DefaultTableModel gradeModel = new DefaultTableModel(new String[]{"Student ID", "Course", "Grade"}, 0);
        JTable gradeTable = new JTable(gradeModel);
        JScrollPane gradeScrollPane = new JScrollPane(gradeTable);
        gradeScrollPane.setBounds(20, 320, 640, 100);

        JLabel courseText = new JLabel("Course: ");
        courseText.setBounds(20, 200, 100, 30);
        List<String> courseList = DatabaseHelper.getCourses();
        JComboBox<String> courseComboBox = new JComboBox<>(courseList.toArray(new String[0]));
        courseComboBox.setBounds(20, 230, 100, 30);

        JLabel gradeText = new JLabel("Grade: ");
        gradeText.setBounds(130, 200, 100, 30);
        JTextField gradeField = new JTextField();
        gradeField.setBounds(130, 230, 100, 30);

        JButton btnAddGrade = new JButton("Add Grade");
        btnAddGrade.setBounds(240, 230, 100, 30);

        JButton btnDeleteGrade = new JButton("Delete Grade");
        btnDeleteGrade.setBounds(340, 230, 120, 30);

        JButton btnUpdateGrade = new JButton("Update Grade");
        btnUpdateGrade.setBounds(460, 230, 120, 30);

        btnAddGrade.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a student to add a grade.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String studentIDString = (String) studentModel.getValueAt(selectedRow, 0);
            int studentID = Integer.parseInt(studentIDString);

            String course = courseComboBox.getSelectedItem().toString();
            String gradeText1 = gradeField.getText().trim();

            if (gradeText1.isEmpty() || !gradeText1.matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "Grade must be a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int grade = Integer.parseInt(gradeField.getText().trim());

            if (grade < 0 || grade > 100) {
                JOptionPane.showMessageDialog(null, "Grade must be between 0 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseHelper.connect()) {
                String sql = "INSERT INTO grades (student_id, course, grade) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, studentID);
                pstmt.setString(2, course);
                pstmt.setInt(3, grade);

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(null, "Grade added successfully!");
                    loadGradeTable(gradeModel); // Notları güncelle
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error adding grade: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnUpdateGrade.addActionListener(e -> {
            int selectedRow = gradeTable.getSelectedRow(); // Seçili satırı kontrol et
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a grade to update.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Seçili satırdan öğrenci ID'si ve ders adını al
            String studentID = gradeModel.getValueAt(selectedRow, 0).toString();
            String course = gradeModel.getValueAt(selectedRow, 1).toString();

            // Yeni notu al
            String gradeText1 = gradeField.getText().trim();
            if (!gradeText1.matches("\\d+") || gradeText1.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Grade must be a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int grade = Integer.parseInt(gradeText1);

            if (grade < 0 || grade > 100) {
                JOptionPane.showMessageDialog(null, "Grade must be between 0 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseHelper.connect()) {
                // SQL sorgusu ile veriyi güncelle
                String sql = "UPDATE grades SET grade = ? WHERE student_id = ? AND course = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, grade);
                pstmt.setString(2, studentID);
                pstmt.setString(3, course);

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    gradeModel.setValueAt(grade, selectedRow, 2); // JTable'daki notu güncelle
                    JOptionPane.showMessageDialog(null, "Grade updated successfully.");
                    gradeField.setText(""); // Giriş alanını temizle
                } else {
                    JOptionPane.showMessageDialog(null, "No rows updated. Please check the selected data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error updating grade: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDeleteGrade.addActionListener(e -> {
            int selectedRow = gradeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a grade to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String studentID = gradeModel.getValueAt(selectedRow, 0).toString();
            String course = (String) gradeModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this grade?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseHelper.connect()) {
                    String sql = "DELETE FROM grades WHERE student_id = ? AND course = ?";
                    var pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, studentID);
                    pstmt.setString(2, course);

                    int rowsDeleted = pstmt.executeUpdate();
                    if (rowsDeleted > 0) {
                        gradeModel.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(null, "Grade deleted successfully.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error deleting grade: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(20, 0, 100, 20);
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new TeacherMainPanel();
            }
        });
        loadStudentsIntoTable(studentModel);
        loadGrades(gradeModel);
        add(btnBack);
        add(btnDeleteGrade);
        add(btnUpdateGrade);
        add(studentScrollPane);
        add(gradeScrollPane);
        add(courseComboBox);
        add(gradeField);
        add(btnAddGrade);
        add(courseText);
        add(gradeText);

        setVisible(true);
    }

    public void loadStudentTable(DefaultTableModel studentModel) {
        studentModel.setRowCount(0);
        try (Connection conn = DatabaseHelper.connect()) {
            String sql = "SELECT id, name, surname, class FROM students";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                studentModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("surname"),
                    rs.getString("class")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading students: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudentsIntoTable(DefaultTableModel studentModel) {
        studentModel.setRowCount(0);
        List<String[]> students = DatabaseHelper.getStudents();
        for (String[] student : students) {
            studentModel.addRow(student);
        }
    }

    public void loadGradeTable(DefaultTableModel gradeModel) {
        gradeModel.setRowCount(0);
        try (Connection conn = DatabaseHelper.connect()) {
            String sql = "SELECT g.student_id, g.course, g.grade, s.name, s.surname "
                    + "FROM grades g INNER JOIN students s ON g.student_id = s.id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                gradeModel.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("course"),
                    rs.getInt("grade"),
                    rs.getString("name"),
                    rs.getString("surname")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading grades: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadGrades(DefaultTableModel gradeModel) {
        List<Object[]> grades = DatabaseHelper.getGrades();
        gradeModel.setRowCount(0);
        for (Object[] grade : grades) {
            gradeModel.addRow(grade);
        }
    }

    public static void main(String[] args) {
        new StudentManagementScreen();
    }
}
