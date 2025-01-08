package saltsheep.ssl.api;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DecisionNode {
    private StateMachine states = new StateMachine();
    private IDecision decision;
    private final List<DecisionNode> subs = new LinkedList<>();
    private Task work;

    public void markStateSwitch(String state, DecisionNode subWhenTrue, DecisionNode subWhenFalse) {
        this.decision = new DecisionStateSwitch(this, state);
        if (subWhenTrue == null)
            subWhenTrue = new DecisionNode();
        if (subWhenFalse == null)
            subWhenFalse = new DecisionNode();
        addSub(subWhenFalse);
        addSub(subWhenTrue);
    }

    public void markProbability() {
        this.decision = new DecisionProbability();
    }

    public void markCustom(IDecision decision) {
        this.decision = decision;
    }

    public void addSubOdds(DecisionNode sub, double probability) {
        if (!(this.decision instanceof DecisionProbability))
            markProbability();
        addSub(sub);
        ((DecisionProbability) this.decision).add(probability);
    }

    public void addSub(DecisionNode sub) {
        sub.acceptStateMachine(this.states);
        this.subs.add(sub);
    }

    public void setWork(Task work) {
        this.work = work;
    }

    public void setState(String state, boolean flag) {
        this.states.set(state, flag);
    }

    public boolean getState(String state) {
        return this.states.get(state);
    }

    public StateMachine getStates() {
        return this.states;
    }

    public void decide() {
        if (this.work != null)
            this.work.start();
        if (!isLeaf()) {
            int next = this.decision.decide();
            if (next >= 0 &&
                    next < this.subs.size()) {
                DecisionNode nextNode = this.subs.get(next);
                if (nextNode != null) {
                    nextNode.decide();
                }
            }
        }
    }

    private void acceptStateMachine(StateMachine states) {
        if (this.states == states)
            return;
        this.states = states;
        for (DecisionNode sub : this.subs)
            sub.acceptStateMachine(states);
    }

    private boolean isLeaf() {
        return (this.decision == null || this.subs.isEmpty());
    }

    public interface IDecision {
        int decide();
    }

    public static class StateMachine {
        private final Map<String, Boolean> states = new HashMap<>();
        private boolean defaultValue = false;

        public boolean get(String key) {
            Boolean flag = this.states.get(key);
            return (flag != null) ? flag.booleanValue() : this.defaultValue;
        }

        public void set(String key, boolean flag) {
            this.states.put(key, Boolean.valueOf(flag));
        }

        public void reset() {
            this.states.clear();
        }

        public void setDefault(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    public static class DecisionStateSwitch implements IDecision {
        private final DecisionNode owner;
        private final String state;

        public DecisionStateSwitch(DecisionNode owner, String state) {
            this.owner = owner;
            this.state = state;
        }

        public int decide() {
            return this.owner.getState(this.state) ? 1 : 0;
        }
    }

    public static class DecisionProbability implements IDecision {
        private final List<Double> subs = new LinkedList<>();
        private double total = 0.0D;

        public void add(double probability) {
            if (probability < 0.0D)
                throw new IllegalArgumentException("Probability cannot be negative.");
            this.total += probability;
            this.subs.add(Double.valueOf(probability));
        }

        public int decide() {
            double decision = Math.random();
            if (decision >= this.total)
                return -1;
            int length = this.subs.size();
            double pass = 0.0D;
            for (int i = 0; i < length; i++) {
                double each = this.subs.get(i).doubleValue();
                pass += each;
                if (pass > decision)
                    return i;
            }
            return -1;
        }
    }
}