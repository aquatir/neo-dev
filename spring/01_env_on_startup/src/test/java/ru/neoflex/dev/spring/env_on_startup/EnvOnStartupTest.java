package ru.neoflex.dev.spring.env_on_startup;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@SpringBootTest
@RunWith(SpringRunner.class)
public class EnvOnStartupTest {

    @Test
    public void testOutputContainsProfile() throws IOException {
        var linesInFile = Files.readAllLines(Path.of("./tmp-file.txt"));
        Assert.assertTrue(linesInFile.contains("spring.profiles.include: test"));
    }
}
