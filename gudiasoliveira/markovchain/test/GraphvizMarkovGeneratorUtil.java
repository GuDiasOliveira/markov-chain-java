package gudiasoliveira.markovchain.test;

import gudiasoliveira.markovchain.MarkovChain;
import gudiasoliveira.markovchain.State;
import gudiasoliveira.markovchain.StateTransition;

public final class GraphvizMarkovGeneratorUtil {
	
	private GraphvizMarkovGeneratorUtil() {
	}
	
	public static String generateDot(MarkovChain<?> chain) {
		StringBuilder dotStr = new StringBuilder();
		dotStr.append("digraph {\n");
		for (State<?> state : chain.getStates()) {
			for (StateTransition<?> stateTrans : state.nextStates) {
				dotStr.append("    ");
				dotStr.append(stateTrans.getOriginState().val);
				dotStr.append(" -> ");
				dotStr.append(stateTrans.state.val);
				dotStr.append("[label=\"");
				dotStr.append(stateTrans.getProbability());
				dotStr.append("\"];\n");
			}
		}
		dotStr.append("}");
		return dotStr.toString();
	}
}