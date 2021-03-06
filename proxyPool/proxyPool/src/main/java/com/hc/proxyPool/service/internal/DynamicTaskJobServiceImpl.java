package com.hc.proxyPool.service.internal;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hc.proxyPool.entity.DynameicTaskJobEntity;
import com.hc.proxyPool.repository.DynameicTaskJobRepository;
import com.hc.proxyPool.service.DynamicTaskJobService;

@Service("dynamicTaskJobService")
public class DynamicTaskJobServiceImpl implements DynamicTaskJobService {
  @Autowired
  private DynameicTaskJobRepository dynameicTaskJobRepository;

  @Override
  public List<DynameicTaskJobEntity> findAll() {

    return dynameicTaskJobRepository.findAll();
  }

  @Override
  @Transactional
  public DynameicTaskJobEntity create(DynameicTaskJobEntity dynameicTaskJob) {
    String jobName = dynameicTaskJob.getJobName();
    Validate.notBlank(jobName, "jobName不能为空");
    String cron = dynameicTaskJob.getCron();
    Validate.notBlank(cron, "cron不能为空");
    DynameicTaskJobEntity old = dynameicTaskJobRepository.findByJobName(jobName);
    Validate.isTrue(old == null, "jobName为%s的任务已存在", jobName);
    dynameicTaskJob.setStop(false);
    return dynameicTaskJobRepository.save(dynameicTaskJob);
  }

  @Override
  @Transactional
  public DynameicTaskJobEntity update(DynameicTaskJobEntity dynameicTaskJob) {
    String id = dynameicTaskJob.getId();
    Validate.notBlank(id, "id不能为空");
    String jobName = dynameicTaskJob.getJobName();
    Validate.notBlank(jobName, "jobName不能为空");
    DynameicTaskJobEntity old = dynameicTaskJobRepository.findByJobName(jobName);
    Validate.notNull(old, "jobName为%s的任务已存在", jobName);
    String cron = dynameicTaskJob.getCron();
    Validate.notBlank(cron, "cron不能为空");
    old = dynameicTaskJobRepository.findOne(id);
    Validate.notNull(old, "id为%s的任务不存在", id);
    old.setJobName(jobName);
    old.setCron(cron);
    old.setDesc(dynameicTaskJob.getDesc());
    return dynameicTaskJobRepository.saveAndFlush(old);
  }

  @Override
  @Transactional
  public DynameicTaskJobEntity stop(DynameicTaskJobEntity dynameicTaskJob) {
    String id = dynameicTaskJob.getId();
    Validate.notBlank(id, "id不能为空");
    DynameicTaskJobEntity old = dynameicTaskJobRepository.findOne(id);
    old.setStop(true);
    return dynameicTaskJobRepository.saveAndFlush(old);
  }

  @Override
  @Transactional
  public DynameicTaskJobEntity start(DynameicTaskJobEntity dynameicTaskJob) {
    String id = dynameicTaskJob.getId();
    Validate.notBlank(id, "id不能为空");
    DynameicTaskJobEntity old = dynameicTaskJobRepository.findOne(id);
    old.setStop(false);
    return dynameicTaskJobRepository.saveAndFlush(old);
  }

  @Override
  public DynameicTaskJobEntity findById(String id) {
    Validate.notBlank(id, "id不能为空");

    return dynameicTaskJobRepository.findOne(id);
  }

  @Override
  public DynameicTaskJobEntity findByJobName(String jobName) {
    Validate.notBlank(jobName, "jobName不能为空");

    return dynameicTaskJobRepository.findByJobName(jobName);
  }

  @Override
  public Page<DynameicTaskJobEntity> findByConditions(String jobName, Pageable page) {
    return dynameicTaskJobRepository.findAll(new Specification<DynameicTaskJobEntity>() {

      @Override
      public Predicate toPredicate(Root<DynameicTaskJobEntity> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (StringUtils.isNotBlank(jobName)) {
          predicates.add(cb.like(root.get("jobName"), jobName));
        }
        query.where(predicates.toArray(new Predicate[predicates.size()]));
        return null;
      }

    }, page); 
  }



}
