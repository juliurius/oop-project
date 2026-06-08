package pl.edu.tcs.tcsball.net.protocol;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NetworkProtocol {
    private static final String SEPARATOR = "|";
    private static final String SEPARATOR_REGEX = "\\|";

    public String serialize(NetworkMessage message) {
        Objects.requireNonNull(message, "message");

        StringBuilder serialized = new StringBuilder(message.type().name());
        for (String field : message.fields()) {
            serialized.append(SEPARATOR).append(encode(field));
        }
        return serialized.toString();
    }

    public NetworkMessage parse(String rawMessage) {
        if (rawMessage == null || rawMessage.isBlank()) {
            throw new IllegalArgumentException("rawMessage cannot be blank");
        }

        String[] parts = rawMessage.split(SEPARATOR_REGEX, -1);
        MessageType type = MessageType.valueOf(parts[0]);
        List<String> fields = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            fields.add(decode(parts[i]));
        }

        return new NetworkMessage(type, fields);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
