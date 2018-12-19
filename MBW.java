import java.io.Serializable;

public class MBW extends Classifier implements Serializable, OptionHandler {
	private double[] u;
	private double[] v;
	private double[] wU;
	private double[] wV;
	private boolean voteOption = false;

	public MBW() {

	}

	public MBW(String[] options) throws Exception {
		setOptions(options);
	}

	public void setOptions(String[] options) {
		for (int i = 0; i < options.length; i++) {
			if (options[i].equals("-v")) {
				this.voteOption = true;
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
			prediction = getDistribution(example);
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
		double f = 0.0, ita = 1.0;
		if (!this.voteOption) {
			for (int z = 0; z < example.size() - 1; z++) {
				if (example.get(z) >= 0)
					f += example.get(z) * (u[z] - v[z]);
			}
		} else {
			for (int z = 0; z < example.size() - 1; z++) {
				if (example.get(z) >= 0)
					f += example.get(z) * (wU[z] - wV[z]);
			}
		}
		f -= ita;
		if (f > 0)
			prediction[0] = 1;
		else
			prediction[0] = -1;
		return prediction;
	}

	public void train(DataSet dataset) throws Exception {
		Examples examples = dataset.getExamples();
		int i = 0;
		double itaPos = 2.0, itaNeg = 1.0, ita = 1.0, alpha = 1.5, beta = 0.5, f = 0.0, M = 1.0;
		u = new double[examples.get(0).size() - 1];
		v = new double[examples.get(0).size() - 1];
		wU = new double[examples.get(0).size() - 1];
		wV = new double[examples.get(0).size() - 1];
		for (int z = 0; z < u.length; z++)
			u[z] = itaPos;
		for (int z = 0; z < v.length; z++)
			v[z] = itaNeg;
		int[] c = new int[examples.size()];
		
		for (int j = 0; j < examples.size(); j++) {
			Example example = examples.get(j);
			f = 0.0;
			for (int z = 0; z < example.size() - 1; z++) {
				if(example.get(z) >= 0)
					f += example.get(z) * (u[z] - v[z]);
			}
			f -= ita;
			f = (f > 0) ? 1 : -1;
			int fX = (example.get(example.size() - 1)) == 0 ? -1 : 1;
			if (f != fX && (f * fX <= M)) {
				i++;
			
					if (fX > 0) {
						for (int z = 0; z < u.length; z++) {
							u[z] = (u[z] * alpha) * (1 + example.get(z));
							v[z] = (v[z] * beta) * (1 - example.get(z));
						}
					} else {
						for (int z = 0; z < u.length; z++) {
							u[z] = (u[z] * beta) * (1 - example.get(z));
							v[z] = (v[z] * alpha) * (1 + example.get(z));
						}
					}
					wU = Utils.voting(c, u);
					wV = Utils.voting(c, v);
			} else {
				c[i] += 1;
			}
		}
	}

	public static void main(String[] args) {
		try {
			Evaluator evaluator = new Evaluator(new MBW(), args);
			Performance performance = evaluator.evaluate();
			System.out.println(performance);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
