package util.random;

import java.io.Serializable;
import java.util.Random;

/**
 * XorShift random number generator, based on XSRandom class
 * (this version is not a subclass of {@link Random}, which unfortunately 
 * suffers from a very poor design and is very heavy on memory).
 * 
 * <b>XorShiftFastPRNG</b> is not a subclass of java.util.Random. It has the
 * same public methods as Random does, and it is algorithmically
 * identical to XorShiftPRNG. XorShiftFastPRNG has hard-code inlined all
 * of its methods directly, and made all of them final (well, the ones of
 * consequence anyway). Further, these methods are <i>not</i> synchronized, so
 * the same XorShiftFastPRNG instance cannot be shared by multiple threads.

 * for an explanation of the algorithm, see
 * http://demesos.blogspot.com/2011/09/pseudo-random-number-generators.html
 * http://www.javamex.com/tutorials/random_numbers/xorshift.shtml
 * 
 * @author Wilfried Elmenreich University of Klagenfurt/Lakeside Labs
 *         http://www.elmenreich.tk
 *         
 * @author Jos� Barranquero Tolosa, European Centre for Soft Computing
 *         http://www.softcomputing.es
 * 
 * This code is released under the GNU Lesser General Public License Version 3
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 */
public strictfp final class XorShiftFastPRNG 
	implements Serializable, Cloneable, Randomizer {
	
	private static final long serialVersionUID = -644279848144552881L;
	
	private static final long ONES_1 =  (1L <<  1) - 1; 
	private static final long ONES_26 = (1L << 26) - 1; 
	private static final long ONES_27 = (1L << 27) - 1;	
	private static final long ONES_31 = (1L << 31) - 1;
	
	private static final double ZEROS_53 = (1L << 53);
	
	private long seed;
	
	private double __nextNextGaussian;
	private boolean __haveNextNextGaussian;

	/**
	 * Creates a new generator seeded using 
	 * {@link RandomizerUtils#generateRandomSeed()}. 
	 */
	public XorShiftFastPRNG() {
		this( RandomizerUtils.generateRandomSeed() );
	}

	/**
	 * Creates a new generator starting with the specified seed,
	 * using <code>setSeed(seed);</code>.
	 * 
	 * @param seed The initial seed
	 */
	public XorShiftFastPRNG(long seed) {
		setSeed( seed );
	}
	
	/**
	 * Returns an XorShiftFastPRNG object with the same state as the original
	 */
	public XorShiftFastPRNG clone() {
		return new XorShiftFastPRNG( getSeed() );
	}
	
	/**
	 * Returns the current state of the seed, can be used to clone the object
	 * 
	 * @returns The current seed (not the original used to initialize)
	 */
	public final long getSeed() {
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
	public final void setSeed(long seed) {
		this.seed = seed;
		__haveNextNextGaussian = false;
	}
	
	@Override
	public final int nextInt(int n) {
		assert(n > 0);
		
		if ((n & -n) == n) {
			/////////////////
			long x = seed;
			x ^= (x << 21);
			x ^= (x >>> 35);
			x ^= (x << 4);
			seed = x;
			/////////////////
			return (int) ((n * (x & ONES_31)) >> 31);
		}

		int bits, val;
		do {
			/////////////////
			long x = seed;
			x ^= (x << 21);
			x ^= (x >>> 35);
			x ^= (x << 4);
			seed = x;
			/////////////////
			bits = (int) (x & ONES_31);
			val = bits % n;
		} while (bits - val + (n - 1) < 0);
		return val;
	}
	
	@Override
	public final boolean nextBoolean() {
		/////////////////
		long x = seed;
		x ^= (x << 21);
		x ^= (x >>> 35);
		x ^= (x << 4);
		seed = x;
		/////////////////
		return (x & ONES_1) != 0;
	}
	
	@Override
	public final double nextDouble() {
		/////////////////
		long x = seed;
		x ^= (x << 21);
		x ^= (x >>> 35);
		x ^= (x << 4);
		/////////////////
		long upbits = ((x & ONES_26) << 27);
		/////////////////
		x ^= (x << 21);
		x ^= (x >>> 35);
		x ^= (x << 4);
		seed = x;
		/////////////////
		return (upbits + (x & ONES_27)) / ZEROS_53;
	}
	
	@Override
	public final double nextGaussian() {
		// See Knuth, ACP, Section 3.4.1 Algorithm C.       
        if (__haveNextNextGaussian) {
			__haveNextNextGaussian = false;
			return __nextNextGaussian;
		} else {
			long x, upbits;
			double v1, v2, s;
			do {
				/////////////////
				x = seed;
				/////////////////
				x ^= (x << 21);
				x ^= (x >>> 35);
				x ^= (x << 4);
				/////////////////
				upbits = ((x & ONES_26) << 27);
				/////////////////
				x ^= (x << 21);
				x ^= (x >>> 35);
				x ^= (x << 4);
				/////////////////
				//v1 = 2 * nextDouble() - 1; // between -1.0 and 1.0
				v1 = 2 * ((upbits + (x & ONES_27)) / ZEROS_53) - 1;
				/////////////////
				x ^= (x << 21);
				x ^= (x >>> 35);
				x ^= (x << 4);
				/////////////////
				upbits = ((x & ONES_26) << 27);
				/////////////////
				x ^= (x << 21);
				x ^= (x >>> 35);
				x ^= (x << 4);
				/////////////////
				seed = x;
				/////////////////
				//v2 = 2 * nextDouble() - 1; // between -1.0 and 1.0
				v2 = 2 * ((upbits + (x & ONES_27)) / ZEROS_53) - 1;
				s = v1 * v1 + v2 * v2;
			} while (s >= 1 || s == 0);
			double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s);
			__nextNextGaussian = v2 * multiplier;
			__haveNextNextGaussian = true;
			return v1 * multiplier;
		}
	}
}