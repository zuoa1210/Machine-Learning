import java.util.ArrayList;

public class Example extends ArrayList<Double>{
	
	 public Example() {
		 
	 }
	 
	 public Example(int n) {
		 if(n >= 2)
			 modCount = n;
	 }

}