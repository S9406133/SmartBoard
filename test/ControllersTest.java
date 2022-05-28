
import com.smartboard.controller.Controller_Utils;
import com.smartboard.model.Column;
import com.smartboard.model.StringLengthException;
import com.smartboard.model.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ControllersTest {

    ArrayList<Column> list = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        list.add(new Column("Col0", 0));
        list.add(new Column("Col1", 1));
        list.add(new Column("Col2", 2));
    }

    @Test
    public void moveItemUpArray() {
        Controller_Utils.moveItem(list, 2, "up");
        assertEquals("Col2", list.get(1).getName());
    }

    @Test
    public void moveItemDownArray() {
        Controller_Utils.moveItem(list, 0, "down");
        assertEquals("Col0", list.get(1).getName());
    }

    @Test
    public void moveItemUpArrayWhenAtStart() {
        Controller_Utils.moveItem(list, 0, "up");
        assertEquals("Col0", list.get(0).getName());
    }

    @Test
    public void moveItemDownArrayWhenAtEnd() {
        Controller_Utils.moveItem(list, 2, "down");
        assertEquals("Col2", list.get(2).getName());
    }

    @Test
    public void getColorWhenStatusIsDATE_NOT_SET() {
        try {
            Task task = new Task("Name", 1);
            assertEquals("azure", Controller_Utils.getStatusColor(task));
        } catch (StringLengthException e) {
            fail("StringLengthException not expected");
        }
    }
}
