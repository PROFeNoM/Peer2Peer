package peer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommandTest {
    @Test
    public void testGetfileRegex() {
        String getfileRegex = Command.getRegex(Command.GETFILE);
        assertTrue("getfile abcdef0123456789".matches(getfileRegex), "getfile regex is not correct");
        assertFalse("getfile".matches(getfileRegex), "getfile regex is not correct");
        assertFalse("getfile hello.txt".matches(getfileRegex), "getfile regex is not correct");
        assertFalse("list abcdef0123456789".matches(getfileRegex), "getfile regex is not correct");
    }
}
