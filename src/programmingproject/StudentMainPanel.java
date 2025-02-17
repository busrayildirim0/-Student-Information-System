package programmingproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentMainPanel extends JFrame {

    private int studentID;

    public StudentMainPanel(int studentID) {
        this.studentID = studentID;
        setTitle("Student Main Panel");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1, 10, 10));

        JButton btnCoursesGrades = new JButton("Courses and Grades");
        btnCoursesGrades.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new StudentReportScreen(studentID);  
            }
        });

        JButton btnTranscript = new JButton("Transcript");
        btnTranscript.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 dispose();
                new Transcript(studentID);  
            }
        });

        JButton btnAnnouncementsAndMessages = new JButton("Announcements And Messages");
        btnAnnouncementsAndMessages.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 dispose();
                new AnnouncementsAndMessages(studentID);
            }
        });

        JButton btnViewAttendance = new JButton("Attendance View Screen");
        btnViewAttendance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 dispose();
                new StudentAttendanceViewScreen(studentID);  
            }
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new StudentLoginScreen();  
            }
        });
        topPanel.add(btnBack);
        add(topPanel);

        add(btnCoursesGrades);
        add(btnTranscript);
        add(btnViewAttendance);
        add(btnAnnouncementsAndMessages);

        setVisible(true);
    }
}
