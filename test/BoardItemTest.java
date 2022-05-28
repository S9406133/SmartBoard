import static org.junit.Assert.*;

import com.smartboard.model.Column;
import com.smartboard.model.StringLengthException;
import com.smartboard.model.Task;
import org.junit.Before;
import org.junit.Test;

public class BoardItemTest {

    Column column;

    @Before
    public void setUp() throws Exception {
        column = new Column("Name", 1);
    }

    @Test
    public void removeSubItemWhenNotFound() {
        try {
            Task task1 = new Task("Task1", 1);
            column.addSubItem(task1.getName());
            assertFalse(column.removeSubItem(task1));
        } catch (StringLengthException e) {
            fail("StringLengthException not expected");
        }
    }

    @Test
    public void removeSubItemWhenFound() {
        try {
            column.addSubItem("Task1");
            Task task1 = column.getSubItem(0);
            assertTrue(column.removeSubItem(task1));
        } catch (StringLengthException e) {
            fail("StringLengthException not expected");
        } catch (IndexOutOfBoundsException e) {
            fail("IndexOutOfBoundsException not expected");
        }
    }

}