package com.solomon.springmvc.Dao;

import com.solomon.springmvc.models.MathGrade;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("mathGradeDao")
@Scope("prototype")
public interface MathGradeDao extends CrudRepository<MathGrade,Integer> {
    public Iterable<MathGrade> findGradeByStudentId(int id);

    public void deleteMathGradeByStudentId(int id);
}
