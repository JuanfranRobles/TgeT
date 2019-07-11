package util.random;

/*		 
 * DSI utilities
 *
 * Copyright (C) 2013-2014 Sebastiano Vigna 
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
 * An unbelievably fast, top-quality 64-bit {@linkplain RandomGenerator pseudorandom number generator} that
 * combines George Marsaglia's Xorshift generators (described in <a
 * href="http://www.jstatsoft.org/v08/i14/paper/">&ldquo;Xorshift RNGs&rdquo;</a>, <i>Journal of
 * Statistical Software</i>, 8:1&minus;6, 2003) with a multiplication. 
 * 
 * <p><strong>Warning</strong>: the parameters of this generator have changed in version 2.1.7.
 *  
 * <p>More details about <code>xorshift*</code> generators can be found in my paper &ldquo;<a href="http://vigna.di.unimi.it/papers.php#VigEEMXGS">An experimental exploration of Marsaglia's <code>xorshift</code> generators,
 * scrambled&rdquo;</a>, 2014.
 * 
 * <p>Note that this is
 * <strong>not</strong> a cryptographic-strength pseudorandom number generator, but its quality is
 * preposterously higher than {@link Random}'s, and its cycle length is
 * 2<sup>1024</sup>&nbsp;&minus;&nbsp;1, which is more than enough for any application (it is actually
 * possible to define analogously a generator with cycle length 2<sup>4096</sup>&nbsp;&minus;&nbsp;1,
 * but its interest is eminently academic). 
 * 
 * <p>The methods of this class returns the same sequences as those of
 * {@link XorShift1024StarRandom}, to which documentation we refer for more details, but this class is not a subclass of {@link Random},
 * which unfortunately suffers from a very poor design and is very heavy on memory.
 * If you need a large number of generators that do not need to be an instance
 * of {@link Random}, or simply if you need a {@link RandomGenerator} to use
 * with <a href="http://commons.apache.org/math/">Commons Math</a>, 
 * you might be wanting this class instead of {@link XorShift1024StarRandom}.
 * 
 * <p>If you want to use less memory at the expense of the period, consider using {@link XorShift128PlusRandomGenerator} or {@link XorShift64StarRandomGenerator}.
 * 
 * <h3>Notes</h3>
 * 
 * <p>Testing with <a href="http://www.iro.umontreal.ca/~simardr/testu01/tu01.html">TestU01</a> and
 * <a href="http://www.phy.duke.edu/~rgb/General/dieharder.php">Dieharder</a> shows no difference in
 * quality between the higher and the lower bits of this generator. Thus, right shifting is used
 * whenever a subset of bits is necessary. 
 */
public strictfp class XorShift1024StarPRNG 
		extends AbstractRandomGenerator implements Randomizer {

	/** 2<sup>-53</sup>. */
	private static final double NORM_53 = 1. / ( 1L << 53 );
	/** 2<sup>-24</sup>. */
	private static final double NORM_24 = 1. / ( 1L << 24 );

	/** The internal state of the algorithm. */
	private final long[] s = new long[ 16 ];
	private int p;
	
	/** 
	 * Creates a new generator seeded using 
	 * {@link RandomizerUtils#generateRandomSeed()}. 
	 */
	public XorShift1024StarPRNG() {
		this( RandomizerUtils.generateRandomSeed() );
	}

	/**
	 *  Creates a new generator using a given seed.
	 * 
	 * @param seed a nonzero seed for the generator 
	 * (if zero, the generator will be seeded with -1).
	 */
	public XorShift1024StarPRNG( final long seed ) {
		setSeed( seed );
	}

	@Override
	public long nextLong() {
		final long s0 = s[ p ];
		long s1 = s[ p = ( p + 1 ) & 15 ];
		s1 ^= s1 << 31;
		return ( s[ p ] = s1 ^ s0 ^ ( s1 >>> 11 ) ^ ( s0 >>> 30 ) ) * 1181783497276652981L;
	}

	@Override
	public int nextInt() {
		return (int) nextLong();
	}
	
	@Override
	public int nextInt( final int n ) {
		return (int) nextLong( n );
	}
		
	/** 
	 * Returns a pseudorandom uniformly distributed {@code long} value
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
		return ( nextLong() >>> 11 ) * NORM_53;
	}
	
	@Override
	public float nextFloat() {
		return (float)( ( nextLong() >>> 40 ) * NORM_24 );
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

	/** Sets the seed of this generator.
	 * 
	 * <p>The argument will be used to seed a {@link XorShift64StarPRNG}, whose output
	 * will in turn be used to seed this generator. This approach makes &ldquo;warmup&rdquo; unnecessary,
	 * and makes the possibility of starting from a state 
	 * with a large fraction of bits set to zero astronomically small.
	 * 
	 * @param seed a nonzero seed for the generator (if zero, the generator will be seeded with -1).
	 */
	@Override
	public void setSeed( final long seed ) {
		p = 0;
		final XorShift64StarPRNG r = new XorShift64StarPRNG( seed );
		for( int i = s.length; i-- != 0; ) s[ i ] = r.nextLong();
	}

	/** Sets the state of this generator.
	 * 
	 * <p>The internal state of the generator will be reset, and the state array filled with the provided array.
	 * 
	 * @param state an array of 16 longs; at least one must be nonzero.
	 * @param p the internal index. 
	 */
	public void setState( final long[] state, final int p ) {
		if ( state.length != s.length ) throw new IllegalArgumentException(
			"The argument array contains " + state.length + " longs instead of " + s.length
		);
		System.arraycopy( state, 0, s, 0, s.length );
		this.p = p;
	}
}
