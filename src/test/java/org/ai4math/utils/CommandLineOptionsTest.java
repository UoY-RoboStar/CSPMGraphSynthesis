package org.ai4math.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import org.apache.commons.cli.ParseException;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLineOptionsTest {

    @Test
    public void givenAllValidArgs_whenParseCommandLine_thenReturnAccurateOptions() throws IOException, ParseException {
        String[] args = {"-p", "test path", "-b", "4", "-c", "500", "-r", "-d", "-re"};
        CommandLineOptions clo = CommandLineOptions.parseCommandLine(args);

        assertEquals(4, clo.getBaseGraphs());
        assertEquals(500, clo.getCombinedGraphs());
        assertEquals("test path", clo.getFilePath());
        assertTrue(clo.isRegenerateDataset());
        assertTrue(clo.isDecorated());
        assertTrue(clo.isRenaming());
    }

    @Test
    public void givenOnlyRequiredArgs_whenParseCommandLine_thenReturnAccurateOptions() throws IOException, ParseException {
        String[] args = {"-b", "4", "-c", "500"};
        CommandLineOptions clo = CommandLineOptions.parseCommandLine(args);

        assertEquals(4, clo.getBaseGraphs());
        assertEquals(500, clo.getCombinedGraphs());
        assertNull(clo.getFilePath());
        assertFalse(clo.isRegenerateDataset());
        assertFalse(clo.isDecorated());
        assertFalse(clo.isRenaming());
    }

    @Test
    public void givenMissingRequiredArgs_whenParseCommandLine_thenThrowError() throws IOException, ParseException {
        @SuppressWarnings("unchecked")
        String[] args = {"-p", "test path", "-c", "500", "-r", "-d", "-re"};
        assertThrows(ParseException.class, () -> CommandLineOptions.parseCommandLine(args));
    }

    @Test
    public void givenInvalidArgs_whenParseCommandLine_thenThrowError() throws IOException, ParseException {
        @SuppressWarnings("unchecked")
        String[] args = {"-p", "test path", "-b", "4", "-c", "x", "-r", "-d", "-re"};
        assertThrows(NumberFormatException.class, () -> CommandLineOptions.parseCommandLine(args));
    }

    @Test
    public void givenValidValues_whenSetter_thenGetterReturnsAccurately() {
        CommandLineOptions commandLineOptions = new CommandLineOptions();

        commandLineOptions.setDecorations(true);
        commandLineOptions.setRegenerateDataset(true);
        commandLineOptions.setRenaming(false);
        commandLineOptions.setBaseGraphs(4);
        commandLineOptions.setFilePath("test path");
        commandLineOptions.setCombinedGraphs(12);

        assertEquals(4, commandLineOptions.getBaseGraphs());
        assertEquals(12, commandLineOptions.getCombinedGraphs());
        assertEquals("test path", commandLineOptions.getFilePath());
        assertTrue(commandLineOptions.isRegenerateDataset());
        assertTrue(commandLineOptions.isDecorated());
        assertFalse(commandLineOptions.isRenaming());
    }
}
