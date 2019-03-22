package samolovov.bugtracker.service;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import samolovov.bugtracker.dao.ProjectBean;
import samolovov.bugtracker.entity.Project;
import samolovov.bugtracker.entity.Task;
import samolovov.bugtracker.enums.ServerMessage;
import samolovov.bugtracker.exception.ServerException;
import samolovov.bugtracker.repository.ProjectRepository;
import samolovov.bugtracker.repository.TaskRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {
    private static final SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
    private static final String PROJECT_1 = "Project1";
    private static final String DESCR_1 = "Descr1";
    private static final String CREATED_1 = "01-01-2019";
    private static final String MODIFIED_1 = "01-02-2019";

    private ProjectService uut;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;

    @Before
    public void setUp() {
        uut = new ProjectService(projectRepository, taskRepository);
    }

    @Test
    public void list() throws ParseException {
        PageRequest pageable = PageRequest.of(0, 5);

        Page<Project> projectPage = createProjectPage(pageable);
        when(projectRepository.findAll(pageable)).thenReturn(projectPage);

        Page<ProjectBean> result = uut.list(pageable);
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(pageable, result.getPageable());

        List<ProjectBean> content = result.getContent();
        assertNotNull(content);
        assertEquals(2, content.size());

        ProjectBean projectBean1 = content.get(0);
        assertEquals(PROJECT_1, projectBean1.getTitle());
        assertEquals(DESCR_1, projectBean1.getDescription());
        assertFalse(projectBean1.isDeleted());
        assertNotNull(projectBean1.getCreateDate());
        assertNotNull(projectBean1.getModifyDate());
    }

    @Test
    public void getNotFound() {
        when(projectRepository.findOneById(1)).thenReturn(null);
        try {
            uut.get(1);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getStatus(), HttpStatus.NOT_FOUND);
            assertEquals(e.getMessage(), ServerMessage.PROJECT_NOT_FOUND.getText());
        }
    }

    @Test
    public void get() throws ParseException {
        Project project = mockProject(1, "Project3", "Descr3");
        when(projectRepository.findOneById(1)).thenReturn(project);
        ProjectBean result = uut.get(1);

        assertNotNull(result);
        assertEquals("Project3", result.getTitle());
        assertEquals("Descr3", result.getDescription());
        assertFalse(result.isDeleted());
        assertEquals(CREATED_1, fmt.format(result.getCreateDate()));
        assertEquals(MODIFIED_1, fmt.format(result.getModifyDate()));
    }

    @Test
    public void create() throws ParseException {
        ProjectBean bean = mockProjectBean(null, PROJECT_1, DESCR_1);
        Project project = mockProject(1, PROJECT_1, DESCR_1);

        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectBean result = uut.create(bean);
        assertNotNull(result);
        assertEquals(1, result.getId().intValue());
        assertEquals(PROJECT_1, result.getTitle());
        assertEquals(DESCR_1, result.getDescription());
        assertEquals(CREATED_1, fmt.format(result.getCreateDate()));
        assertEquals(MODIFIED_1, fmt.format(result.getModifyDate()));
        assertFalse(result.isDeleted());

        ArgumentCaptor<Project> argument = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(argument.capture());

        Project captured = argument.getValue();
        assertEquals(PROJECT_1, captured.getTitle());
        assertEquals(DESCR_1, captured.getDescription());
    }

    @Test
    public void updateNotFound() throws ParseException {
        ProjectBean bean = mockProjectBean(1, PROJECT_1, DESCR_1);
        when(projectRepository.findOneById(1)).thenReturn(null);

        try {
            uut.update(bean);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getMessage(), ServerMessage.PROJECT_NOT_FOUND.getText());
            assertEquals(e.getStatus(), HttpStatus.NOT_FOUND);
        }
    }

    @Test
    public void update() throws ParseException {
        ProjectBean bean = mockProjectBean(1, PROJECT_1, DESCR_1);
        Project project = new Project();
        project.setCreated(fmt.parse(CREATED_1));
        project.setModified(fmt.parse(MODIFIED_1));

        when(projectRepository.findOneById(1)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectBean result = uut.update(bean);
        assertNotNull(result);
        assertEquals(PROJECT_1, result.getTitle());
        assertEquals(DESCR_1, result.getDescription());
        assertEquals(CREATED_1, fmt.format(result.getCreateDate()));
        assertEquals(MODIFIED_1, fmt.format(result.getModifyDate()));
        assertFalse(result.isDeleted());

        ArgumentCaptor<Project> argument = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(argument.capture());

        Project captured = argument.getValue();
        assertEquals(PROJECT_1, captured.getTitle());
        assertEquals(DESCR_1, captured.getDescription());
    }

    @Test
    public void deleteNotFound() {
        when(projectRepository.findOneById(1)).thenReturn(null);
        try {
            uut.delete(1);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getStatus(), HttpStatus.NOT_FOUND);
            assertEquals(e.getMessage(), ServerMessage.PROJECT_NOT_FOUND.getText());
        }
    }

    @Test
    public void delete() {
        Project project = mockProject(1, PROJECT_1, DESCR_1);
        when(projectRepository.findOneById(1)).thenReturn(project);

        Task task1 = new Task();
        Task task2 = new Task();
        Page<Task> tasksPage = new PageImpl<>(Lists.newArrayList(task1, task2), PageRequest.of(0, Integer.MAX_VALUE), 2);
        when(taskRepository.findByProject(eq(project), any(Pageable.class))).thenReturn(tasksPage);

        uut.delete(1);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(2)).save(captor.capture());
        List<Task> capturedTasks = captor.getAllValues();
        assertTrue(capturedTasks.get(0).isDeleted());
        assertTrue(capturedTasks.get(1).isDeleted());

        verify(projectRepository).save(project);
    }

    private static Page<Project> createProjectPage(Pageable pageable) throws ParseException {
        Project project1 = mockProject(1, PROJECT_1, DESCR_1);
        Project project2 = mockProject(2, "Project2", "Descr2");
        return new PageImpl<>(Lists.newArrayList(project1, project2), pageable, 2);
    }

    private static ProjectBean mockProjectBean(Integer id, String title, String description) throws ParseException {
        ProjectBean projectBean = mock(ProjectBean.class);
        when(projectBean.getId()).thenReturn(id);
        when(projectBean.getTitle()).thenReturn(title);
        when(projectBean.getDescription()).thenReturn(description);
        return projectBean;
    }

    static Project mockProject(Integer id, String title, String description) {
        Project project = mock(Project.class);
        when(project.getId()).thenReturn(id);
        when(project.getTitle()).thenReturn(title);
        when(project.getDescription()).thenReturn(description);
        try {
            when(project.getCreated()).thenReturn(fmt.parse(CREATED_1));
            when(project.getModified()).thenReturn(fmt.parse(MODIFIED_1));
        } catch (Exception ignored) {}
        return project;
    }

}