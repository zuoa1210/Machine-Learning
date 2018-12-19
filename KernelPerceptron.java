import java.io.Serializable;

public class KernelPerceptron extends Classifier implements Serializable, OptionHandler {
	private Boolean calOption = false;
	private double[] alpha;
	private Examples trainEx = null;

	public KernelPerceptron() {

	}

	public KernelPerceptron(String[] options) throws Exception {
		setOptions(options);
	}

	public void setOptions(String[] options) {
		for (int i = 0; i < options.length; i++) {
			if (options[i].equals("-fc")) {
				this.calOption = true;
			}
		}
	}

	public Performance classify(DataSet dataset) throws Exception {
		Performance p = new Performance(dataset.attributes);
		double[] prediction = new double[1];
		Examples examples = dataset.getExamples();
		for (int i = 0; i < examples.size(); i++) {
			Example example = examples.get(i);
			int actual = (example.get(example.size() - 1) == 0) ? -1 : 1;
			if (!this.calOption) {
				prediction = getDistribution(example);
			} else {
				prediction = calibration(example, this.alpha);
			}
			p.add(actual, prediction);
		}
		return p;
	}

	public int classify(Example example) throws Exception {
		double[] prediction = getDistribution(example);
		return (int) prediction[0];
	}

	public Classifier clone() {
		return null;
	}

	public double[] getDistribution(Example example) throws Exception {
		double[] prediction = new double[1];
		double f = 0.0, att = 0.0, attI = 0.0;

		for (int i = 0; i < this.trainEx.size(); i++) {
			Example e = this.trainEx.get(i);
			double k = 0.0;
			for (int h = 0; h < e.size() - 1; h++) {
				att = example.get(h);
				attI = e.get(h);
				k += att * attI;
			}
			double y = (e.get(e.size() - 1) == 0) ? -1 : 1;
			f += this.alpha[i] * y * Math.pow(k, 2);
		}
		if (f > 0)
			prediction[0] = 1;
		else
			prediction[0] = -1;
		return prediction;
	}

	public void train(DataSet dataset) throws Exception {
		Examples examples = null;
		examples = dataset.homogeneousCoordinate().getExamples();
		this.trainEx = examples;
		alpha = new double[examples.size()];
		for (int i = 0; i < examples.size(); i++)
			alpha[i] = 0.0;
		Boolean converged = false;
		int num = 0;
		while (!converged) {
			converged = true;
			num++;
			for (int i = 0; i < examples.size(); i++) {
				Example exampleI = examples.get(i);
				int yI = (exampleI.get(exampleI.size() - 1) == 0) ? -1 : 1;
				double temp = 0.0, attI = 0.0, attJ = 0.0;

				for (int j = 0; j < examples.size(); j++) {
					Example exampleJ = examples.get(j);
					int yJ = (exampleJ.get(exampleJ.size() - 1) == 0) ? -1 : 1;
					double k = 0.0;
					for (int h = 0; h < exampleJ.size() - 1; h++) {
						attI = exampleI.get(h);
						attJ = exampleJ.get(h);
						k += attI * attJ;
					}
					temp += alpha[j] * yJ * Math.pow(k, 2);
				}
				temp *= yI;
				if (temp <= 0) {
					alpha[i]++;
					converged = false;
				}
			}
			System.out.println(num);
			if (num == 50000) {
				break;
			}
		}
	}

	public double[] calibration(Example example, double[] alpha) throws Exception {
		double[] prediction = new double[1];
		double[] pPos = new double[this.trainEx.size()];
		double[] pNeg = new double[this.trainEx.size()];

		double dPos = 0.0, dNeg = 0.0, att = 0.0, pos = 1.0, neg = 1.0;

		double y = (example.get(example.size() - 1) == 0) ? -1 : 1;
		for (int h = 0; h < example.size() - 1; h++) {
			att = example.get(h);
			dPos = -1 * this.alpha[h] * Math.pow(att, 2);
			dNeg = 1 * this.alpha[h] * Math.pow(att, 2);

			pPos[h] = 1 / (1 + Math.pow(Math.E, dPos));
			pos *= pPos[h];
			pNeg[h] = 1 / (1 + Math.pow(Math.E, dNeg));
			neg *= pNeg[h];
		}
		pos *= y;
		neg *= y;
		if (pos >= neg)
			prediction[0] = 1;
		else
			prediction[0] = -1;
		return prediction;
	}

	public static void main(String[] args) {
		try {
			String[] arg = { "-t", "/Users/zuoting/Downloads/p1/monks2.te.mff", "-T",
					"/Users/zuoting/Downloads/p1/monks2.te.mff" };
			Evaluator evaluator = new Evaluator(new KernelPerceptron(), arg);
			Performance performance = evaluator.evaluate();
			System.out.println(performance);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
