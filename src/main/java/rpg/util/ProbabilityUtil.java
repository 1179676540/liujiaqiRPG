package rpg.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * 概率生成工具
 * @author ljq
 *
 */
public class ProbabilityUtil {

	private final double[] probability;
	private final int[] alias;
	private final int length;
	private final Random rand;

	public ProbabilityUtil(List<Double> prob) {
		this(prob, new Random());
	}

	public ProbabilityUtil(List<Double> prob, Random rand) {
		//对输入进行检查
		if (prob == null || rand == null)
			throw new NullPointerException();
		if (prob.size() == 0)
			throw new IllegalArgumentException("Probability vector must be nonempty.");

		this.rand = rand;
		this.length = prob.size();
		this.probability = new double[length];
		this.alias = new int[length];

		double[] probtemp = new double[length];
		Deque<Integer> small = new ArrayDeque<Integer>();
		Deque<Integer> large = new ArrayDeque<Integer>();

		//按概率将元素分成两组
		for (int i = 0; i < length; i++) {
			probtemp[i] = prob.get(i) * length; 
			if (probtemp[i] < 1.0)
				small.add(i);
			else
				large.add(i);
		}

		while (!small.isEmpty() && !large.isEmpty()) {
			int less = small.pop();
			int more = large.pop();
			probability[less] = probtemp[less];
			alias[less] = more;
			probtemp[more] = probtemp[more] - (1.0 - probability[less]);
			if (probtemp[more] < 1.0)
				small.add(more);
			else
				large.add(more);
		}

		//所有的事情都在一个列表中，这意味着剩余的概率都应该是1/N。基于此，适当地设置它们。
		while (!small.isEmpty())
			probability[small.pop()] = 1.0;
		while (!large.isEmpty())
			probability[large.pop()] = 1.0;
	}

	/**
	 * Samples a value from the underlying distribution.
	 * 
	 */
	public int next() {
		int column = rand.nextInt(length);
		//进行选择选项
		boolean coinToss = rand.nextDouble() < probability[column];
		//根据结果，返回
		return coinToss ? column : alias[column];
	}
}