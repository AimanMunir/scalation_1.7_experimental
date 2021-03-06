//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** @author  Santosh Uttam Bobade, John Miller
 *  @version 1.7
 *  @date    Wed May 27 14:36:12 EDT 2015
 *  @see     LICENSE (MIT style license file).
 */

package scalation.math

import java.time.{DateTimeException, Instant, ZonedDateTime, ZoneId, ZoneOffset}
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.time.temporal.{ChronoField, UnsupportedTemporalTypeException}

import scala.language.implicitConversions
import scala.math.{abs => ABS, sqrt}
import scala.runtime.ScalaRunTime.stringOf

import scalation.math.TimeO.TimeNum

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `TimeO` object is used to represent and operate on date-time values.
 *  It contains an implicit class definition for `TimeNum`, which extends
 *  `Instant` with `Numeric` operations that can be used in `VectorT`.
 *  The `java.time.Instant` class stores nano-seconds since the UNIX Epoch
 *  using a `long` for seconds and `int` for nano-seconds (12 bytes or 96 bits).
 *  The `java.time.ZonedDateTime` class is used with it to handle various
 *  date-time formats.
 */
object TimeO
{
    val DEBUG = false

    /** Define the default time zone to be Coordinated Universal Time (UTC).
     *  UTC is the successor to Greenwich Mean Time (GMT) that was developed
     *  at Royal Observator in Greenwich, UK (0° longitude).
     *  UTC does not observe Daylight Savings Time and serves as universal
     *  time across regions/time zones.
     *  @see en.wikipedia.org/wiki/Coordinated_Universal_Time
     */
    val DEFAULT_TZ = "UTC"

    /** Default date-time format
     *  month, day, year, hour (24), minute, second, nano-seconds zone
     */
    val DEFAULT_DATETIME_FORMAT = "MM/d/yyyy HH:mm:ss:SSSSSSSSS z"

    /** TimeNum (0) as a date-time corresponding to 01-01-1970 00:00:00 UTC,
     *  the UNIX Epoch
     */
    val _0 = TimeNum (0L)

    /** TimeNum (1) as a date-time corresponding to 01-02-1970 00:00:00 UTC
     *  FIX - why one day
     */
    val _1 = TimeNum (86400L)                       // 1 day = 86400 seconds

    /** Ordering for date-time values
     */
    implicit val ord = new Ordering [TimeNum]
               { def compare (s: TimeNum, t: TimeNum) = s compare t }

    /** Implicit numeric value for establishing type
     */
    implicit val num = _0

    /** Default element separator (e.g., in a CSV file)
     */
    private val SP = ","

    /** Nano-seconds must be strictly than this limit (billion nanoseconds = 1 second)
     */
    private val nanoLimit = 1000000000

    /** The threshold is use for determining whether two `TimeNum` object are
     *  approximately equal (application dependent).  The initial value is one second,
     *  for one minute set it to 60.0, or for one microsecond set it to 1E-6.
     */
    private var threshold = 1.0

    /** The seconds portion of the threshold
     */
    private var threshold_s = threshold.toLong

