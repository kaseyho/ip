package verity.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import verity.exception.VerityException;

/**
 * Tests client validation, matching, IDs, and serialization.
 */
class ClientTest {
    @Test
    void constructor_validFields_createsClient() throws VerityException {
        Client client = new Client(
                1,
                "Alice Tan",
                "+65 9123 4567",
                "alice@example.com",
                "1 Main Street",
                "Acme",
                "Called at 9am\nRequested a quote",
                PreferredContactMethod.EMAIL);

        assertEquals("C001", client.getId());
        assertTrue(client.matchesName("aLi"));
        assertEquals(
                "C\tC001\tAlice Tan\t+65 9123 4567\talice@example.com"
                        + "\t1 Main Street\tAcme\tCalled at 9am\\nRequested a quote\temail",
                client.serialize());
    }

    @Test
    void constructor_nameWithPunctuation_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> createClient("Alice-Tan", "", ""));

        assertEquals(
                "Client names may contain letters and spaces only.",
                exception.getMessage());
    }

    @Test
    void constructor_invalidPhone_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> createClient("Alice Tan", "call-me", ""));
    }

    @Test
    void constructor_invalidEmail_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> createClient("Alice Tan", "", "alice.example.com"));
    }

    @Test
    void constructor_boundaryWhitespace_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> createClient("Alice Tan ", "", ""));

        assertEquals(
                "Client name cannot start or end with whitespace.",
                exception.getMessage());
    }

    @Test
    void constructor_addressWithTab_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> new Client(
                        1, "Alice Tan", "", "", "1 Main\tStreet",
                        "", "", null));
    }

    @Test
    void constructor_notesWithNewline_acceptsNotes() throws VerityException {
        Client client = new Client(
                1, "Alice Tan", "", "", "", "", "First\nSecond", null);

        assertEquals("First\nSecond", client.getNotes());
    }

    @Test
    void constructor_notesWithTab_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> new Client(
                        1, "Alice Tan", "", "", "", "",
                        "First\tSecond", null));
    }

    @Test
    void constructor_overlongName_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> createClient("A".repeat(101), "", ""));
    }

    @Test
    void parseId_mixedCase_returnsNumericId() throws VerityException {
        assertEquals(12, Client.parseId("c012"));
    }

    private Client createClient(String name, String phone, String email)
            throws VerityException {
        return new Client(1, name, phone, email, "", "", "", null);
    }
}
