package com.intrasoft.skyroof.utils;

public final class MyMessages {
public static final String PROJECT_DOESNT_EXIST="The requested projectId does not correspond to an existing project";
public static final String INVALID_TRANSITION="The specified state transition you are requiring is not allowed.";
public static final String PROJECT_STILL_HAS_TASKS="You can't delete a project that still has tasks.";
public static final String PROJECT_NULL_TITLE="The attribute title of a project can't be nullable. Please provide a non null value.";
public static final String PROJECT_ID_NON_NULL="You can't specify the project_id attribute. Please let this field null";
public static final String PROJECT_ID_NULL="ProjectId is the primary key of project table. You need to provide a non null value.";
public static final String TASK_TITLE_NULL="You can't create a task with null title.";
public static final String TASK_ALREADY_EXISTS="Task with the specified title already exists";
public static final String TASK_DOEST_EXIST="Task with the specified projectId and title does not exist.";
public static final String TASK_CANT_BE_DELETED_STATE="You can't delete a task that is not in NOT_START STATE";
public static final String TASK_INAVALID_TRANSITION_TO_NOT_STARTED_STATE="You cant change state from IN_PROGRESS or COMPLETED to NOT_STARTED";
public static final String TASK_INVALID_TRANSITION_TO_COMPLETED_STATE="You cant change state from NOT_STARTED to COMPLETED";
public static final String TASK_TITLE_IS_USED="The value of title you have inserted is already used.";
}
