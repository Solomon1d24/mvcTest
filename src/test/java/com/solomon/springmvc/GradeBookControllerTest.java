package com.solomon.springmvc;

import com.solomon.springmvc.Dao.MathGradeDao;
import com.solomon.springmvc.Dao.StudentDao;
import com.solomon.springmvc.Service.StudentAndGradeService;
import com.solomon.springmvc.models.CollegeStudent;
import com.solomon.springmvc.models.GradebookCollegeStudent;
import com.solomon.springmvc.models.MathGrade;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class GradeBookControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Mock
    private StudentAndGradeService studentCreateServiceMock;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Value("${sql.script.create.student}")
    private String addStudentSql;
    @Value("${sql.script.delete.student}")
    private String deleteStudentSql;
    @Value("${sql.script.create.mathgrade}")
    private String addMathGradeSql;

    @Value("${sql.script.delete.mathgrade}")
    private String deleteMathGradeSql;

    @Value("${sql.script.create.sciencegrade}")
    private String sqlCreateScienceGrade;

    @Value("${sql.script.delete.sciencegrade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.create.historygrade}")
    private String sqlCreateHistoryGrade;

    @Value("${sql.script.delete.historygrade}")
    private String sqlDeleteHistoryGrade;


    @BeforeAll
    public static void setUpAll() {
        request = new MockHttpServletRequest();
        request.setParameter("firstName", "Solomon");
        request.setParameter("lastName", "Chow");
        request.setParameter("emailAddress", "solo5698@connect.hku.hk");
    }

    @BeforeEach
    public void setUp() {
        this.jdbcTemplate.execute(addStudentSql);
        this.jdbcTemplate.execute(addMathGradeSql);
        this.jdbcTemplate.execute(sqlCreateScienceGrade);
        this.jdbcTemplate.execute(sqlCreateHistoryGrade);
    }

    @AfterEach
    public void clearUp() {
        this.jdbcTemplate.execute(deleteStudentSql);
        this.jdbcTemplate.execute(deleteMathGradeSql);
        this.jdbcTemplate.execute(sqlDeleteScienceGrade);
        this.jdbcTemplate.execute(sqlDeleteHistoryGrade);
    }

    @Test
    @DisplayName("Get student http request")
    public void getStudentsHttpRequest() throws Exception {

        CollegeStudent student1 = new CollegeStudent("Katrina", "Hon", "kathon@gmail.com");
        CollegeStudent student2 = new CollegeStudent("Solomon", "Chow", "solomon1d24@gmail.com");

        List<CollegeStudent> studentList = new ArrayList<>(Arrays.asList(student1, student2));

        Mockito.when(this.studentCreateServiceMock.getGradeBooks()).thenReturn(studentList);

        Assertions.assertEquals(studentList, studentCreateServiceMock.getGradeBooks());

        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");
    }

    @Test
    @DisplayName("Post student http request")
    public void postStudentHttpRequest() throws Exception {
        CollegeStudent collegeStudent = new CollegeStudent("Tom", "Wong", "tomwong@hku.com");
        List<CollegeStudent> collegeStudents = new ArrayList<>(Arrays.asList(collegeStudent));
        Mockito.when(this.studentCreateServiceMock.getGradeBooks()).thenReturn(collegeStudents);
        Assertions.assertEquals(collegeStudents, this.studentCreateServiceMock.getGradeBooks());
        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", request.getParameterValues("firstName"))
                        .param("lastName", request.getParameterValues("lastName"))
                        .param("emailAddress", request.getParameterValues("emailAddress")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");

        CollegeStudent student = this.studentDao.findByEmailAddress("solo5698@connect.hku.hk");

        Assertions.assertNotNull(student, "Student should be found");
    }

    @Test
    @DisplayName("Test if the student is deleted")
    public void testStudentIsDeleted() throws Exception {
        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/delete/student/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");

        Assertions.assertFalse(this.studentDao.findById(1).isPresent());
    }

    @Test
    @DisplayName("Test if the student doesn't existing is being deleted")
    public void testDeleteStudentHttpErrorPage() throws Exception {
        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/delete/student/{id}", 0))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    @DisplayName("Test student information request")
    public void testStudentInformationHttpRequest() throws Exception {
        Assertions.assertTrue(this.studentDao.findById(1).isPresent());

        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/studentInformation/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "studentInformation");
    }

    @Test
    @DisplayName("Test student information request failure")
    public void testStudentInformationFailure() throws Exception {
        Assertions.assertFalse(this.studentDao.findById(0).isPresent());

        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/studentInformation/{id}", 0))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    @DisplayName("Test for creating new grades")
    public void testCreatingNewGrades() throws Exception {
        Assertions.assertTrue(this.studentDao.findById(1).isPresent());

        GradebookCollegeStudent student = this.studentService.studentInformation(1);

        Assertions.assertTrue(this.studentService.checkIfStudentIsNull(1));

        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "87.00")
                        .param("gradeType", "math")
                        .param("studentId", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "studentInformation");

        student = this.studentService.studentInformation(1);

        Assertions.assertEquals(
                2, student.getStudentGrades().getMathGradeResults().size());
    }

    @Test
    @DisplayName("Test creating grade with invalid student")
    public void testCreatingGradeWithInvalidStudent() throws Exception {
        Assertions.assertFalse(this.studentDao.findById(0).isPresent());

        Assertions.assertFalse(this.studentService.checkIfStudentIsNull(0));

        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "76.00")
                        .param("gradeType", "math")
                        .param("studentId", "0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    @DisplayName("Test creating grade with invalid grade")
    public void testCreatingGradeWithInvalidGrade() throws Exception {
        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "90.1")
                        .param("gradeType", "literature")
                        .param("studentId", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    @DisplayName("Test delete grade request")
    public void testDeleteGradeHttpRequest() throws Exception {
        Optional<MathGrade> grade = this.mathGradeDao.findById(1);
        Assertions.assertTrue(grade.isPresent());
        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 1, "math"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "studentInformation");
        Assertions.assertFalse(this.mathGradeDao.findById(1).isPresent());
    }

    @Test
    @DisplayName("Test delete invalid grade id")
    public void testDeleteInvalidGradeIdRequest() throws Exception {

        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 10, "math"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        ModelAndView modelAndView = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }

    @Test
    @DisplayName("Test delete invalid grade type")
    public void testDelelteInvalidGradeTypeRequest() throws Exception {
        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 1, "literature"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        ModelAndView modelAndView  = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(modelAndView,"error");
    }
}
