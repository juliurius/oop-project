package pl.edu.tcs.tcsball;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (Runtime.version().feature() >= 24 && System.getProperty("tcsball.relaunched") == null) {
            var classPath = System.getProperty("java.class.path", "");
            var modulePath = System.getProperty("jdk.module.path", "");
            var modular = !modulePath.isBlank();

            if (!modular) {
                var paths = List.of(classPath.split(File.pathSeparator));
                modulePath = String.join(File.pathSeparator, paths.stream().filter(path -> path.contains("javafx-")).toList());
                classPath = String.join(File.pathSeparator, paths.stream().filter(path -> !path.contains("javafx-")).toList());
            }

            var command = new ArrayList<>(List.of(
                    ProcessHandle.current().info().command().orElse("java"),
                    "--enable-native-access=" + (modulePath.isBlank() ? "ALL-UNNAMED" : "javafx.graphics"),
                    "--sun-misc-unsafe-memory-access=allow",
                    "-Dtcsball.relaunched=true"
            ));

            if (!modulePath.isBlank()) command.addAll(List.of("-p", modulePath, "--add-modules=javafx.controls"));
            if (!classPath.isBlank()) command.addAll(List.of("-cp", classPath));
            if (modular) command.addAll(List.of("-m", "pl.edu.tcs.tcsball/" + Main.class.getName()));
            else command.add(Main.class.getName());

            command.addAll(List.of(args));

            System.exit(new ProcessBuilder(command).inheritIO().start().waitFor());
        }

        GameApp.launchApp(args);
    }
}
