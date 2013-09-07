/* Copyright (C) 2008, Groningen Bioinformatics Centre (http://gbic.biol.rug.nl/)
 * This file is part of PeakML.
 * 
 * PeakML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * PeakML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with PeakML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */



package statistics;


// java
import java.util.*;

/**
 * The class {@link Statistical} contains methods for performing basis statistical operations,
 * such as mean, median, standard deviation. Also supported are correlation analysis, factorial
 * calculations, ranking, analysis of variance, etc. Basically it provides the toolbox for
 * performing an analysis of a set of data.
 * <p />
 * A large number of the implementation have been taken from Numerical Recipes in C.
 */
public abstract class Statistical
{
	/** Indicates that the row should be processed */
	public static final int ROW				= 1;
	/** Indicates that the column should be processed */
	public static final int COLUMN			= 2;
	
	/** The minimum value in the result of the {@link Statistical#stats(double[])} method. */
	public static final int MINIMUM			= 0;
	/** The maximum value in the result of the {@link Statistical#stats(double[])} method. */
	public static final int MAXIMUM			= 1;
	/** The mean value in the result of the {@link Statistical#stats(double[])} method. */
	public static final int MEAN			= 2;
	/** The variance value in the result of the {@link Statistical#stats(double[])} method. */
	public static final int VARIANCE		= 3;
	/** The standard deviation value in the result of the {@link Statistical#stats(double[])} method. */
	public static final int STDDEV			= 4;
	/** The total number of statistics in the result of the {@link Statistical#stats(double[])} method. */
	public static final int NRSTATS			= 5;
	
	/** The lower quartile in the result of the {@link Statistical#quartiles(double[])} method. */
	public static final int QUARTILE_LOWER	= 0;
	/** The median quartile in the result of the {@link Statistical#quartiles(double[])} method. */
	public static final int QUARTILE_MEDIAN	= 1;
	/** The upper quartile in the result of the {@link Statistical#quartiles(double[])} method. */
	public static final int QUARTILE_UPPER	= 2;
	
	/** The w-statistic in the result of the {@link Statistical#shapiroWilk(double[])} method. */
	public static final int SHAPIRO_WILK_WSTAT		= 0;
	/** The p-value in the result of the {@link Statistical#shapiroWilk(double[])} method. */
	public static final int SHAPIRO_WILK_PVALUE		= 1;
	
	/** The correlation value in the result of the {@link Statistical#pearsonsCorrelation(double[], double[])} method. */
	public static final int PEARSON_CORRELATION		= 0;
	/** The fisher transformed correlation (normally distributed) in the result of the {@link Statistical#pearsonsCorrelation(double[], double[])} method. */
	public static final int PEARSON_FISHER			= 1;
	/** The fisher transformed standard error value in the result of the {@link Statistical#pearsonsCorrelation(double[], double[])} method. */
	public static final int PEARSON_FISHER_STDDEV	= 2;
	/** The student's t-test value in the result of the {@link Statistical#pearsonsCorrelation(double[], double[])} method. */
	public static final int PEARSON_TTEST			= 3;
	
	/** The correlation value in the result of the {@link Statistical#spearmanCorrelation(double[], double[])} method. */
	public static final int SPEARMAN_CORRELATION			= 0;
	/** The significant level in the result of the {@link Statistical#spearmanCorrelation(double[], double[])} method. */
	public static final int SPEARMAN_TWOSIDED_SIGNIFICANCE	= 1;
	
	
	// standard statistical tests
	/**
	 * Calculates the minimum value from the values in the given array.
	 * 
	 * @param values		The array with the values.
	 * @return				The minimum value.
	 */
	public static double min(double[] values)
	{
		if (values.length == 0)
			return Double.NaN;
		
		double min = values[0];
		for (int i=values.length-1; i>0; --i)
			min = Math.min(min, values[i]);
		return min;
	}
	
	/**
	 * Calculates the maximum value from the values in the given array.
	 * 
	 * @param values		The array with the values.
	 * @return				The maximum value.
	 */
	public static double max(double[] values)
	{
		if (values.length == 0)
			return Double.NaN;
		
		double max = values[0];
		for (int i=values.length-1; i>0; --i)
			max = Math.max(max, values[i]);
		return max;
	}
	
	/**
	 * 
	 */
	public static int indexOfMax(double values[])
	{
		if (values.length == 0)
			return -1;
		
		int indx = 0;
		double max = values[0];
		for (int i=values.length-1; i>0; --i)
		{
			if (max < values[i])
			{
				indx = i;
				max = values[i];
			}
		}
		
		return indx;
	}
	
	/**
	 * Calculates the sum of the values in the given array.
	 * 
	 * @param values		The array with the values.
	 * @return				The sum.
	 */
	public static double sum(double[] values)
	{
		if (values.length == 0)
			return Double.NaN;
		
		double sum = 0;
		for (int i=0; i<values.length; ++i)
			sum += values[i];
		return sum;
	}
	
	/**
	 * Calculates the mean of the values in the given array.
	 * 
	 * @param values		The array with the values.
	 * @return				The mean.
	 */
	public static double mean(double[] values)
	{
		if (values.length == 0)
			return Double.NaN;
		
		double mean = 0;
		for (double value : values)
			mean += value;
		return mean / values.length;
	}
	
	/**
	 * The geometric mean, in mathematics, is a type of mean or average, which indicates
	 * the central tendency or typical value of a set of numbers. The geometric mean can
	 * be understood in terms of geometry. The geometric mean of two numbers, a and b, is
	 * simply the side length of the square whose area is equal to that of a rectangle with
	 * side lengths a and b. That is, what is n such that n^2 = a � b? Similarly, the
	 * geometric mean of three numbers, a, b, and c, is the side length of a cube whose
	 * volume is the same as that of a rectangular prism with side lengths equal to the
	 * three given numbers.
	 * <p />
	 * The geometric mean only applies to positive numbers.
	 * 
	 * @param values		The array with the values.
	 * @return				The geometric mean of the values in the array.
	 * @throws IllegalArgumentException
	 * 						Thrown when the array has 0 elements or the array contains one or more negative values.
	 * @see					http://en.wikipedia.org/wiki/Geometric_mean
	 */
	public static double geomean(double[] values) throws IllegalArgumentException
	{
		double stats[] = stats(values);
		if (values.length == 0)
			throw new IllegalArgumentException("Geometrical mean needs a vector of at least 1 element");
		if (stats[Statistical.MINIMUM] < 0)
			throw new IllegalArgumentException("Geometrical mean only works on positive values.");
		
		double scaled[] = values.clone();
		for (int i=0; i<scaled.length; ++i)
			scaled[i] /= stats[Statistical.MAXIMUM];
		
		double geomean = scaled[0];
		for (int i=1; i<scaled.length; ++i)
			geomean *= scaled[i];
		
		return Math.pow(geomean, 1./scaled.length) * stats[Statistical.MAXIMUM];
	}
	
