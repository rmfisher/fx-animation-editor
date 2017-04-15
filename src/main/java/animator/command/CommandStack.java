package animator.command;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.*;
import java.util.function.Consumer;

public class CommandStack {

    private final Deque<Command> commands = new ArrayDeque<>();
    private final Deque<Command> undoneCommands = new ArrayDeque<>();
    private final List<Consumer<Command>> listeners = new ArrayList<>();
    private final BooleanProperty canUndo = new SimpleBooleanProperty();
    private final BooleanProperty canRedo = new SimpleBooleanProperty();

    private Command savePoint;

    public void append(Command command) {
        commands.addLast(command);
        undoneCommands.clear();
        onChange(command);
    }

    public void appendAndExecute(Command command) {
        command.execute();
        append(command);
    }

    public void undo() {
        if (canUndo()) {
            Command commandToUndo = commands.pollLast();
            commandToUndo.undo();
            undoneCommands.addLast(commandToUndo);
            onChange(commandToUndo);
        }
    }

    public void redo() {
        if (canRedo()) {
            Command commandToRedo = undoneCommands.pollLast();
            commandToRedo.execute();
            commands.addLast(commandToRedo);
            onChange(commandToRedo);
        }
    }

    public boolean canUndo() {
        return canUndo.get();
    }

    public ReadOnlyBooleanProperty canUndoProperty() {
        return canUndo;
    }

    public boolean canRedo() {
        return canRedo.get();
    }

    public ReadOnlyBooleanProperty canRedoProperty() {
        return canRedo;
    }

    public void clear() {
        commands.clear();
        undoneCommands.clear();
        canUndo.set(false);
        canRedo.set(false);
        savePoint = null;
    }

    public void addListener(Consumer<Command> listener) {
        listeners.add(listener);
    }

    public void markSavePoint() {
        savePoint = commands.peekLast();
    }

    public boolean isSavePoint() {
        return Objects.equals(savePoint, commands.peekLast());
    }

    private void onChange(Command command) {
        canUndo.set(!commands.isEmpty());
        canRedo.set(!undoneCommands.isEmpty());
        listeners.forEach(listener -> listener.accept(command));
    }
}
