package programmingproject;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TeacherMainPanel extends JFrame {

    public TeacherMainPanel() {
        setTitle("Teacher Control Panel");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));

        JButton btnStudentManagement = new JButton("Student Management");
        JButton btnCourseManagement = new JButton("Course Management");
        JButton btnReporting = new JButton("Reporting");
        JButton btnSendMessage = new JButton("Send Message to Students");
        JButton btnAttendance = new JButton("Attendance Management");

        btnSendMessage.addActionListener(e -> {
            new TeacherMessageScreen();
        });

        btnStudentManagement.addActionListener(e -> {
            new StudentManagementScreen();
        });

        btnCourseManagement.addActionListener(e -> {
            new CourseManagementScreen();
        });

        btnReporting.addActionListener(e -> {
            TeacherReportScreen reportScreen = new TeacherReportScreen();
            reportScreen.setVisible(true);
        });
        btnAttendance.addActionListener(e -> {
            new AttendanceManagementScreen();
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               
                new TeacherLoginScreen();
                dispose();
            }
        });
        topPanel.add(btnBack);
        panel.add(topPanel);

        panel.add(btnStudentManagement);
        panel.add(btnCourseManagement);
        panel.add(btnReporting);
        panel.add(btnSendMessage);
        panel.add(btnAttendance);
        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new TeacherMainPanel();
    }
}
