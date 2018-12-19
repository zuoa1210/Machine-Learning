import java.io.Serializable;

public class NumericAttribute extends Attribute implements Serializable{

	public NumericAttribute() {
		
	};
	
	public NumericAttribute(String name) {
		this.name = name;
	};

	public String toString() {
		StringBuilder sb = new StringBuilder();
		return sb.append("@attribute " + this.name + " numeric").toString();
	};
	
	public boolean validValue(Double value) {
		return true;
	};
}