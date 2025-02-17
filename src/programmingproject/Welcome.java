package programmingproject;
import javax.swing.*;
import java.awt.*;

public class Welcome extends JFrame {

    public Welcome() {
        setTitle("Main Control Panel");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout()); 

        JLabel lblWelcome = new JLabel("Welcome to the Student Information System!", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Times New Roman", Font.BOLD, 16));
        add(lblWelcome, BorderLayout.NORTH); 

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        JButton teacherLogin = new JButton("Teacher Login");
        JButton studentLogin = new JButton("Student Login");

        teacherLogin.addActionListener(e -> {
            new TeacherLoginScreen(); 
        });

        studentLogin.addActionListener(e -> {
            new StudentLoginScreen();  
        });

        panel.add(teacherLogin);
        panel.add(studentLogin);
        add(panel, BorderLayout.CENTER);

        setVisible(true); 
    }

    public static void main(String[] args) {
        new Welcome();  
    }
}

