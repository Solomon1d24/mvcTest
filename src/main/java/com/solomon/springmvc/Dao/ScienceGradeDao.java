package com.solomon.springmvc.Dao;

import com.solomon.springmvc.models.ScienceGrade;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("scienceGradeDao")
@Scope("prototype")
public interface ScienceGradeDao extends CrudRepository<ScienceGrade,Integer> {

    public Iterable<ScienceGrade> findGradeByStudentId(int id);

    public void deleteScienceGradeByStudentId(int id);
}
