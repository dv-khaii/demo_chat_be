package co.jp.xeex.chat.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import co.jp.xeex.chat.common.AppConstant;
import lombok.extern.log4j.Log4j;

/**
 * The utility class for date and time
 * (add more if needed)
 * 
 * @author v_long
 */
@Log4j
public class DateTimeUtil {
    /**
     * Get the current system date
     * 
     * @return Date
     */
    public static Date getSystemDate() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    /**
     * Get the current system date as a string
     * 
     * @return String representation of the current system date
     *         For example: "Wed Dec 31 23:59:59 JST 2020"
     */
    public static String getSystemDateString() {
        return getSystemDate().toString();
    }

    /**
     * Get the current system date as a string
     * 
     * @return String representation of the current system date
     *         For example: "2020-12-31 23:59:59"
     */
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Get the current system date as a string
     * 
     * @return String representation of the current system date
     *         For example: "2020-12-31 23:59:59"
     */
    public static String getCurrentTimestampString() {
        return getCurrentTimestamp().toString();
    }

    /**
     * Get Zone date time from timestamp
     * 
     * @param timeStamp
     * @return
     */
    public static String getZoneDateTimeString(Timestamp timeStamp) {
        ZonedDateTime zonedDateTime = timeStamp.toInstant().atZone(ZoneId.of(AppConstant.UTC));
        return zonedDateTime.format(DateTimeFormatter.ofPattern(AppConstant.DATE_TIME_ZONE_FORMAT));
    }

    /**
     * Utility method to convert string to timestamp
     * 
     * @param sdate String date
     * @return Timestamp. If sdate is emprt or error, return null
     */
    public static Timestamp convertToTimestamp(String sdate) {
        try {
            if (StringUtils.isEmpty(sdate)) {
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.DATE_FORMAT);
            Timestamp date = new Timestamp(sdf.parse(sdate).getTime());
            return date;
        } catch (Exception e) {
            log.error("Error when convert string to timestamp: " + e.getMessage());
            return null;
        }

    }
}
