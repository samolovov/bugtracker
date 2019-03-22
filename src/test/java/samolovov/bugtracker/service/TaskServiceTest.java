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
import samolovov.bugtracker.dao.TaskBean;
import samolovov.bugtracker.entity.Project;
import samolovov.bugtracker.entity.Task;
import samolovov.bugtracker.enums.ServerMessage;
import samolovov.bugtracker.enums.TaskStatus;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static samolovov.bugtracker.enums.TaskStatus.CLOSED;
import static samolovov.bugtracker.enums.TaskStatus.IN_PROGRESS;
import static samolovov.bugtracker.enums.TaskStatus.NEW;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TaskServiceTest {
    private static final SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
    private static final String TASK_1 = "Task1";
    private static final String DESCR_1 = "Descr1";
    private static final String CREATED_1 = "01-01-2019";
    private static final String MODIFIED_1 = "01-02-2019";

    private static final int PROJECT_ID = 1;

    private TaskService uut;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;

    @Before
    public void setUp() {
        uut = new TaskService(taskRepository, projectRepository);
    }

    @Test
    public void listProjectNotFound() {
        PageRequest pageable = PageRequest.of(0, 5);
        when(projectRepository.findOneById(PROJECT_ID)).thenReturn(null);
        try {
            uut.list(PROJECT_ID, pageable);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getStatus(), HttpStatus.NOT_FOUND);
            assertEquals(e.getMessage(), ServerMessage.PROJECT_NOT_FOUND.getText());
        }
    }

    @Test
    public void list() {
        PageRequest pageable = PageRequest.of(0, 5);
        Project project = ProjectServiceTest.mockProject(PROJECT_ID, "title", "descr");
        when(projectRepository.findOneById(PROJECT_ID)).thenReturn(project);

        Page<Task> taskPage = createTaskPage(pageable, project);
        when(taskRepository.findByProject(project, pageable)).thenReturn(taskPage);

        Page<TaskBean> result = uut.list(PROJECT_ID, pageable);
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(pageable, result.getPageable());

        List<TaskBean> content = result.getContent();
        assertNotNull(content);
        assertEquals(2, content.size());

        TaskBean taskBean1 = content.get(0);
        assertEquals(1, taskBean1.getId().intValue());
        assertEquals(PROJECT_ID, taskBean1.getProjectId().intValue());
        assertEquals(TASK_1, taskBean1.getTitle());
        assertEquals(DESCR_1, taskBean1.getDescription());
        assertEquals(NEW, taskBean1.getStatus());
        assertEquals(1, taskBean1.getPriority());
        assertFalse(taskBean1.isDeleted());
        assertEquals(CREATED_1, fmt.format(taskBean1.getCreateDate()));
        assertEquals(MODIFIED_1, fmt.format(taskBean1.getModifyDate()));

        TaskBean taskBean2 = content.get(1);
        assertEquals(2, taskBean2.getId().intValue());
        assertEquals(PROJECT_ID, taskBean2.getProjectId().intValue());
        assertEquals("Task2", taskBean2.getTitle());
        assertEquals("Descr2", taskBean2.getDescription());
        assertEquals(IN_PROGRESS, taskBean2.getStatus());
        assertEquals(2, taskBean2.getPriority());
        assertFalse(taskBean2.isDeleted());
        assertEquals(CREATED_1, fmt.format(taskBean2.getCreateDate()));
        assertEquals(MODIFIED_1, fmt.format(taskBean2.getModifyDate()));
    }

    @Test
    public void createProjectNotFound() {
        TaskBean taskBean = mockTaskBean(PROJECT_ID, 1, TASK_1, DESCR_1, NEW, 1);
        when(projectRepository.findOneById(PROJECT_ID)).thenReturn(null);
        try {
            uut.create(taskBean);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getStatus(), HttpStatus.NOT_FOUND);
            assertEquals(e.getMessage(), ServerMessage.PROJECT_NOT_FOUND.getText());
        }
    }

    @Test
    public void create() {
        TaskBean taskBean = mockTaskBean(PROJECT_ID, null, TASK_1, DESCR_1, NEW, 1);
        Project project = ProjectServiceTest.mockProject(PROJECT_ID, "title", "descr");
        when(projectRepository.findOneById(PROJECT_ID)).thenReturn(project);

        Task task = mockTask(project, 1, TASK_1, DESCR_1, NEW, 1);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskBean result = uut.create(taskBean);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());

        Task captured = captor.getValue();
        assertEquals(project, captured.getProject());
        assertEquals(taskBean.getStatus(), captured.getStatus());
        assertEquals(taskBean.getPriority(), captured.getPriority());
        assertEquals(taskBean.getTitle(), captured.getTitle());
        assertEquals(taskBean.getDescription(), captured.getDescription());

        assertNotNull(result);
        assertEquals(task.getProject().getId(), result.getProjectId());
        assertEquals(task.getId(), result.getId());
        assertEquals(task.getTitle(), result.getTitle());
        assertEquals(task.getDescription(), result.getDescription());
        assertEquals(task.getStatus(), result.getStatus());
        assertEquals(task.getPriority(), result.getPriority());
        assertEquals(task.getCreated(), result.getCreateDate());
        assertEquals(task.getModified(), result.getModifyDate());
    }

    @Test
    public void updateProjectNotFound() {
        TaskBean taskBean = mockTaskBean(PROJECT_ID, 1, TASK_1, DESCR_1, NEW, 1);
        when(projectRepository.findOneById(PROJECT_ID)).thenReturn(null);
        try {
            uut.update(taskBean);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getStatus(), HttpStatus.NOT_FOUND);
            assertEquals(e.getMessage(), ServerMessage.PROJECT_NOT_FOUND.getText());
        }
    }

    @Test
    public void updateTaskNotFound() {
        TaskBean taskBean = mockTaskBean(PROJECT_ID, 1, TASK_1, DESCR_1, NEW, 1);
        Project project = ProjectServiceTest.mockProject(PROJECT_ID, "title", "descr");
        when(projectRepository.findOneById(PROJECT_ID)).thenReturn(project);
        when(taskRepository.findOneById(1)).thenReturn(null);
        try {
            uut.update(taskBean);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getStatus(), HttpStatus.NOT_FOUND);
            assertEquals(e.getMessage(), ServerMessage.TASK_NOT_FOUND.getText());
        }
    }

    @Test
    public void updateTaskIsClosed() {
        TaskBean taskBean = mockTaskBean(PROJECT_ID, 1, TASK_1, DESCR_1, IN_PROGRESS, 1);
        Project project = ProjectServiceTest.mockProject(PROJECT_ID, "title", "descr");
        when(projectRepository.findOneById(PROJECT_ID)).thenReturn(project);

        Task task = new Task();
        task.setStatus(CLOSED);
        when(taskRepository.findOneById(1)).thenReturn(task);

        try {
            uut.update(taskBean);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getStatus(), HttpStatus.FORBIDDEN);
            assertEquals(e.getMessage(), ServerMessage.TASK_UPDATE_FORBIDDEN.getText());
        }
    }

    @Test
    public void update() throws ParseException {
        TaskBean taskBean = mockTaskBean(PROJECT_ID, 1, TASK_1, DESCR_1, IN_PROGRESS, 1);
        Project project = ProjectServiceTest.mockProject(PROJECT_ID, "title", "descr");
        when(projectRepository.findOneById(PROJECT_ID)).thenReturn(project);

        Task task = new Task();
        task.setCreated(fmt.parse(CREATED_1));
        task.setModified(fmt.parse(MODIFIED_1));
        task.setStatus(IN_PROGRESS);
        when(taskRepository.findOneById(1)).thenReturn(task);

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskBean result = uut.update(taskBean);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());

        Task captured = captor.getValue();
        assertEquals(project, captured.getProject());
        assertEquals(taskBean.getStatus(), captured.getStatus());
        assertEquals(taskBean.getPriority(), captured.getPriority());
        assertEquals(taskBean.getTitle(), captured.getTitle());
        assertEquals(taskBean.getDescription(), captured.getDescription());

        assertNotNull(result);
        assertEquals(task.getProject().getId(), result.getProjectId());
        assertEquals(task.getId(), result.getId());
        assertEquals(task.getTitle(), result.getTitle());
        assertEquals(task.getDescription(), result.getDescription());
        assertEquals(task.getStatus(), result.getStatus());
        assertEquals(task.getPriority(), result.getPriority());
        assertEquals(task.getCreated(), result.getCreateDate());
        assertEquals(task.getModified(), result.getModifyDate());
    }

    @Test
    public void deleteTaskNotFound() {
        when(taskRepository.findByIdAndProjectId(1, PROJECT_ID)).thenReturn(null);
        try {
            uut.delete(PROJECT_ID, 1);
            fail();
        } catch (ServerException e) {
            assertEquals(e.getMessage(), ServerMessage.TASK_NOT_FOUND.getText());
            assertEquals(e.getStatus(), HttpStatus.NOT_FOUND);
        }
    }

    @Test
    public void delete() {
        when(taskRepository.findByIdAndProjectId(1, PROJECT_ID)).thenReturn(new Task());
        uut.delete(PROJECT_ID, 1);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());

        Task task = captor.getValue();
        assertTrue(task.isDeleted());
    }

    private static Page<Task> createTaskPage(Pageable pageable, Project project) {
        Task task1 = mockTask(project, 1, TASK_1, DESCR_1, NEW, 1);
        Task task2 = mockTask(project, 2, "Task2", "Descr2", IN_PROGRESS, 2);
        return new PageImpl<>(Lists.newArrayList(task1, task2), pageable, 2);
    }

    private static Task mockTask(Project project, Integer id, String title, String description, TaskStatus status, int priority) {
        Task task = mock(Task.class);
        when(task.getProject()).thenReturn(project);
        when(task.getId()).thenReturn(id);
        when(task.getTitle()).thenReturn(title);
        when(task.getDescription()).thenReturn(description);
        when(task.getStatus()).thenReturn(status);
        when(task.getPriority()).thenReturn(priority);
        try {
            when(task.getCreated()).thenReturn(fmt.parse(CREATED_1));
            when(task.getModified()).thenReturn(fmt.parse(MODIFIED_1));
        } catch (Exception ignored){}

        return task;
    }

    private static TaskBean mockTaskBean(Integer projectId, Integer id, String title, String description, TaskStatus status, int priority) {
        TaskBean taskBean = mock(TaskBean.class);
        when(taskBean.getId()).thenReturn(id);
        when(taskBean.getProjectId()).thenReturn(projectId);
        when(taskBean.getTitle()).thenReturn(title);
        when(taskBean.getDescription()).thenReturn(description);
        when(taskBean.getStatus()).thenReturn(status);
        when(taskBean.getPriority()).thenReturn(priority);
        return taskBean;
    }
}