	/**
	 * Calculates the median of the values in the given array. This method is highly
	 * optimized and gives correct values for arrays of odd size. For arrays of even
	 * size and an excess of 100 elements, a small error is introduced, the value
	 * values[values.length/2] is returned as opposed to
	 * (values[values.length/2-1]+values[values.length/2])/2. Quoting
	 * from numerical recipes: "When N is odd, the median is the kth element, with
	 * k=(N+1)/2. When N is even, statistics books define the median as the arithmatic
	 * mean of the elements k=N/2 and k=N/2+1 (that is, N/2 from the bottom and N/2
	 * from the top). If you accept such pedantry, you must perform two separate
	 * selections to find these elements. For N>100 we usually define k=N/2 to be
	 * the median element, pedants be dammed."
	 * <p />
	 * For some fixed sizes (3,5,6,7,9) the most optimal sorting is implemented
	 * (taken from XILINX XCELL magazine, vol. 23 by John L. Smith). The method
	 * {@link Statistical#kthElement(double[], int)} is used for other cases.
	 * 
	 * @param values		The array with the values.
	 * @return				The median value.
	 */
	public static double median(double[] values)
	{
		// exclude the stupid case
		if (values.length == 0)
			return 0;
		else if (values.length == 1)
			return values[0];
		else if (values.length == 2)
			return (values[0]+values[1]) * 0.5;
		
		// optmized version
		if (values.length%2==0 && values.length<100 && values.length!=6)
		{
			double sorted[] = values.clone();
			Arrays.sort(sorted);
			return (sorted[sorted.length/2-1]+sorted[sorted.length/2]) / 2.;
		}
		else if (values.length > 9)
		{
			return kthElement(values, values.length/2);
		}
		else if (values.length == 3) // time stable, 100000 iterations 31 vs 47 (Arrays.sort)
		{
			double sorted[] = values.clone();
			pixSort(sorted,0,1); pixSort(sorted,1,2); pixSort(sorted,0,1);
			return sorted[1];
		}
		else if (values.length == 5) // time stable, 100000 iterations 31 vs 47 (Arrays.sort)
		{
			double sorted[] = values.clone();
			pixSort(sorted,0,1); pixSort(sorted,3,4); pixSort(sorted,0,3);
			pixSort(sorted,1,4); pixSort(sorted,1,2); pixSort(sorted,2,3); pixSort(sorted,1,2);
			return sorted[2];
		}
		else if (values.length == 6) // time stable, 100000 iterations 31 vs 62 (Arrays.sort)
		{
			double sorted[] = values.clone();
			pixSort(sorted,1,2); pixSort(sorted,3,4);
			pixSort(sorted,0,1); pixSort(sorted,2,3); pixSort(sorted,4,5);
			pixSort(sorted,1,2); pixSort(sorted,3,4);
			pixSort(sorted,0,1); pixSort(sorted,2,3); pixSort(sorted,4,5);
			pixSort(sorted,1,2); pixSort(sorted,3,4);
		    return (sorted[2]+sorted[3]) * 0.5;
		}
		else if (values.length == 7) // time stable, 100000 iterations 32 vs 62 (Arrays.sort)
		{
			double sorted[] = values.clone();
			pixSort(sorted,0,5); pixSort(sorted,0,3); pixSort(sorted,1,6);
			pixSort(sorted,2,4); pixSort(sorted,0,1); pixSort(sorted,3,5);
			pixSort(sorted,2,6); pixSort(sorted,2,3); pixSort(sorted,3,6);
			pixSort(sorted,4,5); pixSort(sorted,1,4); pixSort(sorted,1,3); pixSort(sorted,3,4);
			return sorted[3];
		}
		else/* if (values.length == 9)*/ // time stable, 100000 iterations 47 vs 62 (Arrays.sort)
		{
			double sorted[] = values.clone();
			pixSort(sorted,1,2); pixSort(sorted,4,5); pixSort(sorted,7,8);
			pixSort(sorted,0,1); pixSort(sorted,3,4); pixSort(sorted,6,7);
			pixSort(sorted,1,2); pixSort(sorted,4,5); pixSort(sorted,7,8);
			pixSort(sorted,0,3); pixSort(sorted,5,8); pixSort(sorted,4,7);
			pixSort(sorted,3,6); pixSort(sorted,1,4); pixSort(sorted,2,5);
			pixSort(sorted,4,7); pixSort(sorted,4,2); pixSort(sorted,6,4); pixSort(sorted,4,2);
			return sorted[4];
		}
	}
	
	/**
	 * The variance of a sample is one measure of statistical dispersion, averaging the
	 * squared distance of its possible values from the expected value (mean). Whereas the
	 * mean is a way to describe the location of a distribution, the variance is a way to
	 * capture its scale or degree of being spread out.
	 * 
	 * @param values		The array with the values.
	 * @return				The variance.
	 * @see					http://en.wikipedia.org/wiki/Variance
	 */
	public static double variance(double[] values)
	{
		double mean = mean(values);
		
		double variance = 0;
		for (double value : values)
			variance += Math.pow(value-mean, 2);
		return variance / (values.length-1);
	}
	
	/**
	 * The standard deviation of a sample is one measure of statistical dispersion, calculated
	 * by taking the square root of the deviation.
	 * 
	 * @param values		The array with the values.
	 * @return				The standard deviation.
	 * @see					http://en.wikipedia.org/wiki/Standard_deviation
	 */
	public static double stddev(double[] values)
	{
		double mean = mean(values);
		
		double stddev = 0;
		for (double value : values)
			stddev += Math.pow(value-mean, 2);
		return Math.sqrt(stddev / (values.length-1));
	}
	
	/**
	 * 
	 * @param values
	 * @return
	 */
	public static double rsd(double[] values)
	{
		double stats[] = stats(values);
		return stats[Statistical.STDDEV] / stats[Statistical.MEAN];
	}
	
	/**
	 * Returns the k-th smallest value in the given array. This is a highly optimized
	 * method, which should be preferred over sorting the complete array with for
	 * example Arrays.sort.
	 * <p />
	 * This method is taken from numerical recipes in paragraph 8.5 (select).
	 * 
	 * @param values		The array with the values.
	 * @param k				The index.
	 * @return				The k-th smalles element.
	 */
	public static double kthElement(double values[], int k)
	{
		if (values.length == 0)
			return 0;
		
		int l=0, ir=values.length-1;
		double sorted[] = values.clone();
		for ( ; ; )
		{
			// active partition contains 1 or 2 elements
			if (ir <= l+1)
			{
				// the case of two elements
				if (ir==l+1 && sorted[ir]<sorted[l])
					pixSwap(sorted,l,ir);
				return sorted[k];
			}
			else
			{
				// choose median of left, center and right elements as partitioning
				// element a. also rearrange so that:
				//		sorted[l] <= sorted[l+1] && sorted[ir] >= sorted[l+1].
				int	mid = (l+ir) >> 1;
				pixSwap(sorted,mid,l+1);
				pixSort(sorted,l,ir);
				pixSort(sorted,l+1,ir);
				pixSort(sorted,l,l+1);

				// pointers for partioning
				int i=l+1, j=ir;
				// partitioning element.
				double a = sorted[l+1];

				for ( ; ; )
				{
					// scan up to find first element > a
					do i++; while (sorted[i] < a);
					// scan down to find last element < a
					do j--; while (sorted[j] > a);

					// pointers crossed, so the partioning is complete.
					if (j<i)
						break;
					pixSwap(sorted,i,j);
				}

				// insert the partitioning element
				sorted[l+1]=sorted[j]; sorted[j]=a;

				// keep active the partition that contains the median
				if (j >= k)
					ir = j-1;
				if (j <= k)
					l = i;
			}
		}
	}
	
	/**
	 * Calculates some basic statistics on the given array of data. An array with the
	 * results is returned, which can safely be accessed by using the constant field
	 * values: {@link Statistical#MINIMUM}, {@link Statistical#MAXIMUM},
	 * {@link Statistical#MEAN}, {@link Statistical#VARIANCE} and
	 * {@link Statistical#STDDEV}.
	 * 
	 * @param values		The array with the values.
	 * @return				The basic statistics calculated from the array.
	 */
	public static double[] stats(double[] values)
	{
		double results[] = new double[NRSTATS];
		
		// calculate the mean
		results[MEAN] = 0;
		results[MINIMUM] = Double.MAX_VALUE;
		results[MAXIMUM] = Double.MIN_VALUE;
		for (double v : values)
		{
			results[MEAN] += v;
			results[MINIMUM] = Math.min(results[MINIMUM], v);
			results[MAXIMUM] = Math.max(results[MAXIMUM], v);
		}
		results[MEAN] /= values.length;
		
		// calculate the variance and stddev
		results[VARIANCE] = 0;
		for (double v : values)
			results[VARIANCE] += Math.pow(v-results[MEAN], 2);
		results[VARIANCE] /= (values.length-1);
		results[STDDEV] = Math.sqrt(results[VARIANCE]);
		
		return results;
	}
	
	/**
	 * Quartiles partition the corresponding distribution into four quarters each
	 * containing 25% of the data. A particular quartile is therefore the border
	 * between two neighboring quarters of the distribution. 
	 * 
	 * @param values		Array with the distribution to quartile.
	 * @return				Array with the three quartile values.
	 * @see					http://en.wikipedia.org/wiki/Quartile
	 * @see					http://www.vias.org/tmdatanaleng/cc_quartile.html
	 */
	public static double[] quartiles(double values[])
	{
		// sort the values
		double sorted[] = values.clone();
		Arrays.sort(sorted);
		
		// calculate the quantiles on the sorted values
		double quantiles[] = new double[3];
		quantiles[QUARTILE_LOWER] = sorted[(int) Math.round(0.25 * sorted.length)];
		if (sorted.length%2 != 0)
			quantiles[QUARTILE_MEDIAN] = sorted[(int) Math.round(sorted.length/2)];
		else
			quantiles[QUARTILE_MEDIAN] = (sorted[(int) Math.round(sorted.length/2)-1] + sorted[(int) Math.round(sorted.length/2)]) / 2.;
		quantiles[QUARTILE_UPPER] = sorted[(int) Math.round(0.75 * sorted.length)];
		
		return quantiles;
	}
	
