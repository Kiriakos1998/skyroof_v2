package com.intrasoft.skyroof.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.intrasoft.skyroof.persistence.model.Task;
import com.intrasoft.skyroof.persistence.model.TaskId;


public interface TaskDao extends JpaRepository<Task, TaskId>{

	@Query("select COUNT(t.projectId)>0 from task t where t.projectId = ?1")
	Boolean hasProjectTasks(Long projectId);
	
	
	List<Task> findByProjectId(Long projectId);
}
