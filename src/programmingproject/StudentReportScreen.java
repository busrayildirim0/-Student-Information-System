package programmingproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StudentReportScreen extends JFrame {

    private int currentStudentID;
    private JTable reportTable; // Sınıf değişkeni olarak tanımla

    public StudentReportScreen(int studentID) {
        this.currentStudentID = studentID;

        setTitle("Student Report Screen");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            dispose();
            new StudentMainPanel(studentID);
        });
        topPanel.add(btnBack);

        JPanel mainPanel = new JPanel(new BorderLayout());

        String[] columnNames = {"Course Name", "Credits", "Grade", "Pass/Fail"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        reportTable = new JTable(tableModel); // Sınıf değişkenini burada başlat

        JScrollPane tableScrollPane = new JScrollPane(reportTable);

        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);

        loadStudentGrades();
    }

    private void loadStudentGrades() {
        List<Object[]> grades = DatabaseHelper.getStudentGrades(currentStudentID);

        DefaultTableModel tableModel = (DefaultTableModel) reportTable.getModel();
        tableModel.setRowCount(0); 

        for (Object[] grade : grades) {
            tableModel.addRow(grade);
        }
    }
}
