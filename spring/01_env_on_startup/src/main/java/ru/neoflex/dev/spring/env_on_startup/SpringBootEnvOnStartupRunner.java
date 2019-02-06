package ru.neoflex.dev.spring.env_on_startup;


import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;


@SpringBootApplication
public class SpringBootEnvOnStartupRunner {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringBootEnvOnStartupRunner.class)
                .web(WebApplicationType.NONE)
                //.listeners() // Здесь должен быть ваш листенер!
                .run(args);
    }


    public static class FileWriter {

        public static void writeProps(Map<String, String> pros) {
            try {
                var pathOfFile = Path.of("./tmp-file.txt");
                Files.deleteIfExists(pathOfFile);
                var filePath = Files.createFile(pathOfFile);

                var linesOfProps = pros.entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue())
                        .collect(Collectors.toList());

                Files.write(filePath, linesOfProps);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}

