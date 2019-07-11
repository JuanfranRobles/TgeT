package util.random;

public abstract class RandomizerFactory {
	
	public enum RandomizerAlgorithm {
		MERSENNE_TWISTER, 
		MERSENNE_TWISTER_2,
		XOR_SHIFT, 
		XOR_SHIFT_64_STAR,
		XOR_SHIFT_1024_STAR,
		XOR_SHIFT_128_PLUS, 
		NUMERICAL_RECIPES, 
		MERSENNE_TWISTER_FAST, 
		XOR_SHIFT_FAST, 
		XOR_SHIFT_128_PLUS_FAST, 
	}

	public static Randomizer createDefaultRandomizer() {
		return createRandomizer(
			RandomizerAlgorithm.MERSENNE_TWISTER_FAST
		);
	}

	public static Randomizer createDefaultRandomizer(long seed) {
		return createRandomizer(
			RandomizerAlgorithm.MERSENNE_TWISTER_FAST, seed
		);
	}
	
	public static Randomizer createRandomizer(RandomizerAlgorithm algorithm) {
		switch (algorithm) {
			case MERSENNE_TWISTER:			return new MersenneTwisterPRNG();
			case MERSENNE_TWISTER_2:		return new MT19937PRNG();
			case XOR_SHIFT: 				return new XorShiftPRNG();
			case XOR_SHIFT_64_STAR:			return new XorShift64StarPRNG();
			case XOR_SHIFT_1024_STAR:		return new XorShift1024StarPRNG();
			case XOR_SHIFT_128_PLUS:		return new XorShift128PlusPRNG();
			case NUMERICAL_RECIPES:			return new NumericalRecipesPRNG();
			case MERSENNE_TWISTER_FAST:		return new MersenneTwisterFastPRNG();
			case XOR_SHIFT_FAST: 			return new XorShiftFastPRNG();
			case XOR_SHIFT_128_PLUS_FAST:	return new XorShift128PlusFastPRNG();
		}
		throw new IllegalArgumentException("Undefined PRNG algorithm");
	}

	public static Randomizer createRandomizer(RandomizerAlgorithm algorithm, long seed) {
		switch (algorithm) {
			case MERSENNE_TWISTER:			return new MersenneTwisterPRNG(seed);
			case MERSENNE_TWISTER_2:		return new MT19937PRNG(seed);
			case XOR_SHIFT: 				return new XorShiftPRNG(seed);
			case XOR_SHIFT_64_STAR:			return new XorShift64StarPRNG(seed);
			case XOR_SHIFT_1024_STAR:		return new XorShift1024StarPRNG(seed);
			case XOR_SHIFT_128_PLUS:		return new XorShift128PlusPRNG(seed);
			case NUMERICAL_RECIPES:			return new NumericalRecipesPRNG(seed);
			case MERSENNE_TWISTER_FAST:		return new MersenneTwisterFastPRNG(seed);
			case XOR_SHIFT_FAST: 			return new XorShiftFastPRNG(seed);
			case XOR_SHIFT_128_PLUS_FAST:	return new XorShift128PlusFastPRNG(seed);
		}
		throw new IllegalArgumentException("Undefined PRNG algorithm");
	}
	
	
}
