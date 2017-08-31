package gudiasoliveira.markovchain;

import java.util.List;

public class TransitionMatrix {
	private double[][] mMat;
	
	public TransitionMatrix(int states) {
		mMat = new double[states][states];
	}
	
	public <T>TransitionMatrix(MarkovChain<T> markovChain) {
		List<State<T>> states = markovChain.getStates();
		int size = states.size();
		mMat = new double[size][size];
		for (int i = 0; i < size; i++) {
			State<T> state = states.get(i);
			for (StateTransition<T> stateTrans : state.nextStates) {
				int j = states.indexOf(stateTrans.state);
				mMat[i][j] = stateTrans.getProbability();
			}
		}
	}
	
	public int size() {
		return mMat.length;
	}
	
	public double get(int i, int j) {
		return mMat[i][j];
	}
	
	public void set(int i, int j, double p) {
		mMat[i][j] = p;
	}
	
	public void set(double[][] mat) {
		int size = size();
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				mMat[i][j] = mat[i][j];
	}
	
	public double[] getP(double[] initialStateProbabilities, int timeInterval) {
		if (timeInterval < 0)
			return null;
		int size = size();
		double[] s = new double[size];
		for (int i = 0; i < size; i++)
			s[i] = initialStateProbabilities[i];
		if (timeInterval == 0) {
			return s;
		}
		for (int time = 0; time < timeInterval; time++) {
			double[] sNext = new double[size];
			for (int i = 0; i < size; i++) {
				sNext[i] = 0;
				for (int j = 0; j < size; j++) {
					sNext[i] += s[j] * mMat[j][i];
				}
			}
			for (int i = 0; i < size; i++)
				s[i] = sNext[i];
		}
		return s;
	}
	
	public double getProbability(double[] initialStateProbabilities,
			int stateIndex, int timeInterval) {
		return getP(initialStateProbabilities, timeInterval)[stateIndex];
	}
	
	public double getProbability(int initialStateIndex, int targetStateIndex, int timeInterval) {
		int size = size();
		double[] initialStatesProbs = new double[size];
		initialStatesProbs[initialStateIndex] = 1;
		return getProbability(initialStatesProbs, targetStateIndex, timeInterval);
	}
}