    /** The nano-seconds portion of the threshold
     */
    private var threshold_n = ((threshold - threshold_s) * nanoLimit).toInt

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Set the threshold to value more suitable to the current application.
     *  @param thres  the new value for the threshold
     */
    def setThreshold (thres: Double)
    {
        threshold   = thres
        threshold_s = threshold.toLong
        threshold_n = ((threshold - threshold_s) * nanoLimit).toInt
    } // setThreshold

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the absolute value of that date-time number.
     *  @param t  that date-time number
     */
    def abs (t: TimeNum): TimeNum =  TimeNum (ABS (t.inst.getEpochSecond), ABS (t.inst.getNano))

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the maximum of two date-time number, 's' and 't'.
     *  @param s  the first date-time number to compare
     *  @param t  the second date-time number to compare
     */
    def max (s: TimeNum, t: TimeNum): TimeNum = if (t.toLong > s.toLong) t else s

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the minimum of two date-time numbers, 's' and 't'.
     *  @param s  the first date-time number to compare
     *  @param t  the second date-time number to compare
     */
    def min (s: TimeNum, t: TimeNum): TimeNum = if (t.toLong < s.toLong) t else s

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the square root of a `TimeNum`.
     *  @param t  the date-time to obtain the square root of
     */
    def sqrt (t: TimeNum): TimeNum = TimeNum (sqrt (t.inst.getEpochSecond).toLong, sqrt (t.inst.getNano).toInt)

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Return the signum (sgn) of a `TimeNum`.
     *  @param t  the date-time to obtain the signum of
     */
    def signum (t: TimeNum): Int =
    {
        val res = t compare _0
        if (res < 0) -1 else if (res > 0) 1 else 0
    } // signum

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** Implicit conversion from 'Double' to 'TimeNum'.
     *  @param d  the Double parameter to convert
     */
    implicit def double2TimeNum (d: Double): TimeNum = TimeNum (d.toLong)

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** The `TimeNum` implicit class is used to represent and operate on date-time
     *  numbers.  Internally, a date-time number is represented as `Instant`.
     *---------------------------------------------------------------------
     *  @param inst  the underlying `Instant` of time
     */
    implicit class TimeNum (val inst: Instant)
             extends Numeric [TimeNum] with Ordered [TimeNum] with Serializable
    {
        /** General alias for the parts of a time number
         */
        lazy val (val1, val2) = (inst.getEpochSecond, inst.getNano)

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Construct a `TimeNum` object from the given date-time by taking a `ZonedDateTime`
         *  and converting it to the corresponding `Instant`, the underlying type of `TimeNum` class.
         *  @param dt  the given date-time
         */
        def this (dt: ZonedDateTime) { this (dt.toInstant ()) }
      
        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Construct a `TimeNum` object from the given date-time by creating a `ZonedDateTime`
         *  from the string  and converting it to the corresponding `Instant`, the underlying
         *  type of `TimeNum` class.  Format it using 'dtPattern'.
         *  @param dt         the given date-time as a string
         *  @param dtPattern  the given date-time pattern/format
         */
        def this (dt: String, dtPattern: String = DEFAULT_DATETIME_FORMAT)
        {
            this (ZonedDateTime.parse (dt, DateTimeFormatter.ofPattern (dtPattern)).toInstant ())
        } // this

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Compute the unary minus (-).
         */
        def unary_- (): TimeNum = negate (this)

        def negate (s: TimeNum): TimeNum = TimeNum (-inst.getEpochSecond, -inst.getNano)

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Add 'this' `TimeNum` and `TimeNum` 't'.  The seconds and nanoseconds are
         *  added separately, with carry from nanoseconds handled.
         *  @param t  the TimeNum to add to this
         */
        def + (t: TimeNum): TimeNum = plus (this, t)

        def plus (s: TimeNum, t: TimeNum): TimeNum =
        {
            val (ss, sn) = (s.inst.getEpochSecond, s.inst.getNano)
            val (ts, tn) = (t.inst.getEpochSecond, t.inst.getNano)
            var (rs, rn) = (ss + ts, sn + tn)
            if (rn >= nanoLimit) { rs += 1; rn - nanoLimit }
            TimeNum (rs, rn)
        } // plus

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Subtract TimeNum` 't' from 'this' `TimeNum`.
         *  @param t  the TimeNum to subtract from this
         */
        def - (t: TimeNum): TimeNum = minus (this, t)

        def minus (s: TimeNum, t: TimeNum): TimeNum =
        {
            val (ss, sn) = (s.inst.getEpochSecond, s.inst.getNano)
            val (ts, tn) = (t.inst.getEpochSecond, t.inst.getNano)
            var (rs, rn) = (ss - ts, sn - tn)
            if (rn < 0L) { rs -= 1; rn + nanoLimit }
            TimeNum (rs, rn)
        } // minus

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Multiply 'this' `TimeNum` and `TimeNum` 't'.
         *  @param t  the TimeNum that multiplies this
         */
        def * (t: TimeNum): TimeNum = times (this, t)

        def times (s: TimeNum, t: TimeNum): TimeNum =
        {
            throw new UnsupportedOperationException ("TimeNum does not support times operation")
        } // times

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Divide 'this' `TimeNum` by `TimeNum` 't'.
         *  @param t  the TimeNum that divides this
         */
        def / (t: TimeNum): TimeNum = divide (this, t)

        def divide (s: TimeNum, t: TimeNum): TimeNum =
        {
            throw new UnsupportedOperationException ("TimeNum does not support divide operation")
        } // divide

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Raise 'this' `TimeNum` to power `TimeNum` 't'.
         *  @param t  the TimeNum that raises this
         */
        def ~^ (t: TimeNum): TimeNum = 
        {
            throw new UnsupportedOperationException ("TimeNum does not support ~^ operation")
        } // ~^

        def ↑ (t: TimeNum): TimeNum =
        {
            throw new UnsupportedOperationException ("TimeNum does not support ↑ operation")
        } // ↑

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Return whether two `TimeNum` numbers are nearly equal.
         *  @param t  the TimeNum to compare with this
         */
        def =~ (t: TimeNum): Boolean =
        {
            val t1 = TimeNum (inst.getEpochSecond - threshold_s, inst.getNano - threshold_n)
            val t2 = TimeNum (inst.getEpochSecond + threshold_s, inst.getNano + threshold_n)
            t1 <= t && t <= t2
        } // =~

        def ≈ (t: TimeNum): Boolean =
        {
            val t1 = TimeNum (inst.getEpochSecond - threshold_s, inst.getNano - threshold_n)
            val t2 = TimeNum (inst.getEpochSecond + threshold_s, inst.getNano + threshold_n)
            t1 <= t && t <= t2
        } // ≈

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Return whether two `TimeNum` numbers not are nearly equal.
         *  @param t  compare this with t
         */
        def !=~ (t: TimeNum): Boolean = ! (this =~ t)

        def ≉ (t: TimeNum): Boolean = ! (this =~ t)

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Return the maximum of 'this' and 't' `TimeNum` numbers.
         *  @param t  the TimeNum number to compare with this
         */
        def max (t: TimeNum): TimeNum = if (inst >= t.inst) this else t

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Return the minimum of 'this' and 't' `TimeNum` numbers.
         *  @param t  the TimeNum number to compare with this
         */
        def min (t: TimeNum): TimeNum = if (inst <= t.inst) this else t

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Compare two `TimeNum` numbers (negative for <, zero for ==, positive for >).
         *  @param t  the TimeNum number to compare with this
         */
        def compare (t: TimeNum): Int = inst compareTo t.inst

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Compare two 'TimeNum` numbers (negative for <, zero for ==, positive for >).
         *  @param s  the first TimeNum number
         *  @param t  the second TimeNum number
         */
        def compare (s: TimeNum, t: TimeNum): Int = s.inst compareTo t.inst

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Compare 'this' `TimeNum` number with that date-time number 't' for inequality.
         *  @param t  that TimeNum number
         */
        def ≠ (t: TimeNum): Boolean = this != t

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Compare 'this' `TimeNum` number with that date-time number 't' for less than
         *  or equal to.
         *  @param t  that TimeNum number
         */
        def ≤ (t: TimeNum): Boolean = this <= t

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Compare 'this' `TimeNum` number with that date-time number 't' for greater
         *  than or equal to.
         *  @param t  that TimeNum number
         */
        def ≥ (t: TimeNum): Boolean = this >= t

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Determine whether 'this' is within the given bounds
         *  @param lim  the given (lower, upper) bounds
         */
        def in (lim: (TimeNum, TimeNum)): Boolean = lim._1 <= this && this <= lim._2
        def ∈ (lim: (TimeNum, TimeNum)): Boolean  = lim._1 <= this && this <= lim._2

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Determine whether 'this' is in the given set.
         *  @param lim  the given set of values
         */
        def in (set: Set [TimeNum]): Boolean = set contains this
        def ∈ (set: Set [TimeNum]): Boolean  = set contains this

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Determine whether 'this' is not within the given bounds
         *  @param lim  the given (lower, upper) bounds
         */
        def not_in (lim: (TimeNum, TimeNum)): Boolean = ! (lim._1 <= this && this <= lim._2)
        def ∉ (lim: (TimeNum, TimeNum)): Boolean      = ! (lim._1 <= this && this <= lim._2)

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Determine whether 'this' is not in the given set.
         *  @param lim  the given set of values
         */
        def not_in (set: Set [TimeNum]): Boolean = ! (set contains this)
        def ∉ (set: Set [TimeNum]): Boolean      = ! (set contains this)

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Create a `TimeNum` number from a `Double`, where the seconds are
         *  extracted from the whole number part and the nanoseconds from the
         *  fractional part.
         *  @param d  the source double
         */
        def fromDouble (d: Double): TimeNum =
        {
            val ts = d.toLong
            val tn = ((d - ts) * nanoLimit).toInt
            TimeNum (ts, tn)
        } // fromDouble

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Create a `TimeNum` number from an `Int`.
         *  @param n  the source integer
         */
        def fromInt (n: Int): TimeNum =  TimeNum (n.toLong)

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Get a temporal aspect (e.g., month, week, day, hour, ...)
         *  @see docs.oracle.com/javase/8/docs/api/java/time/temporal/ChronoField.html
         *  Currently only four (INSTANT_SECONDS, MICRO_OF_SECOND, MILLI_OF_SECOND, NANO_OF_SECOND)
         *  of the enumeration values are implemented, but ScalaTion adds five more (see below).
         *  @param chrono  the enumeration type specifying the aspect to return
         */
        def getChrono (chrono: ChronoField, zone: ZoneId = ZoneOffset.UTC): Long =
        {
            try inst.getLong (chrono)                               
            catch {
                case ex: UnsupportedTemporalTypeException =>
                    chrono match {
                    case ChronoField.DAY_OF_MONTH
                        => ZonedDateTime.ofInstant (inst, zone).getDayOfMonth ()
                    case ChronoField.DAY_OF_WEEK
                        => ZonedDateTime.ofInstant (inst, zone).getDayOfWeek.getValue ()
                    case ChronoField.HOUR_OF_DAY
                        => ZonedDateTime.ofInstant (inst, zone).getHour ()
                    case ChronoField.MONTH_OF_YEAR
                        => ZonedDateTime.ofInstant (inst, zone).getMonthValue ()
                    case ChronoField.YEAR
                        => ZonedDateTime.ofInstant (inst, zone).getYear ()
                    case ChronoField.AMPM_OF_DAY
                        => ZonedDateTime.ofInstant (inst, zone).getHour () / 12
                    case _
                        => throw ex
                    } // match
            } // try
        } // getChrono

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Convert 'this' date-time number to a `TimeNum`.
         */
        def toTimeNum: TimeNum = this

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Convert the given `TimeNum` number to a `Double`.
         *  @param t  that date-time number to convert
         */
        def toDouble (t: TimeNum): Double = t.inst.getEpochSecond + t.inst.getNano.toDouble / nanoLimit
        def toDouble: Double = toDouble (this)

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Convert 'this' `TimeNum` number to a `Float`.
         *  @param t  that date-time number to convert
         */
        def toFloat (t: TimeNum): Float = t.toDouble.toFloat
        def toFloat: Float = toFloat (this)

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Convert 'this' `TimeNum` number to an `Int`.
         *  @param t  that date-time number to convert
         */
        def toInt (t: TimeNum): Int = t.toLong.toInt
        def toInt: Int = toInt (this)

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Convert 'this' `TimeNum` number to a `Long`.
         *  @param t  that date-time number to convert
         */
        def toLong (t: TimeNum): Long = t.inst.getEpochSecond
        def toLong: Long = toLong (this)

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Override equals to determine whether 'this' date-time number equals
         *  date-time 't'.
         *  @param t  the date-time number to compare with this
         */
        override def equals (t: Any): Boolean =
        {
             t match {
             case _ : Instant => inst equals t
             case _ : TimeNum => inst equals t.asInstanceOf [TimeNum].inst
             case _           => false
             } // match
        } // equals

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Must also override hashCode to be compatible with equals.
         */
        override def hashCode: Int = ABS (inst.hashCode)

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Parse the string to create a time number.
         */
        def parseString (str: String): Option [TimeNum] = Some (TimeNum (str))

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Convert 'this' date-time number to a String.
         */
        override def toString: String =
        {
            DateTimeFormatter.ofPattern (DEFAULT_DATETIME_FORMAT).withZone (ZoneId.of (DEFAULT_TZ)).format (inst)
//          DateTimeFormatter.ofPattern (DEFAULT_DATETIME_FORMAT).withZone (ZoneId.systemDefault ()).format (inst)
        } // toString

        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Convert 'this' date time number to a String in the format specified by
         *  the user, use 'DEFAULT_DATETIME_FORMAT' if user does not specify any format.
         *  @param format  the format to be used 
         */
        def toString2 (format: String = DEFAULT_DATETIME_FORMAT): String =
        {
            DateTimeFormatter.ofPattern (format).withZone (ZoneId.of (DEFAULT_TZ)).format (inst)
//          DateTimeFormatter.ofPattern (format).withZone (ZoneId.systemDefault ()).format (inst)
        } // toString2

    } // TimeNum class


    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /** The `TimeNum` companion object for the TimeNum class.
     */
    object TimeNum
    {
        private val DEFAULT_HOUR = (" 00",  " HH")
        private val DEFAULT_ZONE = (" UTC", " z")

