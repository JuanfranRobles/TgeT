package util.random;


public interface Randomizer  {
	
	public void setSeed(long seed);
	
	public int nextInt(int max);
	
	public boolean nextBoolean();
	
	public double nextDouble();

	public double nextGaussian();

}
