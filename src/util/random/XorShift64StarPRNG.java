package util.random;

/*		 
 * DSI utilities
 *
 * Copyright (C) 2012-2014 Sebastiano Vigna 
 *
 *  This library is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by the Free
 *  Software Foundation; either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  This library is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */

import java.util.Random;

import org.apache.commons.math3.random.AbstractRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 *  A very fast, high-quality 64-bit {@linkplain RandomGenerator pseudorandom number generator} 
 *  that combines George Marsaglia's Xorshift generators
 *  (described in <a href="http://www.jstatsoft.org/v08/i14/paper/">&ldquo;Xorshift RNGs&rdquo;</a>,
 * <i>Journal of Statistical Software</i>, 8:1&minus;6, 2003) with a multiplication.
 * 
 * <p>More details about <code>xorshift*</code> generators can be found in my paper 
 * &ldquo;<a href="http://vigna.di.unimi.it/papers.php#VigEEMXGS">
 * An experimental exploration of Marsaglia's <code>xorshift</code> generators,
 * scrambled&rdquo;</a>, 2014.
 * 
 * <p>The methods of this class returns the same sequences as those of
 * {@link XorShift64StarRandom}, to which documentation we refer for more details, 
 * but this class is not a subclass of {@link Random},
 * which unfortunately suffers from a very poor design and is very heavy on memory.
 * If you need a large number of generators that do not need to be an instance
 * of {@link Random}, or simply if you need a {@link RandomGenerator} to use
 * with <a href="http://commons.apache.org/math/">Commons Math</a>, 
 * you might be wanting this class instead of {@link XorShift64StarRandom}.
 * 
 * @see XorShift64StarRandom
 */
public strictfp class XorShift64StarPRNG
		extends AbstractRandomGenerator implements Randomizer {
	
	/** 2<sup>53</sup> &minus; 1. */
	private static final long DOUBLE_MASK = ( 1L << 53 ) - 1;
	/** 2<sup>-53</sup>. */
	private static final double NORM_53 = 1. / ( 1L << 53 );
	/** 2<sup>24</sup> &minus; 1. */
	private static final long FLOAT_MASK = ( 1L << 24 ) - 1;
	/** 2<sup>-24</sup>. */
	private static final double NORM_24 = 1. / ( 1L << 24 );

	/** The internal state of the algorithm. */
	private long x;

	/** 
	 * Creates a new generator seeded using 
	 * {@link RandomizerUtils#generateRandomSeed()}. 
	 */
	public XorShift64StarPRNG() {
		this( RandomizerUtils.generateRandomSeed() );
	}

	/** 
	 * Creates a new generator using a given seed.
	 * 
	 * @param seed a nonzero seed for the generator 
	 * (if zero, the generator will be seeded with -1).
	 */
	public XorShift64StarPRNG( final long seed ) {
		setSeed( seed );
	}

	@Override
	public long nextLong() {
		x ^= x >>> 12;
		x ^= x << 25;
		return 2685821657736338717L * ( x ^= ( x >>> 27 ) );
	}

	@Override
	public int nextInt() {
		return (int) nextLong();
	}
	
	/** Returns a pseudorandom, approximately uniformly distributed {@code int} value
     * between 0 (inclusive) and the specified value (exclusive), drawn from
     * this random number generator's sequence.
     * 
     * <p>The hedge &ldquo;approximately&rdquo; is due to the fact that to be always
     * faster than <a href="http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ThreadLocalRandom.html"><code>ThreadLocalRandom</code></a>
     * we return
     * the upper 63 bits of {@link #nextLong()} modulo {@code n} instead of using
     * {@link Random}'s fancy algorithm (which {@link #nextLong(long)} uses though).
     * This choice introduces a bias: the numbers from 0 to 2<sup>63</sup> mod {@code n}
     * are slightly more likely than the other ones. In the worst case, &ldquo;more likely&rdquo;
     * means 1.00000000023 times more likely, which is in practice undetectable. If for some reason you
     * need truly uniform generation, just use {@link #nextLong(long)}.
     * 
     * @param n the positive bound on the random number to be returned.
     * @return the next pseudorandom {@code int} value between {@code 0} (inclusive) and {@code n} (exclusive).
     */
	@Override
	public int nextInt( final int n ) {
        if ( n <= 0 ) throw new IllegalArgumentException();
        return (int)( ( nextLong() >>> 1 ) % n );
	}
	
	/** Returns a pseudorandom uniformly distributed {@code long} value
     * between 0 (inclusive) and the specified value (exclusive), drawn from
     * this random number generator's sequence. The algorithm used to generate
     * the value guarantees that the result is uniform, provided that the
     * sequence of 64-bit values produced by this generator is. 
     * 
     * @param n the positive bound on the random number to be returned.
     * @return the next pseudorandom {@code long} value between {@code 0} (inclusive) and {@code n} (exclusive).
     */
	public long nextLong( final long n ) {
        if ( n <= 0 ) throw new IllegalArgumentException();
		// No special provision for n power of two: all our bits are good.
		for(;;) {
			final long bits = nextLong() >>> 1;
			final long value = bits % n;
			if ( bits - value + ( n - 1 ) >= 0 ) return value;
		}
	}
	
	@Override
	 public double nextDouble() {
		return ( nextLong() & DOUBLE_MASK ) * NORM_53;
	}
	
	@Override
	public float nextFloat() {
		return (float)( ( nextLong() & FLOAT_MASK ) * NORM_24 );
	}

	@Override
	public boolean nextBoolean() {
		return ( nextLong() & 1 ) != 0;
	}
	
	@Override
	public void nextBytes( final byte[] bytes ) {
		int i = bytes.length, n = 0;
		while( i != 0 ) {
			n = Math.min( i, 8 );
			for ( long bits = nextLong(); n-- != 0; bits >>= 8 ) bytes[ --i ] = (byte)bits;
		}
	}

	/** 
	 * Sets the seed of this generator.
	 * 
	 * <p>The seed will be passed twice through {@link RandomizerUtils#computeMurmurHash3(long)}.
	 * In this way, if the user passes a small value we will avoid the short irregular transient
	 * associated with states with a very small number of bits set. 
	 * 
	 * @param seed a nonzero seed for this generator 
	 * 				(if zero, the generator will be seeded with {@link Long#MIN_VALUE}).
	 */
	@Override
	public void setSeed( final long seed ) {
		x = RandomizerUtils.computeMurmurHash3( seed == 0 ? Long.MIN_VALUE : seed );
	}


	/** 
	 * Sets the state of this generator.
	 * 
	 * @param state the new state for this generator (must be nonzero).
	 */
	public void setState( final long state ) {
		x = ( state == 0 ? -1 : state );
	}
}
