
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class studController {
    private final studView view;

    public studController(studView stud) {
        this.view = stud;
        view.allListener(new AllActions());

    }
        
    class AllActions implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == view.btnAdd) {
                addStud();
            } else if (e.getSource() == view.btnDelete) {
                deleteStud();
            } else if (e.getSource() == view.btnUpdate) {
                updateStud();
            } else if (e.getSource() == view.btnSearch) {
                searchStud();
            } else if (e.getSource() == view.btnDisplay) {
                displayStud();
                view.clearFields();
            }
        }

        void addStud() {
            String name = view.txtNAME.getText().trim();
            String section = view.txtSECT.getText().trim();
            String subject = view.txtSUB.getText().trim();
            String instructor = view.txtINS.getText().trim();

            if (name.isEmpty() || section.isEmpty() || subject.isEmpty() || instructor.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Please fill in all fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            studModel student = new studModel();
            student.setName(name);
            student.setSection(section);
            student.setSubject(subject);
            student.setInstructor(instructor);

            if (add(student)) {
                JOptionPane.showMessageDialog(view, "Student Added Successfully!", "Message", JOptionPane.PLAIN_MESSAGE);
                view.clearFields();
            } else {
                JOptionPane.showMessageDialog(view, "Failed to add student!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        void updateStud() {
            if (!validateIdField()) return;

            int id = Integer.parseInt(view.txtID.getText());
            String name = view.txtNAME.getText().trim();
            String section = view.txtSECT.getText().trim();
            String subject = view.txtSUB.getText().trim();
            String instructor = view.txtINS.getText().trim();

            studModel student = new studModel();
            student.setId(id);
            student.setName(name);
            student.setSection(section);
            student.setSubject(subject);
            student.setInstructor(instructor);

            if (update(student)) {
                JOptionPane.showMessageDialog(view, "Student Updated Successfully!", "Message", JOptionPane.PLAIN_MESSAGE);
                view.clearFields();
            } else {
                JOptionPane.showMessageDialog(view, "Failed to update student!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        void deleteStud() {
            if (!validateIdField()) return;

            int id = Integer.parseInt(view.txtID.getText());
            if (delete(id)) {
                JOptionPane.showMessageDialog(view, "Student Deleted Successfully!", "Message", JOptionPane.PLAIN_MESSAGE);
                view.clearFields();
            } else {
                JOptionPane.showMessageDialog(view, "Failed to delete student!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        void searchStud() {
            if (!validateIdField()) return;

            int id = Integer.parseInt(view.txtID.getText());
            studModel student = search(id);
            if (student != null) {
                view.txtNAME.setText(student.getName());
                view.txtSECT.setText(student.getSection());
                view.txtSUB.setText(student.getSubject());
                view.txtINS.setText(student.getInstructor());
            } else {
                JOptionPane.showMessageDialog(view, "Student Not Found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        void displayStud() {
            List<studModel> students = getAllStudents();
            DefaultTableModel model = (DefaultTableModel) view.jTable1.getModel();
            model.setRowCount(0); 

            for (studModel student : students) {
                model.addRow(new Object[]{student.getId(), student.getName(), student.getSection(), student.getSubject(), student.getInstructor()});
            }
        }

        boolean add(studModel model) {
            try (Connection con = Database.getConnection()) {
                String sql = "INSERT INTO studentsrecord (name, section, subject, instructor) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, model.getName());
                ps.setString(2, model.getSection());
                ps.setString(3, model.getSubject());
                ps.setString(4, model.getInstructor());
                return ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false; 
            }
        }

        boolean update(studModel model) {
            try (Connection con = Database.getConnection()) {
                String sql = "UPDATE studentsrecord SET name=?, section=?, subject=?, instructor=? WHERE id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, model.getName());
                ps.setString(2, model.getSection());
                ps.setString(3, model.getSubject());
                ps.setString(4, model.getInstructor());
                ps.setInt(5, model.getId());
                return ps.executeUpdate() > 0; 
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false; 
            }
        }

        boolean delete(int id) {
            try (Connection con = Database.getConnection()) {
                String sql = "DELETE FROM studentsrecord WHERE id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, id);
                return ps.executeUpdate() > 0; 
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false; 
            }
        }

        studModel search(int id) {
            try (Connection con = Database.getConnection()) {
                String sql = "SELECT * FROM studentsrecord WHERE id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    studModel student = new studModel();
                    student.setId(rs.getInt("id"));
                    student.setName(rs.getString("name"));
                    student.setSection(rs.getString("section"));
                    student.setSubject(rs.getString("subject"));
                    student.setInstructor(rs.getString("instructor"));
                    return student;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }
    boolean validateIdField() {
            String idText = view.txtID.getText();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Please enter a Student ID.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }
    
        List<studModel> getAllStudents(){
            List<studModel> students = new ArrayList<>();
            try (Connection con = Database.getConnection()) {
                String sql = "SELECT * FROM studentsrecord";
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    studModel student = new studModel();
                    student.setId(rs.getInt("id"));
                    student.setName(rs.getString("name"));
                    student.setSection(rs.getString("section"));
                    student.setSubject(rs.getString("subject"));
                    student.setInstructor(rs.getString("instructor"));
                    students.add(student);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return students;
        }
    
}
