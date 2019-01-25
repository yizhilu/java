package com.hc.solr.repository.secondary;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hc.solr.entity.TestEntity;

@Repository("testRepository")
public interface TestRepository
    extends
      JpaRepository<TestEntity, String>,
      JpaSpecificationExecutor<TestEntity> {
  @Query(nativeQuery = true, value = "select *from test where num>=:i and num<:j")
  List<TestEntity> findByNumCond(@Param("i") int i, @Param("j") int j);

  @Query(nativeQuery = true, value = "select *from test where create_date>=DATE_FORMAT(:start,'%Y-%m-%d %H:%i:%S') and create_date<DATE_FORMAT(:now,'%Y-%m-%d %H:%i:%S')")
  List<TestEntity> findByNumCond(@Param("start") Date start, @Param("now") Date now);
  
  @Query(nativeQuery = true, value = "select *from test where create_date>=:start and create_date<:now")
  List<TestEntity> findByNumCond2(@Param("start") Date start, @Param("now") Date now);
}
