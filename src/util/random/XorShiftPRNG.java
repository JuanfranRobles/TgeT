package util.random;

import java.util.Random;

/**
 * XorShift random number generator, based on XSRandom class
 * (this version is not a subclass of {@link Random}, which unfortunately 
 * suffers from a very poor design and is very heavy on memory).
 *  
 * - it is 30% faster than the generator from Java's library - it produces
 * random sequences of higher quality than java.util.Random - this class also
 * provides a clone() function
 * 
 * for an explanation of the algorithm, see
 * http://demesos.blogspot.com/2011/09/pseudo-random-number-generators.html
 * http://www.javamex.com/tutorials/random_numbers/xorshift.shtml
 * 
 * @author Wilfried Elmenreich University of Klagenfurt/Lakeside Labs
 *         http://www.elmenreich.tk
 * 
 * This code is released under the GNU Lesser General Public License Version 3
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 */
public strictfp class XorShiftPRNG implements Cloneable, Randomizer {
	
	private long seed;
	
	private double __nextNextGaussian;
	private boolean __haveNextNextGaussian;

	/**
	 * Creates a new generator seeded using 
	 * {@link RandomizerUtils#generateRandomSeed()}. 
	 */
	public XorShiftPRNG() {
		this( RandomizerUtils.generateRandomSeed() );
	}

	/**
	 * Creates a new generator starting with the specified seed,
	 * using <code>setSeed(seed);</code>.
	 * 
	 * @param seed The initial seed
	 */
	public XorShiftPRNG(long seed) {
		setSeed( seed );
	}
	
	/**
	 * Returns an XorShiftPRNG object with the same state as the original
	 */
	public XorShiftPRNG clone() {
		return new XorShiftPRNG( getSeed() );
	}
	
	/**
	 * Returns the current state of the seed, can be used to clone the object
	 * 
	 * @returns The current seed (not the original used to initialize)
	 */
	synchronized public long getSeed() {
		return seed;
	}

	/**
	 * Sets the seed for this pseudo random number generator. As described
	 * above, two instances of the same random class, starting with the same
	 * seed, produce the same results, if the same methods are called.
	 * 
	 * @param s the new seed
	 */
	@Override
	synchronized public void setSeed(long seed) {
		this.seed = seed;
		__haveNextNextGaussian = false;
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
	
	/**
	 * Implementation of George Marsaglia's elegant XorShift random generator
	 * 30% faster and better quality than the built-in java.util.random 
	 * 
	 * See also
	 * http://demesos.blogspot.com/2011/09/pseudo-random-number-generators.html
	 * http://www.javamex.com/tutorials/random_numbers/xorshift.shtml
	 */
	synchronized protected int next(int nbits) {
		long x = seed;
		x ^= (x << 21);
		x ^= (x >>> 35);
		x ^= (x << 4);
		seed = x;
		// ((1L << nbits) - 1) => ...011111 with nbits x 1
		return (int) (x & ((1L << nbits) - 1));
	}
}
