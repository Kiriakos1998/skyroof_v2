package com.intrasoft.skyroof.persistence.model;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

public class TaskId implements Serializable {

	private static final long serialVersionUID = 1L;
private Long projectId;
private String title;


public Long getProjectId() {
	return projectId;
}
public String getTitle() {
	return title;
}

public TaskId(){
	this.projectId=1L;
	this.title="K";
}

@Autowired
 public TaskId(Long projectId, String title){
	this.projectId=projectId;
	this.title=title;
}

@Override
public boolean equals(Object o) {
    if (o == this)
        return true;
    if (!(o instanceof Task))
        return false;
    Task other = (Task)o;
    boolean currencyCodeEquals = (this.projectId == null && other.getProjectId() == null)
      || (this.projectId != null && this.projectId.equals(other.getProjectId()));
    return this.title == other.getTitle() && currencyCodeEquals;
}
@Override
public final int hashCode() {
    int result = 17;
    if (projectId != null) {
        result = 31 * result + projectId.hashCode();
    }
    if (title != null) {
        result = 31 * result + title.hashCode();
    }
    return result;
}
}
