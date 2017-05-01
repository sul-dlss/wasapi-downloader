package edu.stanford.dlss;

import static org.junit.Assert.*;
import org.junit.*;

import edu.stanford.dlss.WasapiDownloader;

public class TestWasapiDownloader
{

    @Before
    public void setUp()
    {
    }

    @Test
    public void canExecuteMainWithoutCrashing()
    {
        WasapiDownloader.main(null);
    }
}
