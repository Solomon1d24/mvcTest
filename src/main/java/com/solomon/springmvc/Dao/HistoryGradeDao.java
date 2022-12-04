package com.solomon.springmvc.Dao;

import com.solomon.springmvc.models.HistoryGrade;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
@Qualifier("historyGradeDao")
@Scope("prototype")
public interface HistoryGradeDao extends CrudRepository<HistoryGrade, Integer> {

    public Iterable<HistoryGrade> findGradeByStudentId(int id);

    public void deleteHistoryGradeByStudentId(int id);

}
