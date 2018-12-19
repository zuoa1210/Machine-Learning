import java.io.Serializable;

public class Perceptron extends Classifier implements Serializable, OptionHandler {
	private Boolean calOption = false;
	private static double eta = 0.9;
	private double[] w;

	public Perceptron() {

	}

	public Perceptron(String[] options) throws Exception {
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
				prediction = calibration(example, this.w);
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
		double f = 0.0;
		for (int i = 0; i < example.size() - 1; i++) {

			f += this.w[i] * example.get(i);
		}
		f += this.w[w.length - 1] * -1;
		if (f > 0)
			prediction[0] = 1;
		else
			prediction[0] = -1;
		return prediction;
	}

	public void train(DataSet dataset) throws Exception {
		Boolean converged = false;
		int num = 0;
		Examples examples = null;
		examples = dataset.homogeneousCoordinate().getExamples();

		this.w = new double[examples.get(0).size() - 1];

		while (!converged) {
			converged = true;
			num++;
			for (int i = 0; i < examples.size(); i++) {
				Example example = examples.get(i);
				double temp = 0.0;
				int y = (example.get(example.size() - 1) == 0) ? -1 : 1;
				for (int j = 0; j < example.size() - 1; j++) {
					temp += this.w[j] * example.get(j);
				}
				temp *= y;
				if (temp <= 0) {
					for (int j = 0; j < this.w.length; j++) {
						this.w[j] += eta * y * example.get(j);
					}
					converged = false;
				}
			}
			if (num == 50000) {
				break;
			}
		}
	}

	public double[] calibration(Example example, double[] weight) throws Exception {
		double[] prediction = new double[1];
		double[] pPos = new double[example.size() - 1];
		double[] pNeg = new double[example.size() - 1];
		double dPos = 0.0, dNeg = 0.0, pos = 1.0, neg = 1.0;
		
		for(int i = 0; i < example.size() - 1; i++) {
			dPos = -1 * weight[i] * example.get(i);
			pPos[i] = 1 / (1 + Math.pow(Math.E, dPos));
			pos *= pPos[i];

			dNeg = 1 * weight[i] * example.get(i);
			pNeg[i] = 1 / (1 + Math.pow(Math.E, dNeg));
			neg *= pNeg[i];
		}
		if(pos >= neg)
			prediction[0] = 1;
		else
			prediction[0] = -1;
		return prediction;
	}
	
	public static void main(String[] args) {
		try {
			String[] arg = { "-t", "/Users/zuoting/Downloads/p1/votes.mff", "-T",
					"/Users/zuoting/Downloads/p1/votes.mff" };
			Evaluator evaluator = new Evaluator(new Perceptron(), arg);
			Performance performance = evaluator.evaluate();
			System.out.println(performance);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
