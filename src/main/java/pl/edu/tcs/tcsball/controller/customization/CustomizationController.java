package pl.edu.tcs.tcsball.controller.customization;

import pl.edu.tcs.tcsball.controller.InputDelta;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import pl.edu.tcs.tcsball.model.formation.FormationFactory;
import pl.edu.tcs.tcsball.model.player.PlayerFlag;
import pl.edu.tcs.tcsball.model.player.PlayerProfile;

import java.util.List;

/**
 * Edycja profilu gracza na ekranie customizacji (nazwa, flaga, formacja) oraz
 * projekcja tych danych dla widoku ({@link CustomizationView}). Nie dotyka stanu
 * gry ani sieci — operuje wylacznie na {@link CustomizationManager}.
 */
public class CustomizationController {
    private final CustomizationManager customization;
    private final FormationFactory formationFactory;
    private final InputDelta inputDelta;

    public CustomizationController(CustomizationManager customization,
                                   FormationFactory formationFactory,
                                   InputDelta inputDelta) {
        this.customization = customization;
        this.formationFactory = formationFactory;
        this.inputDelta = inputDelta;
    }

    public void handleKey(KeyEvent event) {
        if (event.getCode() == KeyCode.BACK_SPACE) {
            backspaceName();
            inputDelta.markMouseMoved();
            return;
        }

        if (event.getEventType() != KeyEvent.KEY_TYPED) {
            return;
        }

        String text = event.getCharacter();
        if (text == null || text.isEmpty() || text.charAt(0) < ' ') {
            return;
        }

        char ch = text.charAt(0);
        if (!Character.isLetterOrDigit(ch) && ch != ' ' && ch != '-' && ch != '_') {
            return;
        }

        typeNameChar(ch);
        inputDelta.markMouseMoved();
    }

    public void cycleFlag(int direction) {
        List<PlayerFlag> flags = customization.getAvailableFlags();
        int index = flags.indexOf(customization.getCurrentProfile().pawnFlag());
        customization.setPawnFlag(flags.get(Math.floorMod(index + direction, flags.size())));
    }

    public void cycleFormation(int direction) {
        List<String> ids = customization.getAvailableFormationIds();
        int index = ids.indexOf(customization.getCurrentProfile().formationId());
        customization.setFormationId(ids.get(Math.floorMod(index + direction, ids.size())));
    }

    public void typeNameChar(char ch) {
        String name = customization.getCurrentProfile().name();
        if (name.length() < PlayerProfile.MAX_NAME_LENGTH) {
            customization.setName(name + ch);
        }
    }

    public void backspaceName() {
        String name = customization.getCurrentProfile().name();
        if (!name.isEmpty()) {
            customization.setName(name.substring(0, name.length() - 1));
        }
    }

    // --- CustomizationView: odczyt dla ekranu customizacji ---

    public String getPlayerName() {
        return customization.getCurrentProfile().name();
    }

    public String getCurrentFlagCode() {
        return customization.getCurrentProfile().pawnFlag().code();
    }

    public String getCurrentFlagName() {
        return customization.getCurrentProfile().pawnFlag().displayName();
    }

    public String getCurrentFormationName() {
        return formationFactory.getDefinition(customization.getCurrentProfile().formationId()).displayName();
    }
}
