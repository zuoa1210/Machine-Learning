import java.io.Serializable;
import java.util.ArrayList;

public class NaiveBayes extends Classifier implements Serializable, OptionHandler {
	protected Attributes attributes;
	protected CategoricalEstimator classDistribution;
	protected ArrayList<ArrayList<Estimator>> classConditionalDistributions;

	public NaiveBayes() {

	}

	public NaiveBayes(String[] options) throws Exception {
		setOptions(options);
	}

	public Performance classify(DataSet dataSet) throws Exception {
		Performance p = new Performance(dataSet.attributes);

		for (int i = 0; i < dataSet.getExamples().size(); i++) {
			Example example = dataSet.getExamples().get(i);
			double[] prediction = getDistribution(example);
			p.add(example.get(example.size() - 1).intValue(), prediction);
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
		int size = this.attributes.get(this.attributes.getClassIndex()).size();
		double[] prediction = new double[size];

		double total = 0.0;
		for (int i = 0; i < size; i++) {
			total += this.classDistribution.dist.get(i);
		}

		for (int i = 0; i < size; i++) {
			Double temp = 1.0;
			for (int j = 0; j < example.size() - 1; j++) {
				if (this.classConditionalDistributions.get(i).get(j).getClass().equals(GaussianEstimator.class)) {
					GaussianEstimator ge = (GaussianEstimator) this.classConditionalDistributions.get(i).get(j);
					temp *= ge.getProbability(example.get(j));
				} else {
					CategoricalEstimator ce = (CategoricalEstimator) this.classConditionalDistributions.get(i).get(j);
					temp *= ce.getProbability(example.get(j));
				}
			}
			temp *= ((this.classDistribution.dist.get(i)) / total);
			prediction[i] = temp;
		}
		return prediction;
	}

	public void setOptions(String[] options) {
		try {

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void train(DataSet dataset) throws Exception {
		this.attributes = dataset.getAttributes();
		Examples elist = dataset.getExamples();

		NominalAttribute noa = (NominalAttribute) this.attributes.get(this.attributes.getClassIndex());
		int noasize = noa.size();
		this.classDistribution = new CategoricalEstimator(noasize);

		classConditionalDistributions = new ArrayList<ArrayList<Estimator>>();
		for (int i = 0; i < noasize; i++) {
			ArrayList<Estimator> einitial = new ArrayList<Estimator>();
			for (int j = 0; j < this.attributes.size() - 1; j++) {
				if (this.attributes.get(j).getClass().equals(NumericAttribute.class)) {
					einitial.add(new GaussianEstimator());
				} else {
					einitial.add(new CategoricalEstimator());
				}
			}
			classConditionalDistributions.add(einitial);
		}

		for (int i = 0; i < elist.size(); i++) {
			Example example = elist.get(i);
			int classindex = example.get(example.size() - 1).intValue();

			ArrayList<Estimator> estimator = this.classConditionalDistributions.get(classindex);
			for (int j = 0; j < example.size() - 1; j++) {
				if (estimator.get(j).getClass().equals(GaussianEstimator.class)) {
					GaussianEstimator ge = (GaussianEstimator) estimator.get(j);
					ge.add(example.get(j));
					estimator.set(j, ge);
				} else {
					CategoricalEstimator ce = (CategoricalEstimator) estimator.get(j);
					ce.add(example.get(j));
					estimator.set(j, ce);
				}
			}
			this.classConditionalDistributions.set(classindex, estimator);
			
			for (int j = 0; j < noasize; j++) {
				if (classindex == j) {
					int count = this.classDistribution.dist.get(j);
					this.classDistribution.dist.set(j, ++count);
				}
			}
		}
	}

	public static void main(String[] args) {
		try {
			Evaluator evaluator = new Evaluator(new NaiveBayes(), args);
			Performance performance = evaluator.evaluate();
			System.out.println(performance);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}