package com.intrasoft.skyroof.persistence.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.intrasoft.skyroof.utils.LocalDateTimeDeserializer;
import com.intrasoft.skyroof.utils.LocalDateTimeSerializer;

@Entity (name ="task")
@Table(name = "task")
@IdClass(TaskId.class)
public class Task extends Creation implements Serializable {
	
	private static final long serialVersionUID = 1L;	
	
	public  enum State {
			NOT_STARTED,
			IN_PROGRESS,
			COMPLETED
		}
		 @PrePersist
		    public void prePersist(){
		        super.prePersist();
		    }
	

	@Id
	@Column(name="project_id",nullable=false)
	private Long projectId;
	
	@Id
	@Column(name="title",nullable=false,length=100)
	private String title ;
	
    @Enumerated(EnumType.STRING)
    @Column(name="state")
	private State state;
	
    @Column(name="description",length=1000)
    private String description;
   
    @Column(name = "start_date")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;
    
    @Column(name="completed_date")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime completedDate;
    
    
    public Long getProjectId() {return projectId;}
    
    public void setProjectId(Long projectId) {this.projectId= projectId;}
    
    public String getTitle() {return title;}
    
    public void setTitle(String title) {this.title=title;}
    
    public State getState() {return state;}
    
    public void  setState(State state) {this.state=state;}
    
    public String getDescription() {return description;}
   
    public void setDescription(String description) {this.description=description;}
    
    public LocalDateTime getStartDate() {return startDate;}
    
    public void setStartDate(LocalDateTime startDate) {this.startDate=startDate;}
    
    public LocalDateTime getCompletedtDate() {return completedDate;}
    
    public void setCompletedDate(LocalDateTime completedDate) {this.completedDate=completedDate;}
    

}
