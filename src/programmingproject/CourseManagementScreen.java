package programmingproject;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CourseManagementScreen extends JFrame {

    private DefaultTableModel tableModel;
    private JTable courseTable;
    private JTextField txtCourseName, txtInstructor, txtCredits;

    public CourseManagementScreen() {
        DatabaseHelper.createCourseTable();

        setTitle("Course Management");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Course Details"));

        txtCourseName = new JTextField();
        txtInstructor = new JTextField();
        txtCredits = new JTextField();

        formPanel.add(new JLabel("Course Name:"));
        formPanel.add(txtCourseName);
        formPanel.add(new JLabel("Instructor:"));
        formPanel.add(txtInstructor);
        formPanel.add(new JLabel("Credits:"));
        formPanel.add(txtCredits);

        JButton btnAdd = new JButton("Add Course");
        JButton btnUpdate = new JButton("Update Course");
        JButton btnDelete = new JButton("Delete Course");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);

        String[] columnNames = {"Course Name", "Instructor", "Credits"};
        tableModel = new DefaultTableModel(columnNames, 0);
        courseTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(courseTable);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new TeacherMainPanel();
            }
        });
        backPanel.add(btnBack);
        mainPanel.add(backPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String courseName = txtCourseName.getText();
                String instructor = txtInstructor.getText();
                String credits = txtCredits.getText();

                if (courseName.isEmpty() || instructor.isEmpty() || credits.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields are required!");
                } else {
                    try {
                        addCourseToDatabase(courseName, instructor, Integer.parseInt(credits));
                        tableModel.addRow(new Object[]{courseName, instructor, credits});
                        clearForm();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Credits must be a number!");
                    }
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = courseTable.getSelectedRow();
                if (selectedRow != -1) {
                    String courseName = (String) tableModel.getValueAt(selectedRow, 0);
                    deleteCourseFromDatabase(courseName);
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a course to delete!");
                }
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = courseTable.getSelectedRow();
                if (selectedRow != -1) {
                    String oldCourseName = (String) tableModel.getValueAt(selectedRow, 0);
                    String courseName = txtCourseName.getText();
                    String instructor = txtInstructor.getText();
                    String credits = txtCredits.getText();

                    if (courseName.isEmpty() || instructor.isEmpty() || credits.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "All fields are required!");
                    } else {
                        try {
                            updateCourseInDatabase(oldCourseName, courseName, instructor, Integer.parseInt(credits));
                            tableModel.setValueAt(courseName, selectedRow, 0);
                            tableModel.setValueAt(instructor, selectedRow, 1);
                            tableModel.setValueAt(credits, selectedRow, 2);
                            clearForm();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Credits must be a number!");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a course to update!");
                }
            }
        });
        loadCoursesFromDatabase();


        add(mainPanel);
        setVisible(true);
    }

    private void clearForm() {
        txtCourseName.setText("");
        txtInstructor.setText("");
        txtCredits.setText("");
    }

    private void addCourseToDatabase(String courseName, String instructor, int credits) {
        String sql = "INSERT INTO courses (course_name, instructor, credits) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseName);
            pstmt.setString(2, instructor);
            pstmt.setInt(3, credits);
            pstmt.executeUpdate();
            System.out.println("Course added to database.");
        } catch (SQLException e) {
            System.out.println("Error inserting course: " + e.getMessage());
        }
    }

    private void deleteCourseFromDatabase(String courseName) {
        String sql = "DELETE FROM courses WHERE course_name = ?";
        try (Connection conn = DatabaseHelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseName);
            pstmt.executeUpdate();
            System.out.println("Course deleted from database.");
        } catch (SQLException e) {
            System.out.println("Error deleting course: " + e.getMessage());
        }
    }

    private void updateCourseInDatabase(String oldCourseName, String newCourseName, String instructor, int credits) {
        String sql = "UPDATE courses SET course_name = ?, instructor = ?, credits = ? WHERE course_name = ?";
        try (Connection conn = DatabaseHelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newCourseName);
            pstmt.setString(2, instructor);
            pstmt.setInt(3, credits);
            pstmt.setString(4, oldCourseName);
            pstmt.executeUpdate();
            System.out.println("Course updated in database.");
        } catch (SQLException e) {
            System.out.println("Error updating course: " + e.getMessage());
        }
    }
    private void loadCoursesFromDatabase() {
    String sql = "SELECT course_name, instructor, credits FROM courses";
    try (Connection conn = DatabaseHelper.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            tableModel.addRow(new Object[]{
                    rs.getString("course_name"),
                    rs.getString("instructor"),
                    rs.getInt("credits")
            });
        }
    } catch (SQLException e) {
        System.out.println("Error loading courses: " + e.getMessage());
    }
}
    



    public static void main(String[] args) {
        new CourseManagementScreen();
    }
}
