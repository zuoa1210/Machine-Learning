
public class p1 {
	protected final static Double oneOverSqrt2PI = 1.0 / Math.sqrt(2.0 * Math.PI);

	public static void main(String[] args) {
		try {
			TrainTestSets tts = new TrainTestSets();
			tts.setOptions(args);
			System.out.println(tts.toString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}