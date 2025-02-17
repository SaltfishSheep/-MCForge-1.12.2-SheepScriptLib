package saltsheep.ssl.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Callable;

public class StateMachine {

    private Map<String, State> states = new HashMap<>();
    private String defaultState;
    private State currentState;

    public StateMachine() {}

    public StateMachine(String defaultState) {
        this.defaultState = defaultState;
    }

    public State setDefault(String defaultState) {
        this.defaultState = defaultState;
        State result = states.get(defaultState);
        if (result == null) {
            result = new State();
            states.put(defaultState, result);
        }
        return result;
    }

    @Nullable
    public State getState(String stateName) {
        return states.get(stateName);
    }

    public State addState(String stateName) {
        return addState(stateName, new State());
    }

    public State addState(String stateName, Task task) {
        return addState(stateName, new StateTask(task));
    }

    public State addState(String stateName, State state) {
        states.put(stateName, state);
        return state;
    }

    public StateMachine delState(String stateName) {
        states.remove(stateName);
        for (State each:this.states.values()) {
            Iterator<Transition> iterator = each.transitions.iterator();
            while(iterator.hasNext())
                if (iterator.next().destination.equals(stateName))
                    iterator.remove();
        }
        return this;
    }

    public void update() throws Exception {
        if (currentState == null) {
            currentState = states.get(defaultState);
            if (currentState == null)
                return;
            currentState.enter();
        } else {
            boolean canTrans = false;
            String transTo = null;
            for (Transition transition: currentState.transitions) {
                if (transition.condition.call()) {
                    canTrans = true;
                    transTo = transition.destination;
                    break;
                }
            }
            if (canTrans) {
                currentState.exit();
                if (transTo != null) {
                    State toState = states.get(transTo);
                    currentState = toState;
                    toState.enter();
                } else
                    currentState = null;
            } else
                currentState.stay();
        }
    }

    public static class State {
        public Runnable onEntered;
        public Runnable onStayed;
        public Runnable onExited;
        private final List<Transition> transitions = new LinkedList<>();

        public void enter() {
            if (this.onEntered != null)
                this.onEntered.run();
        }

        public void stay() {
            if (this.onStayed != null)
                this.onStayed.run();
        }

        public void exit() {
            if (this.onExited != null)
                this.onExited.run();
        }

        public State addTransition(String destination, Callable<Boolean> condition) {
            this.transitions.add(new Transition(destination, condition));
            return this;
        }

        public State clearTranstions() {
            this.transitions.clear();
            return this;
        }
    }

    public static class StateTask extends State {
        public StateTask(Task task) {
            this.onEntered = task::start;
            this.onExited = task::stop;
        }
    }

    public static class Transition {
        @Nullable
        private final String destination;
        @Nonnull
        private final Callable<Boolean> condition;
        public Transition(@Nullable String destination, @Nonnull Callable<Boolean> condition) {
            this.destination = destination;
            this.condition = condition;
        }
    }

}