        private val formats_12h = Array ("M/d/y h:m a z",
                                         "MMM/d/y h:m a z",
                                         "MM/dd/yyyy h:m a z",
                                         "MM/dd/yyyy h:m:s:SSSSSSSSS a z",
                                         "MM/dd/yyyy hh:mm:ss a Z",
                                         "yyyy/M/d h:m:s Z)",
                                         "yyyy/M/d H:m:s z")
        private val formats_24h = Array (DEFAULT_DATETIME_FORMAT,
                                         "MM/dd/yyyy H:m:s",
                                         "M/d/y H:m z",
                                         "MMM/d/y H:m z",
                                         "MM/dd/yyyy H:m z",
                                         "MM/dd/yyyy H:m:s:SSSSSSSSS z",
                                         "yyyy/M/d H:m:s Z",
                                         "yyyy/M/d H:m:s z")

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Create a TimeNum object from Number of seconds that have elapsed
         *  since January 1, 1970 UTC.
         *  @param sec  the number of seconds
         *  @param ns   the number of nano-seconds
         */
        def apply (sec: Long, ns: Int = 0): TimeNum =  Instant.ofEpochSecond (sec, ns)

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Create a `TimeNum` object by adding default time values.
         *  @param dt         the date-time in string format
         *  @param dtPattern  the pattern/format for the date-time
         */
        def apply (dt: String, dtPattern: String): TimeNum =
        {
            var (dts, dtp) = (dt, dtPattern)
            if (! dtPattern.contains ("h") && ! dtPattern.contains ("H")) {
                dts += DEFAULT_HOUR._1; dtp +=  DEFAULT_HOUR._2
            } // if
            if (! dtPattern.contains ("z") && ! dtPattern.contains ("Z")) {
                dts += DEFAULT_ZONE._1; dtp +=  DEFAULT_ZONE._2
            } // if
            if (DEBUG) println(s"dts = $dts, dtp = $dtp")
            new TimeNum (dts, dtp)
        } // apply

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /** Create a `TimeNum` object by adding some default time values, this version doesnt accept
         *  date pattern from user, it makes best guess
         *  @param dt         the date-time in string format
         */
        def apply (dt_ : String): TimeNum =
        {
            val dt = dt_.replace('-', '/')
            var time = noTimeNum
            val timeFormat = if (dt.contains("AM") || dt.contains("PM"))
                             formats_12h else formats_24h

            var caught = false
            var msg    = ""
            for (format <- timeFormat) {
                try {
                    time = TimeNum (dt, format)
                    return time
                } catch {
                    case ex: Exception => caught = true; msg = ex.getMessage
                } // catch
            } // for
            if (caught) throw new DateTimeException (msg)
            else time
        } // apply

    } // TimeNum object

} // TimeO object


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `TimeNumTest` object is used to test the `TimeNum` class.
 *  > runMain scalation.math.TimeNumTest
 */
