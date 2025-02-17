package programmingproject;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.util.List;

public class TeacherReportScreen extends JFrame {

    private DefaultTableModel reportModel;
    private JComboBox<String> courseComboBox;
    private JTextField studentIDField;
    private JCheckBox allStudentsCheckBox;
    private JCheckBox allCoursesCheckBox;

    public TeacherReportScreen() {
        setTitle("Teacher Report");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Back button
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(0, 0, 70, 30);
        btnBack.addActionListener(e -> {
            dispose();
            new TeacherMainPanel();
        });

        // Export to PDF button
        JButton btnExportToPDF = new JButton("Export to PDF");
        btnExportToPDF.setBounds(100, 0, 150, 30);
        btnExportToPDF.addActionListener(e -> exportTableToPDF());

        // Filters
        JLabel lblCourse = new JLabel("Course:");
        lblCourse.setBounds(20, 40, 100, 30);
        courseComboBox = new JComboBox<>();
        courseComboBox.setBounds(100, 40, 150, 30);
        loadCourses(); // Dersleri veritabanından çekip comboBox'a yükle

        JLabel lblStudentID = new JLabel("Student ID:");
        lblStudentID.setBounds(20, 80, 100, 30);
        studentIDField = new JTextField();
        studentIDField.setBounds(100, 80, 150, 30);

        allStudentsCheckBox = new JCheckBox("All Students");
        allStudentsCheckBox.setBounds(260, 80, 120, 30);
        allStudentsCheckBox.addActionListener(e -> studentIDField.setEnabled(!allStudentsCheckBox.isSelected()));

        allCoursesCheckBox = new JCheckBox("All Courses");
        allCoursesCheckBox.setBounds(260, 40, 120, 30);
        allCoursesCheckBox.addActionListener(e -> courseComboBox.setEnabled(!allCoursesCheckBox.isSelected()));

        // Table
        reportModel = new DefaultTableModel(new Object[]{"Student ID", "Name", "Surname", "Class", "Course", "Grade"}, 0);
        JTable reportTable = new JTable(reportModel);
        JScrollPane reportScrollPane = new JScrollPane(reportTable);
        reportScrollPane.setBounds(20, 120, 650, 400);

        add(lblCourse);
        add(courseComboBox);
        add(lblStudentID);
        add(studentIDField);
        add(allStudentsCheckBox);
        add(allCoursesCheckBox);
        add(reportScrollPane);
        add(btnBack);
        add(btnExportToPDF);

        loadStudentGrades(null, null); // Tüm verileri yükle
    }

    private void loadCourses() {
        List<String> courses = DatabaseHelper.getAllCourses();
        courseComboBox.addItem("Select a course"); // Varsayılan seçenek
        for (String course : courses) {
            courseComboBox.addItem(course);
        }
    }

    private void loadStudentGrades(String selectedCourse, Integer studentID) {
        reportModel.setRowCount(0);
        List<String[]> studentGrades = DatabaseHelper.getStudentGrades(selectedCourse, studentID);
        for (String[] row : studentGrades) {
            reportModel.addRow(row);
        }
    }

    private void exportTableToPDF() {
        try {
            String selectedCourse = allCoursesCheckBox.isSelected() ? null : (String) courseComboBox.getSelectedItem();
            Integer studentID = allStudentsCheckBox.isSelected() ? null : (studentIDField.getText().isEmpty() ? null : Integer.parseInt(studentIDField.getText()));

            // Verileri filtrele
            loadStudentGrades(selectedCourse, studentID);

            String filePath = "C:/Users/Hp/Downloads/TeacherReport.pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Başlık
            document.add(new Paragraph("Teacher Report"));
            document.add(new Paragraph(" ")); // Boşluk eklemek için

            // Tablo oluşturma
            PdfPTable pdfTable = new PdfPTable(reportModel.getColumnCount());

            // Sütun başlıklarını ekleme
            for (int i = 0; i < reportModel.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(reportModel.getColumnName(i)));
                pdfTable.addCell(cell);
            }

            // Satırları ekleme
            for (int row = 0; row < reportModel.getRowCount(); row++) {
                for (int col = 0; col < reportModel.getColumnCount(); col++) {
                    PdfPCell cell = new PdfPCell(new Phrase(reportModel.getValueAt(row, col).toString()));
                    pdfTable.addCell(cell);
                }
            }

            document.add(pdfTable);
            document.close();

            JOptionPane.showMessageDialog(null, "PDF Exported Successfully: " + filePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while exporting to PDF.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new TeacherReportScreen();
    }
}
