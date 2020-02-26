
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.7
 *  @date    Sun Feb 23 15:55:52 EST 2020
 *  @see     LICENSE (MIT style license file).
 */

package scalation.math

import scalation.math.StrO.StrNum
import scalation.math.TimeO.TimeNum

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `PrimitiveType` object provides comparison operators for `Vec` elements.
 *  @see `Vec_Elem`
 */
object PrimitiveType
{
    /** Union type defintion for primitive types
     */
//  type PrimType = Complex | Double | Int | Long | Rational | Real | StrNum | TimeNum

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Compare the two elements and return if the first element is smaller than the second.
     *  @param x  first element to compare
     *  @param y  second element to compare
     */
    def < (x: Any, y: Any): Boolean =
    {
        y match {
        case _: Complex  => x.asInstanceOf [Complex]  < y.asInstanceOf [Complex]
        case _: Double   => x.asInstanceOf [Double]   < y.asInstanceOf [Double]
        case _: Int      => x.asInstanceOf [Int]      < y.asInstanceOf [Int]
        case _: Long     => x.asInstanceOf [Long]     < y.asInstanceOf [Long]
        case _: Rational => x.asInstanceOf [Rational] < y.asInstanceOf [Rational]
        case _: Real     => x.asInstanceOf [Real]     < y.asInstanceOf [Real]
        case _: StrNum   => x.asInstanceOf [StrNum]   < y.asInstanceOf [StrNum]
        case _: TimeNum  => x.asInstanceOf [TimeNum]  < y.asInstanceOf [TimeNum]
        case _  => println (s"<: scalar type ${y.getClass} is not supported"); false
        } // match
    } // <

} // PrimitiveType object

