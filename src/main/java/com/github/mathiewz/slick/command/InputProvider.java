package com.github.mathiewz.slick.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.github.mathiewz.slick.Input;
import com.github.mathiewz.slick.command.ControllerDirectionControl.Direction;
import com.github.mathiewz.slick.util.InputAdapter;

/**
 * The central provider that maps real device input into abstract commands
 * defined by the developer. Registering a control against an command with this
 * class will cause the provider to produce an event for the command when the
 * input is pressed and released.
 *
 * @author joverton
 */
public class InputProvider {
    /** The commands that have been defined */
    private final HashMap<Control, Command> commands;

    /** The list of listeners that may be listening */
    private final ArrayList<InputProviderListener> listeners = new ArrayList<>();

    /** The command input states */
    private final HashMap<Command, CommandState> commandState = new HashMap<>();

    /** True if this provider is actively sending events */
    private boolean active = true;

    /**
     * Create a new input proider which will provide abstract input descriptions
     * based on the input from the supplied context.
     *
     * @param input
     *            The input from which this provider will receive events
     */
    public InputProvider(Input input) {
        input.addListener(new InputListenerImpl());
        commands = new HashMap<>();
    }

    /**
     * Get the list of commands that have been registered with the provider,
     * i.e. the commands that can be issued to the listeners
     *
     * @return The list of commands (@see Command) that can be issued from this
     *         provider
     */
    public List<Command> getUniqueCommands() {
        List<Command> uniqueCommands = new ArrayList<>();

        for (Command command2 : commands.values()) {
            Command command = command2;

            if (!uniqueCommands.contains(command)) {
                uniqueCommands.add(command);
            }
        }

        return uniqueCommands;
    }

    /**
     * Get a list of the registered controls (@see Control) that can cause a
     * particular command to be invoked
     *
     * @param command
     *            The command to be invoked
     * @return The list of controls that can cause the command (@see Control)
     */
    public List<Control> getControlsFor(Command command) {
        List<Control> controlsForCommand = new ArrayList<>();

        for (Entry<Control, Command> entry : commands.entrySet()) {
            Control key = entry.getKey();
            Command value = entry.getValue();

            if (value == command) {
                controlsForCommand.add(key);
            }
        }
        return controlsForCommand;
    }

