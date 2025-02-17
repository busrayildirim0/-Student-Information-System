package programmingproject;

import java.awt.Font;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StudentAttendanceViewScreen extends JFrame {

    public StudentAttendanceViewScreen(int studentID) {
        
        setTitle("View Attendance Records");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblTitle = new JLabel("Attendance Records", SwingConstants.CENTER);
        lblTitle.setBounds(20, 20, 540, 30);
        lblTitle.setFont(new Font("Times New Roman", Font.BOLD, 16));
        add(lblTitle);

        // Attendance table
        DefaultTableModel attendanceModel = new DefaultTableModel(new String[]{"Date", "Status"}, 0);
        JTable attendanceTable = new JTable(attendanceModel);
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBounds(20, 70, 540, 250);
        add(scrollPane);

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(20, 330, 100, 30);
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new StudentMainPanel(studentID); 
            }
        });

        // Automatically fetch attendance based on logged-in student's ID
        List<String[]> attendanceList = DatabaseHelper.getStudentAttendance(studentID);
        for (String[] attendance : attendanceList) {
            attendanceModel.addRow(attendance);
        }

        add(btnBack);

        setVisible(true);
    }
}

