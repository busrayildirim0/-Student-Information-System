package programmingproject;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class StudentLoginScreen {
    private int studentID;

    public StudentLoginScreen() {
        
        JFrame frame = new JFrame("Student Login Screen");
        frame.setLayout(null);
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblStudentID = new JLabel("Student ID:");
        lblStudentID.setBounds(40, 60, 150, 30);
        
        JTextField txtStudentID = new JTextField();
        txtStudentID.setBounds(150, 60, 150, 30);
        
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(40, 120, 150, 30);
        
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 120, 150, 30);
        
        JButton btnLogin = new JButton("Login in");
        btnLogin.setBounds(100, 180, 130, 30);
        
        JButton btnForgotPassword = new JButton("Forget Password");
        btnForgotPassword.setBounds(240, 180, 130, 30);
        
        JLabel lblWelcome = new JLabel("Student Login Screen!", SwingConstants.CENTER);
        lblWelcome.setBounds(55, 20, 350, 20);
        lblWelcome.setFont(new Font("Times New Roman", Font.BOLD, 16));
        frame.add(lblWelcome, BorderLayout.NORTH);
        
        btnLogin.addActionListener(e -> {
            String studentIDText = txtStudentID.getText().trim();
            String password = new String(txtPassword.getPassword());

            if (studentIDText.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int studentID = Integer.parseInt(studentIDText); 

                try (Connection conn = DatabaseHelper.connect()) {
                    String sql = "SELECT * FROM students WHERE id = ? AND password = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, studentID);
                    pstmt.setString(2, password);

                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Student login successful!");
                        new StudentMainPanel(studentID); 
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid student ID or password!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Student ID must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnForgotPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Please contact your administrator.");
            }
        });
        
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(10, 10, 70, 30); 
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new Welcome(); 
            }
        });
        
        frame.add(btnForgotPassword);
        frame.add(btnLogin);
        frame.add(lblPassword);
        frame.add(lblStudentID);
        frame.add(lblWelcome);
        frame.add(txtPassword);
        frame.add(txtStudentID);
        frame.add(btnBack);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

    

