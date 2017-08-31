package gudiasoliveira.markovchain;

public interface ProbabilityCondition<T> {
	boolean test(T valueToTest);
}