import java.util.Scanner;

public class AttributeFactory {

	public static Attribute make(Scanner scanner) throws Exception {
		NumericAttribute nua;
		NominalAttribute noa;
		if (scanner.hasNextLine()) {
			String s = scanner.nextLine();
			if (s.contains("@attribute")) {
				String[] parameters = s.trim().split(" ");
				if (s.contains("numeric")) {
					nua = new NumericAttribute(parameters[1]);
					return nua;
				} 
				else {
					noa = new NominalAttribute(parameters[1]);
					for (int i = 0; i < parameters.length; i++) {
						noa.addValue(parameters[i]);
					}
					return noa;
				}
			}
		}
		return null;
	}
}