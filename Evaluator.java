import java.util.ArrayList;
import java.util.Random;

public class Evaluator implements OptionHandler {
	private long seed = 2026875034;
	private Random random;
	private int folds = 10;
	private Classifier classifier;
	private TrainTestSets tts;
	private Double propotion;

	public Evaluator() {

	}

	public Evaluator(Classifier classifier, String[] options) throws Exception {
		this.classifier = classifier;
		this.tts = new TrainTestSets(options);
		setOptions(options);
		this.classifier.setOptions(options);
	}

	public Performance evaluate() throws Exception {
		Performance p = new Performance(this.tts.getTrainingSet().getAttributes());
		if (this.classifier.getClass().equals(BW.class) || this.classifier.getClass().equals(MBW.class)) {
			tts.train = tts.getTrainingSet().homogeneousCoordinate();
			tts.train = tts.getTrainingSet().normalization();
			if (tts.getTestingSet() != null) {
				tts.test = tts.getTestingSet().homogeneousCoordinate();
				tts.test = tts.getTestingSet().removeRedundantFeature(tts.test.attributes);
				tts.test = tts.getTestingSet().normalization();
			}
		}

		if (this.tts.getTestingSet() == null) {
			if (this.propotion != null) {
				Examples examples = tts.getTrainingSet().getExamples();
				int trainSize = (int) Math.ceil(examples.size() * this.propotion);
				DataSet train = new DataSet(tts.getTrainingSet().getAttributes());
				DataSet test = new DataSet(tts.getTrainingSet().getAttributes());

				ArrayList<Integer> r = null;
				r = Utils.getRandomSet(examples.size(), this.random);

				for (int i = 0; i < trainSize; i++)
					train.examples.add(examples.get(r.get(i)));
				for (int i = trainSize; i < examples.size(); i++)
					test.examples.add(examples.get(r.get(i)));

				this.classifier.train(train);
				p = this.classifier.classify(test);
			} else {
				if (this.classifier.getClass().equals(IBk.class))
					this.classifier.train(tts.getTrainingSet());
				for (int i = 0; i < this.folds; i++) {
					TrainTestSets tt = tts.getTrainingSet().getCVSets(i);
					if (!this.classifier.getClass().equals(IBk.class)) {
						this.classifier.train(tt.getTrainingSet());
					}
					Performance performance = this.classifier.classify(tt.getTestingSet());
					p.add(performance);
				}
			}
		} else {
			this.classifier.train(this.tts.getTrainingSet());
			p = this.classifier.classify(this.tts.getTestingSet());
		}
		return p;
	}

	public long getSeed() {
		return this.seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
		this.random = new Random(this.seed);
	}

	public void setOptions(String args[]) throws Exception {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-s")) {
				this.setSeed(Long.parseLong(args[++i]));
			} else if (args[i].equals("-x")) {
				this.folds = Integer.parseInt(args[++i]);
				this.tts.train.setFolds(this.folds);
			} else if (args[i].equals("-p")) {
				this.propotion = Double.valueOf(args[++i]);
			}

			this.random = new Random(this.seed);
		}
	}
}