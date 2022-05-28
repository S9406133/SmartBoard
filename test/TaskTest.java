import com.smartboard.model.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class TaskTest {

    Task task;

    @Before
    public void setUp() throws Exception {
        task = new Task("Name", 1);
    }

    @Test
    public void setCompletedWithNoDueDate() {
        task.setCompleted(true);
        assertEquals("COMPLETED_ON_TIME", task.getStatus().toString());
    }

    @Test
    public void setCompletedPastDueDate() {
        task.setDueDate(LocalDate.of(2022, 1, 1));
        task.setCompleted(true);
        assertEquals("COMPLETED_LATE", task.getStatus().toString());
    }

    @Test
    public void notCompletedPastDueDate() {
        task.setDueDate(LocalDate.of(2022, 1, 1));
        task.setCompleted(false);
        assertEquals("OVERDUE", task.getStatus().toString());
    }

    @Test
    public void notCompletedOnDueDate() {
        task.setDueDate(LocalDate.now());
        task.setCompleted(false);
        assertEquals("APPROACHING", task.getStatus().toString());
    }

    @Test
    public void setCompletedOnDueDate() {
        task.setDueDate(LocalDate.now());
        task.setCompleted(true);
        assertEquals("COMPLETED_ON_TIME", task.getStatus().toString());
    }
}
