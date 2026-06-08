package pl.edu.tcs.tcsball.net.protocol;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record NetworkMessage(MessageType type, List<String> fields) {
    public NetworkMessage {
        type = Objects.requireNonNull(type, "type");
        fields = List.copyOf(Objects.requireNonNull(fields, "fields"));
    }

    public static NetworkMessage of(MessageType type, String... fields) {
        return new NetworkMessage(type, Arrays.asList(fields));
    }
}