	/**
	 * In descriptive statistics, the interquartile range (IQR), also called the midspread, middle
	 * fifty and middle of the #s, is a measure of statistical dispersion, being equal to the
	 * difference between the third and first quartiles.
	 * <br>
	 * Unlike the (total) range, the interquartile range is a robust statistic, having a breakdown
	 * point of 25%, and is thus often preferred to the total range.
	 * 
	 * @param values		The array with the values.
	 * @return				The inter quartile range.
	 * @see					http://en.wikipedia.org/wiki/Interquartile_range
	 */
	public static double interquartileRange(double values[])
	{
		double quantiles[] = quartiles(values);
		return quantiles[QUARTILE_UPPER] - quantiles[QUARTILE_LOWER];
	}
	
	/**
	 * Normalizes the values in the given vector to the maximum (ie the maximum value will be
	 * 1). The values in the vector are adjusted.
	 * 
	 * @param values		The array with the values.
	 */
	public static void normalize(double values[])
	{
		double max = max(values);
		for (int i=0; i<values.length; ++i)
			values[i] /= max;
	}
	
	/**
	 * Normalizes the values in the given vector to given the maximum. The values
	 * in the vector are adjusted.
	 * 
	 * @param values		The array with the values.
	 * @param max			The maximum to scale to.
	 */
	public static void normalize(double values[], double max)
	{
		for (int i=0; i<values.length; ++i)
			values[i] /= max;
	}
	
	
	// scaling
	/**
	 * Calculates the standard score for either each row or each column. The standard score
	 * indicates how many standard deviations an observation is above or below the mean. It
	 * allows comparison of observations from different normal distributions, which is done
	 * frequently in research.
	 * <br />
	 * For each value the mean of either the row or the column is subtracted and the result
	 * divided by the standard deviation of either the row or the column. This causes the mean
	 * to be 0 and the standard deviation to be 1 for either each row or each column.
	 * 
	 * @param data				The data matrix to be scaled
	 * @param rowcolumn			Either {@link Statistical#ROW} or {@link Statistical#COLUMN}.
	 */
	public static double[][] scale(double data[][], int rowcolumn)
	{
		if (rowcolumn!=ROW && rowcolumn!=COLUMN)
			throw new IllegalArgumentException("rowcolumn needs to be either Statistical.ROW or Statistical.COLUMN");
		
		int rows = data.length;
		int cols = data[0].length;
		
		double scaled[][] = new double[rows][cols];
		if (rowcolumn == COLUMN)
		{
			double col[] = new double[rows];
			for (int c=0; c<cols; ++c)
			{
				// calculate the sorted array
				for (int r=0; r<rows; ++r)
					col[r] = data[r][c];
				
				// get the statistics
				double stats[] = Statistical.stats(col);
				
				// scale the values in the matrix
				for (int r=0; r<rows; ++r)
					scaled[r][c] = (data[r][c]-stats[MEAN]) / stats[STDDEV];
			}
		}
		else if (rowcolumn == ROW)
		{
			double row[] = new double[cols];
			for (int r=0; r<rows; ++r)
			{
				// calculate the sorted array
				System.arraycopy(data[r], 0, row, 0, cols);
				
				// get the statistics
				double stats[] = Statistical.stats(row);
				
				// scale the values in the matrix
				for (int c=0; c<cols; ++c)
					scaled[r][c] = (data[r][c]-stats[MEAN]) / stats[STDDEV];
			}
		}
		
		return scaled;
	}
	
	
	// correlations
	/**
	 * In statistics, the Pearson product-moment correlation coefficient (sometimes referred
	 * to as the MCV or PMCC, and typically denoted by r) is a common measure of the correlation
	 * (linear dependence) between two variables X and Y. It is very widely used in the sciences
	 * as a measure of the strength of linear dependence between two variables, giving a value
	 * somewhere between +1 and -1 inclusive.
	 * <p />
	 * This implementation has been based on Numerical Recipes in C paragraph 14.5.
	 * 
	 * @param xvalues		The array with the x-values.
	 * @param yvalues		The array with the y-values.
	 * @return				The correlation between the x- and y-values.
	 */
	public static double[] pearsonsCorrelation(double xvalues[], double yvalues[])
	{
		final double TINY = 1.e-20; // used to normalize complete correlation
		
		int length = xvalues.length;
		if (xvalues.length != yvalues.length)
			throw new RuntimeException("Pearson's correlation needs two vectors of the same size.");
		
		double xmean = mean(xvalues);
		double ymean = mean(yvalues);
		
		double sxx = 0;
		double syy = 0;
		double sxy = 0;
		for (int i=0; i<length; ++i)
		{
			double xt = xvalues[i] - xmean;
			double yt = yvalues[i] - ymean;
			
			sxx += xt*xt;
			syy += yt*yt;
			sxy += xt*yt;
		}
		
		// correlation
		double r = sxy / Math.sqrt(sxx*syy) + TINY;
		
		// returning array
		double values[] = new double[4];
		
		// Pearson's correlation
		values[PEARSON_CORRELATION] = r;
		// Fisher's z-transformation
		values[PEARSON_FISHER] = .5 * Math.log((1.+r+TINY) / (1.-r+TINY));
		values[PEARSON_FISHER_STDDEV] = 1 / Math.sqrt(length-3);
		// Student's t probability
		double df = length - 2;
		double t = r * Math.sqrt(df / ((1.-r+TINY) * (1.+r+TINY)));
		values[PEARSON_TTEST] = Statistical.betai(0.5*df, 0.5, df/(df+t*t));
		
		return values;
	}
	
	public static double pearsonsCorrelationS(double xvalues[], double yvalues[])
	{
		final double TINY = 1.e-20; // used to normalize complete correlation
		
		int length = xvalues.length;
		if (xvalues.length != yvalues.length)
			throw new RuntimeException("Pearson's correlation needs two vectors of the same size.");
		
		double xmean = mean(xvalues);
		double ymean = mean(yvalues);
		
		double sxx = 0;
		double syy = 0;
		double sxy = 0;
		for (int i=0; i<length; ++i)
		{
			double xt = xvalues[i] - xmean;
			double yt = yvalues[i] - ymean;
			
			sxx += xt*xt;
			syy += yt*yt;
			sxy += xt*yt;
		}
		
		// correlation
		return sxy / Math.sqrt(sxx*syy) + TINY;
	}
	
	/**
	 * Calculates the Spearman rank correlation and the two-sided significance levels of its
	 * deviation from zero between the two given arrays. A small significance level indicates
	 * a good correlation (correlation positive) or anti-correlation (correlation negative).
	 * <p />
	 * This implementation has been based on Numerical Recipes in C paragraph 14.6.
	 * 
	 * @param xvalues		The array with the x-values.
	 * @param yvalues		The array with the y-values.
	 * @return				The calculated values.
	 */
	public static double[] spearmanCorrelation(double xvalues[], double yvalues[])
	{
		double values[] = new double[2];
		
		int n = xvalues.length;
		double xranks[] = new double[xvalues.length];
		System.arraycopy(xvalues, 0, xranks, 0, xvalues.length);
		double yranks[] = new double[xvalues.length];
		System.arraycopy(yvalues, 0, yranks, 0, yvalues.length);
		
		qsort(xranks, yranks);
		double sf = crank(xranks);
		qsort(yranks, xranks);
		double sg = crank(yranks);
		
		double d = 0;
		for (int i=0; i<n; ++i)
			d += Math.pow(xranks[i]-yranks[i], 2);
		
		double en = n;
		double en3n = en*en*en-en;
//		double aved = en3n/6. - (sf+sg)/12.;
		double fac = (1.0-sf/en3n) * (1.0-sg/en3n);
//		double vard =((en-1.0)*en*en * Math.pow(en+1.0, 2)/36.0)*fac;
//		double zd = (d-aved) / Math.sqrt(vard);
//		double probd = erfcc(Math.abs(zd) / 1.4142136);
		double rs = (1.0-(6.0/en3n) * (d+(sf+sg)/12.0)) / Math.sqrt(fac);
		fac = (rs+1.0) * (1.0-rs);
		if (fac > 0.0)
		{
			double t = rs * Math.sqrt((en-2.0)/fac);
			double df = en - 2.0;
			values[Statistical.SPEARMAN_TWOSIDED_SIGNIFICANCE] = betai(0.5*df, 0.5, df/(df+t*t));
		}
		else
		{
			values[Statistical.SPEARMAN_TWOSIDED_SIGNIFICANCE] = 0.;
		}
		
		values[Statistical.SPEARMAN_CORRELATION] = rs;
		
		return values;
	}
	
