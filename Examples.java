import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Examples extends ArrayList<Example> {

	private Attributes attributes;
	private int[] classCounts;

	public Examples(Attributes attributes) {
		this.attributes = attributes;
	}

	public void parse(Scanner scanner) throws Exception {
		Boolean flag = false;
		while (scanner.hasNextLine()) {
			String s = scanner.nextLine();
			if (!s.equals("")) {
				String[] parameters = s.trim().split(" ");
				Example example = new Example();
				for (int i = 0; i < parameters.length; i++) {
					flag = false;
					String temp = parameters[i];
					int index = 0;
					if (this.attributes.get(i).getClass().equals(NumericAttribute.class)) {
						example.add(i, Double.valueOf(temp));
					} else {
						NominalAttribute noa = (NominalAttribute) this.attributes.get(i);
						for (int j = 0; j < noa.size(); j++) {
							if (noa.getValue(j).equals(temp)) {
								index = j;
								example.add(i, Double.valueOf(index));
								flag = true;
							}
						}
						if(!flag)
							example.add(i, -1.0);
					}
				}
				this.add(example);
			}
		}
	};

	public String toString() {
		Example example;
		StringBuilder sb = new StringBuilder();
		sb.append("@examples" + "\n\n");
		for (int i = 0; i < this.size(); i++) {
			example = this.get(i);
			for (int j = 0; j < example.size(); j++) {
				if (this.attributes.get(j).getClass().equals(NumericAttribute.class))
					sb.append(example.get(j).toString() + " ");
				else {
					NominalAttribute noa = (NominalAttribute) this.attributes.get(j);
					int index = example.get(j).intValue();
					String s = noa.getValue(index);
					sb.append(s + " ");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public int[] getClassCounts() {
		NominalAttribute na = (NominalAttribute) this.attributes.getClassAttribute();
		this.classCounts = new int[na.size()];

		for (int i = 0; i < this.size(); i++) {
			Example example = this.get(i);
			int classIndex = example.size() - 1;
			int classLabel = example.get(classIndex).intValue();
			this.classCounts[classLabel] += 1;
		}
		return this.classCounts;
	}

	public boolean add(Example example) {
		return super.add(example);
	}
}