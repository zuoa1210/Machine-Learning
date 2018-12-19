public class GaussianEstimator extends Estimator{
  protected Double sum = 0.0;
  protected Double sumsqr = 0.0;
  protected final static Double oneOverSqrt2PI = 1.0/Math.sqrt(2.0*Math.PI);

  public GaussianEstimator() {
	  
  }
  
  public void add( Number x ) throws Exception{
	  this.sum += x.doubleValue();
	  this.sumsqr += Math.pow(x.doubleValue(), 2);
	  this.n += 1;
  }
  
  public Double getMean() {
	  Double mean = 0.0;
	  mean = this.sum / this.getN();
	  return mean; 
  }
  
  public Double getVariance() {
	  Double variance = 0.0;
	  variance = (this.sumsqr - (Math.pow(this.sum, 2) / this.getN())) / (this.getN() - 1);	  
	  return variance; 
  }
  
  public Double getProbability( Number x ) {
	  Double pro = 0.0;
	  Double num = x.doubleValue() - this.getMean();
	  Double pownum = (-1 * Math.pow(num, 2)) / (2 * this.getVariance());
	  pro = this.oneOverSqrt2PI * (1 / Math.sqrt(this.getVariance()));
	  pro = pro * Math.pow(Math.E, pownum);
	  return pro;
  }
}