
public class Attribute {
	
	protected String name;
	
	public Attribute() {
		
	}
	
	public Attribute(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public int size() {
		return Integer.MAX_VALUE;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		StringBuilder att = new StringBuilder();
		att.append("@attribute " + this.name + " ");
		return att.toString();
	}
	
}
