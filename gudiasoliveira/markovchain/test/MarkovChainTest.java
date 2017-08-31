package gudiasoliveira.markovchain.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import gudiasoliveira.markovchain.MarkovChain;
import gudiasoliveira.markovchain.ProbabilityCondition;
import gudiasoliveira.markovchain.State;
import gudiasoliveira.markovchain.StateTransition;
import gudiasoliveira.markovchain.TransitionMatrix;

public class MarkovChainTest {
	
	@Test
	public void chainMatrixTest() {
		MarkovChain<Integer> chain = new MarkovChain<>();
		List<State<Integer>> states = Arrays.asList(
				new State<>(0),
				new State<>(1),
				new State<>(2),
				new State<>(3),
				new State<>(4)
		);
		chain.initialState = states.get(0);
		states.get(0).nextStates = Arrays.asList(
				new StateTransition<>(states.get(0), states.get(1), 1)
		);
		states.get(1).nextStates = Arrays.asList(
				new StateTransition<>(states.get(1), states.get(0), 0.25),
				new StateTransition<>(states.get(1), states.get(2), 0.75)
		);
		states.get(2).nextStates = Arrays.asList(
				new StateTransition<>(states.get(2), states.get(1), 0.5),
				new StateTransition<>(states.get(2), states.get(3), 0.5)
		);
		states.get(3).nextStates = Arrays.asList(
				new StateTransition<>(states.get(3), states.get(2), 0.75),
				new StateTransition<>(states.get(3), states.get(4), 0.25)
		);
		states.get(4).nextStates = Arrays.asList(
				new StateTransition<>(states.get(4), states.get(3), 1)
		);
		
		double[][] expectedMatrix = {
				{0,    1,    0,    0,    0   },
				{0.25, 0,    0.75, 0,    0   },
				{0,    0.5,  0,    0.5,  0   },
				{0,    0,    0.75, 0,    0.25},
				{0,    0,    0,    1,    0   }
		};
		
		TransitionMatrix matrix = new TransitionMatrix(chain);
		
		for (int i = 0; i < matrix.size(); i++) {
			for (int j = 0; j < matrix.size(); j++) {
				System.out.print("\t" + matrix.get(i, j));
				assertEquals(expectedMatrix[i][j], matrix.get(i, j), 0.001);
			}
			System.out.println();
		}
		
		assertEquals(chain, new MarkovChain<>(matrix, 0, Arrays.asList(0, 1, 2, 3, 4)));
		
		System.out.println("\nEXPECTED CHAIN:\n" + GraphvizMarkovGeneratorUtil.generateDot(chain));
		System.out.println("\nCHAIN FROM MATRIX:\n" + GraphvizMarkovGeneratorUtil.generateDot(
				 new MarkovChain<>(matrix, 0, Arrays.asList(0, 1, 2, 3, 4))));
	}
	
	@Test
	public void matrixToChainTest() {
		TransitionMatrix matrix = new TransitionMatrix(4);
		matrix.set(new double[][] {
			{1.0 / 3,  2.0 / 3,  0,        0      },
			{1.0 / 3,  1.0 / 3,  1.0 / 3,  0      },
			{1.0 / 3,  1.0 / 3,  1.0 / 6,  1.0 / 6},
			{0,        0,        0,        1}
		});
		
		MarkovChain<String> resultChain =
				new MarkovChain<>(matrix, 0, Arrays.asList("A", "B", "C", "D"));
		
		MarkovChain<String> expectedChain = new MarkovChain<>();
		List<State<String>> expectedStates = Arrays.asList(
				new State<>("A"),
				new State<>("B"),
				new State<>("C"),
				new State<>("D")
		);
		expectedChain.initialState = expectedStates.get(0);
		expectedStates.get(0).nextStates = Arrays.asList(
				new StateTransition<>(expectedStates.get(0), expectedStates.get(0), 1.0 / 3),
				new StateTransition<>(expectedStates.get(0), expectedStates.get(1), 2.0 / 3)
		);
		expectedStates.get(1).nextStates = Arrays.asList(
				new StateTransition<>(expectedStates.get(1), expectedStates.get(0), 1.0 / 3),
				new StateTransition<>(expectedStates.get(1), expectedStates.get(1), 1.0 / 3),
				new StateTransition<>(expectedStates.get(1), expectedStates.get(2), 1.0 / 3)
		);
		expectedStates.get(2).nextStates = Arrays.asList(
				new StateTransition<>(expectedStates.get(2), expectedStates.get(0), 1.0 / 3),
				new StateTransition<>(expectedStates.get(2), expectedStates.get(1), 1.0 / 3),
				new StateTransition<>(expectedStates.get(2), expectedStates.get(2), 1.0 / 6),
				new StateTransition<>(expectedStates.get(2), expectedStates.get(3), 1.0 / 6)
		);
		expectedStates.get(3).nextStates = Arrays.asList(
				new StateTransition<>(expectedStates.get(3), expectedStates.get(3), 1)
		);
		
		assertEquals(expectedChain, resultChain);
		
		TransitionMatrix expectedMatrix = new TransitionMatrix(resultChain);
		assertEquals(expectedMatrix.size(), matrix.size());
		for (int i = 0; i < expectedMatrix.size(); i++) {
			for (int j = 0; j < expectedMatrix.size(); j++) {
				assertEquals(expectedMatrix.get(i, j), matrix.get(i, j), 0.001);
			}
		}
		
		System.out.println("\n\nCHAIN 2:");
		System.out.println(GraphvizMarkovGeneratorUtil.generateDot(resultChain));
	}
	
