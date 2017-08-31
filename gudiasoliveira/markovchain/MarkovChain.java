package gudiasoliveira.markovchain;

import java.util.ArrayList;
import java.util.List;

public class MarkovChain<T> {
	public State<T> initialState;
	
	public MarkovChain() {
	}
	
	public MarkovChain(State<T> initialState) {
		this.initialState = initialState;
	}
	
	public MarkovChain(TransitionMatrix matrix, int initialStateIndex, List<T> stateVals) {
		int size = matrix.size();
		List<State<T>> states = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			State<T> state = new State<>();
			state.val = stateVals.get(i);
			state.nextStates = new ArrayList<>();
			states.add(state);
		}
		initialState = states.get(initialStateIndex);
		for (int i = 0; i < size; i++) {
			State<T> state = states.get(i);
			for (int j = 0; j < size; j++) {
				double p = matrix.get(i, j);
				if (p != 0) {
					StateTransition<T> stateTrans = new StateTransition<>(state);
					stateTrans.probabilityWeight = p;
					stateTrans.state = states.get(j);
					state.nextStates.add(stateTrans);
				}
			}
		}
	}
	
	public List<State<T>> getStates() {
		List<State<T>> states = new ArrayList<>();
		getStates(states, initialState);
		return states;
	}
	
	private void getStates(List<State<T>> states, State<T> fromState) {
		if (states.contains(fromState))
			return;
		states.add(fromState);
		for (StateTransition<T> stateTrans : fromState.nextStates) {
			State<T> nextState = stateTrans.state;
			getStates(states, nextState);
		}
	}
	
	public boolean changeInitialState(State<T> state) {
		for (State<T> s : getStates()) {
			if (s == state) {
				initialState = s;
				return true;
			}
		}
		return false;
	}
	
	public boolean changeInitialState(T stateValue) {
		for (State<T> s : getStates()) {
			if (s.val.equals(stateValue)) {
				initialState = s;
				return true;
			}
		}
		return false;
	}
	
	public double getProbability(int timeInterval, ProbabilityCondition<T> condition) {
		TransitionMatrix matrix = new TransitionMatrix(this);
		double p = 0;
		List<State<T>> states = getStates();
		for (State<T> state : states) {
			if (condition.test(state.val))
				p += matrix.getProbability(
						states.indexOf(initialState),
						states.indexOf(state), timeInterval);
		}
		return p;
	}
	
	public double getProbability(int timeInterval, T value) {
		TransitionMatrix matrix = new TransitionMatrix(this);
		double p = 0;
		List<State<T>> states = getStates();
		for (State<T> state : states) {
			if (value.equals(state.val))
				p += matrix.getProbability(
						states.indexOf(initialState),
						states.indexOf(state), timeInterval);
		}
		return p;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MarkovChain<?>))
			return false;
		@SuppressWarnings("unchecked")
		MarkovChain<Object> other = (MarkovChain<Object>) obj;
		
		List<State<T>> thisStates = this.getStates();
		List<State<Object>> otherStates = other.getStates();
		
		if (thisStates.size() != otherStates.size())
			return false;
		
		for (State<T> state : thisStates) {
			int otherStateIndex = otherStates.indexOf(state);
			if (otherStateIndex < 0)
				return false;
			State<Object> otherState = otherStates.get(otherStateIndex);
			if ((state.nextStates == null) ^ (otherState.nextStates == null)) // ^ exclusive or
				return false;
			if (state.nextStates != null) {
				if (state.nextStates.size() != otherState.nextStates.size())
					return false;
				if (!state.nextStates.containsAll(otherState.nextStates))
					return false;
			}
		}
		
		return true;
	}
}
