package pl.edu.tcs.tcsball.controller;

import pl.edu.tcs.tcsball.model.Ball;
import pl.edu.tcs.tcsball.model.Match;
import pl.edu.tcs.tcsball.model.Pawn;
import pl.edu.tcs.tcsball.model.PhysicsBody;
import pl.edu.tcs.tcsball.model.Vector2D;
import pl.edu.tcs.tcsball.net.protocol.MessageType;
import pl.edu.tcs.tcsball.net.protocol.NetworkMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Zamiana stanu meczu na komunikat GAME_STATE i z powrotem.
 * Czysty format sieciowy — bez logiki kontrolera (przejscia stanow, zdarzenia
 * domenowe i reset celowania zostaja po stronie {@link GameManager}).
 *
 * Uklad pol: [stan, tura, wynik1, wynik2, pilka(4) + spin + kat, pionek_i(4)...]
 */
public class GameStateCodec {
    private static final int HEADER_FIELDS = 10;
    private static final int BODY_FIELD_COUNT = 4;

    public NetworkMessage encode(GameState state, Match match) {
        List<String> fields = new ArrayList<>();
        fields.add(state.name());
        fields.add(Integer.toString(match.getPlayerTurn()));
        fields.add(Integer.toString(match.getTeamScore(1)));
        fields.add(Integer.toString(match.getTeamScore(2)));

        Ball ball = match.getBall();
        addBodyFields(fields, ball);
        fields.add(Double.toString(ball.getSpin()));
        fields.add(Double.toString(ball.getAngle()));

        for (Pawn pawn : match.getPawns()) {
            addBodyFields(fields, pawn);
        }

        return new NetworkMessage(MessageType.GAME_STATE, fields);
    }

    /**
     * Wczytuje stan z komunikatu prosto do {@code match} i raportuje, co sie zmienilo.
     * Rzuca {@link IllegalArgumentException}, gdy komunikat jest niepelny.
     */
    public Decoded decode(NetworkMessage message, Match match) {
        List<String> fields = message.fields();
        int expectedFields = HEADER_FIELDS + match.getPawns().size() * BODY_FIELD_COUNT;
        if (fields.size() < expectedFields) {
            throw new IllegalArgumentException("Invalid game state message");
        }

        GameState receivedState = GameState.valueOf(fields.get(0));
        int oldTurn = match.getPlayerTurn();
        int oldScore1 = match.getTeamScore(1);
        int oldScore2 = match.getTeamScore(2);

        match.setPlayerTurn(Integer.parseInt(fields.get(1)));
        match.setScores(Integer.parseInt(fields.get(2)), Integer.parseInt(fields.get(3)));

        int index = applyBodyFields(match.getBall(), fields, 4);
        match.getBall().setSpin(Double.parseDouble(fields.get(index++)));
        match.getBall().setAngle(Double.parseDouble(fields.get(index++)));

        for (Pawn pawn : match.getPawns()) {
            index = applyBodyFields(pawn, fields, index);
        }

        boolean turnChanged = oldTurn != match.getPlayerTurn();
        boolean scoreChanged = oldScore1 != match.getTeamScore(1) || oldScore2 != match.getTeamScore(2);
        return new Decoded(receivedState, turnChanged, scoreChanged);
    }

    private void addBodyFields(List<String> fields, PhysicsBody body) {
        fields.add(Double.toString(body.getPosition().x()));
        fields.add(Double.toString(body.getPosition().y()));
        fields.add(Double.toString(body.getVelocity().x()));
        fields.add(Double.toString(body.getVelocity().y()));
    }

    private int applyBodyFields(PhysicsBody body, List<String> fields, int index) {
        body.setPosition(new Vector2D(
                Double.parseDouble(fields.get(index)),
                Double.parseDouble(fields.get(index + 1))
        ));
        body.setVelocity(new Vector2D(
                Double.parseDouble(fields.get(index + 2)),
                Double.parseDouble(fields.get(index + 3))
        ));
        return index + BODY_FIELD_COUNT;
    }

    /** Wynik dekodowania: nowy stan plus informacja, czy zmienila sie tura/wynik. */
    public record Decoded(GameState state, boolean turnChanged, boolean scoreChanged) {
    }
}
