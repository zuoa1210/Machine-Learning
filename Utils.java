import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Utils {
	private ArrayList<ArrayList<Estimator>> classConditionalDistributions;
	
	public static int maxIndex(double[] p) {
		int maxindex = 0;
		double max = p[0];

		for (int i = 0; i < p.length; i++) {
			if (p[i] > max) {
				max = p[i];
				maxindex = i;
			}
		}
		return maxindex;
	}

	public static ArrayList<Integer> getRandomSet(int setsize, Random random) {
		Boolean flag = false;
		ArrayList<Integer> checklist = new ArrayList<Integer>();
		int count = setsize;

		while (count > 0) {
			flag = false;
			int temp = random.nextInt(setsize);
			for (int h = 0; h < checklist.size(); h++) {
				if (checklist.get(h).equals(temp))
					flag = true;
			}
			if (!flag) {
				checklist.add(temp);
				count--;
			}
		}
		return checklist;
	}

	public static double[] InsertSort(double[] list) {
		int n = list.length;
		double[] temp = new double[n];

		for (int i = 0; i < n; i++)
			temp[i] = list[i];

		for (int i = 1; i < n; ++i) {
			double key = temp[i];
			int j = i - 1;

			while (j >= 0 && temp[j] > key) {
				temp[j + 1] = temp[j];
				j = j - 1;
			}
			temp[j + 1] = key;
		}
		return temp;
	}

//https://alvinalexander.com/java/java-deep-clone-example-source-code
	public static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static double u25(int n, int x) {
		double p = 0.0;
		double z = 0.6925;
		double temp = ((x + 0.5) * (1 - ((x + 0.5)/n))) + (Math.pow(z, 2)/4);
		double m = (x + 0.5 + (Math.pow(z, 2)/2) + z * Math.sqrt(temp));
		p = m / (n + Math.pow(z, 2));
		return p;
	}
	
	public static double[] voting(int[] c, double[] w) {
		int cSum = 0;
		double wSum = 0.0;
		for(int j = 0; j < c.length; j++)
			cSum += c[j];
		for(int j = 0; j < w.length; j++) 
			wSum += w[j] * c[j];
		
		double[] wNew = new double[w.length];
		for(int j = 0; j < wNew.length; j++) {
			if(cSum == 0)
				wNew[j] = 0;
			else
				wNew[j] = wSum / cSum;
		}
		return wNew;
	}
}