package verity.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import verity.client.Client;
import verity.client.ClientList;
import verity.exception.VerityException;

/**
 * Tests reconstruction of clients from saved data.
 */
class SavedClientParserTest {
    private final SavedClientParser parser = new SavedClientParser();

    @Test
    void parseSavedClients_validData_restoresFieldsAndNextId()
            throws VerityException {
        ClientList clients = parser.parseSavedClients(List.of(
                "NEXT_ID\t2",
                "C\tC001\tAlice Tan\t+65 9123 4567\talice@example.com"
                        + "\t1 Main Street\tAcme\tCalled\\nRequested quote\temail"));

        Client client = clients.getById("C001");
        assertEquals(2, clients.getNextId());
        assertEquals("Called\nRequested quote", client.getNotes());
        assertEquals("email", client.getPreferredContactMethod().getValue());
    }

    @Test
    void parseSavedClients_emptyLines_returnsEmptyClientList()
            throws VerityException {
        ClientList clients = parser.parseSavedClients(List.of());

        assertEquals(0, clients.size());
        assertEquals(1, clients.getNextId());
    }

    @Test
    void parseSavedClients_duplicateEmail_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> parser.parseSavedClients(List.of(
                        "NEXT_ID\t3",
                        "C\tC001\tAlice Tan\t\talice@example.com\t\t\t\t",
                        "C\tC002\tBob Lee\t\tALICE@example.com\t\t\t\t")));
    }

    @Test
    void parseSavedClients_invalidNextId_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> parser.parseSavedClients(List.of(
                        "NEXT_ID\t1",
                        "C\tC001\tAlice Tan\t\t\t\t\t\t")));
    }
}
