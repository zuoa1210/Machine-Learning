import java.io.Serializable;

import javax.rmi.CORBA.Util;

public class BP extends Classifier implements Serializable, OptionHandler {
	private static double eta = 0.9;
	private static double errMin = 0.1;
	private static double lambda = 1.0;
	private int j = 0;
	private double[][] v = null;
	private double[][] w = null;

	public BP() {

	}

	public BP(String[] options) throws Exception {
		setOptions(options);
	}

	public void setOptions(String[] options) {
		for (int i = 0; i < options.length; i++) {
			if (options[i].equals("-J")) {
				this.j = Integer.valueOf(options[i + 1]);
			}
		}
	}

	public Performance classify(DataSet dataset) throws Exception {
		Performance p = new Performance(dataset.attributes);
		double[] prediction = new double[w.length];
		Examples examples = dataset.getExamples();
		for (int i = 0; i < examples.size(); i++) {
			Example example = examples.get(i);
			int actual = (example.get(example.size() - 1).intValue()) == 0 ? -1 : 1;
			prediction = getDistribution(example);	
			p.add(actual, prediction);
		}
		return p;
	}

	public int classify(Example example) throws Exception {
		return Utils.maxIndex(getDistribution(example));
	}

	public Classifier clone() {
		return null;
	}

	public double[] getDistribution(Example example) throws Exception {
		double[] prediction = new double[w.length];// check w.length == k?
		int i = example.size() - 1;
		double[] x = new double[i];
		double[] fH = new double[this.j];
		double[] h = new double[this.j];
		double[] fO = new double[w.length];

		for (int a = 0; a < example.size() - 1; a++)
			x[a] = example.get(a);

		for (int b = 0; b < this.j; b++) {
			for (int c = 0; c < i; c++)
				fH[b] += x[c] * v[b][c];
		}
		for (int b = 0; b < this.j; b++) {
			h[b] = 1 / (1 + Math.pow(Math.E, (-1 * lambda * fH[b])));
		}

		for (int b = 0; b < w.length; b++) {
			for (int c = 0; c < h.length; c++)
				fO[b] += h[c] * w[b][c];
		}
		for (int b = 0; b < w.length; b++) {
			prediction[b] = 1 / (1 + Math.pow(Math.E, (-1 * lambda * fO[b])));
		}

		return prediction;
	}

	public void train(DataSet dataset) throws Exception {
		Examples examples = null;
		examples = dataset.homogeneousCoordinate().getExamples();
		Boolean flag = true;
		int num = 0;
		// Step One: initialize weight matrices V and W to small random values
		int m = 1, q = 1, M = examples.size();
		double err = 0;
		int i = examples.get(0).size() - 1, k = dataset.getAttributes().getClassAttribute().size();
		
		v = new double[this.j][i];
		w = new double[k][this.j];
		for (int a = 0; a < this.j; a++) {
			for (int b = 0; b < i; b++) {
				v[a][b] = Math.random();
			}
		}
		for (int a = 0; a < this.j; a++) {
			for (int b = 0; b < k; b++) {
				w[b][a] = Math.random();
			}
		}
		
		while (flag) {
			num++;
			
			// Step Two: training step starts here. present input and compute layers output
			double[] x = new double[examples.get(0).size() - 1];
			double[] fH = new double[this.j];
			double[] h = new double[this.j];
			double[] fO = new double[k];
			double[] o = new double[k];
			double[] y = new double[k];
			for (int a = 0; a < k; a++)
				y[a] = 0;
			double[] deltaO = new double[k];
			double[] deltaH = new double[this.j];

			for (int a = 0; a < examples.size(); a++) {
				Example example = examples.get(a);
				y[example.get(example.size() - 1).intValue()] = 1;
				for (int b = 0; b < example.size() - 1; b++) {
					x[b] = example.get(b);	
				}
				for(int z = 0; z < this.j; z++)
					fH[z] = 0;
				for (int b = 0; b < this.j; b++) {
					for (int c = 0; c < i; c++)
						fH[b] += x[c] * v[b][c];
				}
				for (int b = 0; b < this.j; b++) {
					double temp = -1 * lambda * fH[b];
					h[b] = 1 / (1 + Math.pow(Math.E, temp));
					
				}
				h[h.length - 1] = -1;
				for(int z = 0; z < k; z++)
					fO[z] = 0;
				for (int b = 0; b < k; b++) {
					for (int c = 0; c < this.j; c++) 
						fO[b] += h[c] * w[b][c];
				}
				for (int b = 0; b < k; b++) {
					double temp = -1 * lambda * fO[b];
					o[b] = 1 / (1 + Math.pow(Math.E, temp));
				}

				// Step Three: compute error value
				for (int b = 0; b < k; b++) {
					err += 0.5 * Math.pow((y[b] - o[b]), 2);
				}

				// Step Four: compute error signal vectors deltaO and deltaH for both layers
				for (int b = 0; b < k; b++) {
					deltaO[b] = (y[b] - o[b]) * (1 - o[b]) * o[b];
				}
				for (int b = 0; b < this.j; b++) {
					double temp = 0.0;
					for (int c = 0; c < k; c++) {
						temp += deltaO[c] * w[c][b];
					}
					deltaH[b] = h[b] * (1 - h[b]) * temp;
				}

				// Step Five: adjust output layer weights
				for (int b = 0; b < k; b++) {
					for (int c = 0; c < this.j; c++) {
						w[b][c] += eta * deltaO[b] * h[c];
					}
				}

				// Step Six: hidden layer weights are adjusted
				for (int b = 0; b < this.j; b++) {
					for (int c = 0; c < i; c++) {
						v[b][c] += eta * deltaH[b] * x[c];
					}
				}

				// Step Seven
				if (m < M) {
					m++;
					q++;
				}
			}
			// Step Eight
			if (err < errMin) {
				for (int a = 0; a < this.j; a++) {
					for (int b = 0; b < i; b++) {
						System.out.println("v[" + a + "][" + b + "]: " + v[a][b]);
					}
				}
				for (int a = 0; a < k; a++) {
					for (int b = 0; b < this.j; b++) {
						System.out.println("w[" + a + "][" + b + "]: " + w[a][b]);
					}
				}
				System.out.println("q: " + q);
				System.out.println("E: " + err);
				return;
			} else {
				err = 0;
				m = 1;
			}
			if (num == 50000) {
				throw new FailedToConvergeException("Training iteration reached 50000.");
			}
		}
	}

	public static void main(String[] args) {
		try {
			Evaluator evaluator = new Evaluator(new BP(), args);
			Performance performance = evaluator.evaluate();
			System.out.println(performance);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
