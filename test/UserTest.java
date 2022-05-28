import static org.junit.Assert.*;

import com.smartboard.model.StringLengthException;
import com.smartboard.model.User;
import com.smartboard.model.User_Utils;
import org.junit.Before;
import org.junit.Test;

public class UserTest {

    User user;

    @Before
    public void setUp() throws Exception {
        user = new User("Name", "Password", "Firstname", "Lastname");
        User_Utils.users.add(user);
    }

    @Test
    public void addingUserWhenUsernameExists() {
        try {
            User_Utils.addNewUser("Name", "aa", "aa", "aa", "aa");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (StringLengthException e) {
            fail("StringLengthException not expected");
        }
    }

    @Test
    public void loginPasswordIncorrect() {
        assertFalse(user.validateLogin("Name", "invalid"));
    }

    @Test
    public void loginUsernameIncorrect() {
        assertFalse(user.validateLogin("invalid", "Password"));
    }

    @Test
    public void loginUsernameDiffCaseIsValid() {
        assertTrue(user.validateLogin("NAME", "Password"));
    }

    @Test
    public void getDefaultProjectReturnsNull() {
        assertNull(user.getDefaultProject());
    }

    @Test
    public void addProjectWithInvalidNameLength() {
        try {
            user.addSubItem("a");
            fail("StringLengthException expected");
        } catch (StringLengthException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void setDefaultToHighestIndex() {
        try {
            user.addSubItem("Name0");
            user.addSubItem("Name1");
            user.addSubItem("Name2");
            user.toggleDefaultProject(user.getListSize() - 1);
            assertEquals("Name2", user.getDefaultProject().getName());
        } catch (StringLengthException e) {
            fail("StringLengthException not expected");
        } catch (IndexOutOfBoundsException oob) {
            fail("IndexOutOfBoundsException not expected");
        }
    }

    @Test
    public void setDefaultToIndexOutOfBounds() {
        try {
            user.addSubItem("Name0");
            user.addSubItem("Name1");
            user.addSubItem("Name2");
            user.toggleDefaultProject(user.getListSize() + 1);
            fail("IndexOutOfBoundsException expected");
        } catch (StringLengthException e) {
            fail("StringLengthException not expected");
        } catch (IndexOutOfBoundsException oob) {
            oob.printStackTrace();
        }
    }

    @Test
    public void setDefaultToHigherIndex() {
        try {
            user.addSubItem("Name0");
            user.addSubItem("Name1");
            user.addSubItem("Name2");
            user.toggleDefaultProject(0);
            user.toggleDefaultProject(2);
            assertEquals("Name2", user.getDefaultProject().getName());
        } catch (StringLengthException e) {
            fail("StringLengthException not expected");
        } catch (IndexOutOfBoundsException oob) {
            fail("IndexOutOfBoundsException not expected");
        }
    }
}