	public static double spearmanCorrelationS(double xvalues[], double yvalues[])
	{
		int n = xvalues.length;
		double xranks[] = new double[xvalues.length];
		System.arraycopy(xvalues, 0, xranks, 0, xvalues.length);
		double yranks[] = new double[xvalues.length];
		System.arraycopy(yvalues, 0, yranks, 0, yvalues.length);
		
		qsort(xranks, yranks);
		double sf = crank(xranks);
		qsort(yranks, xranks);
		double sg = crank(yranks);
		
		double d = 0;
		for (int i=0; i<n; ++i)
			d += Math.pow(xranks[i]-yranks[i], 2);
		
		double en = n;
		double en3n = en*en*en-en;
		double fac = (1.0-sf/en3n) * (1.0-sg/en3n);
		return (1.0-(6.0/en3n) * (d+(sf+sg)/12.0)) / Math.sqrt(fac);
	}	
	
	// factorial
	/**
	 * In mathematics, the factorial of a non-negative integer n, denoted by n!, is the product
	 * of all positive integers less than or equal to n. This method calculates the natural
	 * logarithm of factorial of the given n. This method utilizes the
	 * {@link Statistical#gammaln(double)} method, which is highly optimized.
	 * 
	 * @param n				The non-negative integer.
	 * @return				The natural logarithm of the factorial.
	 */
	public static double factorialln(int n)
	{
		if (n <= 1)
			return 0;
		return gammaln(n+1);
	}
	
	/**
	 * A binomial is a polynomial with two terms�the sum of two monomials�often bound by
	 * parenthesis or brackets when operated upon. It is the simplest kind of polynomial
	 * other than monomials. This method calculates the natural logarithm if the
	 * bionomial.
	 * 
	 * @param n				The non-negative integer.
	 * @param k				The non-negative integer.
	 * @return				The natural logarithm of the binomial.
	 */
	public static double binomialln(int n, int k)
	{
		return factorialln(n) - factorialln(k) - factorialln(n - k); 
	}

	
	// analysis of variance
	/**
	 * A t-test is any statistical hypothesis test in which the test statistic has a
	 * Student's t distribution if the null hypothesis is true. It is applied when the
	 * population is assumed to be normally distributed but the sample sizes are small
	 * enough that the statistic on which inference is based is not normally distributed
	 * because it relies on an uncertain estimate of standard deviation rather than on a
	 * precisely known value.
	 * 
	 * @param a				Population 1.
	 * @param b				Population 2.
	 * @return				The t-test p-value. Less than 0.05 could be considered significant.
	 */
	public static double ttest(double a[], double b[])
	{
		int n1 = a.length;
		int n2 = b.length;
		double mean1 = Statistical.mean(a);
		double mean2 = Statistical.mean(b);
		double variance1 = Statistical.variance(a);
		double variance2 = Statistical.variance(b);
		
		double df = n1 + n2 - 2;
		double svar = ((n1-1)*variance1 + (n2-1)*variance2) / df;
		double t = (mean1-mean2) / Math.sqrt(svar * (1.0/n1 + 1.0/n2));
		
		return betai(0.5*df, 0.5, df/(df+(t*t))) / 2.;
	}
	
	/**
	 * An F-test is any statistical test in which the test statistic has an F-distribution
	 * if the null hypothesis is true.
	 * 
	 * @param a				Population 1.
	 * @param b				Population 2.
	 * @return				The f-test p-value. Less than 0.05 could be considered significant.
	 */
	public static double ftest(double a[], double b[])
	{
		int n1 = a.length;
		int n2 = b.length;
		double variance1 = Statistical.variance(a);
		double variance2 = Statistical.variance(b);
		
		double f, df1, df2;
		if (variance1 > variance2)
		{
			f = variance1 / variance2;
			df1 = n1 - 1;
			df2 = n2 - 1;
		}
		else
		{
			f = variance2 / variance1;
			df1 = n2 - 1;
			df2 = n1 - 1;
		}
		
		double pvalue = 2.0 * betai(0.5*df2, 0.5*df1, df2 / (df2+df1*f));
		if (pvalue > 1.0)
			return 2.0 * pvalue;
		return pvalue;
	}
	
	
	// ranking
	/**
	 * Highly optimized function for ranking the contents of a vector. The result is a
	 * vector of equal size as the data-vector given as a parameter, which is filled with
	 * the ranks of the cells in the data vector.
	 * <p>
	 * A binary search is utilized to find the rank-values of each of the cells.
	 * 
	 * @param data			The data vector to rank.
	 * @param reversed		When set to true, the ranks will be reversed
	 * @return				The ranks of the data in the vector.
	 */
	public static double[] rank(double data[], boolean reversed)
	{
		int size = data.length;
		
		// make a sorted copy
		double dup[] = new double[data.length];
		System.arraycopy(data, 0, dup, 0, data.length);
		Arrays.sort(dup);
		
		// make the ranks buffer
		double ranks[] = new double[data.length];
		for (int i=0; i<size; ++i)
		{
			double val = data[i];
			
			int low = 0; int mid = size / 2; int hgh = size-1;
			while (val != dup[mid])
			{
				if (val < dup[mid])
					hgh = mid - 1;
				else
					low = mid + 1;
				mid = (low+hgh) / 2;
			}
			
			// check if we have a tie
			int jt = 0;
			int ties = 0;
			jt = mid - 1;
			while (jt>=0 && dup[jt--]==val) ties++;
			jt = mid + 1;
			if (ties != 0)		// check if we have ties lower than mid index, then decrease mid-index to find the first one in the sorted array
				mid -= ties;
			while (jt<size && dup[jt++]==val) ties++;
			
			if (ties == 0)
				ranks[i] = (reversed ? size-mid : mid+1);
			else
				ranks[i] = (reversed ? size-(0.5*(mid+mid+ties)+1) : (0.5*(mid+mid+ties)+1));
		}
		
		return ranks;
	}
	