object TimeNumTest extends App
{
    import  TimeO._

    val tztest    = ZonedDateTime.now (ZoneId.of("GMT-05:00"))
    val date1     = new TimeNum (ZonedDateTime.now (ZoneId.of ("GMT-05:00")))
    Thread.sleep (2000)                                                   // to create a different TimeNum object
    val date2     = new TimeNum (ZonedDateTime.now (ZoneId.of ("GMT-05:00")))
    val date3     = date1
    val datezero  = ZonedDateTime.ofInstant (Instant.ofEpochSecond (0), ZoneId.of ("UTC"))

    println ("date1                         = " + date1)
    println ("date2                         = " + date2)

    println ("date1 ≥ date2                 = " + (date1 ≥ date2))
    println ("date1 ≤ date2                 = " + (date1 ≤ date2))
    println ("date1 ≠ date2                 = " + (date1 ≠ date2))
    println ("date1.inst.toEpoSecond        = " + date1.inst.getEpochSecond)
    println ("datezero (should print EPOCH) = " + datezero)
    println ("date1 min date2               = " + (date1 min date2))
    println ("date1 max date2               = " + (date1 max date2))
    println ("date1 hashcode                = " + date1.hashCode)
    println ("date1 equals date2            = " + (date1 equals date2))
    println ("date1 equals date3            = " + (date1 equals date3))
    println ("date1.toLong                  = " + date1.toLong)
    println ("date1.toFloat                 = " + date1.toFloat)
    println ("fromInt                       = " + date1.fromInt (date1.inst.getEpochSecond.toInt))
    println ("date1.fromDouble (600.8)      = " + date1.fromDouble (600.8))
    println ("date1.fromInt (18000)         = " + date1.fromInt (18000))
    println ("date1.toDouble (date2)        = " + date1.toDouble (date2))
    println ("date1.toFloat (date2)         = " + date1.toFloat (date2))
    println ("date1.toInt (date2)           = " + date1.toInt (date2))
    println ("date1.toLong (date2)          = " + date1.toLong (date2))
    println ("date2.hashCode                = " + date1.hashCode)

