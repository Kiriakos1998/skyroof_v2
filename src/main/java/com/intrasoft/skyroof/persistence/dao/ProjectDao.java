package com.intrasoft.skyroof.persistence.dao;

import com.intrasoft.skyroof.persistence.model.Project;
import com.intrasoft.skyroof.persistence.model.Task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectDao extends JpaRepository<Project, Long> {

	@Query("SELECT COUNT(p.projectId) FROM project p JOIN task t on t.projectId=p.projectId WHERE p.projectId=?1 AND t.state=?2" )
	Integer tasksState(Long projectId, Task.State state);
	
}
