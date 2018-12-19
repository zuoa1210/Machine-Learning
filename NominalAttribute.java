import java.io.Serializable;
import java.util.ArrayList;

public class NominalAttribute extends Attribute implements Serializable{
	
	ArrayList<String> domain = new ArrayList<String>();
	
	public NominalAttribute() {
		
	};

	public NominalAttribute(String name) {
		this.name = name;
	};
	
	public void addValue(String value) {
		this.domain.add(value);
	};
	
	public int size() {
		return this.domain.size();
	};
	
	public String getValue(int index) {
		return this.domain.get(index);
	};
	
	public int getIndex(String value) throws Exception{
		int index = -1;
		for(int i = 0; i < this.domain.size(); i++) {
			if(this.domain.get(i).equals(value)) {
				index = i;
				return index;
			}
		}
		return index;
	};
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < this.domain.size(); i++) 
			sb.append(this.domain.get(i)+" ");
		return sb.toString();
	};
	
	public boolean validValue(String value) {
		Boolean flag = false;
		for(int i = 0; i < this.domain.size(); i++) {
			if(this.domain.get(i).equals(value))
				flag = true;
		}
		return flag;
	};
}