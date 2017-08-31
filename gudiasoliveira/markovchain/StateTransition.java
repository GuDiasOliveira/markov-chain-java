package gudiasoliveira.markovchain;

public class StateTransition<T> {
	private State<T> mOriginState;
	
	public double probabilityWeight;
	public State<T> state;
	
	public StateTransition(State<T> originState) {
		mOriginState = originState;
	}
	
	public StateTransition(State<T> originState, State<T> state, double probabilityWeight) {
		mOriginState = originState;
		this.state = state;
		this.probabilityWeight = probabilityWeight;
	}
	
	public double getProbability() {
		return probabilityWeight / mOriginState.getWeightSum();
	}
	
	public State<T> getOriginState() {
		return mOriginState;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StateTransition<?>))
			return false;
		@SuppressWarnings("unchecked")
		StateTransition<Object> other = (StateTransition<Object>) obj;
		if (!this.mOriginState.equals(other.mOriginState))
			return false;
		if (!this.state.equals(other.state))
			return false;
		return true;
	}
}