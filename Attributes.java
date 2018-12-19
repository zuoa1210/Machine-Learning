import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public class Attributes implements Serializable{

	  private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	  private boolean hasNumericAttributes = false;
	  private int classIndex;

	  public void add(Attribute attribute) {
		  attributes.add(attribute);
	  };
	  
	  public int getClassIndex() {
		  return this.classIndex;
	  };
	  
	  public boolean getHasNumericAttributes() {
		  for(int i = 0; i < attributes.size(); i++) {
			  if(attributes.get(i).getClass().equals(NumericAttribute.class))
				  this.hasNumericAttributes = true;
		  }
		  return this.hasNumericAttributes;
	  };
	  
	  public Attribute get(int i) {
		  return this.attributes.get(i);
	  };
	  
	  public Attribute getClassAttribute() {
		  return this.attributes.get(this.classIndex);
	  };
	  
	  public int getIndex(String name) throws Exception{
		  int index = -1;
		  for(int i = 0; i < attributes.size(); i++) {
			  if(attributes.get(i).name.equals(name))
				  index = i;
		  }
		  return index;
	  };
	  
	  public int size() {
		  return attributes.size();
	  };
	  
	  public void parse(Scanner scanner) throws Exception {
		  NumericAttribute nua;
		  NominalAttribute noa;
		  while(scanner.hasNextLine()) {
			  String s = scanner.nextLine();
			  String[] parameters = s.trim().split(" ");
			  if (s.contains("@attribute")) {
				if(s.contains("numeric")) {
					nua = new NumericAttribute(parameters[1]);
					this.attributes.add(nua);
				}
				else {
					noa = new NominalAttribute(parameters[1]);
					for (int i = 2; i < parameters.length; i++)
						noa.addValue(parameters[i]);
					this.attributes.add(noa);
				}
			  }else {
				  break;
			  }
		  }
		  this.classIndex = attributes.size() - 1;
	  };
	  
	  public void setClassIndex(int classIndex) throws Exception{
		  this.classIndex = classIndex;
	  };
	  
	  public String toString() {
		  StringBuilder sb = new StringBuilder();
		  NumericAttribute nua;
		  NominalAttribute noa;
		  for(int i = 0; i < this.attributes.size(); i++) {
			  if(this.attributes.get(i).getClass().equals(NumericAttribute.class)) {
				  nua = (NumericAttribute) this.attributes.get(i);
				  sb.append(nua.toString()); 
			  }
			  else if(this.attributes.get(i).getClass().equals(NominalAttribute.class)) {
				  noa = (NominalAttribute) this.attributes.get(i);
				  sb.append("@attribute " + noa.name + " ");
				  sb.append(noa.toString());
			  }
			  sb.append("\n");
		  }
		  return sb.toString();
	  };
}
