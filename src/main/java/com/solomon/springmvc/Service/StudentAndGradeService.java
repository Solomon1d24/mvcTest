package com.solomon.springmvc.Service;


import com.solomon.springmvc.models.CollegeStudent;
import com.solomon.springmvc.models.GradebookCollegeStudent;

public interface StudentAndGradeService {
    public void createStudent(String firstName, String lastName, String emailAddress);

    public boolean checkIfStudentIsNull(int id);

    public void deleteStudent(int id);

    public Iterable<CollegeStudent> getGradeBooks();

    public boolean createGrade(double grade, int studentId, String gradeType);

    public int deleteGrade(int id, String gradeType);

    public GradebookCollegeStudent studentInformation(int studentId);

}