    import scala.util.Sorting.quickSort
    val arr = Array (date1, date2, date3)
    println ("compare = " + (date1 compare date2))
    println ("original arr = " + stringOf (arr))
    quickSort (arr)(TimeO.ord)
    println ("sorted arr   = " + stringOf (arr))

} // TimeNumTest object


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `TimeNumTest2` object is used to test the `TimeNum` class regarding
 *  approximately equal date-times.
 *  > runMain scalation.math.TimeNumTest2
 */
object TimeNumTest2 extends App
{
    TimeO.setThreshold (10.1)                                   // 10.1 second - 10 seconds, 100,000,000 nanoseconds

    val dt =  "07/26/2018 1:12:30:800000000 UTC"                // reference

    val dtArray = Array (
        "07/26/2018 1:12:19:800000000 UTC",    // under by seconds F
        "07/26/2018 1:12:20:699999999 UTC",    // under by nanoseconds F
        "07/26/2018 1:12:20:700000000 UTC",    // on threshold T (returns F as nanoThreshold is 99,999,999 instead of 100M)
        "07/26/2018 1:12:20:700000001 UTC",    // on threshold T (returns F as nanoThreshold is 99,999,999 instead of 100M)
        "07/26/2018 1:12:20:899999999 UTC",    // within by nanoseconds T
        "07/26/2018 1:12:20:900000000 UTC",    // within by nanoseconds T
        "07/26/2018 1:12:21:800000000 UTC",    // within by seconds T

        "07/26/2018 1:12:39:800000000 UTC",    // within by seconds T
        "07/26/2018 1:12:40:800000000 UTC",    // within by seconds T
        "07/26/2018 1:12:40:899999999 UTC",    // within by nanoseconds T
        "07/26/2018 1:12:40:900000000 UTC",    // on threshold T (returns F as nanoThreshold is 99,999,999 instead of 100M)
        "07/26/2018 1:12:40:900000001 UTC",    // over by nanoseconds F
        "07/26/2018 1:12:41:800000000 UTC")    // over by seconds F

