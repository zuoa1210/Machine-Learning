import java.io.Serializable;
import java.util.ArrayList;

public class DT extends Classifier implements Serializable, OptionHandler {
	protected Attributes attributes;
	protected Node root;
	protected Boolean prouneOption = true;

	public DT() {

	}

	public DT(String[] options) throws Exception {
		setOptions(options);
	}

	public void setOptions(String[] options) {
		for (int i = 0; i < options.length; i++) {
			if (options[i].equals("-u")) {
				this.prouneOption = false;
			}
		}
	}

	public Performance classify(DataSet ds) throws Exception {
		Performance p = new Performance(ds.attributes);

		if (this.prouneOption == true)
			prune();

		for (int i = 0; i < ds.getExamples().size(); i++) {
			Example example = ds.getExamples().get(i);
			double[] prediction = getDistribution(example);
			p.add(example.get(example.size() - 1).intValue(), prediction);
		}
		return p;
	}

	public int classify(Example example) throws Exception {
		double[] prediction = getDistribution(root, example);
		return (int) prediction[0];
	}

	public double[] getDistribution(Example example) throws Exception {
		return getDistribution(root, example);
	}

	public void prune() throws Exception {
		prune(root);
	}

	public void train(DataSet ds) throws Exception {
		this.attributes = ds.attributes;
		Node node = new Node();
		node = train_aux(ds);
		root = new Node();
		root.children = node.children;
	}

	// private recursive methods

	private double[] getDistribution(Node node, Example example) throws Exception {
		double[] prediction = new double[1];

		for (int i = 0; i < node.children.size(); i++) {
			Node subNode = node.children.get(i);
			if (subNode.label == example.get(subNode.attribute)) {
				if (!subNode.isEmpty() && !subNode.isLeaf()) {
					prediction = getDistribution(subNode, example);
				} else if (subNode.isLeaf()) {
					prediction[0] = subNode.label;
				} else if (subNode.isEmpty()) {
					prediction[0] = subNode.label;
				}
			}
		}
		return prediction;
	}

	private double prune(Node node) throws Exception {
		double pruneN = 0.0;
		double pruneC = 0.0;

		if (node != root && !node.isEmpty()) {
			pruneN = node.getError();
		}

		if (node.isLeaf() && !node.isEmpty())
			return pruneN;

		if (node.children.size() > 0) {
			for (int i = 0; i < node.children.size(); i++) {
				if (!node.children.get(i).isEmpty())
					pruneC += prune(node.children.get(i));
			}
		}

		if (pruneC > pruneN && node != root) {
			int[] classCounts = node.classCounts;
			double maxClass = 0.0;
			int index = 0;

			for (int i = 0; i < classCounts.length; i++) {
				if (maxClass < classCounts[i]) {
					maxClass = classCounts[i];
					index = i;
				}
			}
			node.attribute = this.attributes.getClassIndex();
			node.label = index;
			node.children = new ArrayList<Node>();
		}
		return pruneN;
	}

	private Node train_aux(DataSet ds) throws Exception {
		Node node = new Node();

		if (ds.getExamples().size() <= 3 || ds.homogeneous()) {
			node.attribute = ds.getAttributes().getClassIndex();
			node.label = ds.getMajorityClassLabel();
			node.classCounts = ds.getClassCounts();
			return node;
		}

		int maxIndex = ds.getBestSplittingAttribute();
		if (maxIndex == 0) {
			node.attribute = ds.getAttributes().getClassIndex();
			node.label = ds.getMajorityClassLabel();
			node.classCounts = ds.getClassCounts();
			return node;
		}

		ArrayList<DataSet> splitDS = ds.splitOnAttribute(maxIndex);
		node.attribute = maxIndex;

		for (int i = 0; i < splitDS.size(); i++) {
			Node subNode = new Node();
			if (splitDS.get(i).isEmpty()) {
				subNode.attribute = ds.getAttributes().getClassIndex();
				subNode.label = ds.getMajorityClassLabel();
			} else {
				subNode.attribute = maxIndex;
				subNode.label = Integer.valueOf(splitDS.get(i).name);
				subNode.classCounts = splitDS.get(i).getClassCounts();
				Node n = new Node();
				splitDS.get(i).attributes = ds.attributes;
				n = train_aux(splitDS.get(i));
				if (n.children.size() == 0)
					subNode = n;
				else
					subNode.children = n.children;
			}
			node.children.add(subNode);
		}
		return node;
	}

	public Classifier clone() {
		return null;
	}

	public static void main(String[] args) {
		try {
			Evaluator evaluator = new Evaluator(new DT(), args);
			Performance performance = evaluator.evaluate();
			System.out.println(performance);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
