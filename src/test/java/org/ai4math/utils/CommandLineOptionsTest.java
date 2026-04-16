package org.ai4math.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandLineOptionsTest {

    @Test
    public void givenValidValues_whenSetter_thenGetterReturnsAccurately() {
        CommandLineOptions commandLineOptions = new CommandLineOptions();

        commandLineOptions.setDecorations(true);
        commandLineOptions.setRegenerateDataset(true);
        commandLineOptions.setBaseGraphs(4);
        commandLineOptions.setFilePath("test path");
        commandLineOptions.setCombinedGraphs(12);

        assertEquals(4, commandLineOptions.getBaseGraphs());
        assertEquals(12, commandLineOptions.getCombinedGraphs());
        assertEquals("test path", commandLineOptions.getFilePath());
        assertTrue(commandLineOptions.isRegenerateDataset());
        assertTrue(commandLineOptions.isDecorated());
    }
}
