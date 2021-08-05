package com.intrasoft.skyroof.rest.controller;

import com.intrasoft.skyroof.persistence.dao.ProjectDao;
import com.intrasoft.skyroof.persistence.dao.TaskDao;
import com.intrasoft.skyroof.persistence.model.Project;
import com.intrasoft.skyroof.persistence.model.Task;
import com.intrasoft.skyroof.persistence.model.Task.State;
import com.intrasoft.skyroof.persistence.model.TaskId;
import com.intrasoft.skyroof.rest.RestConfig;
import com.intrasoft.skyroof.utils.MyMessages;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.time.LocalDateTime;


@RestController
@CrossOrigin
@RequestMapping(path = RestConfig.API_URI + "/projects",
                produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
                consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Transactional
public class ProjectResource {

	org.jboss.logging.Logger logger = LoggerFactory.logger(ProjectResource.class);

	
    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    private TaskDao taskDao;

    @PostMapping
    public ResponseEntity<Project> create(@RequestBody Project project) {
    	// Check if the project Id is null 
        if (isNull(project.getProjectId())) {
           // if it is then check if the title is nonNull
        	if(nonNull(project.getTitle()) )
        		   {
        	   // save the project object
        		logger.info("New projct has been added with title:" + project.getTitle() );
        		return ResponseEntity.ok(projectDao.save(project));
        }
           else {
        	   // throw exception because project title can't be null
        	   logger.error("Failed to create new project due to null title");
        	   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,MyMessages.PROJECT_NULL_TITLE);
           }
        }
        // throw exception because project_id is being handled by the application and not the user so it has to be null
        else{
        	logger.error("Failed to create new project because of non null projectId");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,MyMessages.PROJECT_ID_NON_NULL);
        }
    }
    
    

    @PutMapping
    public ResponseEntity<Project> update(@RequestBody Project project) {
        //check if the projectId is non null
    	if (nonNull(project.getProjectId())) {
            // try to find the project with the specified id and if you can't find it throw exception
    		Project found = projectDao.findById(project.getProjectId())//
               .orElseThrow(() ->{ logger.error("Failed because project with projectId:"+project.getProjectId().toString()+" doesn't exist");
                  return new ResponseStatusException(HttpStatus.NOT_FOUND,MyMessages.PROJECT_DOESNT_EXIST); } );
          // else set title and description and save 
    		logger.info("Project with projectId:"+ found.getProjectId().toString()+" was updated");
    		if(nonNull(project.getTitle()))
    		found.setTitle(project.getTitle());
    		
            found.setDescription(project.getDescription());
            return ResponseEntity.ok(projectDao.save(found));
        }
    	// else throw exception because the id is null
        else{
        	logger.error("Failed to update project because of null projectId " );
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,MyMessages.PROJECT_ID_NULL);
        }
    }

    
    @DeleteMapping(path = "/{projectId}")
    public ResponseEntity<Project> deleteById(@PathVariable("projectId") Long projectId) {
       //try to find project by projectId and if you can't throw exception because project doesn't exist
    	Project project = projectDao.findById(projectId)//
                .orElseThrow(() -> {logger.error("Project with projectId:"+ projectId.toString()+" does not exist");
                	return new ResponseStatusException(HttpStatus.NOT_FOUND,MyMessages.PROJECT_DOESNT_EXIST);});
     // if the project exist check if there are any tasks assigned to it
     if( !taskDao.hasProjectTasks(projectId) ) {
    	// if not proceed deleting it	
    	 logger.info("Project with projectId:"+ projectId.toString()+" was deleted successfully");
    	 projectDao.delete(project);
           	return ResponseEntity.ok().build(); 
     }
     // else throw exception because a project with tasks assigned can't be deleted
     else {
    	 logger.error("Failed to delete project with projectId:"+project.getProjectId().toString()+" because there are tasks assigned to it");
    	 throw new ResponseStatusException(HttpStatus.BAD_REQUEST,MyMessages.PROJECT_STILL_HAS_TASKS);
     }
      
     
    }

    
    @GetMapping(path = "/{projectId}")
    public ResponseEntity<Project> findById(@PathVariable("projectId") Long projectId) {
    	//try to find project by projectId and if you can't throw exception because project doesn't exist
    	Project project = projectDao.findById(projectId)//
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,MyMessages.PROJECT_DOESNT_EXIST));
        //return project found
    	return ResponseEntity.ok(project);
    }

    @GetMapping
    public ResponseEntity<List<Project>> findAll() {
        return ResponseEntity.ok(projectDao.findAll());
    }
    
    @PostMapping (path="/{projectId}/tasks")
    public ResponseEntity<Task> create(@RequestBody Task task , @PathVariable Long projectId){
    	
    	task.setProjectId(projectId);
    		// check if the project exists  and the title is non null
    	if((projectDao.existsById(projectId)) && nonNull(task.getTitle())  ) {
    		// check if there is a task with the same title
    		if (taskDao.existsById(new TaskId(projectId,task.getTitle()))) {
    			
    		logger.error("Failed to create because task with projectId:" + projectId.toString() +" and title:" +task.getTitle()+ " already exists");
    			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,MyMessages.TASK_ALREADY_EXISTS);
    		}
    		else {
    			// set state to not started 
    			logger.info("Task with projectId:" + projectId.toString() +" and title:" +task.getTitle()+ " successfully created");
    			task.setState(Task.State.NOT_STARTED);
    			return ResponseEntity.ok(taskDao.save(task));
    		}
    	}
    	// throw exception because either the title is null or the project does not exist
    	else {
    		if (isNull(task.getTitle())) {
    			logger.error(" Failed to create task because title is null");
    			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,MyMessages.TASK_TITLE_NULL);
    		}
    		else {
    			logger.error("Failed to create because task with projectId:"+ projectId.toString()+" does not exist");
    			throw new ResponseStatusException(HttpStatus.NOT_FOUND,MyMessages.PROJECT_DOESNT_EXIST);
    		}
    	}
    	
    
    }

    @DeleteMapping(path="/{projectId}/tasks/{title}")
    public ResponseEntity<Task> deleteById(@PathVariable Long projectId, @PathVariable String title){
    // try find task with specified projectId and title or throw exception
    		Task task = taskDao.findById(new TaskId(projectId,title)).//
    		orElseThrow(() ->{logger.error("Failed to delete task with projectId:" + projectId.toString() +" and title:" +title+ " doest not exist");
    			return new ResponseStatusException(HttpStatus.NOT_FOUND,MyMessages.TASK_DOEST_EXIST);});
    	//check if the task is in NOT_STARTED state
    		if(task.getState()==State.NOT_STARTED) {
    		// if it is delete it
    			logger.info("Task with projectId:" + projectId.toString() +" and title:" +title+ " successfully deleted");
    			taskDao.delete(task);
    		return ResponseEntity.ok().build();
    	}
    		// else throw exception because a task can't be delete if it not in NOT_STARTED state
    	else {
    		logger.error("Failed to delete task with projectId:" + projectId.toString() +" and title:" +task.getTitle()+ " because it is not in NOT_STARTED state");
    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST,MyMessages.TASK_CANT_BE_DELETED_STATE);
    	}
    }
    

    @GetMapping(path = "/{projectId}/tasks/{title}")
    public ResponseEntity<Task> findById(@PathVariable Long projectId, @PathVariable String title) {
        // try find task with specified projectId and title or throw exception
    	Task task = taskDao.findById(new TaskId(projectId,title)).//
        		orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,MyMessages.TASK_DOEST_EXIST));
        return ResponseEntity.ok(task);
    }
    @GetMapping(path = "/{projectId}/tasks")
    public ResponseEntity<List<Task>> findAll(@PathVariable Long projectId) {
    	// check if the project exists
    	if(projectDao.existsById(projectId)) {
    	
    // take all the tasks of the project
    	List<Task> tasks = taskDao.findByProjectId(projectId);
        		
        return ResponseEntity.ok(tasks);
    }
    	// else throw exception
    else
    {
    	throw new ResponseStatusException(HttpStatus.NOT_FOUND,MyMessages.PROJECT_DOESNT_EXIST);
    }
    }
    
    @PutMapping (path="/{projectId}/tasks/{title}")
    public ResponseEntity<Task> updateById(@RequestBody Task task, @PathVariable Long projectId, @PathVariable String title){
    // take the projectId from the path and the title
    	task.setProjectId(projectId);
    	// search for the task with the specified title and 
    		Task found= taskDao.findById(new TaskId(projectId,title)).//
    				orElseThrow(()-> {logger.error("Failed to update task with projectId:" + projectId.toString() +" and title:" +title+ " doest not exist");
    					return new ResponseStatusException(HttpStatus.NOT_FOUND,MyMessages.TASK_DOEST_EXIST);});
    		// check is the title set is used by another task
    		if (!taskDao.existsById(new TaskId(projectId,task.getTitle())) || task.getTitle().equals(title) )
    		{
    		// check if the state transition is valid
    		// if the user wants to set state at NOT_STATRED it has to be at this state 
    		// else throw exception because you can't set a project from IN_PROGRESS 
    		// or from COMPLETED state to NOT_STARTED
    		if (task.getState()==Task.State.NOT_STARTED) {
    			if(found.getState()!=Task.State.NOT_STARTED) {
    				logger.error("Failed to update task with projectId:" + projectId.toString() +" and title:" +title+ " because transition from any other state"
    						+ " to NOT_STARTED is not allowed");
    				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,MyMessages.TASK_INAVALID_TRANSITION_TO_NOT_STARTED_STATE);
    			}
    			// set description and update task
    			else {
    				logger.info("Task with projectId:" + projectId.toString() +" and title:" +title+ " successfully updated");
    				found.setDescription(task.getDescription());
    				return ResponseEntity.ok(taskDao.save(found));
    			}
    			
    		}
    		// you can go to IN_PROGRESS state from either NOT_STARTED state or COMPLETED
    		// because a task can be set to COMPLETED state but after further evaluation 
    		// user may decide that is not completed
    		else if (task.getState()==Task.State.IN_PROGRESS){
    		
    			found.setState(Task.State.IN_PROGRESS);
    		// if the transition is from COMPLETED state we have to set completedDate to null again
    		// but even if it is in NOT_STARTED state completedDate should be null
    			found.setCompletedDate(null);
    			found.setDescription(task.getDescription());
    		// check if it is in NOT_STARTED state in order to set the startedDate
    			if(isNull(found.getStartDate())) {
    				found.setStartDate(LocalDateTime.now());
    			}
    			// save the task
    			return ResponseEntity.ok(taskDao.save(found));
    		}
    		//else if the user wants to set state to COMPLETED
    		else {
    			// check if it is currently at NOT_STARTED state and throw exception
    			if(found.getState()==Task.State.NOT_STARTED) {
    				logger.error("Failed to update task with projectId:" + projectId.toString() +" and title:" +title+ " because transition from NOT_STARTED state"
    						+ " to COMPLETED is not allowed");
    				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,MyMessages.TASK_INVALID_TRANSITION_TO_COMPLETED_STATE);
    				
    			}
    			// if not throw exception because a task can jump from 
    			else {
    				found.setState(Task.State.COMPLETED);
    				found.setDescription(task.getDescription());
    			// set completedDate only if it is in IN_PROGRESS state
    				if (found.getState()==Task.State.IN_PROGRESS) {
    				found.setCompletedDate(LocalDateTime.now());
    				}
    				return ResponseEntity.ok(taskDao.save(found));
    			}
    		
    	}
    		}
    		else {
    			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,MyMessages.TASK_TITLE_IS_USED);
    		}
    }
    // Calculate the state of a project 
    public Task.State calcProjectState (Long projectId){
    	if (projectDao.tasksState(projectId,Task.State.NOT_STARTED)<1) {
    		return Task.State.NOT_STARTED;
    	}
    	else if
    	(projectDao.tasksState(projectId, Task.State.IN_PROGRESS)>1){
    		return Task.State.IN_PROGRESS;
    	}
    	else {
    		return Task.State.COMPLETED;
    	}
    }

}
