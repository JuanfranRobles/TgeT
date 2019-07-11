package util.random;


/**
 * See: http://www.javamex.com/tutorials/random_numbers/numerical_recipes.shtml
 */

public strictfp class NumericalRecipesPRNG implements Randomizer {

	private long u;
	private long v = 4101842887655102017L;
	private long w = 1;

	private double __nextNextGaussian;
	private boolean __haveNextNextGaussian;
	
	/**
	 * Creates a new generator seeded using 
	 * {@link RandomizerUtils#generateRandomSeed()}. 
	 */
	public NumericalRecipesPRNG() {
		this( RandomizerUtils.generateRandomSeed() );
	}

	/**
	 * Creates a new generator starting with the specified seed,
	 * using <code>setSeed(seed);</code>.
	 * 
	 * @param seed The initial seed
	 */
	public NumericalRecipesPRNG(long seed) {
		setSeed( seed );
	}
	
	@Override
	synchronized public void setSeed(long seed) {
		u = seed ^ v;
		nextLong();
		v = u;
		nextLong();
		w = v;
		nextLong();
	}
	
	@Override
	public int nextInt(int n) {
		if (n <= 0) throw new IllegalArgumentException(
			"n must be positive, got: " + n
		);

		if ((n & -n) == n)
			return (int) ((n * (long) next(31)) >> 31);

		int bits, val;
		do {
			bits = next(31);
			val = bits % n;
		} while (bits - val + (n - 1) < 0);
		return val;
	}
	
	@Override
	public boolean nextBoolean() {
		return next(1) != 0;
	}
	
	@Override
	public double nextDouble() {
		return (((long) next(26) << 27) + next(27)) / (double) (1L << 53);
	}
	
	@Override
	public double nextGaussian() {
		// See Knuth, ACP, Section 3.4.1 Algorithm C.       
        if (__haveNextNextGaussian) {
			__haveNextNextGaussian = false;
			return __nextNextGaussian;
		} else {
			double v1, v2, s;
			do {
				v1 = 2 * nextDouble() - 1; // between -1.0 and 1.0
				v2 = 2 * nextDouble() - 1; // between -1.0 and 1.0
				s = v1 * v1 + v2 * v2;
			} while (s >= 1 || s == 0);
			double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s);
			__nextNextGaussian = v2 * multiplier;
			__haveNextNextGaussian = true;
			return v1 * multiplier;
		}
	}

	protected long nextLong() {
		u = u * 2862933555777941757L + 7046029254386353087L;
		v ^= v >>> 17;
		v ^= v << 31;
		v ^= v >>> 8;
		w = 4294957665L * (w & 0xffffffff) + (w >>> 32);
		long x = u ^ (u << 21);
		x ^= x >>> 35;
		x ^= x << 4;
		long ret = (x + v) ^ w;
		return ret;
	}

	synchronized protected int next(int bits) {
		return (int) (nextLong() >>> (64 - bits));
	}
}