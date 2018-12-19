import java.io.Serializable;

public class IBk extends Classifier implements Serializable, OptionHandler {
	protected DataSet dataset;
	protected Scaler scaler;
	protected int k = 3;

	public IBk() {

	}

	public IBk(String[] options) throws Exception {
		setOptions(options);
	}

	public Performance classify(DataSet dataset) throws Exception {
		Performance p = new Performance(dataset.attributes);
		
		for (int i = 0; i < dataset.getExamples().size(); i++) {
			Example example = dataset.getExamples().get(i);
			double[] distance = getDistribution(example);
			p.add(example.get(example.size() - 1).intValue(), distance);
		}
		return p;
	}

	public int classify(Example query) throws Exception {
		return Utils.maxIndex(getDistribution(query));
	}

	public Classifier clone() {
		return null;
	}

	public double[] getDistribution(Example query) throws Exception {
		int esize = this.dataset.getExamples().size();
		double[] distance = new double[esize];
		Examples examples = this.dataset.getExamples();
		query = scaler.scale(query);

		for (int i = 0; i < esize; i++) {
			double numericd = 0.0;
			double categoricald = 0.0;
			Example example = examples.get(i);
			
			for (int j = 0; j < query.size() - 1; j++) {
				if (this.dataset.getAttributes().get(j).getClass().equals(NumericAttribute.class)) {
					numericd += Math.pow((query.get(j) - example.get(j)), 2);
				} else {
					if (!example.get(j).equals(query.get(j))) {
						categoricald += 1;
					}
				}
			}
			distance[i] = Math.sqrt(numericd + categoricald);
		}

		double[] temp = Utils.InsertSort(distance);

		int classnum = this.dataset.getAttributes().get(this.dataset.getAttributes().getClassIndex()).size();
		double[] classcountlist = new double[classnum];

		for (int i = 0; i < this.k; i++) {
			for (int j = 0; j < distance.length; j++) {
				if (temp[i] == distance[j] && i < this.k) {
					Example e = examples.get(j);
					double label = e.get(e.size()-1);
					classcountlist[(int) label] += 1;
					i++;
				}
			}
		}
		return classcountlist;
	}

	public void setK(int k) {
		this.k = k;
	}

	public void setOptions(String args[]) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-k")) {
				this.setK(Integer.parseInt(args[++i]));
			}
		}
	}

	public void train(DataSet dataset) throws Exception {
		scaler = new Scaler();
		scaler.configure(dataset);
		dataset = scaler.scale(dataset);
		this.dataset = dataset;
	}

	public static void main(String[] args) {
		try {
			Evaluator evaluator = new Evaluator(new IBk(), args);
			Performance performance = evaluator.evaluate();
			System.out.println(performance);
		} 
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
	}
}