    /**
     * Indicate whether this provider should be sending events
     *
     * @param active
     *            True if this provider should be sending events
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Check if this provider should be sending events
     *
     * @return True if this provider should be sending events
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Add a listener to the provider. This listener will be notified of
     * commands detected from the input.
     *
     * @param listener
     *            The listener to be added
     */
    public void addListener(InputProviderListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener from this provider. The listener will no longer be
     * provided with notification of commands performe.
     *
     * @param listener
     *            The listener to be removed
     */
    public void removeListener(InputProviderListener listener) {
        listeners.remove(listener);
    }

    /**
     * Bind an command to a control.
     *
     * @param command
     *            The command to bind to
     * @param control
     *            The control that is pressed/released to represent the command
     */
    public void bindCommand(Control control, Command command) {
        commands.put(control, command);

        if (commandState.get(command) == null) {
            commandState.put(command, new CommandState());
        }
    }

    /**
     * Clear all the controls that have been configured for a given command
     *
     * @param command
     *            The command whose controls should be unbound
     */
    public void clearCommand(Command command) {
        List<Control> controls = getControlsFor(command);

        for (int i = 0; i < controls.size(); i++) {
            unbindCommand(controls.get(i));
        }
    }

    /**
     * Unbinds the command associated with this control
     *
     * @param control
     *            The control to remove
     */
    public void unbindCommand(Control control) {
        Command command = commands.remove(control);
        if (command != null && !commands.values().contains(command)) {
            commandState.remove(command);
        }
    }

    /**
     * Get the recorded state for a given command
     *
     * @param command
     *            The command to get the state for
     * @return The given command state
     */
    private CommandState getState(Command command) {
        return commandState.get(command);
    }

    /**
     * Check if the last control event we recieved related to the given command
     * indicated that a control was down
     *
     * @param command
     *            The command to check
     * @return True if the last event indicated a button down
     */
    public boolean isCommandControlDown(Command command) {
        return getState(command).isDown();
    }

    /**
     * Check if one of the controls related to the command specified has been
     * pressed since we last called this method
     *
     * @param command
     *            The command to check
     * @return True if one of the controls has been pressed
     */
    public boolean isCommandControlPressed(Command command) {
        return getState(command).isPressed();
    }

    /**
     * Fire notification to any interested listeners that a control has been
     * pressed indication an particular command
     *
     * @param command
     *            The command that has been pressed
     */
    protected void firePressed(Command command) {
        getState(command).down = true;
        getState(command).pressed = true;

        if (!isActive()) {
            return;
        }

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).controlPressed(command);
        }
    }

    /**
     * Fire notification to any interested listeners that a control has been
     * released indication an particular command should be stopped
     *
     * @param command
     *            The command that has been pressed
     */
    protected void fireReleased(Command command) {
        getState(command).down = false;

        if (!isActive()) {
            return;
        }

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).controlReleased(command);
        }
    }

    /**
     * A token representing the state of all the controls causing an command to
     * be invoked
     *
     * @author kevin
     */
    private class CommandState {
        /** True if one of the controls for this command is down */
        private boolean down;

        /** True if one of the controls for this command is pressed */
        private boolean pressed;

        /**
         * Check if a control for the command has been pressed since last call.
         *
         * @return True if the command has been pressed
         */
        public boolean isPressed() {
            if (pressed) {
                pressed = false;
                return true;
            }

            return false;
        }

        /**
         * Check if the last event we had indicated the control was pressed
         *
         * @return True if the control was pressed
         */
        public boolean isDown() {
            return down;
        }
    }

    /**
     * A simple listener to respond to input and look up any required commands
     *
     * @author kevin
     */
    private class InputListenerImpl extends InputAdapter {
        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#isAcceptingInput()
         */
        @Override
        public boolean isAcceptingInput() {
            return true;
        }

        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#keyPressed(int, char)
         */
        @Override
        public void keyPressed(int key, char c) {
            Command command = commands.get(new KeyControl(key));
            if (command != null) {
                firePressed(command);
            }
        }

        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#keyReleased(int, char)
         */
        @Override
        public void keyReleased(int key, char c) {
            Command command = commands.get(new KeyControl(key));
            if (command != null) {
                fireReleased(command);
            }
        }

        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#mousePressed(int, int, int)
         */
        @Override
        public void mousePressed(int button, int x, int y) {
            Command command = commands.get(new MouseButtonControl(button));
            if (command != null) {
                firePressed(command);
            }
        }

        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#mouseReleased(int, int, int)
         */
        @Override
        public void mouseReleased(int button, int x, int y) {
            Command command = commands.get(new MouseButtonControl(button));
            if (command != null) {
                fireReleased(command);
            }
        }

        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#controllerLeftPressed(int)
         */
        @Override
        public void controllerLeftPressed(int controller) {
            controllerDirectionPressed(controller, Direction.LEFT);
        }
        
        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#controllerLeftReleased(int)
         */
        @Override
        public void controllerLeftReleased(int controller) {
            controllerDirectionReleased(controller, Direction.LEFT);
        }

        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#controllerRightPressed(int)
         */
        @Override
        public void controllerRightPressed(int controller) {
            controllerDirectionPressed(controller, Direction.RIGHT);
        }

        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#controllerRightReleased(int)
         */
        @Override
        public void controllerRightReleased(int controller) {
            controllerDirectionReleased(controller, Direction.RIGHT);
        }

        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#controllerUpPressed(int)
         */
        @Override
        public void controllerUpPressed(int controller) {
            controllerDirectionPressed(controller, Direction.UP);
        }

        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#controllerUpReleased(int)
         */
        @Override
        public void controllerUpReleased(int controller) {
            controllerDirectionReleased(controller, Direction.UP);
        }

        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#controllerDownPressed(int)
         */
        @Override
        public void controllerDownPressed(int controller) {
            controllerDirectionPressed(controller, Direction.DOWN);
        }

        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#controllerDownReleased(int)
         */
        @Override
        public void controllerDownReleased(int controller) {
            controllerDirectionReleased(controller, Direction.DOWN);
        }
        
        private void controllerDirectionPressed(int controller, Direction direction) {
            controllerDirectionStateChanged(controller, direction, InputProvider.this::firePressed);
        }
        
        private void controllerDirectionReleased(int controller, Direction direction) {
            controllerDirectionStateChanged(controller, direction, InputProvider.this::fireReleased);
        }
        
        private void controllerDirectionStateChanged(int controller, Direction direction, Consumer<Command> action) {
            Command command = commands.get(new ControllerDirectionControl(controller, direction));
            if (command != null) {
                action.accept(command);
            }
        }

        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#controllerButtonReleased(int,
         *      int)
         */
        @Override
        public void controllerButtonReleased(int controller, int button) {
            Command command = commands.get(new ControllerButtonControl(controller, button));
            if (command != null) {
                fireReleased(command);
            }
        }
        
        /**
         * @see com.github.mathiewz.slick.util.InputAdapter#controllerButtonPressed(int,
         *      int)
         */
        @Override
        public void controllerButtonPressed(int controller, int button) {
            Command command = commands.get(new ControllerButtonControl(controller, button));
            if (command != null) {
                firePressed(command);
            }
        }

    }
}