	/**
	 * Highly optimized function for ranking the contents of a matrix either on the COLUMN
	 * or the ROW. The result is a matrix of equal size as the data-matrix given as a 
	 * parameter, which is filled with the ranks of the cells in the data matrix. The
	 * rowcolumn parameter can be used to indicate whether the ranking needs to be done
	 * on the rows or the columns.
	 * <p>
	 * A binary search is utilized to find the rank-values of each of the cells.
	 * 
	 * @param data			The data-matrix to rank
	 * @param reversed		When set to true, the ranks will be reversed
	 * @param rowcolumn		Either {@link Statistical#ROW} or {@link Statistical#COLUMN}
	 * @return				A matrix of the same size as data filled with the ranks
	 * throws IllegalArgumentException
	 * 						Thrown when the rowcolumn parameter has an illegal value
	 */
	public static double[][] rank(double data[][], boolean reversed, int rowcolumn) throws IllegalArgumentException
	{
		if (rowcolumn!=ROW && rowcolumn!=COLUMN)
			throw new IllegalArgumentException("rowcolumn needs to be either Statistical.ROW or Statistical.COLUMN");
		
		int rows = data.length;
		int cols = data[0].length;
		
		double ranks[][] = new double[rows][cols];
		if (rowcolumn == COLUMN)
		{
			double col[] = new double[rows];
			for (int c=0; c<cols; ++c)
			{
				// calculate the sorted array
				for (int r=0; r<rows; ++r)
					col[r] = data[r][c];
				Arrays.sort(col);
				
				// retrieve the ranks with a binary search (should be a bit more optimal :) ).
				for (int r=0; r<rows; ++r)
				{
					double val = data[r][c];
					
					int low = 0; int mid = rows / 2; int hgh = rows-1;
					while (val != col[mid])
					{
						if (val < col[mid])
							hgh = mid - 1;
						else
							low = mid + 1;
						mid = (low+hgh) / 2;
					}
					
					// check if we have a tie
					int jt = 0;
					int ties = 0;
					jt = mid - 1;
					while (jt>=0 && col[jt--]==val) ties++;
					jt = mid + 1;
					if (ties != 0)		// check if we have ties lower than mid index, then decrease mid-index to find the first one in the sorted array
						mid -= ties;
					while (jt<rows && col[jt++]==val) ties++;
					
					if (ties == 0)
						ranks[r][c] = (reversed ? rows-mid : mid+1);
					else
						ranks[r][c] = (reversed ? rows-(0.5*(mid+mid+ties)+1) : (0.5*(mid+mid+ties)+1));
				}
			}
		}
		else if (rowcolumn == ROW)
		{
			double row[] = new double[cols];
			for (int r=0; r<rows; ++r)
			{
				// calculate the sorted array
				System.arraycopy(data[r], 0, row, 0, cols);
				Arrays.sort(row);
				
				// retrieve the ranks with a binary search (should be a bit more optimal :) ).
				for (int c=0; c<cols; ++c)
				{
					double val = data[r][c];
					
					int low = 0; int mid = cols / 2; int hgh = cols-1;
					while (val != row[mid])
					{
						if (val < row[mid])
							hgh = mid - 1;
						else
							low = mid + 1;
						mid = (low+hgh) / 2;
					}
					
					// check if we have a tie
					int jt = 0;
					int ties = 0;
					jt = mid - 1;
					while (jt>=0 && row[jt--]==val) ties++;
					jt = mid + 1;
					if (ties != 0)		// check if we have ties lower than mid index, then decrease mid-index to find the first one in the sorted array
						mid -= ties;
					while (jt<cols && row[jt++]==val) ties++;
					
					if (ties == 0)
						ranks[r][c] = (reversed ? cols-mid : mid+1);
					else
						ranks[r][c] = (reversed ? cols-(0.5*(mid+mid+ties)+1) : (0.5*(mid+mid+ties)+1));
				}
			}
		}
		
		return ranks;
	}
	
	
	// sorting
	/**
	 * 
	 * @param values
	 * @param arrays
	 */
	public static final void qsort(double values[], double[]... arrays)
	{
		final int M = 7;
		final int NSTACK = 50;
		
		
		// start of procedure
		int n = values.length;
		int istack[] = new int[NSTACK];
		
		double b[] = new double[arrays.length];
		
		int l=0, ir=n-1, jstack=0;
		for ( ; ; )
		{
			// insertion sort
			if (ir-l < M)
			{
				for (int j=l+1; j<=ir; ++j)
				{
					int i = j-1;
					double a = values[j];
					for (int arrindx=0; arrindx<arrays.length; ++arrindx)
						b[arrindx] = arrays[arrindx][j];
					for ( ; i>=l; --i)
					{
						if (values[i] <= a) break;
						values[i+1] = values[i];
						for (double[] arr2 : arrays)
							arr2[i+1] = arr2[i];
					}
					values[i+1] = a;
					for (int arrindx=0; arrindx<arrays.length; ++arrindx)
						arrays[arrindx][i+1] = b[arrindx];
				}
				if (jstack == 0) return;
				
				ir = istack[jstack];
				l = istack[jstack - 1];
				jstack -= 2;
			}
			// quick sort
			else
			{
				// choose median of left, center and right elements as partitioning element a
				// also rearrange so that a[l] <= a[l+1] <= a[ir]
				int k = (l+ir) >> 1;
				pixSwap(values, k, l+1);
				for (double arr2[] : arrays)
					pixSwap(arr2, k, l+1);
				
				if (values[l] > values[ir])
				{
					pixSwap(values, l, ir);
					for (double arr2[] : arrays)
						pixSwap(arr2, l, ir); 
				}
				if (values[l+1] > values[ir])
				{
					pixSwap(values, l+1, ir);
					for (double arr2[] : arrays)
						pixSwap(arr2, l+1, ir);
				}
				if (values[l] > values[l+1])
				{
					pixSwap(values, l, l+1);
					for (double arr2[] : arrays)
						pixSwap(arr2, l, l+1);
				}
				
				int j = ir;
				int i = l + 1;
				double a = values[l+1];
				for (int arrindx=0; arrindx<arrays.length; ++arrindx)
					b[arrindx] = arrays[arrindx][l+1];
				
				for ( ; ; )
				{
					do i++; while (values[i]<a);
					do j--; while (values[j]>a);
					if (j < i) break;
					pixSwap(values, i, j);
					for (double arr2[] : arrays)
						pixSwap(arr2, i, j);
				}
				
				values[l+1] = values[j]; values[j] = a;
				for (int arrindx=0; arrindx<arrays.length; ++arrindx)
				{
					arrays[arrindx][l+1] = arrays[arrindx][j];
					arrays[arrindx][j] = b[arrindx];
				}
				jstack += 2;
				
				if (jstack >= NSTACK)
					throw new RuntimeException("hard value for the stack size was too small in Statistical.sortTwo: '" + NSTACK + "'");
				
				if (ir-i+1 >= j-l)
				{
					istack[jstack] = ir;
					istack[jstack-1] = i;
					ir = j - 1;
				}
				else
				{
					istack[jstack] = j - 1;
					istack[jstack-1] = l;
					l = i;
				}
			}
		}
	}
	
	
	// randomization
	/**
	 * Randomly permutes the values in the given vector. This method makes use of an internally
	 * seeded with time of creation for this class. This should ensure that the pseudo random
	 * values generated are reasonable. The passed vector is not affected.
	 * 
	 * @param vector		The vector which needs to be permuted.
	 * @return				The new vector with the permutation.
	 */
	public static double[] permute(double[] vector)
	{
		int length = vector.length;
		
		double result[] = new double[length];
		System.arraycopy(vector, 0, result, 0, length);
		
		for (int i=0; i<length; ++i)
			pixSwap(result, i, (int) (random.nextDouble() * length));
		
		return result;
	}
	
	/**
	 * Randomly permutes the values in the given matrix. The rowcolumn argument indicates
	 * whether it is {@link Statistical#ROW} or {@link Statistical#COLUMN}. This method makes
	 * use of an internally seeded with time of creation for this class. This should ensure
	 * that the pseudo random values generated are reasonable. The passed matrix is not
	 * affected.
	 * 
	 * @param data			The matrix to be permuted.
	 * @param rowcolumn		Either {@link Statistical#ROW} or {@link Statistical#COLUMN}
	 * @return				The new matrix  with the permutation
	 * @throws IllegalArgumentException
	 * 						Thrown when the rowcolumn parameter has an illegal value
	 */
	public static double[][] permute(double[][] data, int rowcolumn) throws IllegalArgumentException
	{
		if (rowcolumn!=ROW && rowcolumn!=COLUMN)
			throw new IllegalArgumentException("rowcolumn needs to be either Statistical.ROW or Statistical.COLUMN");
		
		int rows = data.length;
		int cols = data[0].length;
		
		double result[][] = new double[rows][cols];
		if (rowcolumn == COLUMN)
		{
			for (int c=0; c<cols; ++c)
			{
				// fill completely
				for (int r=0; r<rows; ++r)
					result[r][c] = data[r][c];
				
				// swap all the row-values in this column
				for (int r=0; r<rows; ++r)
				{
					// swap with a random row-value
					int target = (int) (random.nextDouble() * rows);
					double t = result[r][c];
					result[r][c] = result[target][c];
					result[target][c] = t;
				}
			}
		}
		else if (rowcolumn == ROW)
		{
			for (int r=0; r<rows; ++r)
			{
				// fill completely
				System.arraycopy(data[r], 0, result[r], 0, cols);
				
				// swap all the row-values in this column
				for (int c=0; c<cols; ++c)
				{
					// swap with a random row-value
					int target = (int) (random.nextDouble() * rows);
					double t = result[r][c];
					result[r][c] = result[target][c];
					result[target][c] = t;
				}
			}
		}
		
		return result;
	}
	
	
	// special functions
	/**
	 * The gamma function interpolates the factorial function. For integer n:
	 * gamma(n+1) = n! = prod(1:n).
	 * <p />
	 * Reference: "Lanczos, C. 'A precision approximation of the gamma function', 
	 * J. SIAM Numer. Anal., B, 1, 86-96, 1964.".
	 * Translation of Alan Miller's FORTRAN-implementation.
	 * 
	 * @param z				The value for which to calculate the gammaln.
	 * @return				The gammaln of z.
	 * @see					See http://lib.stat.cmu.edu/apstat/245
	 */
	public static double gammaln(double z)
	{
		double x = 0;
		
		x += 0.1659470187408462e-06 / (z + 7);
		x += 0.9934937113930748e-05 / (z + 6);
		x -= 0.1385710331296526 / (z + 5);
		x += 12.50734324009056 / (z + 4);
		x -= 176.6150291498386 / (z + 3);
		x += 771.3234287757674 / (z + 2);
		x -= 1259.139216722289 / (z + 1);
		x += 676.5203681218835 / (z);
		x += 0.9999999999995183;
		
		return (Math.log(x) - 5.58106146679532777 - z + (z - 0.5) * Math.log(z + 6.5));
	}
	
