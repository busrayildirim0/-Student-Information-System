package programmingproject;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.sql.*;

public class TeacherLoginScreen {

    public TeacherLoginScreen() {
        JFrame frame = new JFrame("Teacher Login Screen");
        frame.setLayout(null);
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblUsername = new JLabel("Teacher User Name:");
        lblUsername.setBounds(20, 60, 150, 30);

        JTextField txtUsername = new JTextField();
        txtUsername.setBounds(150, 60, 150, 30);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(20, 120, 150, 30);

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 120, 150, 30);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(100, 180, 130, 30);

        JButton btnForgotPassword = new JButton("Forget Password");
        btnForgotPassword.setBounds(240, 180, 130, 30);

        JLabel lblWelcome = new JLabel("Teacher Login Screen!", SwingConstants.CENTER);
        lblWelcome.setBounds(55, 20, 350, 20);
        lblWelcome.setFont(new Font("Times New Roman", Font.BOLD, 16));
        frame.add(lblWelcome);

        btnLogin.addActionListener(e -> {
            String name = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());

            if (name.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseHelper.connect()) {
                String sql = "SELECT * FROM teacher WHERE name = ? AND password = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, password);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Teacher login successful!");
                    new TeacherMainPanel();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid name or password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
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

        frame.add(btnBack);
        frame.add(btnForgotPassword);
        frame.add(btnLogin);
        frame.add(lblPassword);
        frame.add(lblUsername);
        frame.add(txtPassword);
        frame.add(txtUsername);

        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new TeacherLoginScreen();
    }
}
