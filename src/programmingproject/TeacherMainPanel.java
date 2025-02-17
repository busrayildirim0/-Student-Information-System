package programmingproject;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class TeacherMainPanel extends JFrame {

    public TeacherMainPanel() {
        // Set a modern FlatLaf look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        setTitle("Teacher Control Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top panel with Back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            new TeacherLoginScreen();
            dispose();
        });
        topPanel.add(btnBack);
        add(topPanel, BorderLayout.NORTH);

        // Main panel with buttons in a vertical BoxLayout for better spacing
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnStudentManagement = new JButton("Student Management");
        JButton btnCourseManagement = new JButton("Course Management");
        JButton btnReporting = new JButton("Reporting");
        JButton btnSendMessage = new JButton("Send Message to Students");
        JButton btnAttendance = new JButton("Attendance Management");

        // Center buttons horizontally
        btnStudentManagement.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCourseManagement.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReporting.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSendMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAttendance.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(btnStudentManagement);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnCourseManagement);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnReporting);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnSendMessage);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(btnAttendance);

        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TeacherMainPanel());
    }
}
