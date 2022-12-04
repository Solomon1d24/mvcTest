package com.solomon.springmvc.Dao;

import com.solomon.springmvc.models.CollegeStudent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Qualifier("StudentDao")
@Repository
public interface StudentDao extends CrudRepository<CollegeStudent,Integer> {
    public CollegeStudent findByEmailAddress(String emailAddress);

}
