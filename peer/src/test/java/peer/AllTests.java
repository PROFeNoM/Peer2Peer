package peer.src.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AllTests {
    static private void run(Class<?> c) throws Exception {
        int nbTest = 0;
        int nbTestOK = 0;
        Method[] methods = c.getMethods();

        for (Method m : methods) {
            if (m.getName().startsWith("test")) {
                try {
                    System.out.print('.'); nbTest++;
                    m.invoke(c.getDeclaredConstructor().newInstance());
                    nbTestOK++;
                } catch (InvocationTargetException e) {
                    e.getCause().printStackTrace();
                }
            }
        }

        System.out.println("(" + nbTestOK + '/' + nbTest + "):"+ (nbTestOK == nbTest ? "" : "N") + "OK: " + c.getName());
    }

    static public void main(String[] args) throws Exception {
        boolean assertionEnabled = false;
        assert assertionEnabled = true;

        if (!assertionEnabled) {
            System.out.println("Assertions must be enabled to run tests (use -ea command line option)");
            return;
        }

        for (String c : args)
            run(Class.forName(c));
    }
}
