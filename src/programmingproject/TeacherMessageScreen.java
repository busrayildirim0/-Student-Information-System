package programmingproject;

import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TeacherMessageScreen extends JFrame {

    public TeacherMessageScreen() {
        setTitle("Send Message to Students");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel lblStudentId = new JLabel("Student ID:");
        lblStudentId.setBounds(30, 30, 100, 25);
        JTextField txtStudentId = new JTextField();
        txtStudentId.setBounds(130, 30, 150, 25);

        JLabel lblMessage = new JLabel("Message:");
        lblMessage.setBounds(30, 80, 100, 25);
        JTextArea txtMessage = new JTextArea();
        txtMessage.setBounds(30, 110, 400, 150);
        txtMessage.setLineWrap(true);
        txtMessage.setWrapStyleWord(true);
        txtMessage.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JCheckBox chkAllStudents = new JCheckBox("Send to all students");
        chkAllStudents.setBounds(30, 270, 200, 25);

        JButton btnSend = new JButton("Send");
        btnSend.setBounds(150, 310, 100, 30);
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentId = txtStudentId.getText();
                String message = txtMessage.getText();
                boolean sendToAll = chkAllStudents.isSelected();

                if (sendToAll) {
                    try (Connection conn = DatabaseHelper.connect()) {
                        String getAllStudentsSql = "SELECT id FROM students";
                        PreparedStatement getAllStudentsPstmt = conn.prepareStatement(getAllStudentsSql);
                        ResultSet rs = getAllStudentsPstmt.executeQuery();

                        while (rs.next()) {
                            int studentID = rs.getInt("id");

                            String sql = "INSERT INTO messages (student_id, message, sender) VALUES (?, ?, ?)";
                            PreparedStatement pstmt = conn.prepareStatement(sql);
                            pstmt.setInt(1, studentID);
                            pstmt.setString(2, message);
                            pstmt.setString(3, "Teacher");
                            pstmt.executeUpdate();
                        }

                        JOptionPane.showMessageDialog(null, "Message sent to all students:\n" + message);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "An error occurred while sending the message to all students.");
                    }
                } else if (!studentId.isEmpty()) {
                    try (Connection conn = DatabaseHelper.connect()) {
                        String checkStudentSql = "SELECT COUNT(*) FROM students WHERE id = ?";
                        PreparedStatement checkStudentPstmt = conn.prepareStatement(checkStudentSql);
                        checkStudentPstmt.setInt(1, Integer.parseInt(studentId));
                        ResultSet rs = checkStudentPstmt.executeQuery();

                        if (rs.next() && rs.getInt(1) == 0) {
                            JOptionPane.showMessageDialog(null, "Invalid Student ID. Please enter a valid ID from the system.");
                            return; 
                        }

                        String sql = "INSERT INTO messages (student_id, message, sender) VALUES (?, ?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setInt(1, Integer.parseInt(studentId));
                        pstmt.setString(2, message);
                        pstmt.setString(3, "Teacher");
                        pstmt.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Message sent to Student ID " + studentId + ":\n" + message);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "An error occurred while sending the message.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a Student ID or select 'Send to all students'!");
                }
                txtStudentId.setText("");
                txtMessage.setText("");

            }
        });

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(10, 10, 70, 20);
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TeacherMainPanel();
                dispose();
            }
        });

        panel.add(lblStudentId);
        panel.add(txtStudentId);
        panel.add(lblMessage);
        panel.add(txtMessage);
        panel.add(chkAllStudents);
        panel.add(btnSend);
        panel.add(btnBack);

        add(panel);
        setVisible(true);
    }

    private void insertMessage(Integer studentId, String message) {
        try (Connection conn = DatabaseHelper.connect()) {
            String sql = "INSERT INTO messages (student_id, message) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (studentId == null) {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(1, studentId);
            }
            pstmt.setString(2, message);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TeacherMessageScreen();
    }
}
