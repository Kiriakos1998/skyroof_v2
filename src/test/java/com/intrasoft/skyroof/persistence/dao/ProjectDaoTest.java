package com.intrasoft.skyroof.persistence.dao;

import com.intrasoft.skyroof.persistence.model.Project;
import com.intrasoft.skyroof.utils.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class ProjectDaoTest {

    @Autowired
    private ProjectDao projectDao;

    @Test
    public void testCreate(){
        Project project = new Project();
        project.setTitle("Title");
        project.setDescription("Description");
        projectDao.save(project);

        Project found = projectDao.findById(project.getProjectId()).orElseThrow(() -> new IllegalArgumentException());
        Assert.equals(found.getTitle(), project.getTitle(), "");
        Assert.notNull(found.getCreationDate());
    }


}