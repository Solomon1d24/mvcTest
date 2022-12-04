package com.solomon.springmvc;

import com.solomon.springmvc.Dao.HistoryGradeDao;
import com.solomon.springmvc.Dao.MathGradeDao;
import com.solomon.springmvc.Dao.ScienceGradeDao;
import com.solomon.springmvc.Dao.StudentDao;
import com.solomon.springmvc.Service.StudentAndGradeService;
import com.solomon.springmvc.models.*;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@TestPropertySource("/application-test.properties")
public class StudentAndGradeServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("StudentAndGradeServiceImpl")
    private StudentAndGradeService studentService;

    @Autowired
    @Qualifier("StudentDao")
    private StudentDao studentDao;

    @Autowired
    @Qualifier("mathGradeDao")
    private MathGradeDao mathGradeDao;

    @Autowired
    @Qualifier("scienceGradeDao")
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    @Qualifier("historyGradeDao")
    private HistoryGradeDao historyGradeDao;

    @Value("${sql.script.create.student}")
    private String sqlAddStudent;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.create.mathgrade}")
    private String sqlCreateMathGrade;

    @Value("${sql.script.create.sciencegrade}")
    private String sqlCreateScienceGrade;

    @Value("${sql.script.create.historygrade}")
    private String sqlCreateHistoryGrade;

    @Value("${sql.script.delete.mathgrade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.sciencegrade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.historygrade}")
    private String sqlDeleteHistoryGrade;

    @BeforeEach
    public void setUp() {
        this.jdbcTemplate.execute(sqlAddStudent);
        this.jdbcTemplate.execute(sqlCreateMathGrade);
        this.jdbcTemplate.execute(sqlCreateScienceGrade);
        this.jdbcTemplate.execute(sqlCreateHistoryGrade);
    }

    @AfterEach
    public void clearUp() {
        this.jdbcTemplate.execute(sqlDeleteStudent);
        this.jdbcTemplate.execute(sqlDeleteMathGrade);
        this.jdbcTemplate.execute(sqlDeleteScienceGrade);
        this.jdbcTemplate.execute(sqlDeleteHistoryGrade);
    }

    @Test
    @DisplayName("Create Student Service")
    public void createStudentService() {
        this.jdbcTemplate.execute("DELETE FROM student");
        this.studentService.createStudent("Solomon", "Chow", "solomon1d24@gmail.com");
        CollegeStudent student = this.studentDao.findByEmailAddress("solomon1d24@gmail.com");
        Assertions.assertEquals("solomon1d24@gmail.com", student.getEmailAddress(), "find by email");
    }

    @Test
    @DisplayName("Check if student is null")
    public void checkIfStudentIsNull() {
        Assertions.assertTrue(this.studentService.checkIfStudentIsNull(1));
        Assertions.assertFalse(this.studentService.checkIfStudentIsNull(0));
    }

    @Test
    @DisplayName("Check if student is deleted")
    public void deleteStudentTest() {
        Optional<CollegeStudent> collegeStudentOptional = this.studentDao.findById(1);
        Optional<MathGrade> mathGrade = this.mathGradeDao.findById(1);
        Optional<ScienceGrade> scienceGrade = this.scienceGradeDao.findById(1);
        Optional<HistoryGrade> historyGrade = this.historyGradeDao.findById(1);

        Assertions.assertTrue(collegeStudentOptional.isPresent());
        Assertions.assertTrue(mathGrade.isPresent());
        Assertions.assertTrue(scienceGrade.isPresent());
        Assertions.assertTrue(historyGrade.isPresent());

        this.studentService.deleteStudent(1);
        Optional<CollegeStudent> collegeStudentOptional2 = this.studentDao.findById(1);
        Optional<MathGrade> mathGrade2 = this.mathGradeDao.findById(1);
        Optional<ScienceGrade> scienceGrade2 = this.scienceGradeDao.findById(1);
        Optional<HistoryGrade> historyGrade2 = this.historyGradeDao.findById(1);
        Assertions.assertFalse(collegeStudentOptional2.isPresent());
        Assertions.assertFalse(mathGrade2.isPresent());
        Assertions.assertFalse(scienceGrade2.isPresent());
        Assertions.assertFalse(historyGrade2.isPresent());
    }

    @Test
    @DisplayName("Test get grade books")
    @Sql("/insertData.sql")
    public void testGetGradeBookService() {
        Iterable<CollegeStudent> collegeStudents = this.studentService.getGradeBooks();
        List<CollegeStudent> collegeStudentList = new ArrayList<>();
        collegeStudents.forEach(collegeStudent -> {
            collegeStudentList.add(collegeStudent);
        });

        Assertions.assertEquals(4, collegeStudentList.size(), "Should equals to 4");
    }

    @Test
    @DisplayName("Test Create Grades")
    public void testCreateMathGrade() {

        // Create the grade
        Assertions.assertTrue(this.studentService.createGrade(80.5, 1, "math"));
        Assertions.assertTrue(this.studentService.createGrade(80.5, 1, "science"));
        Assertions.assertTrue(this.studentService.createGrade(80.5, 1, "history"));

        // Get all the grades with student id
        Iterable<MathGrade> mathGrades = this.mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = this.scienceGradeDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = this.historyGradeDao.findGradeByStudentId(1);

        // Verify there is grades
        Assertions.assertTrue(((Collection<MathGrade>) mathGrades).size() == 2, "Student has 2 math grades");
        Assertions.assertTrue(((Collection<ScienceGrade>) scienceGrades).size() == 2, "Student has 2 science grades");
        Assertions.assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 2, "Student has 2 history grades");
    }

    @Test
    @DisplayName("Test Create Grades return false")
    public void testCreateGradeReturnFalse() {
        Assertions.assertFalse(this.studentService.createGrade(80.5, 1, "sport"));
        Assertions.assertFalse(this.studentService.createGrade(-80.5, 1, "math"));
        Assertions.assertFalse(this.studentService.createGrade(80.5, 2, "math"));
        Assertions.assertFalse(this.studentService.createGrade(1000, 1, "math"));
    }

    @Test
    @DisplayName("Test delete the grades")
    public void testDeleteGrades() {
        Assertions.assertEquals(1, this.studentService.deleteGrade(1, "math"), "Return student id after deletion");
        Assertions.assertEquals(1, this.studentService.deleteGrade(1, "history"), "Return student id after deletion");
        Assertions.assertEquals(1, this.studentService.deleteGrade(1, "science"), "Return student id after deletion");
    }

    @Test
    @DisplayName("Test delete grades return student id")
    public void testDeleteGradesReturnZeroStudentId() {
        Assertions.assertEquals(0, this.studentService.deleteGrade(0, "math"), "There is no math grade with id zero");
        Assertions.assertEquals(0, this.studentService.deleteGrade(1, "literature"), "There is no history grade");
    }

    @Test
    @DisplayName("Test student information")
    public void testStudentInformation() {
        GradebookCollegeStudent gradebookCollegeStudent = this.studentService.studentInformation(1);
        Assertions.assertNotNull(gradebookCollegeStudent);
        Assertions.assertEquals(1, gradebookCollegeStudent.getId());
        Assertions.assertEquals("Solomon", gradebookCollegeStudent.getFirstname());
        Assertions.assertEquals("Chow", gradebookCollegeStudent.getLastname());
        Assertions.assertEquals("solomon1d24@gmail.com", gradebookCollegeStudent.getEmailAddress());

        Assertions.assertEquals(
                1,
                gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size());
        Assertions.assertEquals(
                1,
                gradebookCollegeStudent
                        .getStudentGrades()
                        .getHistoryGradeResults()
                        .size());
        Assertions.assertEquals(
                1,
                gradebookCollegeStudent
                        .getStudentGrades()
                        .getScienceGradeResults()
                        .size());
    }

    @Test
    @DisplayName("test student information return null")
    public void testStudentInformationReturnNull(){
        GradebookCollegeStudent gradebookCollegeStudent = this.studentService.studentInformation(0);
        Assertions.assertNull(gradebookCollegeStudent);
    }





}
