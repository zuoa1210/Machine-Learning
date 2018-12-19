import java.util.ArrayList;

public class Node {
	public int attribute = -1;
	public int label = -1;
	public int[] classCounts = null;
	public ArrayList<Node> children = new ArrayList<Node>();

	Node() {

	}

	Node(int[] classCounts) {
		this.classCounts = classCounts;
	}

	public boolean isLeaf() {
		this.children.trimToSize();
		if (this.children.size() == 0 && this.classCounts != null) 
			return true;
		else
			return false;
	}

	public boolean isEmpty() {
		if (this.classCounts == null)
			return true;
		else
			return false;
	}

	public double getError() {
		double errRate = 0.0;
		int total = 0;

		for (int j = 0; j < this.classCounts.length; j++)
			total += this.classCounts[j];
		
		int max = 0;

		for (int i = 0; i < this.classCounts.length; i++) {
			if (this.classCounts[i] > max) {
				max = this.classCounts[i];
			}
		}
		
		errRate = total * Utils.u25(total, total - max);
		return errRate;
	}
}
