package com.solomon.springmvc.controller;

import com.solomon.springmvc.Service.StudentAndGradeService;
import com.solomon.springmvc.models.*;
import com.solomon.springmvc.models.Gradebook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

    @Autowired
    private Gradebook gradebook;

    @Autowired
    private StudentAndGradeService studentAndGradeService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getStudents(Model m) {
        Iterable<CollegeStudent> collegeStudents = this.studentAndGradeService.getGradeBooks();
        m.addAttribute("students", collegeStudents);
        return "index";
    }

    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id, Model m) {
        if (!this.studentAndGradeService.checkIfStudentIsNull(id)) {
            return "error";
        }

        return "studentInformation";
    }

    @PostMapping(value = "/")
    public String createStudent(@ModelAttribute("student") CollegeStudent collegeStudent, Model model) {
        this.studentAndGradeService.createStudent(
                collegeStudent.getFirstname(), collegeStudent.getLastname(), collegeStudent.getEmailAddress());
        Iterable<CollegeStudent> collegeStudents = this.studentAndGradeService.getGradeBooks();
        model.addAttribute("students", collegeStudents);

        return "index";
    }

    @GetMapping("delete/student/{id}")
    public String deleteStudent(@PathVariable int id, Model model) {
        if (!studentAndGradeService.checkIfStudentIsNull(id)) {
            return "error";
        }
        this.studentAndGradeService.deleteStudent(id);
        Iterable<CollegeStudent> collegeStudents = this.studentAndGradeService.getGradeBooks();
        model.addAttribute("students", collegeStudents);
        return "index";
    }

    @PostMapping(value = "/grades")
    public String createGrade(
            @RequestParam("grade") double grade,
            @RequestParam("gradeType") String gradeType,
            @RequestParam("studentId") int studentId,
            Model model) {
        if (!this.studentAndGradeService.checkIfStudentIsNull(studentId)) {
            return "error";
        }
        boolean success = this.studentAndGradeService.createGrade(grade, studentId, gradeType);

        if (!success) {
            return "error";
        }

        configurateStudentInformation(studentId, model);
        return "studentInformation";
    }

    @GetMapping("/grades/{id}/{gradeType}")
    public String deleteGrade(@PathVariable int id, @PathVariable String gradeType){
        int studentId = this.studentAndGradeService.deleteGrade(id,gradeType);
        if(studentId == 0){
            return "error";
        }
        return "studentInformation";
    }


    private void configurateStudentInformation(int studentId, Model model) {
        GradebookCollegeStudent student = this.studentAndGradeService.studentInformation(studentId);

        model.addAttribute("student", student);

        if (student.getStudentGrades().getMathGradeResults().size() > 0) {
            model.addAttribute(
                    "mathAverage",
                    student.getStudentGrades()
                            .findGradePointAverage(student.getStudentGrades().getMathGradeResults()));
        } else {
            model.addAttribute("mathAverage", "N/A");
        }
        if (student.getStudentGrades().getScienceGradeResults().size() > 0) {
            model.addAttribute(
                    "mathAverage",
                    student.getStudentGrades()
                            .findGradePointAverage(student.getStudentGrades().getScienceGradeResults()));
        } else {
            model.addAttribute("scienceAverage", "N/A");
        }
        if (student.getStudentGrades().getHistoryGradeResults().size() > 0) {
            model.addAttribute(
                    "historyAverage",
                    student.getStudentGrades()
                            .findGradePointAverage(student.getStudentGrades().getHistoryGradeResults()));
        } else {
            model.addAttribute("historyAverage", "N/A");
        }
    }
}
