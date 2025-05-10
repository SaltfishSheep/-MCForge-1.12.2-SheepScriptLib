package saltsheep.ssl.api;

import saltsheep.ssl.common.IGetter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Callable;

public class StateMachine {

    private final Map<String, State> states = new HashMap<>();
    private final List<Transition> anytimeTrans = new LinkedList<>();
    private boolean anytimeEnabled = false;
    private String defaultState;
    private State currentState;

    public StateMachine() {}

    public StateMachine(String defaultState) {
        this.defaultState = defaultState;
    }

    public void setDefault(String defaultState) {
        this.defaultState = defaultState;
    }

    public void setDefault(String defaultState, State state) {
        this.defaultState = defaultState;
    }

    public void setAnytimeEnabled(boolean anytimeEnabled) {
        this.anytimeEnabled = anytimeEnabled;
    }

    public void addAnytimeTransition(String destination, Callable<Boolean> condition) {
        anytimeTrans.add(new Transition(destination, condition));
    }

    public void addAnytimeTransition(String destination, Callable<Boolean> condition, long duration, IGetter<Long> tickGetter) {
        anytimeTrans.add(new TransitionTimer(destination, condition, duration, tickGetter));
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
            if (anytimeEnabled) {
                for (Transition transition : anytimeTrans) {
                    if (transition.condition.call()) {
                        canTrans = true;
                        transTo = transition.destination;
                        break;
                    }
                }
            }
            if (!canTrans) {
                for (Transition transition : currentState.transitions) {
                    if (transition.condition.call()) {
                        canTrans = true;
                        transTo = transition.destination;
                        break;
                    }
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
            for (Transition transition:transitions)
                transition.onStateEntered();
        }

        public void stay() {
            if (this.onStayed != null)
                this.onStayed.run();
            for (Transition transition:transitions)
                transition.onStateStayed();
        }

        public void exit() {
            if (this.onExited != null)
                this.onExited.run();
            for (Transition transition:transitions)
                transition.onStateExited();
        }

        public State addTransition(String destination, Callable<Boolean> condition) {
            this.transitions.add(new Transition(destination, condition));
            return this;
        }

        public State addTransition(String destination, Callable<Boolean> condition, long duration, IGetter<Long> tickGetter) {
            this.transitions.add(new TransitionTimer(destination, condition, duration, tickGetter));
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
        protected String destination;
        @Nonnull
        protected Callable<Boolean> condition;
        public Transition(@Nullable String destination, @Nonnull Callable<Boolean> condition) {
            this.destination = destination;
            this.condition = condition;
        }
        public void onStateEntered() {}
        public void onStateStayed() {}
        public void onStateExited() {}
    }

    public static class TransitionTimer extends Transition {
        protected long startTick;
        protected final long duration;
        protected final IGetter<Long> tickGetter;
        public TransitionTimer(@Nullable String destination, @Nonnull Callable<Boolean> condition, long duration, @Nonnull IGetter<Long> tickGetter) {
            super(destination, condition);
            this.duration = duration;
            this.tickGetter = tickGetter;
            this.condition = ()-> this.tickGetter.get() - this.startTick >= duration && condition.call();
        }
        @Override
        public void onStateEntered() {
            startTick = tickGetter.get();
        }
        @Override
        public void onStateExited() {
            startTick = Long.MIN_VALUE;
        }
    }

}
