package com.solomon.springmvc.Service;

import com.solomon.springmvc.Dao.HistoryGradeDao;
import com.solomon.springmvc.Dao.MathGradeDao;
import com.solomon.springmvc.Dao.ScienceGradeDao;
import com.solomon.springmvc.Dao.StudentDao;
import com.solomon.springmvc.models.*;
import org.hibernate.internal.util.MathHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("StudentAndGradeServiceImpl")
@Transactional
public class StudentAndGradeServiceImpl implements StudentAndGradeService {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;

    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;

    @Autowired
    private StudentGrades studentGrades;

    @Override
    public void createStudent(String firstName, String lastName, String emailAddress) {
        CollegeStudent student = new CollegeStudent(firstName, lastName, emailAddress);
        student.setId(0);
        this.studentDao.save(student);
    }

    public boolean checkIfStudentIsNull(int id) {
        Optional<CollegeStudent> student = this.studentDao.findById(id);
        if (student.isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteStudent(int id) {
        if (this.checkIfStudentIsNull(id)) {
            this.studentDao.deleteById(id);
            this.scienceGradeDao.deleteScienceGradeByStudentId(id);
            this.mathGradeDao.deleteMathGradeByStudentId(id);
            this.historyGradeDao.deleteHistoryGradeByStudentId(id);
        }
    }

    @Override
    public Iterable<CollegeStudent> getGradeBooks() {
        return this.studentDao.findAll();
    }

    @Override
    public boolean createGrade(double grade, int studentId, String gradeType) {
        if (!this.checkIfStudentIsNull(studentId)) {
            return false;
        }
        if ((grade >= 0 && grade <= 100)) {
            switch (gradeType) {
                case "math":
                    this.mathGrade.setId(0);
                    this.mathGrade.setGrade(grade);
                    this.mathGrade.setStudentId(studentId);
                    this.mathGradeDao.save(mathGrade);
                    return true;
                case "science":
                    this.scienceGrade.setId(0);
                    this.scienceGrade.setGrade(grade);
                    this.scienceGrade.setStudentId(studentId);
                    this.scienceGradeDao.save(scienceGrade);
                    return true;
                case "history":
                    this.historyGrade.setId(0);
                    this.historyGrade.setGrade(grade);
                    this.historyGrade.setStudentId(studentId);
                    this.historyGradeDao.save(historyGrade);
                    return true;
                default:
                    return false;
            }
        }

        return false;
    }

    public int deleteGrade(int id, String gradeType) {
        int studentId = 0;
        switch (gradeType) {
            case "math":
                Optional<MathGrade> grade = this.mathGradeDao.findById(id);
                if (!grade.isPresent()) {
                    return studentId;
                }
                studentId = grade.get().getStudentId();
                this.mathGradeDao.deleteById(id);
                return studentId;
            case "history":
                Optional<HistoryGrade> grade1 = this.historyGradeDao.findById(id);
                if (!grade1.isPresent()) {
                    return studentId;
                }
                studentId = grade1.get().getStudentId();
                this.historyGradeDao.deleteById(id);
                return studentId;
            case "science":
                Optional<ScienceGrade> grade2 = this.scienceGradeDao.findById(id);
                if (!grade2.isPresent()) {
                    return studentId;
                }
                studentId = grade2.get().getStudentId();
                this.scienceGradeDao.deleteById(id);
                return studentId;
            default:
                return studentId;
        }
    }

    @Override
    public GradebookCollegeStudent studentInformation(int studentId) {
        Optional<CollegeStudent> collegeStudent = this.studentDao.findById(studentId);

        if (!collegeStudent.isPresent()) {
            return null;
        }

        Iterable<MathGrade> mathGrade = this.mathGradeDao.findGradeByStudentId(studentId);
        Iterable<ScienceGrade> scienceGrades = this.scienceGradeDao.findGradeByStudentId(studentId);
        Iterable<HistoryGrade> historyGrades = this.historyGradeDao.findGradeByStudentId(studentId);

        List<Grade> mathGradeList = new ArrayList<>();
        List<Grade> historyGradeList = new ArrayList<>();
        List<Grade> scienceGradeList = new ArrayList<>();

        mathGrade.forEach(mathGradeList::add);
        historyGrades.forEach(historyGradeList::add);
        scienceGrades.forEach(scienceGradeList::add);

        this.studentGrades.setHistoryGradeResults(historyGradeList);
        this.studentGrades.setScienceGradeResults(scienceGradeList);
        this.studentGrades.setMathGradeResults(mathGradeList);

        GradebookCollegeStudent gradebookCollegeStudent = new GradebookCollegeStudent(
                collegeStudent.get().getId(),
                collegeStudent.get().getFirstname(),
                collegeStudent.get().getLastname(),
                collegeStudent.get().getEmailAddress(),
                this.studentGrades);

        return gradebookCollegeStudent;
    }
}
