package verity.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import verity.command.ClientAddCommand;
import verity.command.ClientAssociateCommand;
import verity.command.ClientDeleteCommand;
import verity.command.ClientDissociateCommand;
import verity.command.ClientEditCommand;
import verity.command.ClientFindCommand;
import verity.command.ClientListCommand;
import verity.command.ClientViewCommand;
import verity.exception.VerityException;

/**
 * Tests parsing of commands in the client namespace.
 */
class ClientCommandParserTest {
    private final ClientCommandParser parser = new ClientCommandParser();

    @Test
    void parse_supportedCommands_returnsExpectedCommandTypes()
            throws VerityException {
        assertInstanceOf(
                ClientAddCommand.class,
                parser.parse("client add /name Alice Tan", 2));
        assertInstanceOf(
                ClientListCommand.class,
                parser.parse("client list", 2));
        assertInstanceOf(
                ClientViewCommand.class,
                parser.parse("client view C001", 2));
        assertInstanceOf(
                ClientFindCommand.class,
                parser.parse("client find alice", 2));
        assertInstanceOf(
                ClientEditCommand.class,
                parser.parse("client edit C001 /phone +65 9123 4567", 2));
        assertInstanceOf(
                ClientAssociateCommand.class,
                parser.parse("client associate C001 /task 1 /task 2", 2));
        assertInstanceOf(
                ClientDissociateCommand.class,
                parser.parse("client dissociate C001 /task 1", 2));
        assertInstanceOf(
                ClientDeleteCommand.class,
                parser.parse("client delete C001 confirm", 2));
    }

    @Test
    void parse_addWithoutName_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "client add /email alice@example.com", 0));
    }

    @Test
    void parse_repeatedField_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "client add /name Alice /name Bob", 0));
    }

    @Test
    void parse_duplicateTaskNumber_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "client associate C001 /task 1 /task 1", 1));
    }

    @Test
    void parse_noteEscapes_acceptsNewlineEscape() throws VerityException {
        assertInstanceOf(
                ClientAddCommand.class,
                parser.parse(
                        "client add /name Alice Tan /notes Called\\nRequested quote",
                        0));
    }

    @Test
    void parse_caseInsensitiveCommandAndMarkers_returnsCommand()
            throws VerityException {
        assertInstanceOf(
                ClientAddCommand.class,
                parser.parse("CLIENT ADD /NAME Alice Tan /PREFERRED EMAIL", 0));
    }

    @Test
    void parse_unknownMarker_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> parser.parse("client add /name Alice /fax 123", 0));
    }

    @Test
    void parse_clearName_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> parser.parse("client edit C001 /clear name", 0));
    }

    @Test
    void parse_associateWithoutTask_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> parser.parse("client associate C001", 1));
    }
}
