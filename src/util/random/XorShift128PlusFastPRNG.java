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

import org.apache.commons.math3.random.RandomGenerator;

/** 
 * An unbelievably fast, top-quality 64-bit pseudorandom number generator that
 * returns the sum of consecutive outputs of a Marsaglia Xorshift generator (described in <a
 * href="http://www.jstatsoft.org/v08/i14/paper/">&ldquo;Xorshift RNGs&rdquo;</a>, <i>Journal of
 * Statistical Software</i>, 8:1&minus;6, 2003). This is the fastest generator we provide
 * that passes the BigCrush statistical test suite. 
 * 
 * <p>More details about <code>xorshift+</code> generators can be found in my 
 * paper &ldquo;<a href="http://vigna.di.unimi.it/papers.php#VigFSMXG">Further scramblings 
 * of Marsaglia's <code>xorshift</code> generators&rdquo;</a>, 2014. The basic
 * idea is taken from Mutsuo Saito and Makuto Matsumoto's 
 * <a href="http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/XSADD/"><code>XSadd</code></a> generator, which
 * is however based on 32-bit shifts and fails several statistical tests when reversed.
 * 
 * <p>Note that this is
 * <strong>not</strong> a cryptographic-strength pseudorandom number generator, but its quality is
 * preposterously higher than {@link Random}'s, and its cycle length is
 * 2<sup>128</sup>&nbsp;&minus;&nbsp;1, which is more than enough for any single-thread application.
 * 
 * <p>The methods of this class returns the same sequences as those of
 * {@link XorShift128PlusRandom}, to which documentation we refer for more details, 
 * but this class is not a subclass of {@link Random},
 * which unfortunately suffers from a very poor design and is very heavy on memory.
 * If you need a large number of generators that do not need to be an instance
 * of {@link Random}, or simply if you need a {@link RandomGenerator} to use
 * with <a href="http://commons.apache.org/math/">Commons Math</a>, 
 * you might be wanting this class instead of {@link XorShift128PlusRandom}.
 */
public final class XorShift128PlusFastPRNG implements Randomizer {
	
	/** 2<sup>-53</sup>. */
	private static final double NORM_53 = 1. / ( 1L << 53 );

	/** The internal state of the algorithm. */
	private long s0, s1;
	
	private double __nextNextGaussian;
	private boolean __haveNextNextGaussian;
	
	/**
	 *  Creates a new generator seeded using 
	 *  {@link RandomizerUtils#generateRandomSeed()}. 
	 */
	public XorShift128PlusFastPRNG() {
		this( RandomizerUtils.generateRandomSeed() );
	}

	/**
	 * Creates a new generator using a given seed.
	 * 
	 * @param seed a nonzero seed for the generator 
	 * (if zero, the generator will be seeded with -1).
	 */
	public XorShift128PlusFastPRNG( final long seed ) {
		setSeed( seed );
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
	public final void setSeed( final long seed ) {
		s0 = RandomizerUtils.computeMurmurHash3( seed == 0 ? Long.MIN_VALUE : seed );
		s1 = RandomizerUtils.computeMurmurHash3( s0 );
		__haveNextNextGaussian = false;
	}

	protected final long nextLong() {
		long s1 = this.s0;
		final long s0 = this.s1;
		this.s0 = s0;
		s1 ^= s1 << 23;
		return ( this.s1 = ( s1 ^ s0 ^ ( s1 >>> 17 ) ^ ( s0 >>> 26 ) ) ) + s0;
	}
	
	@Override
	public final int nextInt( final int n ) {
		assert ( n > 0 );
		// No special provision for n power of two: all our bits are good.
		for(;;) {
			long s1 = this.s0;
			final long s0 = this.s1;
			this.s0 = s0;
			s1 ^= s1 << 23;
			final long bits = ( 
				( this.s1 = ( s1 ^ s0 ^ ( s1 >>> 17 ) ^ ( s0 >>> 26 ) ) ) + s0 
			) >>> 1;
			final long value = bits % n;
			if ( bits - value + ( n - 1 ) >= 0 ) return (int) value;
		}
	}
	
	@Override
	public final double nextDouble() {
		long s1 = this.s0;
		final long s0 = this.s1;
		this.s0 = s0;
		s1 ^= s1 << 23;
		return ( ( 
			( this.s1 = ( s1 ^ s0 ^ ( s1 >>> 17 ) ^ ( s0 >>> 26 ) ) ) + s0 
		) >>> 11 ) * NORM_53;
	}

	@Override
	public final boolean nextBoolean() {
		long s1 = this.s0;
		final long s0 = this.s1;
		this.s0 = s0;
		s1 ^= s1 << 23;
		return ( ( 
			( this.s1 = ( s1 ^ s0 ^ ( s1 >>> 17 ) ^ ( s0 >>> 26 ) ) ) + s0 
		) & 1 ) != 0;
	}

	@Override
	public final double nextGaussian() {
		// See Knuth, ACP, Section 3.4.1 Algorithm C.       
        if (__haveNextNextGaussian) {
			__haveNextNextGaussian = false;
			return __nextNextGaussian;
		} else {
			long s0, s1;
			double v1, v2, s;
			do {
				//////////////////////////////////////////////////////////////////
				s1 = this.s0;
				s0 = this.s1;
				this.s0 = s0;
				s1 ^= s1 << 23;
				//v1 = 2 * nextDouble() - 1; // between -1.0 and 1.0
				v1 = 2 * ( ( ( 
					( this.s1 = ( s1 ^ s0 ^ ( s1 >>> 17 ) ^ ( s0 >>> 26 ) ) ) + s0 
				) >>> 11 ) * NORM_53 ) - 1;
				//////////////////////////////////////////////////////////////////
				s1 = this.s0;
				s0 = this.s1;
				this.s0 = s0;
				s1 ^= s1 << 23;
				//v2 = 2 * nextDouble() - 1; // between -1.0 and 1.0
				v2 = 2 * ( ( ( 
					( this.s1 = ( s1 ^ s0 ^ ( s1 >>> 17 ) ^ ( s0 >>> 26 ) ) ) + s0 
				) >>> 11 ) * NORM_53 ) - 1;
				//////////////////////////////////////////////////////////////////
				s = v1 * v1 + v2 * v2;
			} while (s >= 1 || s == 0);
			double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s);
			__nextNextGaussian = v2 * multiplier;
			__haveNextNextGaussian = true;
			return v1 * multiplier;
		}
	}
}
