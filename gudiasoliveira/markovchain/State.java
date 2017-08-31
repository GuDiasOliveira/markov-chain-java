package gudiasoliveira.markovchain;

import java.util.ArrayList;
import java.util.List;

public class State<T> {
	public T val;
	public List<StateTransition<T>> nextStates;
	
	public State() {
	}
	
	public State(T val) {
		this.val = val;
	}
	
	public State(T val, List<StateTransition<T>> nextStates) {
		this.val = val;
		this.nextStates = new ArrayList<>();
		this.nextStates.addAll(nextStates);
	}
	
	public float getWeightSum() {
		float sum = 0.0f;
		for (StateTransition<T> state : nextStates)
			sum += state.probabilityWeight;
		return sum;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof State<?>))
			return false;
		State<?> other = (State<?>) obj;
		return this.val.equals(other.val);
	}
}
