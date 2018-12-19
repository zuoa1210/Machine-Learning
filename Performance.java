import java.util.ArrayList;

public class Performance extends Object {

	private Attributes attributes;
	private int[][] confusionMatrix;
	private int corrects = 0;
	private double sumAUC = 0.0;
	private double sumSqrAUC = 0.0;
	private double sum = 0.0;
	private double sumSqr = 0.0;
	private double sumF1 = 0.0;
	private int c; // number of classes
	private int n = 0; // number of predictions
	private int m = 0; // number of additions

	private ArrayList<String> distribution = new ArrayList<String>();

	public Performance(Attributes attributes) throws Exception {
		this.attributes = attributes;
		this.c = this.attributes.getClassAttribute().size();
		this.confusionMatrix = new int[this.c][this.c];
		for (int i = 0; i < this.c; i++) {
			for (int j = 0; j < this.c; j++) {
				this.confusionMatrix[i][j] = 0;
			}
		}
	}

	public void add(int actual, double[] prediction) throws Exception {
		this.n += 1;
		StringBuilder sb = new StringBuilder();
		int max = 0, x = 0, actualX = 0;
		if(actual == 1)
			actualX = 1;
		if (prediction.length == 1) {
				if((int) prediction[0] == 1)
					x = 1;
				this.confusionMatrix[x][actualX] +=1;
			
		} else {
			max = Utils.maxIndex(prediction);
			prediction[max] = (max == 0) ? -1 : 1;
		}
		sb.append(actual + "," + (int) prediction[max]);
		distribution.add(sb.toString());
		if (prediction[max] == actual)
			this.corrects++;
	}

	public void add(Performance p) throws Exception {
		this.sum += p.getAccuracy();
		this.sumSqr += Math.pow(p.getAccuracy(), 2);
		this.sumAUC += p.getAUC();
		this.sumSqrAUC += Math.pow(p.getAUC(), 2);
		this.sumF1 += p.getF1();
		this.m += 1;
	}

	public double getAccuracy() {
		double acc = Double.valueOf(this.corrects) / this.n;
		return acc;
	}

	public double getSDAcc() {
		return Math.sqrt((this.sumSqr - (Math.pow(this.sum, 2) / this.m)) / (this.m - 1));
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (sum == 0.0 && sumSqr == 0.0) {
			sb.append("Accuracy is " + this.getAccuracy() + "\n");
			sb.append("AUC is " + this.getAUC());
		}

		else {
			sb.append("Average Accuracy is " + (this.sum / this.m) + ", Standard Deviation of Accuracy is "
					+ this.getSDAcc() + "\n");
			sb.append("Average AUC is " + (this.sumAUC / this.m) + ", Standard Deviation of AUC is " + this.getSDAUC() + "\n");
			sb.append("Average F1 is " + this.getAvgF1());
		}

		return sb.toString();
	}

	public double getAUC() {
		double a = 0.0;
		ArrayList<Integer> positive = new ArrayList<Integer>();
		ArrayList<Integer> negative = new ArrayList<Integer>();
		
		for (int i = 0; i < this.distribution.size(); i++) {
			String s = this.distribution.get(i);
			String[] pair = s.split(",");
			
			if (pair[0].equals("1"))
				positive.add(Integer.valueOf(pair[1]));
			else
				negative.add(Integer.valueOf(pair[1]));
		}
		int total = positive.size() * negative.size();
		
		for (int i = 0; i < negative.size(); i++) {
			for (int j = 0; j < positive.size(); j++) {
				if (negative.get(i) > positive.get(j))
					a += 1.0;
				else if (negative.get(i) == positive.get(j))
					a += 0.5;
			}
		}
		if (total != 0)
			return a / total;
		else
			return 0;
	}

	public double getSDAUC() {
		return Math.sqrt((this.sumSqrAUC - (Math.pow(this.sumAUC, 2) / this.m)) / (this.m - 1));
	}
	
	public double getF1() {
		double precision = 0.0, recall = 0.0;
		
		if((this.confusionMatrix[0][0] + this.confusionMatrix[0][1]) != 0) 
			precision = (double)this.confusionMatrix[0][0] / (this.confusionMatrix[0][0] + this.confusionMatrix[0][1]);
		if((this.confusionMatrix[0][0] + this.confusionMatrix[1][0]) != 0)
			recall = (double)this.confusionMatrix[0][0] / (this.confusionMatrix[0][0] + this.confusionMatrix[1][0]);
		
		if(precision == 0 || recall == 0)
			return 0;
		return (2 * precision * recall) / (precision + recall);
	}
	
	public double getAvgF1() {
		return this.sumF1 / this.m;
	}
}