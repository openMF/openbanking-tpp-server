/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.openbank.tpp.acefintech.backend.util;

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
    public static final DateTimeFormatter ISO8601_UTC_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(DateUtils.ISO8601_DATE_TIME_PATTERN);

    private static final SimpleDateFormat LOCAL_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public static @NotNull String getTimezoneIdOfTenant() {
        return ZoneId.of("UTC").getId();
    }

    public static @NotNull ZoneId getZoneIdOfTenant() {
        return ZoneId.of(DateUtils.getTimezoneIdOfTenant());
    }

    public static @NotNull TimeZone getTimeZoneOfTenant() {
        return TimeZone.getTimeZone(DateUtils.getTimezoneIdOfTenant());
    }

    private static long getMillisOfTenant() {
        return System.currentTimeMillis();
    }

    /**
     * @return business date of the current tenant
     */
    public static Date getDateOfTenant() {
        return new Date(DateUtils.getMillisOfTenant());
    }

    public static @NotNull Timestamp getDateTimeOfTenant() {
        return new Timestamp(DateUtils.getMillisOfTenant());
    }

    public static @NotNull LocalDate getLocalDateOfTenant() {
        return LocalDate.now(DateUtils.getZoneIdOfTenant());
    }

    public static @NotNull LocalDate getLocalDateOfTenant(final Date date) {
        return date == null ? null : date.toInstant().atZone(DateUtils.getZoneIdOfTenant()).toLocalDate();
    }

    public static @NotNull LocalDateTime getLocalDateTimeOfTenant() {
        return LocalDateTime.now(DateUtils.getZoneIdOfTenant());
    }

    public static @NotNull LocalDateTime getLocalDateTimeOfTenant(final Timestamp stamp) {
        return stamp == null ? null : stamp.toLocalDateTime();
    }

    public static int compareDatePart(@NotNull final Date first, @NotNull final Date second) {
        return DateUtils.getDatePartOf(first).compareTo(DateUtils.getDatePartOf(second));
    }

    public static boolean isEquals(@NotNull final Date first, @NotNull final Date second) {
        return first == null
                ? second == null
                : second != null && DateUtils.compareDatePart(first, second) == 0;
    }

    public static boolean isEquals(@NotNull final LocalDate first, @NotNull final LocalDate second) {
        return first == null
                ? second == null
                : second != null && DateUtils.compareDatePart(first, second) == 0;
    }

    public static boolean isBefore(@NotNull final Date first, @NotNull final Date second) {
        return first == null || (second != null && DateUtils.compareDatePart(first, second) < 0);
    }

    public static boolean isBefore(@NotNull final LocalDate first, @NotNull final LocalDate second) {
        return first == null || (second != null && DateUtils.compareDatePart(first, second) < 0);
    }

    public static boolean isAfter(@NotNull final Date first, @NotNull final Date second) {
        return first != null && (second == null || DateUtils.compareDatePart(first, second) > 0);
    }

    public static boolean isAfter(@NotNull final LocalDate first, @NotNull final LocalDate second) {
        return first != null && (second == null || DateUtils.compareDatePart(first, second) > 0);
    }

    /**
     * @return the date which is not null and earlier than the other. Still can return null if both dates are null
     */
    public static Date getEarlierNotNull(@NotNull final Date first, @NotNull final Date second) {
        if (first == null)
            return second;
        if (second == null)
            return first;
        return DateUtils.isBefore(first, second) ? first : second;
    }

    public static int compareDatePart(@NotNull final LocalDate first, @NotNull final LocalDate second) {
        return first.compareTo(second);
    }

    public static @NotNull int compareToDateOfTenant(final Date date) {
        return DateUtils.compareDatePart(date, DateUtils.getDateOfTenant());
    }

    public static @NotNull int compareToDateOfTenant(final LocalDate date) {
        return DateUtils.compareDatePart(date, DateUtils.getLocalDateOfTenant());
    }

    public static @NotNull boolean isBeforeDateOfTenant(final Date date) {
        return DateUtils.compareToDateOfTenant(date) < 0;
    }

    public static @NotNull boolean isBeforeDateOfTenant(final LocalDate date) {
        return DateUtils.compareToDateOfTenant(date) < 0;
    }

    public static @NotNull boolean isAfterDateOfTenant(final Date date) {
        return DateUtils.compareToDateOfTenant(date) > 0;
    }

    public static @NotNull boolean isAfterDateOfTenant(final LocalDate date) {
        return DateUtils.compareToDateOfTenant(date) > 0;
    }

    public static @NotNull Date getDatePartOf(final Date date) {
        return DateUtils.toDate(DateUtils.toLocalDate(date));
    }

    public static Date toDate(final LocalDate localDate) {
        return localDate == null ? null : Date.from(localDate.atStartOfDay(DateUtils.getZoneIdOfTenant()).toInstant());
    }

    public static Date toDate(final LocalDateTime localDateTime) {
        return localDateTime == null ? null : Date.from(localDateTime.atZone(DateUtils.getZoneIdOfTenant()).toInstant());
    }

    public static LocalDate toLocalDate(final Date date) {
        return date.toInstant().atZone(DateUtils.getZoneIdOfTenant()).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(final Date date) {
        return date.toInstant().atZone(DateUtils.getZoneIdOfTenant()).toLocalDateTime();
    }

    public static Date plusDays(final Date date, final int days) {
        return date == null
                ? null
                : days == 0 ? date : DateUtils.toDate(DateUtils.toLocalDate(date).plusDays(days));
    }

    public static LocalDate plusDays(final LocalDate date, final int days) {
        return date == null
                ? null
                : date.plusDays(days);
    }

    public static Date minusDays(final Date date, final int days) {
        return date == null
                ? null
                : days == 0 ? date : DateUtils.toDate(DateUtils.toLocalDate(date).minusDays(days));
    }

    public static LocalDate minusDays(final LocalDate date, final int days) {
        return date == null
                ? null
                : date.minusDays(days);
    }

    public static long daysBetween(@NotNull final Date first, @NotNull final Date second) {
        final ZoneId zoneId = DateUtils.getZoneIdOfTenant();
        return ChronoUnit.DAYS.between(first.toInstant().atZone(zoneId), second.toInstant().atZone(zoneId));
    }

    public static long daysBetween(@NotNull final LocalDate first, @NotNull final LocalDate second) {
        return ChronoUnit.DAYS.between(first, second);
    }

    public static LocalDate parseLocalDate(final String stringDate, final String pattern, final Locale clientLocale) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withLocale(clientLocale).withZone(DateUtils.getZoneIdOfTenant());
        return LocalDate.parse(stringDate, formatter);
    }

    public static String formatToSqlDate(final Date date) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(DateUtils.getTimeZoneOfTenant());
        final String formattedSqlDate = df.format(date);
        return formattedSqlDate;
    }

    public static @NotNull String toIsoString(@NotNull final Date date) {
        return DateUtils.toIsoString(LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC")));
    }

    public static @NotNull String toIsoString(@NotNull final LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "LocalDateTime must be given.");
        return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME) + "Z";
    }

    public static @NotNull LocalDateTime fromIsoString(@NotNull final String isoDateTimeString) {
        Assert.notNull(isoDateTimeString, "ISO date time must be given.");
        return LocalDateTime.from(Instant.parse(isoDateTimeString).atZone(ZoneOffset.UTC));
    }

    public static @NotNull LocalDate dateFromIsoString(@NotNull final String isoDateString) {
        Assert.notNull(isoDateString, "ISO date time must be given.");
        final int zIndex = isoDateString.indexOf("Z");
        final String shortenedString = isoDateString.substring(0, zIndex);
        return LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(shortenedString));
    }

    public static @NotNull String toIsoString(@NotNull final LocalDate localDate) {
        Assert.notNull(localDate, "LocalDateTime must be given.");
        return localDate.format(DateTimeFormatter.ISO_DATE) + "Z";
    }

    public static @NotNull LocalDate toLocalDate(@NotNull final LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "LocalDateTime must be given.");
        return localDateTime.toLocalDate();
    }

    public static LocalDateTime parseLocalFormatDateTime(final String date) {
        return date == null ? null : LocalDateTime.ofInstant(OffsetDateTime.parse(date).toInstant(), ZoneOffset.UTC);
    }

    public static String formatLocalFormatDateTime(final LocalDateTime date) {
        return date == null ? null : DateUtils.LOCAL_DATE_TIME_FORMAT.format(Date.from(date.toInstant(ZoneOffset.UTC)));
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
