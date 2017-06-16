package es.urjc.mov.javsan.timetab;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TimeTabClientTest {
    private final String TESTDNI = "test";

    @Test
    public void happyPath_isCorrect() throws Exception {
        String dniMissing = "4342353535ccc";
        String dni = TESTDNI;
        TimeTabClient timeTabClient = testServerTimeTab(dniMissing);

        assertTrue(timeTabClient.getGroups().isEmpty());
        assertNotNull(timeTabClient.getStudent().toString());

        timeTabClient = testServerTimeTab(dni);

        assertTrue(!timeTabClient.getGroups().isEmpty());
        assertNotNull(timeTabClient.getStudent().toString());
    }

    @Test
    public void fail_isCorrect () throws IOException {
        TimeTabClient timeTabClient = new TimeTabClient();

        String err = timeTabClient.connect("localhost", 1);
        assertNotEquals(err , "");

        err = timeTabClient.sendRequestTimeTab("fail");
        assertNotEquals(err , "");

        err = timeTabClient.receiveReplyTimeTab();
        assertNotEquals(err , "");

        err = timeTabClient.close();
        assertEquals(err , "");
    }

    private TimeTabClient testServerTimeTab (String dni) throws IOException {
        TimeTabClient timeTabClient = new TimeTabClient();

        String err = timeTabClient.connect("localhost", 2000);
        assertEquals(err, "");

        err = timeTabClient.sendRequestTimeTab(dni);
        assertEquals(err, "");

        err = timeTabClient.receiveReplyTimeTab();
        if (dni.equals(TESTDNI)) {
            assertEquals(err, "");
        } else {
            assertNotEquals(err, "");
        }
        err = timeTabClient.close();
        assertEquals(err, "");
        
        return timeTabClient;
    }
}