import java.util.ArrayList;

public class Scaler extends Object {

	private Attributes attributes;
	private ArrayList<Double> mins = new ArrayList<Double>();
	private ArrayList<Double> maxs = new ArrayList<Double>();

	public Scaler() {

	}

	public void configure(DataSet ds) throws Exception {
		Examples examples = ds.getExamples();
		this.attributes = ds.getAttributes();

		for (int i = 0; i < this.attributes.size() - 1; i++) {
			mins.add(Double.MAX_VALUE);
			maxs.add(0.0);
		}

		for (int i = 0; i < examples.size(); i++) {

			Example example = examples.get(i);
			for (int j = 0; j < example.size() - 1; j++) {
				if (this.attributes.get(j).getClass().equals(new NumericAttribute().getClass())) {
					if (mins.get(j) > example.get(j)) {
						mins.set(j, example.get(j));
					}
					if (maxs.get(j) < example.get(j)) {
						maxs.set(j, example.get(j));
					}
				}
			}

		}
	}

	public DataSet scale(DataSet ds) throws Exception {
		Examples examples = ds.getExamples();
		for (int i = 0; i < examples.size(); i++) {
			Example e = scale(examples.get(i));
			examples.set(i, e);
		}
		ds.examples = examples;
		return ds;
	}

	public Example scale(Example example) throws Exception {
		for (int i = 0; i < example.size() - 1; i++) {
			if (this.attributes.get(i).getClass()== NumericAttribute.class) {
			
				Double delta = maxs.get(i) - mins.get(i);
				
				double temp = ((example.get(i) - mins.get(i)) / delta);
				if (temp >= 1.0)
					example.set(i, 1.0);
				else if (temp <= 0.0)
					example.set(i, 0.0);
				else
					example.set(i, temp);
			}
		}
		return example;
	}
}