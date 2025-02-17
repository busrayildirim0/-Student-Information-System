package programmingproject;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.util.List;

public class Transcript extends JFrame {
    private int studentID;

    public Transcript(int studentID) {
        this.studentID = studentID;
        setTitle("Transcript");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel studentInfoPanel = new JPanel(new GridLayout(3, 1));
        studentInfoPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));

        JLabel lblName = new JLabel("Student Info");
        JLabel lblStudentID = new JLabel("Student ID: " + studentID);
        JLabel lblDepartment = new JLabel("Department: Placeholder Department");

        studentInfoPanel.add(lblName);
        studentInfoPanel.add(lblStudentID);
        studentInfoPanel.add(lblDepartment);

        String[] columnNames = {"Course Name", "Credits", "Grade", "Total Points", "Pass/Fail"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        List<Object[]> courseData = DatabaseHelper.getStudentGrades(studentID);
        for (Object[] course : courseData) {
            int credits = (int) course[1];
            int grade = (int) course[2];
            Object[] row = {
                course[0],                  // Course Name
                credits,                    // Credits
                grade,                      // Grade
                credits * grade,            // Total Points
                grade >= 50 ? "Pass" : "Fail" // Pass/Fail
            };
            tableModel.addRow(row);
        }

        JTable transcriptTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(transcriptTable);

        JPanel gpaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblGPA = new JLabel("GPA: " + calculateGPA(courseData));
        gpaPanel.add(lblGPA);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            dispose();
            new StudentMainPanel(studentID);
        });
        topPanel.add(btnBack);

        // Add Generate PDF button
        JButton btnGeneratePDF = new JButton("Generate PDF");
        btnGeneratePDF.addActionListener(e -> {
            generatePDF(studentID, studentInfoPanel, courseData);
        });
        topPanel.add(btnGeneratePDF);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(studentInfoPanel, BorderLayout.WEST);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(gpaPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private double calculateGPA(List<Object[]> courseData) {
        double totalPoints = 0;
        int totalCredits = 0;

        for (Object[] row : courseData) {
            int credits = (int) row[1];
            int grade = (int) row[2];
            totalPoints += credits * grade;
            totalCredits += credits;
        }

        return totalCredits == 0 ? 0 : Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }

    // Generate PDF method
    private void generatePDF(int studentID, JPanel studentInfoPanel, List<Object[]> courseData) {
        String filePath = "C:/Users/Hp/Downloads/";
            Document document = new Document();
            

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath+"Transcript_" + studentID + ".pdf"));
            document.open();

            // Student Info
            document.add(new Paragraph("Student Transcript"));
            document.add(new Paragraph("Student ID: " + studentID));
            document.add(new Paragraph("Department: Placeholder Department"));
            document.add(new Paragraph("\n"));

            // Table
            PdfPTable table = new PdfPTable(5);
            table.addCell("Course Name");
            table.addCell("Credits");
            table.addCell("Grade");
            table.addCell("Total Points");
            table.addCell("Pass/Fail");

            for (Object[] row : courseData) {
                table.addCell((String) row[0]);  // Course Name
                table.addCell(String.valueOf(row[1]));  // Credits
                table.addCell(String.valueOf(row[2]));  // Grade
                table.addCell(String.valueOf((int) row[1] * (int) row[2]));  // Total Points
                table.addCell((int) row[2] >= 50 ? "Pass" : "Fail");  // Pass/Fail
            }

            document.add(table);

            // GPA
            document.add(new Paragraph("\nGPA: " + calculateGPA(courseData)));
            document.close();

            JOptionPane.showMessageDialog(this, "PDF generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

