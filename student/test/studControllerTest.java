
import javax.swing.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class studControllerTest {
    private studView view;
    private studController controller;

    @Before
    public void setUp() {
        view = new studView();
        controller = new studController(view);
    }

    @Test
    public void testaddStud() {
        view.txtNAME.setText("John Doe");
        view.txtSECT.setText("A1");
        view.txtSUB.setText("Math");
        view.txtINS.setText("Mr. Smith");
        
        view.btnAdd.doClick(); 
        view.btnDisplay.doClick();
        validateStudentInTable("John Doe", "A1", "Math", "Mr. Smith");
    }

    @Test
    public void testupdateStud() {
        testaddStud();

        List<studModel> students = controller.getAllStudents();
        studModel studentToUpdate = students.stream()
            .filter(student -> student.getName().equals("John Doe"))
            .findFirst()
            .orElse(null);
        
        assertNotNull("Student should exist to update", studentToUpdate);

        view.txtID.setText(String.valueOf(studentToUpdate.getId()));
        view.txtNAME.setText("John Smith");
        view.txtSECT.setText("A2");
        view.txtSUB.setText("Science");
        view.txtINS.setText("Ms. Johnson");

        view.btnUpdate.doClick();  
        view.btnDisplay.doClick();
        validateStudentInTable("John Smith", "A2", "Science", "Ms. Johnson");
    }

    @Test
    public void testdeleteStud() {
         testaddStud();

        List<studModel> students = controller.getAllStudents();
        studModel studentToDelete = students.stream()
            .filter(student -> student.getName().equals("John Doe"))
            .findFirst()
            .orElse(null);
        
        assertNotNull("Student should exist to delete", studentToDelete);

        view.txtID.setText(String.valueOf(studentToDelete.getId()));
        view.btnDelete.doClick();  

        view.btnDisplay.doClick();
        assertNull("Student should be deleted", controller.getAllStudents().stream()
            .filter(student -> student.getId() == studentToDelete.getId())
            .findFirst()
            .orElse(null));
    }

    @Test
    public void testsearchStud() {
         testaddStud();

        List<studModel> students = controller.getAllStudents();
        studModel studentToSearch = students.stream()
            .filter(student -> student.getName().equals("John Doe"))
            .findFirst()
            .orElse(null);

        assertNotNull("Student should exist to search", studentToSearch);

        view.txtID.setText(String.valueOf(studentToSearch.getId()));
        view.btnSearch.doClick();

        assertEquals("Search result name should match", "John Doe", view.txtNAME.getText());
        assertEquals("Search result section should match", "A1", view.txtSECT.getText());
        assertEquals("Search result subject should match", "Math", view.txtSUB.getText());
        assertEquals("Search result instructor should match", "Mr. Smith", view.txtINS.getText());
    }

    @Test
    public void testdisplayStud() {
        testaddStud(); 
        
        view.btnDisplay.doClick(); 
        DefaultTableModel model = (DefaultTableModel) view.jTable1.getModel();
        assertEquals("Table should contain one student", 1 ,model.getRowCount());
        assertEquals("Name should match", "John Doe", model.getValueAt(0, 1));
    }

    @Test
    public void testValidationForIdField() {
        view.txtID.setText(""); 
        assertFalse("Validation should fail for empty ID", controller.validateIdField());
    }

    private void validateStudentInTable(String name, String section, String subject, String instructor) {
        List<studModel> students = controller.getAllStudents();
        studModel addedStudent = students.stream()
            .filter(student -> student.getName().equals(name))
            .findFirst()
            .orElse(null);

        assertNotNull("Student should be in the table", addedStudent);
        assertEquals("Section should match", section, addedStudent.getSection());
        assertEquals("Subject should match", subject, addedStudent.getSubject());
        assertEquals("Instructor should match", instructor, addedStudent.getInstructor());
    }
}
