
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.6
 *  @date    Tue Feb 18 12:13:23 EST 2020
 *  @see     LICENSE (MIT style license file).
 */

package scalation

import scalation.math.{Complex, Rational, Real, StrO, TimeO}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `PrimitiveType` object maintains the primitive type supported by the ScalaTion
 *  database module.
 */
object PrimitiveType
{
   /** The zero object for all primitive types supported in ScalaTion database module
    */
   val zeros = Vector (Complex._0, 0.0, 0, 0l, Rational._0, Real._0, StrO._0, TimeO._0)

   /** The classes for all primitive types supported in ScalaTion database module
    */
   val types = zeros.map (_.getClass)

   /** The class names for all primitive types supported in ScalaTion database module
    */
   val tnames = types.map (_.getName)

} // PrimitiveType object 


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `PrimitiveTypeTest` prints the zero objects and types for all primitive types.
 *  > runMain scalation.PrimitiveTypeTest
 */
object PrimitiveTypeTest extends App
{
   println (s"PrimitiveType.zeros  = ${PrimitiveType.zeros}")
   println (s"PrimitiveType.types  = ${PrimitiveType.types}")
   println (s"PrimitiveType.tnames = ${PrimitiveType.tnames}")

} // PrimitiveTypeTest

