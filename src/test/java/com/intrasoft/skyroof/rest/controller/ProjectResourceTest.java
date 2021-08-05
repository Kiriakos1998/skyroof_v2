package com.intrasoft.skyroof.rest.controller;

import com.intrasoft.skyroof.persistence.dao.ProjectDao;
import com.intrasoft.skyroof.persistence.model.Project;
import com.intrasoft.skyroof.persistence.model.Task;
import com.intrasoft.skyroof.persistence.model.TaskId;
import com.intrasoft.skyroof.utils.MyMessages;
import com.intrasoft.skyroof.persistence.dao.TaskDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class ProjectResourceTest {

    @Autowired
    private MockMvc mvc;
    
    @Autowired 
    private ProjectDao projectDao;

    @Autowired
    private TaskDao taskDao;
    
    @Autowired
    ProjectResource projectResource;
    // test if the system calculates correctly the 
    // state of a project.
    @Test
    public void testProjectState() throws Exception{
    	assertThat( projectResource.calcProjectState(2L).equals(Task.State.IN_PROGRESS));
    	assertThat( projectResource.calcProjectState(9L).equals(Task.State.COMPLETED));
    	assertThat( projectResource.calcProjectState(8L).equals(Task.State.NOT_STARTED));
    }
    
    @Test
    public void testCreateResource() throws Exception {
        Project project = new Project();
        project.setTitle("Title");
        project.setDescription("Description");

        mvc.perform(post("/api/v1/projects")//
                .content(" {\"title\":\"Title\", \"description\":\"Description\"}")//
                .contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isOk())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Title"))//
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Description"));
    }
    
    
    // Test deletion of a project that has tasks
    // Business expects system not to allow this request
    // Also a message explaining that deleting a project that
    // still has tasks assigned is expected
    @Test 
    public void testDeleteProjectThatHasTasks() throws Exception{
 
         mvc.perform(MockMvcRequestBuilders.delete("/api/v1/projects/2")//
        		 .contentType(MediaType.APPLICATION_JSON_UTF8))//
                 .andExpect(status().isBadRequest())//
                 .andExpect(content().string(containsString(MyMessages.PROJECT_STILL_HAS_TASKS)))//
                 .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8));
               
    }
    //Test deletion of a project that does not exist
    // Expect system to fail with response 404 NOT FOUND
    @Test 
     public void testDeleteProjectThatDoesNotExist() throws Exception{
    	  mvc.perform(MockMvcRequestBuilders.delete("/api/v1/projects/1")//
         		 .contentType(MediaType.APPLICATION_JSON_UTF8))//
                  .andExpect(status().isNotFound())//
                  .andExpect(content().string(containsString(MyMessages.PROJECT_DOESNT_EXIST)))//
                  .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8));
    }
    
    // Test deletion of a project that exists and has no tasks assigned
    // The expected result is status 200 OK
    @Test
    public void testDeleteSuccess() throws Exception {
    	Project project= new Project();
    	project.setTitle("Title");
    	project.setDescription("hey");
    	projectDao.save(project);
    	mvc.perform(MockMvcRequestBuilders.delete("/api/v1/projects/"+project.getProjectId().toString())//
        		 .contentType(MediaType.APPLICATION_JSON_UTF8))//
                 .andExpect(status().isOk());
    assertThat(projectDao.findById(project.getProjectId()).empty());
    }
    
    // Test creation of project where user tries to provide with projectId
    // Expected result is a BAD REQUEST response to the request
    @Test
    public void testCreateProjectWithNonNullId() throws Exception{
    	 mvc.perform(post("/api/v1/projects")//
                 .content(" {\"projectId\":\"2\",\"title\":\"Title\", \"description\":\"Description\"}")//
                 .contentType(MediaType.APPLICATION_JSON))//
                 .andExpect(status().isBadRequest())//
                 .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                 .andExpect(content().string(containsString(MyMessages.PROJECT_ID_NON_NULL)));
    }
    
    // Test creation of project with null title
    // Expected result is BAD REQUEST response
    @Test
    public void testCreateProjectWithNullTitle() throws Exception{
    	 mvc.perform(post("/api/v1/projects")//
                 .content(" {\"description\":\"Description\"}")//
                 .contentType(MediaType.APPLICATION_JSON))//
                 .andExpect(status().isBadRequest())//
                 .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                 .andExpect(content().string(containsString(MyMessages.PROJECT_NULL_TITLE)));
    }
    
    //Test trying to access project that does not exist
    // Which means provide URL with non existing projectId
    @Test 
    public void testAccesNonExistingProject() throws Exception{
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/projects/1")//
                .contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isNotFound())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(content().string(containsString(MyMessages.PROJECT_DOESNT_EXIST)));
    }
    //Test trying to access project that a exists
    //Expected result is status 200 OK
    @Test
    public void testAccessExistingProject() throws Exception{
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/projects/2")//
                .contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isOk())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
   
    //Test try to update project that doesn't exist
    //Expected result is to fail with status 404 NOT FOUND
    @Test
    public void testUpdateProjectNullProjectId() throws Exception{
    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/projects")//
                .content("{\"title\":\"new title\"}")
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isBadRequest())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(content().string(containsString(MyMessages.PROJECT_ID_NULL)));
    }
    //Test try to update project that doesn't exist
    //Expected result is to fail with status 404 NOT FOUND
    @Test 
    public void testUpdateProjectThatDoesNotExist() throws Exception{
    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/projects")//
                .content("{\"projectId\": \"1\" , \"title\":\"new title\"}")
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isNotFound())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(content().string(containsString(MyMessages.PROJECT_DOESNT_EXIST)));
    }
    
    // Test try to update project that exists
    // Expected result is to succeed with status 200 OK returned
    @Test 
    public void testUpdateProjectSuccessfully() throws Exception
    {
    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/projects")//
                .content("{\"projectId\": \"4\" , \"title\":\"new title\" , \"description\":\"Updated\"}")
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isOk())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("new title"))//
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated"));
    }
    
    // Test creating a new task successfully
    // Expected status is 200 OK 
    @Test
    public void testPostTaskSuccess() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.post("/api/v1/projects/2/tasks")//
                .content("{\"projectId\": \"2\" , \"title\":\"new title\" , \"description\":\"Created\"}")
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isOk())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("new title"))//
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Created"));
    	taskDao.delete(taskDao.findById(new TaskId(2L,"new title")).orElseThrow( () -> new Exception()));
    }
    
    // Test creating a new task fails because task already exists
    // Expected status is 400 BAD REQUEST
    @Test
    public void testPostTaskAlreadyExists() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.post("/api/v1/projects/2/tasks")//
                .content("{\"projectId\": \"2\" , \"title\":\"C\" , \"description\":\"Created\"}")
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isBadRequest())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(content().string(containsString(MyMessages.TASK_ALREADY_EXISTS)));
    }
    
    // Test creating a new task fails because project does not exist
    // Expected status is 404 NOT FOUND
    @Test
    public void testPostTaskProjectDoesNotExists() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.post("/api/v1/projects/1/tasks")//
                .content("{\"title\":\"C\" , \"description\":\"Created\"}")
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isNotFound())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(content().string(containsString(MyMessages.PROJECT_DOESNT_EXIST)));
    }

    // Test creating a new task fails because null title is provided
    // Expected status is 400 BAD request
    @Test
    public void testPostTaskTitleNull() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.post("/api/v1/projects/2/tasks")//
                .content("{ \"description\":\"Created\"}")
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isBadRequest())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(content().string(containsString(MyMessages.TASK_TITLE_NULL)));
    }
    // Test accessing a task successfully 
    // Expected result is 200 OK
    @Test
    public void testAccessTaskSuccessfully() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/projects/2/tasks/C")//
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isOk())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("C"))//
                .andExpect(MockMvcResultMatchers.jsonPath("$.projectId").value("2"));

    }

    // Test accessing a task providing with a projectId title combination that does not 
    // correspond to an existing task. Expected result is 404 NOT FOUND
    @Test
    public void testAccessTaskDoesNotExist() throws Exception{
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/projects/2/tasks/dinosaur")//
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isNotFound())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(content().string(containsString(MyMessages.TASK_DOEST_EXIST)));
    }
    
    // Test deleting a task successfully. 
    // The expected result is  200 OK
    @Test
    public void testDeleteTaskSuccessfully() throws Exception{
    	Task task= taskDao.findById(new TaskId(2L,"C")).orElseThrow(()-> new Exception());
    	mvc.perform(MockMvcRequestBuilders.delete("/api/v1/projects/2/tasks/C")//
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isOk());
    	assertThat(taskDao.findById(new TaskId(2L,"C"))).isEmpty();
    	taskDao.save(task);
    }
    // Test deleting a task that is not in NOT_STARTED state 
    // The expected result is 400 BAD REQUEST
    @Test
    public void testDeleteTaskNotInNotStartedState() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.delete("/api/v1/projects/2/tasks/D")//
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isBadRequest())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(content().string(containsString(MyMessages.TASK_CANT_BE_DELETED_STATE)));
    }
    // Test deleting a task that does not exist 
    // The expected result is 404 NOT FOUND
    @Test 
    public void testDeleteTaskThatDoesNotExist() throws Exception{
    	mvc.perform(MockMvcRequestBuilders.delete("/api/v1/projects/1/tasks/D")//
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isNotFound())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(content().string(containsString(MyMessages.TASK_DOEST_EXIST)));
    }
    
    // Test updating a task changing state from NOT STARTED to
    // COMPLETED. The expected result is 400 BAD REQUEST
    @Test
    public void testUpdateTaskIllegalStateTransition() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/projects/2/tasks/C")//
                .content("{ \"title\":\"new title\" , \"state\": \"COMPLETED\", \"description\":\"Updated\"}")
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isBadRequest())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(content().string(containsString(MyMessages.TASK_INVALID_TRANSITION_TO_COMPLETED_STATE)));
    }
    
    // Test updating a task changing state from IN_PROGRESS to
    // NOT_STARTED or from COMPLETED to NOT_STARTED. The expected result is
    // 400 BAD REQUEST
    @Test
    public void testUpdateTaskIllegalStateTransitionToNotStarted() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/projects/2/tasks/D")//
                .content("{ \"state\": \"NOT_STARTED\", \"description\":\"Updated\"}")
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isBadRequest())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(content().string(containsString(MyMessages.TASK_INAVALID_TRANSITION_TO_NOT_STARTED_STATE)));
    }
    // Test updating a task that does not exist
    // The expected result is  404 NOT FOUND
    @Test
    public void testUpdateTaskDoesNotExist() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/projects/2/tasks/dinosaur")//
                .content("{ \"state\": \"NOT_STARTED\", \"description\":\"Updated\"}")
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isNotFound())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(content().string(containsString(MyMessages.TASK_DOEST_EXIST)));
    }
    // Test updating a task providing a title that is already in use
    // The expected result is  400 BAD REQUEST
    @Test
    public void testUpdateTaskProvidingTitleAlreadyUsed() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/projects/2/tasks/D")//
                .content("{ \"title\":\"C\" , \"state\": \"NOT_STARTED\", \"description\":\"Updated\"}")
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isBadRequest())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(content().string(containsString(MyMessages.TASK_TITLE_IS_USED)));
    }
    
    // Test updating a task successfully
    // The expected result is 200 OK
    @Test
    public void testUpdateTaskSuccessfully() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/projects/2/tasks/C")//
                .content("{ \"title\":\"C\" , \"state\": \"NOT_STARTED\", \"description\":\"Updated\"}")
    			.contentType(MediaType.APPLICATION_JSON))//
                .andExpect(status().isOk())//
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))//
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("C"))//
                .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("NOT_STARTED"))//
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated"));
    }
}