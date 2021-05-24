package com.cleanup.todoc.DataBaseTest;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cleanup.todoc.database.SaveMyTasksDatabase;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TaskDaoTest {

    // FOR DATA
    private SaveMyTasksDatabase database;

    // DATA SET FOR TEST
    private final static long PROJECT_ID = 3L;
    private final static Project PROJECT_DEMO = new Project(PROJECT_ID, "Projet Tartampion", 0xFFEADAD1);
    private static Task NEW_TASK_1 = new Task(1, PROJECT_ID, "Task1", 1);
    private static Task NEW_TASK_2 = new Task(2, PROJECT_ID, "Task2", 2);
    private static Task NEW_TASK_3 = new Task(3, PROJECT_ID, "Task3", 3);

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void initDb() throws Exception {
        this.database = Room.inMemoryDatabaseBuilder(androidx.test.core.app.ApplicationProvider.getApplicationContext(),
                SaveMyTasksDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() throws Exception {
        database.close();
    }

    @Test
    public void insertAndGetProject() throws InterruptedException {
        // BEFORE : Adding a new project
        this.database.projectDao().createProject(PROJECT_DEMO);
        // TEST
        Project project = LiveDataTestUtil.getValue(this.database.projectDao().getProject(PROJECT_ID));
        assertTrue(project.getProjectName().equals(PROJECT_DEMO.getProjectName()) && project.getId() == PROJECT_ID);
    }

    @Test
    public void getTasksWhenNoTaskInserted() throws InterruptedException {
        // TEST
        List<Task> tasks = LiveDataTestUtil.getValue(this.database.taskDao().getTasks());
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void insertAndGetTasks() throws InterruptedException {
        // BEFORE : Adding demo project & demo tasks

        this.database.projectDao().createProject(PROJECT_DEMO);
        this.database.taskDao().insertTask(NEW_TASK_1);
        this.database.taskDao().insertTask(NEW_TASK_2);
        this.database.taskDao().insertTask(NEW_TASK_3);

        // TEST
        List<Task> tasks = LiveDataTestUtil.getValue(this.database.taskDao().getTasks());
        assertTrue(tasks.size() == 3);
    }

    @Test
    public void insertAndUpdateTask() throws InterruptedException {
        String name = "newName";
        // BEFORE : Adding demo project & demo tasks. Next, update task added & re-save it
        this.database.projectDao().createProject(PROJECT_DEMO);
        this.database.taskDao().insertTask(NEW_TASK_1);
        Task taskAdded = LiveDataTestUtil.getValue(this.database.taskDao().getTasks()).get(0);
        taskAdded.setName(name);
        this.database.taskDao().updateTask(taskAdded);

        //TEST
        List<Task> tasks = LiveDataTestUtil.getValue(this.database.taskDao().getTasks());
        assertTrue(tasks.size() == 1 && tasks.get(0).getName().equalsIgnoreCase(name));
    }

    @Test
    public void insertAndDeleteTask() throws InterruptedException {
        // BEFORE : Adding demo project & demo task. Next, get the task added & delete it.
        this.database.projectDao().createProject(PROJECT_DEMO);
        this.database.taskDao().insertTask(NEW_TASK_1);

        //TEST
        List<Task> itemsBefore = LiveDataTestUtil.getValue(this.database.taskDao().getTasks());
        assertFalse(itemsBefore.isEmpty());

        Task taskAdded = LiveDataTestUtil.getValue(this.database.taskDao().getTasks()).get(0);
        this.database.taskDao().deleteTask(taskAdded.getId());

        //TEST
        List<Task> itemsAfter = LiveDataTestUtil.getValue(this.database.taskDao().getTasks());
        assertTrue(itemsAfter.isEmpty());
    }

}