	public static double beta(double z, double w)
	{
		return Math.exp(gammaln(z) + gammaln(w) - gammaln(z+w));
	}
	
	public static double betai(double a, double b, double x)
	{
		// sanity check
		if (x<0.0 || x>1.0)
			return Double.NaN;

		// building the parameters
		double bt;
		if (x==0.0 || x==1.0)
			bt = 0.0;
		else
			bt = Math.exp(gammaln(a+b) - gammaln(a) - gammaln(b) + a*Math.log(x) + b*Math.log(1.0-x));
		
		// calculate the value
		if (x < (a+1.0)/(a+b+2.0))
			return bt * betacf(a,b,x)/a;
		else
			return 1.0 - bt*betacf(b,a,1.0-x)/b;
	}
	
	public static double betacf(double a, double b, double x)
	{
		// twiddleable parameters
		final int MAXIT = 100;
		final double EPS = 3.0e-7;
		final double FPMIN = 1.0e-30;
		
		// the q's that will be used in factors that occur in the coefficients
		double qab = a + b;
		double qap = a + 1.0;
		double qam = a - 1.0;
		
		// first step of Lentz's method
		double c = 1.0;
		double d = 1.0 - qab*x/qap;
		if (Math.abs(d) < FPMIN)
			d = FPMIN;
		d = 1.0 / d;
		double h = d;
		
		int m;
		for (m=1; m<=MAXIT; ++m)
		{
			int m2 = 2 * m;
			double aa = m * (b-m) * x / ((qam+m2) * (a+m2));
			
			d = 1.0 + aa*d;
			if (Math.abs(d) < FPMIN)
				d = FPMIN;
			
			c = 1.0 + aa/c;
			if (Math.abs(c) < FPMIN)
				c = FPMIN;
			
			d = 1.0 / d;
			h *= d * c;
			aa = -(a+m) * (qab+m) * x / ((a+m2) * (qap+m2));
			
			d = 1.0 + aa * d;
			if (Math.abs(d) < FPMIN)
				d = FPMIN;
			
			c = 1.0 + aa/c;
			if (Math.abs(c) < FPMIN)
				c = FPMIN;
			
			d = 1.0 / d;
			double del = d * c;
			h *= del;
			if (Math.abs(del-1.0) < EPS)
				break;
		}
		
		if (m > MAXIT)
			return Double.NaN;
		return h;
	}
	
	
	// normality tests
	/**
	 * The Shapiro-Wilk test tests the null hypothesis that a sample x1, ..., xn came from
	 * a normally distributed population.
	 * 
	 * @param values			The distribution to test.
	 * @return					The result of the test, which is a double array of 2 elements: ({@link Statistical#SHAPIRO_WILK_PVALUE} and {@link Statistical#SHAPIRO_WILK_WSTAT}).
	 * @throws IllegalArgumentException
	 * 							Thrown when the length of the vector is less than 3 elements.
	 */
	public static double[] shapiroWilk(double[] values) throws IllegalArgumentException
	{
		if (values.length < 3)
			throw new IllegalArgumentException("The Shapiro-Wilk test needs at least 3 values in the distribution (20 is preferable)");
		
		// make a sorted copy of the values
		int length = values.length;
		double sorted[] = new double[length+1];	// need padding for the FORTRAN port
		for (int i=0; i<length; ++i)
			sorted[i+1] = values[i];
		Arrays.sort(sorted);
		
		// call the fortran port
		boolean init[] = new boolean[1];
		double a[] = new double[length + 1];
		double w[] = new double[1];
		double pw[] = new double[1];
		int ifault[] = new int[]{-1};
		SWilk.swilk(init, sorted, length, length, length/2, a, w, pw, ifault);
		
		// is there an error?
		if (ifault[0]!=0 && ifault[0]!=2)
			return new double[] { -1, -1 };
		return new double[]{ w[0], pw[0] };
	}
	
	/**
	 * Simplistic implementation of the Durbin-Watson statistic. This statistic is a test
	 * statistic used to detect the presence of autocorrelation in the residuals from a
	 * regression analysis. It can be used to check whether a signal is very noisy.
	 * <p />
	 * The residual e is calculated by subtracting the measured value from the mean
	 * of all the values.
	 * 
	 * @param values		The array with the values.
	 * @return				The Durbin-Watson statistic.
	 */
	public static double durbinWatson(double values[])
	{
		double mean = mean(values);
		
		double upper = 0;
		double lower = Math.pow(values[0]-mean, 2);
		for (int i=1; i<values.length; ++i)
		{
			upper += Math.pow((values[i]-mean)-(values[i-1]-mean), 2);
			lower += Math.pow((values[i]-mean), 2);
		}
		return upper / lower;
	}
	
	/**
	 * This implementation has been taken from 
	 * <a href="https://bosshog.lbl.gov/repos/colt/trunk/src/cern/jet/stat/Descriptive.java">http://www.lbl.gov/</a>
	 * 
	 * @param values
	 * @return
	 */
	public static double durbinWatsonCERN(double values[])
	{
		if (values.length < 2)
			throw new IllegalArgumentException("data sequence must contain at least two values.");

		double run = 0;
		double run_sq = 0;
		run_sq = values[0] * values[0];
		for(int i=1; i<values.length; ++i) {
			double x = values[i] - values[i-1];
			run += x*x;
			run_sq += values[i] * values[i];
		}
		return run / run_sq;
	}

	
	
	// static init
	static protected Random random = new Random();
	static {
		random.setSeed(System.nanoTime());
	}
	
	
	// helper functions
	private static final void pixSwap(double values[], int a, int b)
	{
		double tmp = values[a];
		values[a] = values[b];
		values[b] = tmp;
	}
	
	private static final void pixSort(double values[], int a, int b)
	{
		if (values[a] > values[b])
			pixSwap(values, a, b);
	}
	
//	private static double erfcc(double x)
//	{
//		double t,z,ans;
//
//		z = Math.abs(x);
//		t = 1.0 / (1.0+0.5*z);
//		ans = t*Math.exp(
//				-z*z-1.26551223+t*(1.00002368+t*(0.37409196+t*(0.09678418+t*(-0.18628806+t*(0.27886807+t*(-1.13520398+t*(1.48851587+t*(-0.82215223+t*0.17087277))))))))
//			);
//		return x >= 0.0 ? ans : 2.0-ans;
//	}
	
