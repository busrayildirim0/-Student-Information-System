package programmingproject;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementsAndMessages extends JFrame {
    private int studentID;

    public AnnouncementsAndMessages(int studentID) {
        this.studentID = studentID;
        setTitle("Announcements and Messages");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            dispose();
            new StudentMainPanel(studentID);
        });
        topPanel.add(btnBack);

        JPanel messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBorder(BorderFactory.createTitledBorder("Messages"));

        List<String> messages = getMessagesForStudent(studentID);

        for (String msg : messages) {
            JLabel lblMessage = new JLabel(msg);
            lblMessage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            messagesPanel.add(lblMessage);
        }

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(messagesPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private List<String> getMessagesForStudent(int studentId) {
        List<String> messages = new ArrayList<>();
        try (Connection conn = DatabaseHelper.connect()) {
            String sql = "SELECT sender, message FROM messages " +
                         "WHERE student_id = ? OR student_id IS NULL " +
                         "ORDER BY timestamp DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String sender = rs.getString("sender");
                String message = rs.getString("message");
                messages.add("<html><b>From: " + sender + "</b><br>" + message + "</html>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
