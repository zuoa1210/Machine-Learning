import java.util.ArrayList;

public class CategoricalEstimator extends Estimator {

	protected ArrayList<Integer> dist = new ArrayList<Integer>();

	public CategoricalEstimator() {

	}

	public CategoricalEstimator(Integer k) {
		for(int i = 0; i < k; i++)
			this.dist.add(0);
	}

	public void add(Number x) throws Exception {
		this.n += 1;
		this.dist.add(x.intValue());
	}

	public Double getProbability(Number x) {
		double pro = 0.0;
		double count = 0.0;
		StringBuilder sb = new StringBuilder();
		
		for (int i = 1; i < dist.size(); i++) {
			if (dist.get(i) == x.intValue()) {
				count += 1;
			}

			if(!sb.toString().contains(dist.get(i).toString())) {
				sb.append(dist.get(i).toString());
			}			
		}
		pro = Double.valueOf(count + 1) / (this.getN() + sb.length());
		return pro;
	}
}