	private static double crank(double values[])
	{
		int j = 0;
		double s=0.;
		while (j < values.length-1)
		{
			if (values[j+1] != values[j])
			{
				values[j] = j;
				j++;
			}
			else
			{
				int jt=j+1;
				for ( ; jt<values.length && values[jt]==values[j]; ++jt);
				
				double rank = 0.5 * (j+jt-1);
				for (int ji=j; ji<=(jt-1); ++ji)
					values[ji] = rank;
				double t = jt - j;
				s += t*t*t-t;
				j = jt;
			}
		}
		
		if (j == values.length-1)
			values[values.length-1] = values.length-1;
		
		return s;
	}
	
	
	// bowels which need to be fixed up
    /**
     * Calculates the Shapiro-Wilk W test and its significance level.
     * 
     * Ported from original FORTRAN 77 code from the journal Applied Statistics published by the Royal Statistical Society
     * and distributed by Carnegie Mellon University at http://lib.stat.cmu.edu/apstat/.
     * 
     * To help facilitate debugging and maintenance, this port has been changed as little as feasible from the original
     * FORTRAN 77 code, to allow comparisons with the original. Variable names have been left alone when possible (except
     * for capitalizing constants), and the logic flow (though translated and indented) is essentially unchanged.
     * 
     * The original FORTRAN source for these routines has been released by the Royal Statistical Society for free public
     * distribution, and this Java implementation is released to the public domain.
     */
    private static class SWilk {
        /*
         * Constants and polynomial coefficients for swilk(). NOTE: FORTRAN counts the elements of the array x[length] as
         * x[1] through x[length], not x[0] through x[length-1]. To avoid making pervasive, subtle changes to the algorithm
         * (which would inevitably introduce pervasive, subtle bugs) the referenced arrays are padded with an unused 0th
         * element, and the algorithm is ported so as to continue accessing from [1] through [length].
         */
        private static final double C1[] = { Double.NaN, 0.0E0, 0.221157E0, -0.147981E0, -0.207119E1, 0.4434685E1,
                -0.2706056E1 };
        private static final double C2[] = { Double.NaN, 0.0E0, 0.42981E-1, -0.293762E0, -0.1752461E1, 0.5682633E1,
                -0.3582633E1 };
        private static final double C3[] = { Double.NaN, 0.5440E0, -0.39978E0, 0.25054E-1, -0.6714E-3 };
        private static final double C4[] = { Double.NaN, 0.13822E1, -0.77857E0, 0.62767E-1, -0.20322E-2 };
        private static final double C5[] = { Double.NaN, -0.15861E1, -0.31082E0, -0.83751E-1, 0.38915E-2 };
        private static final double C6[] = { Double.NaN, -0.4803E0, -0.82676E-1, 0.30302E-2 };
        private static final double C7[] = { Double.NaN, 0.164E0, 0.533E0 };
        private static final double C8[] = { Double.NaN, 0.1736E0, 0.315E0 };
        private static final double C9[] = { Double.NaN, 0.256E0, -0.635E-2 };
        private static final double G[] = { Double.NaN, -0.2273E1, 0.459E0 };
        private static final double Z90 = 0.12816E1, Z95 = 0.16449E1, Z99 = 0.23263E1;
        private static final double ZM = 0.17509E1, ZSS = 0.56268E0;
        private static final double BF1 = 0.8378E0, XX90 = 0.556E0, XX95 = 0.622E0;
        private static final double SQRTH = 0.70711E0, TH = 0.375E0, SMALL = 1E-19;
        private static final double PI6 = 0.1909859E1, STQR = 0.1047198E1;
        private static final boolean UPPER = true;

        /**
         * ALGORITHM AS R94 APPL. STATIST. (1995) VOL.44, NO.4
         * 
         * Calculates Shapiro-Wilk normality test and P-value for sample sizes 3 <= n <= 5000 . Handles censored or
         * uncensored data. Corrects AS 181, which was found to be inaccurate for n > 50.
         * 
         * NOTE: Semi-strange porting kludge alert. FORTRAN allows subroutine arguments to be modified by the called routine
         * (passed by reference, not value), and the original code for this routine makes use of that feature to return
         * multiple results. To avoid changing the code any more than necessary, I've used Java arrays to simulate this
         * pass-by-reference feature. Specifically, in the original code w, pw, and ifault are output results, not input
         * parameters. Pass in double[1] arrays for w and pw, and an int[1] array for ifault, and extract the computed
         * values from the [0] element on return. The argument init is both input and output; use a boolean[1] array and
         * initialize [0] to false before the first call. The routine will update the value to true to record that
         * initialization has been performed, to speed up subsequent calls on the same data set. Note that although the
         * contents of a[] will be computed by the routine on the first call, the caller must still allocate the array space
         * and pass the unfilled array in to the subroutine. The routine will set the contents but not allocate the space.
         * 
         * As described above with the constants, the data arrays x[] and a[] are referenced with a base element of 1 (like
         * FORTRAN) instead of 0 (like Java) to avoid screwing up the algorithm. To pass in 100 data points, declare x[101]
         * and fill elements x[1] through x[100] with data. x[0] will be ignored.
         * 
         * You might want to eliminate the ifault parameter completely, and throw Java exceptions instead. I didn't want to
         * change the code that much.
         * 
         * @param init
         *            Input & output; pass in boolean[1], initialize to false before first call, routine will set to true
         * @param x
         *            Input; Data set to analyze; 100 points go in x[101] array from x[1] through x[100]
         * @param n
         *            Input; Number of data points in x
         * @param n1
         *            Input; dunno
         * @param n2
         *            Input; dunno either
         * @param a
         *            Output when init[0] == false, Input when init[0] == true; holds computed test coefficients
         * @param w
         *            Output; pass in double[1], will contain result in w[0] on return
         * @param pw
         *            Output; pass in double[1], will contain result in pw[0] on return
         * @param ifault
         *            Output; pass in int[1], will contain error code (0 == good) in ifault[0] on return
         */
        private static void swilk(boolean[] init, double[] x, int n, int n1, int n2, double[] a, double[] w, double[] pw,
                int[] ifault) {

            pw[0] = 1.0;
            if (w[0] >= 0.0)
                w[0] = 1.0;
            double an = n;
            ifault[0] = 3;
            int nn2 = n / 2;
            if (n2 < nn2)
                return;
            ifault[0] = 1;
            if (n < 3)
                return;

            // If INIT is false, calculates coefficients for the test

            if (!init[0]) {
                if (n == 3) {
                    a[1] = SQRTH;
                } else {
                    double an25 = an + 0.25;
                    double summ2 = 0.0;
                    for (int i = 1; i <= n2; ++i) {
                        a[i] = ppnd((i - TH) / an25);
                        summ2 += a[i] * a[i];
                    }
                    summ2 *= 2.0;
                    double ssumm2 = Math.sqrt(summ2);
                    double rsn = 1.0 / Math.sqrt(an);
                    double a1 = poly(C1, 6, rsn) - a[1] / ssumm2;

                    // Normalize coefficients

                    int i1;
                    double fac;
                    if (n > 5) {
                        i1 = 3;
                        double a2 = -a[2] / ssumm2 + poly(C2, 6, rsn);
                        fac = Math.sqrt((summ2 - 2.0 * a[1] * a[1] - 2.0 * a[2] * a[2])
                                / (1.0 - 2.0 * a1 * a1 - 2.0 * a2 * a2));
                        a[1] = a1;
                        a[2] = a2;
                    } else {
                        i1 = 2;
                        fac = Math.sqrt((summ2 - 2.0 * a[1] * a[1]) / (1.0 - 2.0 * a1 * a1));
                        a[1] = a1;
                    }
                    for (int i = i1; i <= nn2; ++i) {
                        a[i] = -a[i] / fac;
                    }
                }
                init[0] = true;
            }
            if (n1 < 3)
                return;
            int ncens = n - n1;
            ifault[0] = 4;
            if (ncens < 0 || (ncens > 0 && n < 20))
                return;
            ifault[0] = 5;
            double delta = ncens / an;
            if (delta > 0.8)
                return;

            // If W input as negative, calculate significance level of -W

            double w1, xx;
            if (w[0] < 0.0) {
                w1 = 1.0 + w[0];
                ifault[0] = 0;
            } else {

                // Check for zero range

                ifault[0] = 6;
                double range = x[n1] - x[1];
                if (range < SMALL)
                    return;

                // Check for correct sort order on range - scaled X

                ifault[0] = 7;
                xx = x[1] / range;
                double sx = xx;
                double sa = -a[1];
                int j = n - 1;
                for (int i = 2; i <= n1; ++i) {
                    double xi = x[i] / range;
                    // IF (XX-XI .GT. SMALL) PRINT *,' ANYTHING'
                    sx += xi;
                    if (i != j)
                        sa += sign(1, i - j) * a[Math.min(i, j)];
                    xx = xi;
                    --j;
                }
                ifault[0] = 0;
                if (n > 5000)
                    ifault[0] = 2;

                // Calculate W statistic as squared correlation between data and coefficients

                sa /= n1;
                sx /= n1;
                double ssa = 0.0;
                double ssx = 0.0;
                double sax = 0.0;
                j = n;
                double asa;
                for (int i = 1; i <= n1; ++i) {
                    if (i != j)
                        asa = sign(1, i - j) * a[Math.min(i, j)] - sa;
                    else
                        asa = -sa;
                    double xsx = x[i] / range - sx;
                    ssa += asa * asa;
                    ssx += xsx * xsx;
                    sax += asa * xsx;
                    --j;
                }

                // W1 equals (1-W) calculated to avoid excessive rounding error
                // for W very near 1 (a potential problem in very large samples)

                double ssassx = Math.sqrt(ssa * ssx);
                w1 = (ssassx - sax) * (ssassx + sax) / (ssa * ssx);
            }
            w[0] = 1.0 - w1;

            // Calculate significance level for W (exact for N=3)

            if (n == 3) {
                pw[0] = PI6 * (Math.asin(Math.sqrt(w[0])) - STQR);
                return;
            }
            double y = Math.log(w1);
            xx = Math.log(an);
            double m = 0.0;
            double s = 1.0;
            if (n <= 11) {
                double gamma = poly(G, 2, an);
                if (y >= gamma) {
                    pw[0] = SMALL;
                    return;
                }
                y = -Math.log(gamma - y);
                m = poly(C3, 4, an);
                s = Math.exp(poly(C4, 4, an));
            } else {
                m = poly(C5, 4, xx);
                s = Math.exp(poly(C6, 3, xx));
            }
            if (ncens > 0) {

                // Censoring by proportion NCENS/N. Calculate mean and sd of normal equivalent deviate of W.

                double ld = -Math.log(delta);
                double bf = 1.0 + xx * BF1;
                double z90f = Z90 + bf * Math.pow(poly(C7, 2, Math.pow(XX90, xx)), ld);
                double z95f = Z95 + bf * Math.pow(poly(C8, 2, Math.pow(XX95, xx)), ld);
                double z99f = Z99 + bf * Math.pow(poly(C9, 2, xx), ld);

                // Regress Z90F,...,Z99F on normal deviates Z90,...,Z99 to get
                // pseudo-mean and pseudo-sd of z as the slope and intercept

                double zfm = (z90f + z95f + z99f) / 3.0;
                double zsd = (Z90 * (z90f - zfm) + Z95 * (z95f - zfm) + Z99 * (z99f - zfm)) / ZSS;
                double zbar = zfm - zsd * ZM;
                m += zbar * s;
                s *= zsd;
            }
            pw[0] = alnorm((y - m) / s, UPPER);
        }