	@Test
	public void calcPTest() {
		TransitionMatrix matrix = new TransitionMatrix(4);
		matrix.set(new double[][] {
			{1.0 / 3,  2.0 / 3,  0,        0      },
			{1.0 / 3,  1.0 / 3,  1.0 / 3,  0      },
			{1.0 / 3,  1.0 / 3,  1.0 / 6,  1.0 / 6},
			{0,        0,        0,        1}
		});
		
		// p_2
		double[] expectedResult = {1.0 / 3,  4.0 / 9,  11.0 / 54,  1.0 / 54};
		double[] result = matrix.getP(
				new double[] {2.0 / 3,  1.0 / 3,  0,  0},
				2);
		assertEquals(expectedResult.length, result.length);
		for (int i = 0; i < result.length; i++)
			assertEquals(expectedResult[i], result[i], 0.001);
		
		// p_3
		expectedResult = new double[] {0.32716, 0.43827, 0.18210, 0.05247};
		result = matrix.getP(
				new double[] {2.0 / 3,  1.0 / 3,  0,  0},
				3);
		assertEquals(expectedResult.length, result.length);
		for (int i = 0; i < result.length; i++)
			assertEquals(expectedResult[i], result[i], 0.0001);
		
		// p_1
		expectedResult = new double[] {1.0 / 3,  5.0 / 9,  1.0 / 9, 0};
		result = matrix.getP(
				new double[] {2.0 / 3,  1.0 / 3,  0,  0},
				1);
		assertEquals(expectedResult.length, result.length);
		for (int i = 0; i < result.length; i++)
			assertEquals(expectedResult[i], result[i], 0.0001);
	}
	
	@Test
	public void getProbabilityTest() {
		TransitionMatrix matrix = new TransitionMatrix(4);
		matrix.set(new double[][] {
			{1.0 / 3,  2.0 / 3,  0,        0      },
			{1.0 / 3,  1.0 / 3,  1.0 / 3,  0      },
			{1.0 / 3,  1.0 / 3,  1.0 / 6,  1.0 / 6},
			{0,        0,        0,        1}
		});
		
		// P {stateIndex = 2, time = 2}
		double res = matrix.getProbability(new double[] {2.0 / 3,  1.0 / 3,  0,  0},
				2, 2);
		assertEquals(11.0 / 54, res, 0.001);
		// P {stateIndex = 0, time = 3}
		res = matrix.getProbability(new double[] {2.0 / 3,  1.0 / 3,  0,  0},
				0, 3);
		assertEquals(0.32716, res, 0.00001);
		// P {stateIndex = 3, time = 1}
		res = matrix.getProbability(new double[] {2.0 / 3,  1.0 / 3,  0,  0},
				3, 1);
		assertEquals(0, res, 0.001);
		
		// P{initialStateIndex = 1, stateIndex = 0, time = 2}
		res = matrix.getProbability(1, 0, 2);
		assertEquals(1.0 / 3, res, 0.001);
		// P{initialStateIndex = 1, stateIndex = 3, time = 3}
		res = matrix.getProbability(1, 3, 3);
		assertEquals(9.0 / 108, res, 0.0001);
		// P{initialStateIndex = 2, stateIndex = 0, time = 1}
		res = matrix.getProbability(2, 0, 1);
		assertEquals(1.0 / 3, res, 0.0001);
		// P{initialStateIndex = 2, stateIndex = 2, time = 0}
		res = matrix.getProbability(2, 2, 0);
		assertEquals(1.0, res, 0.0001);
		// P{initialStateIndex = 2, stateIndex = 3, time = 0}
		res = matrix.getProbability(2, 3, 0);
		assertEquals(0.0, res, 0.0001);
		// P{initialStateIndex = 2, stateIndex = 1, time = 0}
		res = matrix.getProbability(2, 1, 0);
		assertEquals(0.0, res, 0.0001);
		// P{initialStateIndex = 2, stateIndex = 0, time = 0}
		res = matrix.getProbability(2, 0, 0);
		assertEquals(0.0, res, 0.0001);
	}
	
	@Test
	public void getChainProbabilityTest() {
		TransitionMatrix matrix = new TransitionMatrix(4);
		matrix.set(new double[][] {
			{1.0 / 3,  2.0 / 3,  0,        0      },
			{1.0 / 3,  1.0 / 3,  1.0 / 3,  0      },
			{1.0 / 3,  1.0 / 3,  1.0 / 6,  1.0 / 6},
			{0,        0,        0,        1}
		});
		MarkovChain<Character> markov = new MarkovChain<>(matrix, 1,
				Arrays.asList('A', 'B', 'C', 'D'));
		
		double res;
		res = markov.getProbability(2, 'A');
		assertEquals(1.0 / 3, res, 0.0001);
		res = markov.getProbability(3, 'D');
		assertEquals(9.0 / 108, res, 0.0001);
		
//		markov = new MarkovChain<>(matrix, 2,
//				Arrays.asList('A', 'B', 'C', 'D'));
		markov.changeInitialState('C');
		
		res = markov.getProbability(1, 'A');
		assertEquals(1.0 / 3, res, 0.0001);
		res = markov.getProbability(1, 'D');
		assertEquals(1.0 / 6, res, 0.0001);
		res = markov.getProbability(0, 'C');
		assertEquals(1.0, res, 0.0001);
		res = markov.getProbability(0, 'A');
		assertEquals(0.0, res, 0.0001);
		res = markov.getProbability(0, 'B');
		assertEquals(0.0, res, 0.0001);
		res = markov.getProbability(0, 'D');
		assertEquals(0.0, res, 0.0001);
		
		res = markov.getProbability(2, new ProbabilityCondition<Character>() {
			@Override
			public boolean test(Character valueToTest) {
				return valueToTest > 'B';
			}
		});
		assertEquals(12.0 / 36, res, 0.0001);
	}
}
