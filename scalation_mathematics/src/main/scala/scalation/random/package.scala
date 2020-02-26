
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.6
 *  @date    Fri Jul 24 14:35:58 EDT 2015
 *  @see     LICENSE (MIT style license file).
 */

package scalation

/** The `random` package contains classes, traits and objects for
 *  the generation of random numbers.
 */
package object random
{
    /** Type definition for parameters to a distribution. `Vector` is used instead
     *  of `Array` since they are covariant, while Scala arrays are not.
     */
    type Parameters = Vector [Double]

    /** The function type for distribution functions, including
     *  (1) Cumulative Distribution Function (CDF)
     *  (2) inverse Cumulative Distribution Function (iCDF)
     *  The arguments are `Double` for coordinate 'x' or probability 'p' and a
     *  `Vector` of parameters, e.g., degrees of freedom.
     */
    type Distribution = (Double, Parameters) => Double

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Cumulate the values of 'this' array from left to right (e.g., create a
     *  CDF from a pmf).  Example: (4, 2, 3, 1) --> (4, 6, 9, 10)
     */
    def cumulate (x: Array [Double]): Array [Double] =
    {
        val c = Array.ofDim [Double] (x.length)
        var s = 0.0
        for (i <- x.indices) { s += x(i); c(i) = s }
        c
    } // cumulate

} // random package object 

