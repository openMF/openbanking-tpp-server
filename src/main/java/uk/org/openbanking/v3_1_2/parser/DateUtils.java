/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package uk.org.openbanking.v3_1_2.parser;

import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static final String ISO8601_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final DateTimeFormatter ISO8601_UTC_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(ISO8601_DATE_TIME_PATTERN);

    private static final SimpleDateFormat LOCAL_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public static @NotNull String getTimezoneIdOfTenant() {
        return ZoneId.of("UTC").getId();
    }

    public static @NotNull ZoneId getZoneIdOfTenant() {
        return ZoneId.of(getTimezoneIdOfTenant());
    }

    public static @NotNull TimeZone getTimeZoneOfTenant() {
        return TimeZone.getTimeZone(getTimezoneIdOfTenant());
    }

    private static long getMillisOfTenant() {
        return System.currentTimeMillis();
    }

    /**
     * @return business date of the current tenant
     */
    public static Date getDateOfTenant() {
        return new Date(getMillisOfTenant());
    }

    public static @NotNull Timestamp getDateTimeOfTenant() {
        return new Timestamp(getMillisOfTenant());
    }

    public static @NotNull LocalDate getLocalDateOfTenant() {
        return LocalDate.now(getZoneIdOfTenant());
    }

    public static @NotNull LocalDate getLocalDateOfTenant(final Date date) {
        return null == date ? null : date.toInstant().atZone(getZoneIdOfTenant()).toLocalDate();
    }

    public static @NotNull LocalDateTime getLocalDateTimeOfTenant() {
        return LocalDateTime.now(getZoneIdOfTenant());
    }

    public static @NotNull LocalDateTime getLocalDateTimeOfTenant(final Timestamp stamp) {
        return null == stamp ? null : stamp.toLocalDateTime();
    }

    public static int compareDatePart(final @NotNull Date first, final @NotNull Date second) {
        return getDatePartOf(first).compareTo(getDatePartOf(second));
    }

    public static boolean isEquals(final @NotNull Date first, final @NotNull Date second) {
        return null == first
                ? null == second
                : null != second && 0 == compareDatePart(first, second);
    }

    public static boolean isEquals(final @NotNull LocalDate first, final @NotNull LocalDate second) {
        return null == first
                ? null == second
                : null != second && 0 == compareDatePart(first, second);
    }

    public static boolean isBefore(final @NotNull Date first, final @NotNull Date second) {
        return null == first || (null != second && 0 > compareDatePart(first, second));
    }

    public static boolean isBefore(final @NotNull LocalDate first, final @NotNull LocalDate second) {
        return null == first || (null != second && 0 > compareDatePart(first, second));
    }

    public static boolean isAfter(final @NotNull Date first, final @NotNull Date second) {
        return null != first && (null == second || 0 < compareDatePart(first, second));
    }

    public static boolean isAfter(final @NotNull LocalDate first, final @NotNull LocalDate second) {
        return null != first && (null == second || 0 < compareDatePart(first, second));
    }

    /**
     * @return the date which is not null and earlier than the other. Still can return null if both dates are null
     */
    public static Date getEarlierNotNull(final @NotNull Date first, final @NotNull Date second) {
        if (null == first)
            return second;
        if (null == second)
            return first;
        return isBefore(first, second) ? first : second;
    }

    public static int compareDatePart(final @NotNull LocalDate first, final @NotNull LocalDate second) {
        return first.compareTo(second);
    }

    public static @NotNull int compareToDateOfTenant(final Date date) {
        return compareDatePart(date, getDateOfTenant());
    }

    public static @NotNull int compareToDateOfTenant(final LocalDate date) {
        return compareDatePart(date, getLocalDateOfTenant());
    }

    public static @NotNull boolean isBeforeDateOfTenant(final Date date) {
        return 0 > compareToDateOfTenant(date);
    }

    public static @NotNull boolean isBeforeDateOfTenant(final LocalDate date) {
        return 0 > compareToDateOfTenant(date);
    }

    public static @NotNull boolean isAfterDateOfTenant(final Date date) {
        return 0 < compareToDateOfTenant(date);
    }

    public static @NotNull boolean isAfterDateOfTenant(final LocalDate date) {
        return 0 < compareToDateOfTenant(date);
    }

    public static @NotNull Date getDatePartOf(final Date date) {
        return toDate(toLocalDate(date));
    }

    public static Date toDate(final LocalDate localDate) {
        return null == localDate ? null : Date.from(localDate.atStartOfDay(getZoneIdOfTenant()).toInstant());
    }

    public static Date toDate(final LocalDateTime localDateTime) {
        return null == localDateTime ? null : Date.from(localDateTime.atZone(getZoneIdOfTenant()).toInstant());
    }

    public static LocalDate toLocalDate(final Date date) {
        return date.toInstant().atZone(getZoneIdOfTenant()).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(final Date date) {
        return date.toInstant().atZone(getZoneIdOfTenant()).toLocalDateTime();
    }

    public static Date plusDays(final Date date, final int days) {
        return null == date
                ? null
                : 0 == days ? date : toDate(toLocalDate(date).plusDays(days));
    }

    public static LocalDate plusDays(final LocalDate date, final int days) {
        return null == date
                ? null
                : date.plusDays(days);
    }

    public static Date minusDays(final Date date, final int days) {
        return null == date
                ? null
                : 0 == days ? date : toDate(toLocalDate(date).minusDays(days));
    }

    public static LocalDate minusDays(final LocalDate date, final int days) {
        return null == date
                ? null
                : date.minusDays(days);
    }

    public static long daysBetween(final @NotNull Date first, final @NotNull Date second) {
        final ZoneId zoneId = getZoneIdOfTenant();
        return ChronoUnit.DAYS.between(first.toInstant().atZone(zoneId), second.toInstant().atZone(zoneId));
    }

    public static long daysBetween(final @NotNull LocalDate first, final @NotNull LocalDate second) {
        return ChronoUnit.DAYS.between(first, second);
    }

    public static LocalDate parseLocalDate(final String stringDate, final String pattern, final Locale clientLocale) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withLocale(clientLocale).withZone(getZoneIdOfTenant());
        return LocalDate.parse(stringDate, formatter);
    }

    public static String formatToSqlDate(final Date date) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(getTimeZoneOfTenant());
        return df.format(date);
    }

    public static @NotNull String toIsoString(final @NotNull Date date) {
        return toIsoString(LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC")));
    }

    public static @NotNull String toIsoString(final @NotNull LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "LocalDateTime must be given.");
        return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME) + "Z";
    }

    public static @NotNull LocalDateTime fromIsoString(final @NotNull String isoDateTimeString) {
        Assert.notNull(isoDateTimeString, "ISO date time must be given.");
        return LocalDateTime.from(Instant.parse(isoDateTimeString).atZone(ZoneOffset.UTC));
    }

    public static @NotNull LocalDate dateFromIsoString(final @NotNull String isoDateString) {
        Assert.notNull(isoDateString, "ISO date time must be given.");
        final int zIndex = isoDateString.indexOf("Z");
        final String shortenedString = isoDateString.substring(0, zIndex);
        return LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(shortenedString));
    }

    public static @NotNull String toIsoString(final @NotNull LocalDate localDate) {
        Assert.notNull(localDate, "LocalDateTime must be given.");
        return localDate.format(DateTimeFormatter.ISO_DATE) + "Z";
    }

    public static @NotNull LocalDate toLocalDate(final @NotNull LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "LocalDateTime must be given.");
        return localDateTime.toLocalDate();
    }

    public static LocalDateTime parseLocalFormatDateTime(final String date) {
        return null == date ? null : LocalDateTime.ofInstant(OffsetDateTime.parse(date).toInstant(), ZoneOffset.UTC);
    }

    public static String formatLocalFormatDateTime(final LocalDateTime date) {
        return null == date ? null : LOCAL_DATE_TIME_FORMAT.format(Date.from(date.toInstant(ZoneOffset.UTC)));
    }

    public static void main(final String[] args) throws ParseException {
//        2019-12-31T11:16:31.663+01:00
//        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
//        LocalDateTime parse1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(f.parse("2019-12-31T11:22:33+01:00").getTime()), ZoneOffset.UTC);
//        LocalDateTime parse2 = LocalDateTime.ofInstant(OffsetDateTime.parse("2019-12-31T11:22:33+01:00").toInstant(), ZoneOffset.UTC);
//        System.out.println("parse1 = " + parse1);
//        System.out.println("parse2 = " + parse2);
//        System.out.println("format1 = " + LOCAL_DATE_TIME_FORMAT.format(Date.from(parse1.toInstant(ZoneOffset.UTC))));
//        System.out.println("format2 = " + OffsetDateTime.of(parse2, ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")));
//        final String ISO8601_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
//        LocalDateTimeDeserializer deserializer = LocalDateTimeDeserializer.INSTANCE;
//        LocalDateTimeSerializer serializer = LocalDateTimeSerializer.INSTANCE;
//        String dateS = "2019-12-31T11:00:00.000+02:00";
//        LocalDateTime date = LocalDateTime.parseDate(dateS);
//
//        LocalDateTime localDateTime = LocalDateTime.parseDate(dateS, DateTimeFormatter.ISO_DATE_TIME);
//
//        System.out.println("ZonedDateTime formatDate OFFSET: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
//        System.out.println("ZonedDateTime formatDate ZONED: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_ZONED_DATE_TIME));
//        System.out.println("ZonedDateTime formatDate LOCAL: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//        System.out.println("ZonedDateTime formatDate DATE TIME: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_DATE_TIME));
//        System.out.println("ZonedDateTime formatDate DATE: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_DATE));
//
//        System.out.println("\nlocalDateTime formatDate LOCAL: " + localDateTime.formatDate(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//        System.out.println("localDateTime formatDate DATE TIME: " + localDateTime.formatDate(DateTimeFormatter.ISO_DATE_TIME));
//        System.out.println("localDateTime formatDate DATE: " + localDateTime.formatDate(DateTimeFormatter.ISO_DATE));
//
//        SimpleDateFormat zoneOrZuluFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSXXX");
//        SimpleDateFormat isoDateFormat = new SimpleDateFormat(ISO8601_DATE_TIME_PATTERN);
//        SimpleDateFormat nozoneFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
//
//        "yyyy-mm-dd hh:mm:ss[.fffffffff]"
//        Timestamp timestamp = Timestamp.valueOf("2019-12-31 11:00:00.001"); // no zone allowed
//        System.out.println("\ntimestamp formatDate: " + zoneOrZuluFormat.formatDate(timestamp));
//
//        Date date = zoneOrZuluFormat.parseDate(dateS);
//
//        Instant instant = Instant.ofEpochMilli(date.getTime());
//        localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
//
//        System.out.println("\ndate formatDate zoneOrZulu: " + zoneOrZuluFormat.formatDate(date));
//        System.out.println("date formatDate isoDateFormat: " + isoDateFormat.formatDate(date));
//        System.out.println("date formatDate nozone: " + nozoneFormat.formatDate(date));
//
//
//        Date dateFromLocal = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
//        System.out.println("\ndateFromLocal formatDate zoneOrZulu: " + zoneOrZuluFormat.formatDate(dateFromLocal));
//        System.out.println("dateFromLocal formatDate isoDateFormat: " + isoDateFormat.formatDate(dateFromLocal));
//        System.out.println("dateFromLocal formatDate nozone: " + nozoneFormat.formatDate(dateFromLocal));
//
//        zoneOrZuluFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
//        isoDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
//        nozoneFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
//
//        System.out.println("\ndate formatDate zoneOrZulu timezone UTC: " + zoneOrZuluFormat.formatDate(date)); //OK
//        System.out.println("date formatDate isoDateFormat UTC: " + isoDateFormat.formatDate(date)); //OK
//        System.out.println("date formatDate nozone UTC: " + nozoneFormat.formatDate(date));
//
//        System.out.println("\ndateFromLocal formatDate zoneOrZulu timezone UTC: " + zoneOrZuluFormat.formatDate(dateFromLocal)); //OK
//        System.out.println("dateFromLocal formatDate isoDateFormat UTC: " + isoDateFormat.formatDate(dateFromLocal)); //OK
//        System.out.println("dateFromLocal formatDate nozone UTC: " + nozoneFormat.formatDate(dateFromLocal));
//        BigDecimal amount;
//        System.out.println("123.45: " + AMOUNT_FORMAT.format(new BigDecimal("123.45")));
//        System.out.println("123.00: " + AMOUNT_FORMAT.format(new BigDecimal("123.00")));
//        System.out.println("123.40: " + AMOUNT_FORMAT.format(new BigDecimal("123.40")));
//        System.out.println("123.040: " + AMOUNT_FORMAT.format(new BigDecimal("123.040")));
//        System.out.println("0.00: " + AMOUNT_FORMAT.format(new BigDecimal("0.00")));
//        System.out.println("23456.70: " + AMOUNT_FORMAT.format(new BigDecimal("23456.70")));
//        System.out.println("23456789.10: " + AMOUNT_FORMAT.format(new BigDecimal("23456789.10")));
//        System.out.println("123456789.123456789: " + AMOUNT_FORMAT.format(new BigDecimal("123456789.123456789")));
//        System.out.println("0.123456789: " + AMOUNT_FORMAT.format(new BigDecimal("0.123456789")));
//        System.out.println("1234567890123456789.023456089: " + AMOUNT_FORMAT.format(new BigDecimal("1234567890123456789.023456089")));
    }
}
