package pl.edu.tcs.tcsball.net.protocol;

public abstract class NetworkMessage {
    private MessageType type;

    public abstract MessageType getType();
}
