package programmingproject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String URL = "jdbc:sqlite:C:/Users/Hp/OneDrive/Masaüstü/busra.db/";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }

    public static List<Object[]> getGrades() {
        List<Object[]> grades = new ArrayList<>();
        String sql = "SELECT student_id, course, grade FROM grades";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("student_id"),
                    rs.getString("course"),
                    rs.getInt("grade")
                };
                grades.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching grades: " + e.getMessage());
        }
        return grades;
    }

    // Courses tablosunu oluştur
    public static void createCourseTable() {
        String sql = "CREATE TABLE IF NOT EXISTS courses ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "course_name TEXT NOT NULL"
                + ");";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Courses table created.");
        } catch (SQLException e) {
            System.out.println("Error creating courses table: " + e.getMessage());
        }
    }

    // Courses tablosundan dersleri çek
    public static List<String> getCourses() {
        List<String> courses = new ArrayList<>();
        String sql = "SELECT course_name FROM courses";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(rs.getString("course_name"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching courses: " + e.getMessage());
        }
        return courses;
    }

    // Students tablosundan öğrencileri çek
    public static List<String[]> getStudents() {
        List<String[]> students = new ArrayList<>();
        String sql = "SELECT id, name, surname, class FROM students";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String[] student = {
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("surname"),
                    rs.getString("class")
                };
                students.add(student);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching students: " + e.getMessage());
        }
        return students;
    }
    // Öğrenci bilgilerini ve notlarını birleştirip döner

    public static List<String[]> getStudentGrades() {
        List<String[]> studentGrades = new ArrayList<>();
        String sql = "SELECT s.id AS student_id, s.name, s.surname, s.class, g.course, g.grade "
                + "FROM students s "
                + "INNER JOIN grades g ON s.id = g.student_id";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String[] row = {
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("surname"),
                    rs.getString("class"),
                    rs.getString("course"),
                    rs.getString("grade")
                };
                studentGrades.add(row);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching student grades: " + e.getMessage());
        }
        return studentGrades;
    }

    public static List<Object[]> getStudentGrades(int studentID) {
    List<Object[]> grades = new ArrayList<>();
    try (Connection conn = DatabaseHelper.connect()) {
       String sql = "SELECT c.course_name, c.credits, g.grade, " +
             "CASE WHEN g.grade >= 50 THEN 'Pass' ELSE 'Fail' END AS pass_fail " +
             "FROM grades g " +
             "JOIN courses c ON g.course = c.course_name " +
             "WHERE g.student_id = ?";


        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, studentID);

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Object[] grade = {
                rs.getString("course_name"),
                rs.getInt("credits"),
                rs.getInt("grade"),
                rs.getString("pass_fail")
            };
            grades.add(grade);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    return grades;
}


    public static void addAttendance(int studentID, String date) throws SQLException {
    try (Connection conn = connect()) {
        String sql = "INSERT INTO attendance (student_id, date) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentID);
            pstmt.setString(2, date);
            pstmt.executeUpdate();
        }
    }
}


    public static List<String[]> getStudentAttendance(int studentID) {
        List<String[]> attendanceList = new ArrayList<>();
        String sql = "SELECT date FROM attendance WHERE student_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                attendanceList.add(new String[]{rs.getString("date"), "Absent"});
            }
        } catch (SQLException e) {
            System.out.println("Error fetching attendance: " + e.getMessage());
        }
        return attendanceList;
    }
    public static List<Object[]> getAllAttendances() {
    List<Object[]> attendanceList = new ArrayList<>();
    String query = "SELECT student_id, date FROM attendance";

    try (Connection conn = connect();
         PreparedStatement pstmt = conn.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            int studentID = rs.getInt("student_id");
            String date = rs.getString("date");
            attendanceList.add(new Object[]{studentID, date});
        }

    } catch (SQLException e) {
        System.err.println("Error fetching attendance data: " + e.getMessage());
    }

    return attendanceList;
}
    public static List<String> getAllCourses() {
    List<String> courses = new ArrayList<>();
    try (Connection conn = connect()) {
        String sql = "SELECT course_name FROM courses";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            courses.add(rs.getString("course_name"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return courses;
}
public static List<String[]> getStudentGrades(String courseName, Integer studentID) {
    List<String[]> grades = new ArrayList<>();
    try (Connection conn = connect()) {
        // SQL sorgusu için temel yapı
        StringBuilder sql = new StringBuilder(
                "SELECT s.id AS student_id, s.name, s.surname, s.class, g.course, g.grade " +
                "FROM students s " +
                "JOIN grades g ON s.id = g.student_id " +
                "WHERE 1=1"
        );

        // Filtreler
        if (courseName != null && !courseName.equals("Select a course")) {
            sql.append(" AND g.course = ?");
        }
        if (studentID != null) {
            sql.append(" AND s.id = ?");
        }

        PreparedStatement pstmt = conn.prepareStatement(sql.toString());

        // Parametreleri hazırlama
        int paramIndex = 1;
        if (courseName != null && !courseName.equals("Select a course")) {
            pstmt.setString(paramIndex++, courseName);
        }
        if (studentID != null) {
            pstmt.setInt(paramIndex, studentID);
        }

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            grades.add(new String[]{
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("surname"),
                    rs.getString("class"),
                    rs.getString("course"),
                    rs.getString("grade")
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return grades;
}



}