    val refTimeNum = TimeNum (dt)
    println (s"refTimeNum = $refTimeNum")
    for (s <- dtArray) {
        val t2 = TimeNum (s)
        println (s"$refTimeNum =~ $t2 is ${refTimeNum =~ t2}")
    } // for

} // TimeNumTest2 object


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
/** The `TimeNumTest3` object is used to test the `TimeNum` class regarding
 *  its getChrono method.
 *  FIX - crashes - format exception
 *  > runMain scalation.math.TimeNumTest3
 */
object TimeNumTest3 extends App
{
    val str = "07/26/2018 16:30:01"
    val t   = TimeNum (str)

    println (s"t = $t")
    // prints actual hours and minute
    println (s"early ${ZonedDateTime.ofInstant (t.inst, ZoneOffset.UTC)}")
    // chops hours, minutes and seconds to zero
    println (s"later ${ZonedDateTime.ofInstant (t.inst, ZoneOffset.UTC).withHour(0).withMinute(0).withSecond(0)}")

    println (s"DAY_OF_MONTH   = ${t.getChrono (ChronoField.DAY_OF_MONTH)}")
    println (s"DAY_OF_WEEK    = ${t.getChrono (ChronoField.DAY_OF_WEEK)}")
    println (s"HOUR_OF_DAY    = ${t.getChrono (ChronoField.HOUR_OF_DAY)}")
    println (s"MONTH_OF_YEAR  = ${t.getChrono (ChronoField.MONTH_OF_YEAR)}")
    println (s"YEAR           = ${t.getChrono (ChronoField.YEAR)}")
    println (s"NANO_OF_SECOND = ${t.getChrono (ChronoField.NANO_OF_SECOND)}")
    println (s"AM(0)/PM(1)    = ${t.getChrono (ChronoField.AMPM_OF_DAY)}")

} // TimeNumTest3 object