        /**
         * Constructs an int with the absolute value of x and the sign of y
         * 
         * @param x
         *            int to copy absolute value from
         * @param y
         *            int to copy sign from
         * @return int with absolute value of x and sign of y
         */
        private static int sign(int x, int y) {
            int result = Math.abs(x);
            if (y < 0.0)
                result = -result;
            return result;
        }

        // Constants & polynomial coefficients for ppnd(), slightly renamed to avoid conflicts. Could define
        // them inside ppnd(), but static constants are more efficient.

        // Coefficients for P close to 0.5
        private static final double A0_p = 3.3871327179E+00, A1_p = 5.0434271938E+01, A2_p = 1.5929113202E+02,
                A3_p = 5.9109374720E+01, B1_p = 1.7895169469E+01, B2_p = 7.8757757664E+01, B3_p = 6.7187563600E+01;

        // Coefficients for P not close to 0, 0.5 or 1 (names changed to avoid conflict with swilk())
        private static final double C0_p = 1.4234372777E+00, C1_p = 2.7568153900E+00, C2_p = 1.3067284816E+00,
                C3_p = 1.7023821103E-01, D1_p = 7.3700164250E-01, D2_p = 1.2021132975E-01;

        // Coefficients for P near 0 or 1.
        private static final double E0_p = 6.6579051150E+00, E1_p = 3.0812263860E+00, E2_p = 4.2868294337E-01,
                E3_p = 1.7337203997E-02, F1_p = 2.4197894225E-01, F2_p = 1.2258202635E-02;

        private static final double SPLIT1 = 0.425, SPLIT2 = 5.0, CONST1 = 0.180625, CONST2 = 1.6;

        /**
         * ALGORITHM AS 241 APPL. STATIST. (1988) VOL. 37, NO. 3, 477-484.
         * 
         * Produces the normal deviate Z corresponding to a given lower tail area of P; Z is accurate to about 1 part in
         * 10**7.
         * 
         * @param p
         * @return
         */
        private static double ppnd(double p) {
            double q = p - 0.5;
            double r;
            if (Math.abs(q) <= SPLIT1) {
                r = CONST1 - q * q;
                return q * (((A3_p * r + A2_p) * r + A1_p) * r + A0_p) / (((B3_p * r + B2_p) * r + B1_p) * r + 1.0);
            } else {
                if (q < 0.0)
                    r = p;
                else
                    r = 1.0 - p;
                if (r <= 0.0)
                    return 0.0;
                r = Math.sqrt(-Math.log(r));
                double normal_dev;
                if (r <= SPLIT2) {
                    r -= CONST2;
                    normal_dev = (((C3_p * r + C2_p) * r + C1_p) * r + C0_p) / ((D2_p * r + D1_p) * r + 1.0);
                } else {
                    r -= SPLIT2;
                    normal_dev = (((E3_p * r + E2_p) * r + E1_p) * r + E0_p) / ((F2_p * r + F1_p) * r + 1.0);
                }
                if (q < 0.0)
                    normal_dev = -normal_dev;
                return normal_dev;
            }
        }

        /**
         * Algorithm AS 181.2 Appl. Statist. (1982) Vol. 31, No. 2
         * 
         * Calculates the algebraic polynomial of order nord-1 with array of coefficients c. Zero order coefficient is c[1]
         * 
         * @param c
         * @param nord
         * @param x
         * @return
         */
        private static double poly(double[] c, int nord, double x) {
            double poly = c[1];
            if (nord == 1)
                return poly;
            double p = x * c[nord];
            if (nord != 2) {
                int n2 = nord - 2;
                int j = n2 + 1;
                for (int i = 1; i <= n2; ++i) {
                    p = (p + c[j]) * x;
                    --j;
                }
            }
            poly += p;
            return poly;
        }

        // Constants & polynomial coefficients for alnorm(), slightly renamed to avoid conflicts.
        private static final double CON_a = 1.28, LTONE_a = 7.0, UTZERO_a = 18.66;
        private static final double P_a = 0.398942280444, Q_a = 0.39990348504, R_a = 0.398942280385, A1_a = 5.75885480458,
                A2_a = 2.62433121679, A3_a = 5.92885724438, B1_a = -29.8213557807, B2_a = 48.6959930692, C1_a = -3.8052E-8,
                C2_a = 3.98064794E-4, C3_a = -0.151679116635, C4_a = 4.8385912808, C5_a = 0.742380924027,
                C6_a = 3.99019417011, D1_a = 1.00000615302, D2_a = 1.98615381364, D3_a = 5.29330324926,
                D4_a = -15.1508972451, D5_a = 30.789933034;

        /**
         * Algorithm AS66 Applied Statistics (1973) vol.22, no.3
         * 
         * Evaluates the tail area of the standardised normal curve from x to infinity if upper is true or from minus
         * infinity to x if upper is false.
         * 
         * @param x
         * @param upper
         * @return
         */
        private static double alnorm(double x, boolean upper) {
            boolean up = upper;
            double z = x;
            if (z < 0.0) {
                up = !up;
                z = -z;
            }
            double fn_val;
            if (z > LTONE_a && (!up || z > UTZERO_a))
                fn_val = 0.0;
            else {
                double y = 0.5 * z * z;
                if (z <= CON_a)
                    fn_val = 0.5 - z * (P_a - Q_a * y / (y + A1_a + B1_a / (y + A2_a + B2_a / (y + A3_a))));
                else
                    fn_val = R_a * Math.exp(-y) / (z + C1_a + D1_a / (z + C2_a + D2_a
                        / (z + C3_a + D3_a / (z + C4_a + D4_a / (z + C5_a + D5_a / (z + C6_a))))));
            }
            if (!up)
                fn_val = 1.0 - fn_val;
            return fn_val;
        }
    }
}
