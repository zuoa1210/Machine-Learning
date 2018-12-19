import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class DataSet implements Serializable {

	protected String name;
	protected Attributes attributes = null;
	protected Examples examples = null;
	protected Random random;

	protected int folds = 10;
	protected int[] partitions = null;

	public DataSet() {

	};

	public DataSet(String[] options) {

	};

	public DataSet(Attributes attributes) {
		this.attributes = attributes;
		this.examples = new Examples(this.attributes);
	};

	public void add(Example example) {
		this.examples.add(example);
	};

	public Attributes getAttributes() {
		return this.attributes;
	};

	public Examples getExamples() {
		return this.examples;
	};

	public int getFolds() {
		return this.folds;
	}

	public void setFolds(int folds) throws Exception {
		this.folds = folds;
	}

	public boolean getHasNumericAttributes() {
		Boolean flag = false;
		for (int i = 0; i < attributes.size(); i++) {
			if (attributes.get(i).getClass().equals(NumericAttribute.class))
				flag = true;
		}
		return flag;
	};

	public void load(String filename) throws Exception {
		File file = new File(filename);
		Scanner scanner = new Scanner(file);
		parse(scanner);
	};

	private void parse(Scanner scanner) throws Exception {
		this.attributes = new Attributes();

		String s = scanner.nextLine();
		while (s != null) {
			if (s.contains("@dataset")) {
				String[] head = s.trim().split(" ");
				this.name = head[1];
				break;
			}
		}
		scanner.nextLine();
		this.attributes.parse(scanner);
		this.examples = new Examples(this.attributes);
		while (scanner.hasNextLine()) {
			if (scanner.nextLine().contains("@examples")) {
				this.examples.parse(scanner);
			}
		}
		scanner.close();
	};

	public void setRandom(Random random) {
		partitions = new int[this.getExamples().size()];
		int setSize = this.getExamples().size() / this.getFolds();
		ArrayList<Integer> temp = new ArrayList<Integer>();

		for (int i = 0; i < setSize; i++) {
			temp = Utils.getRandomSet(this.folds, random);
			for (int j = 0; j < temp.size(); j++) {
				int index = i * temp.size() + j;
				partitions[index] = temp.get(j);
			}
		}
	}

	public String toString() {
		StringBuilder ds = new StringBuilder();
		ds.append("@dataset " + this.name + "\n\n");
		ds.append(this.getAttributes().toString() + "\n");
		ds.append(this.getExamples().toString());
		return ds.toString();
	};

	public TrainTestSets getCVSets(int p) throws Exception {
		if (partitions == null)
			setRandom(new Random());
		int eSize = this.getExamples().size();
		DataSet train = new DataSet(this.attributes);
		DataSet test = new DataSet(this.attributes);

		for (int i = 0; i < eSize; i++) {
			if (partitions[i] == p) {
				Example e = this.getExamples().get(i);
				test.examples.add(e);
			} else {
				Example e = this.getExamples().get(i);
				train.examples.add(e);
			}
		}
		TrainTestSets tts = new TrainTestSets(train, test);
		return tts;
	}

	public boolean isEmpty() {

		if (this.examples.size() == 0)
			return true;
		else
			return false;
	}

	public double gainRatio(int attribute) throws Exception {
		NominalAttribute na = (NominalAttribute) this.getAttributes().get(attribute);
		int[] classCounts = this.getClassCounts();
		int[] attCounts = new int[na.size()];
		double entropyS = 0.0, gain = 0.0, splitInfo = 0.0, gainRatio = 0.0;
		double total = this.getExamples().size();

		for (int i = 0; i < total; i++) {
			Example example = this.getExamples().get(i);
			for (int j = 0; j < na.size(); j++) {
				if (Double.valueOf(j).equals(example.get(attribute))) {
					attCounts[j]++;
				}
			}
		}

		for (int i = 0; i < classCounts.length; i++) {
			entropyS += -1 * (classCounts[i] / total) * (Math.log(classCounts[i] / total) / Math.log(2));
		}

		gain = entropyS;
		ArrayList<DataSet> splitDS = this.splitOnAttribute(attribute);
		double[] entropy = new double[na.size()];

		for (int i = 0; i < entropy.length; i++) {
			int[] subClassCount = splitDS.get(i).getClassCounts();

			for (int j = 0; j < subClassCount.length; j++) {
				if (attCounts[i] != 0) {
					if (subClassCount[j] == 0) {
						entropy[i] = 0.0;
					} else {
						double Sc = subClassCount[j];
						double S = attCounts[i];
						entropy[i] += -1 * (Sc / S) * ((Math.log(Sc / S)) / Math.log(2));
					}
				}
			}
		}

		for (int i = 0; i < na.size(); i++) {
			if (attCounts[i] != 0) {
				double attC = attCounts[i];
				gain += -1 * ((attC / total) * entropy[i]);
				splitInfo += -1 * ((attC / total) * (Math.log(attC / total) / Math.log(2)));
			}
		}

		if (gain == 0.0)
			gainRatio = 0;
		else
			gainRatio = gain / splitInfo;

		return gainRatio;
	}

	public int getBestSplittingAttribute() throws Exception {
		int index = 0;
		double maxAtt = 0.0;

		for (int i = 0; i < this.attributes.size() - 1; i++) {
			if (maxAtt < this.gainRatio(i)) {
				maxAtt = this.gainRatio(i);
				index = i;
			}
		}
		return index;
	}

	public ArrayList<DataSet> splitOnAttribute(int attribute) throws Exception {
		ArrayList<DataSet> splitDS = new ArrayList<DataSet>();
		NominalAttribute na = (NominalAttribute) this.getAttributes().get(attribute);
		int total = this.getExamples().size();

		for (int i = 0; i < na.size(); i++) {
			DataSet ds = new DataSet();
			Examples e = new Examples(this.attributes);
			for (int j = 0; j < total; j++) {
				Example example = this.getExamples().get(j);
				if (example.get(attribute) == (double) i) {
					e.add(example);
				}
			}
			ds.name = String.valueOf(i);
			ds.examples = e;
			splitDS.add(ds);
		}
		return splitDS;
	}

	public boolean homogeneous() throws Exception {
		int[] classCounts = this.getClassCounts();
		double entropy = 0.0;
		double total = this.getExamples().size();

		for (int i = 0; i < classCounts.length; i++) {
			if (classCounts[i] != 0) {
				double cs = 0.0;
				cs = (double) classCounts[i];
				entropy += -1 * (cs / total) * (Math.log(cs / total) / Math.log(2));
			}
		}
		if (entropy == 0)
			return true;
		else
			return false;
	}

	public int[] getClassCounts() throws Exception {
		return this.getExamples().getClassCounts();
	}

	public int getMajorityClassLabel() throws Exception {
		int[] classCounts = this.getClassCounts();
		double maxClass = 0.0;
		int index = 0;

		for (int i = 0; i < classCounts.length; i++) {
			if (maxClass < classCounts[i]) {
				maxClass = classCounts[i];
				index = i;
			}
		}
		return index;
	}

	public DataSet homogeneousCoordinate() {
		for (int i = 0; i < examples.size(); i++) {
			Example example = examples.get(i);

			double y = example.get(example.size() - 1);
			example.set(example.size() - 1, 1.0);
			example.add(y);
			examples.set(i, example);
		}
		return this;
	}

	public DataSet normalization() {
		double sum = 0;
		for (int i = 0; i < examples.size(); i++) {
			Example example = examples.get(i);
			sum = 1.0;
			for (int j = 0; j < example.size() - 2; j++) {
				if(attributes.get(j).getClass().equals(NominalAttribute.class) && example.get(j) >= 0)
					sum += example.get(j);
			}
			for (int j = 0; j < example.size() - 2; j++) {
				if(attributes.get(j).getClass().equals(NominalAttribute.class) && example.get(j) >= 0)
					example.set(j, (example.get(j) / sum));
			}
			example.set(example.size() - 2, 1.0 / sum);
			examples.set(i, example);
		}
		return this;
	}

	public DataSet removeRedundantFeature(Attributes maintanedAttributes) {
		boolean attExists = false;
		Examples newExamples = new Examples(maintanedAttributes);
		for (int i = 0; i < this.getExamples().size(); i++)
			newExamples.add(new Example());
		for (int i = 0; i < maintanedAttributes.size(); i++) {
			Attribute matt = maintanedAttributes.get(i);
			for (int j = 0; j < this.getAttributes().size(); j++) {
				Attribute att = this.getAttributes().get(j);
				if (att.name.equals(matt.name)) {
					attExists = true;
					for (int z = 0; z < this.getExamples().size(); z++) {
						Example example = this.getExamples().get(z);
						Example e = examples.get(z);
						e.set(i, example.get(j));
						newExamples.set(z, e);
					}
				}
			}
			if (!attExists) {
				for (int z = 0; z < this.getExamples().size(); z++) {
					Example e = examples.get(z);
					e.set(i, 0.0);
					newExamples.set(z, e);
				}
			}
		}
		this.examples = newExamples;
		return this;
	}
}