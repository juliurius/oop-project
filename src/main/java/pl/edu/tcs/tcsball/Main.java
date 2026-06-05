package pl.edu.tcs.tcsball;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (Runtime.version().feature() >= 24 && System.getProperty("tcsball.relaunched") == null) {
            var command = new ArrayList<>(List.of(
                    ProcessHandle.current().info().command().orElse("java"),
                    "--enable-native-access=javafx.graphics",
                    "--sun-misc-unsafe-memory-access=allow",
                    "-Dtcsball.relaunched=true"
            ));

            var classPath = System.getProperty("java.class.path");
            if (classPath != null && !classPath.isBlank()) command.addAll(List.of("-cp", classPath));

            var modulePath = System.getProperty("jdk.module.path");
            if (modulePath == null || modulePath.isBlank()) command.add(Main.class.getName());
            else command.addAll(List.of("-p", modulePath, "-m", "pl.edu.tcs.tcsball/" + Main.class.getName()));

            command.addAll(List.of(args));

            System.exit(new ProcessBuilder(command).inheritIO().start().waitFor());
        }

        GameApp.launchApp(args);
    }
}
