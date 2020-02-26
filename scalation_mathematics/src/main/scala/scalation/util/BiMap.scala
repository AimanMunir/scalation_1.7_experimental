
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  John Miller
 *  @version 1.7
 *  @date    Fri Aug 24 15:46:03 EDT 2018
 *  @see     LICENSE (MIT style license file).
 */

package scalation.util

import scala.collection.mutable.HashMap

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `BiMap` class maps keys to values and values to keys.
 *  @tparam K  the key type
 *  @tparam V  the value type
 */
class BiMap [K, V] extends HashMap [K, V]
{
    /** The back map from values to keys
     */
    private val back = HashMap [V, K] ()

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Put the (key, value) to the map.
     *  @param kv  the key-value pair
     */
    def put (kv: (K, V)): BiMap.this.type =
    {
        super.+= (kv)
        back  += kv._2 -> kv._1
        this
    } // put

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Given the a value 'v', return the most recent key it was entered with.
     *  If values are unqiue, keys and values will be one-to-one correspondence.
     *  Note, if all keys are needed should use a `MultiMap`.
     *  @param v  the value whose key is sought
     */
    def from (v: V) = back (v)

} // BiMap class


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `BiMapTest` class is used the test the `BiMap` class.
 * runMain scalation.util.BiMapTest
 */
object BiMapTest extends App
{
    val m = new BiMap [String, Int] ()
    m put "Jan" ->  1
    m put "Feb" ->  2
    m put "Mar" ->  3
    m put "Apr" ->  4
    m put "May" ->  5
    m put "Jun" ->  6
    m put "Jul" ->  7
    m put "Aug" ->  8
    m put "Sep" ->  9
    m put "Oct" -> 10
    m put "Nov" -> 11
    m put "Dec" -> 12

    for (i <- 1 to 12) println (s"month $i is ${m.from (i)}")

} // BiMapTest object

