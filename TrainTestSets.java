import java.io.Serializable;

public class TrainTestSets implements OptionHandler, Serializable{

	  protected DataSet train;
	  protected DataSet test;

	  public TrainTestSets() {
		  
	  };
	  
	  public TrainTestSets(String [] options) throws Exception{
		  setOptions(options);
	  };
	  
	  public TrainTestSets(DataSet train, DataSet test) {
		  this.train = train;
		  this.test = test;
	  };
	  
	  public DataSet getTrainingSet() {
		  return this.train;
	  };
	  
	  public DataSet getTestingSet() {
		  return this.test;
	  };
	  
	  public void setTrainingSet(DataSet train) {
		  this.train = train;
	  };
	  
	  public void setTestingSet(DataSet test) {
		  this.test = test;
	  };
	  
	  public void setOptions(String[] options) throws Exception{
		  for(int i = 0; i < options.length; i++) {
			  if(options[i].equals("-t")) {
				  train = new DataSet();
				  train.load(options[++i]);
			  }
			  else if(options[i].equals("-T")) {
				  test = new DataSet();
				  test.load(options[++i]);
			  }	  
		  }
	  };
	  
	  public String toString() {
		  if(train != null && test != null) 
			  return this.train.toString()+"\n"+this.test.toString();
		  else if(train != null)
			  return this.train.toString();
		  else if(test != null)
			  return this.test.toString();
		  return null;
	  };
	
}