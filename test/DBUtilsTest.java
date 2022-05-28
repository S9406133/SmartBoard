
import com.smartboard.model.DB_Utils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DBUtilsTest {

    @Test
    public void testTableNotExistsIsTrue() {
        assertTrue(DB_Utils.TableNotExists("RANDOM"));
    }

    @Test
    public void testTableNotExistsIsFalse() {
        assertFalse(DB_Utils.TableNotExists("USER"));
    }
}
