package org.esbench.elastic.stats;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public final class DateStatsParserConstants {
	public static final String DATE_SEPARATOR = "\\|\\|";
	public static final String FORMAT_PROP = "format";
	public static final String EPOCH_MILLIS = "epoch_millis";
	public static final String EPOCH_SECOND = "epoch_second";
	public static final String DEFAULT_DATE_FORMAT = "strict_date_optional_time||epoch_millis";

	public static final List<String> UNSUPPORTED_ES_FORMATS = ImmutableList.<String> builder()
			.add("basicWeekDate", "basic_week_date", "basicWeekDateTime", "basic_week_date_time", "basicWeekDateTimeNoMillis", "basic_week_date_time_no_millis",
					"weekDate", "week_date", "weekDateTime", "week_date_time", "weekDateTimeNoMillis", "week_date_time_no_millis", "weekyear", "week_year",
					"weekyearWeek", "weekyear_week", "weekyearWeekDay", "weekyear_week_day", "strictWeekDate", "strict_week_date", "strictWeekDateTime",
					"strict_week_date_time", "strictWeekDateTimeNoMillis", "strict_week_date_time_no_millis", "strictWeekyear", "strict_weekyear",
					"strictWeekyearWeek", "strict_weekyear_week", "strictWeekyearWeekDay", "strict_weekyear_week_day", "strictBasicWeekDate",
					"strict_basic_week_date", "strictBasicWeekDateTime", "strict_basic_week_date_time", "strictBasicWeekDateTimeNoMillis",
					"strict_basic_week_date_time_no_millis")
			.build();

	public static final List<String> SUPPORTED_ES_FORMATS = ImmutableList.<String> builder()
			.add("basicDate", "basic_date", "basicDateTime", "basic_date_time", "basicDateTimeNoMillis", "basic_date_time_no_millis", "basicOrdinalDate",
					"basic_ordinal_date", "basicOrdinalDateTime", "basic_ordinal_date_time", "basicOrdinalDateTimeNoMillis",
					"basic_ordinal_date_time_no_millis", "basicTime", "basic_time", "basicTimeNoMillis", "basic_time_no_millis", "basicTTime", "basic_t_Time",
					"basicTTimeNoMillis", "basic_t_time_no_millis", "date", "dateHour", "date_hour", "dateHourMinute", "date_hour_minute",
					"dateHourMinuteSecond", "date_hour_minute_second", "dateHourMinuteSecondFraction", "date_hour_minute_second_fraction",
					"dateHourMinuteSecondMillis", "date_hour_minute_second_millis", "dateOptionalTime", "date_optional_time", "dateTime", "date_time",
					"dateTimeNoMillis", "date_time_no_millis", "hour", "hourMinute", "hour_minute", "hourMinuteSecond", "hour_minute_second",
					"hourMinuteSecondFraction", "hour_minute_second_fraction", "hourMinuteSecondMillis", "hour_minute_second_millis", "ordinalDate",
					"ordinal_date", "ordinalDateTime", "ordinal_date_time", "ordinalDateTimeNoMillis", "ordinal_date_time_no_millis", "time", "timeNoMillis",
					"time_no_millis", "tTime", "t_time", "tTimeNoMillis", "t_time_no_millis", "year", "yearMonth", "year_month", "yearMonthDay",
					"year_month_day", "strictDate", "strict_date", "strictDateHour", "strict_date_hour", "strictDateHourMinute", "strict_date_hour_minute",
					"strictDateHourMinuteSecond", "strict_date_hour_minute_second", "strictDateHourMinuteSecondFraction",
					"strict_date_hour_minute_second_fraction", "strictDateHourMinuteSecondMillis", "strict_date_hour_minute_second_millis",
					"strictDateOptionalTime", "strict_date_optional_time", "strictDateTime", "strict_date_time", "strictDateTimeNoMillis",
					"strict_date_time_no_millis", "strictHour", "strict_hour", "strictHourMinute", "strict_hour_minute", "strictHourMinuteSecond",
					"strict_hour_minute_second", "strictHourMinuteSecondFraction", "strict_hour_minute_second_fraction", "strictHourMinuteSecondMillis",
					"strict_hour_minute_second_millis", "strictOrdinalDate", "strict_ordinal_date", "strictOrdinalDateTime", "strict_ordinal_date_time",
					"strictOrdinalDateTimeNoMillis", "strict_ordinal_date_time_no_millis", "strictTime", "strict_time", "strictTimeNoMillis",
					"strict_time_no_millis", "strictTTime", "strict_t_time", "strictTTimeNoMillis", "strict_t_time_no_millis", "strictYear", "strict_year",
					"strictYearMonth", "strict_year_month", "strictYearMonthDay", "strict_year_month_day")
			.build();

	public static final List<String> SUPPORTED_ES_NON_DATE_FORMATS = ImmutableList.<String> builder().add("epoch_second", "epoch_millis").build();

	public static final List<String> ES_FORMATS = ImmutableList.<String> builder()
			.addAll(SUPPORTED_ES_FORMATS)
			.addAll(SUPPORTED_ES_NON_DATE_FORMATS)
			.addAll(UNSUPPORTED_ES_FORMATS)
			.build();

	public static final ImmutableMap<String, String> JODA_FORMAT_TO_JDK = new ImmutableMap.Builder<String, String>().put("basicDate", "yyyyMMdd")
			.put("basicdate", "yyyyMMdd")
			.put("basicdatetime", "yyyyMMdd'T'HHmmss.SSSX")
			.put("basicdatetimenomillis", "yyyyMMdd'T'HHmmssX")
			.put("basicordinaldate", "yyyyDDD")
			.put("basicordinaldatetime", "yyyyDDD'T'HHmmss.SSSX")
			.put("basicordinaldatetimenomillis", "yyyyDDD'T'HHmmssX")
			.put("basictime", "HHmmss.SSSX")

			.put("basictimenomillis", "HHmmssX")
			.put("basicttime", "'T'HHmmss.SSSX")
			.put("basicttimenomillis", "'T'HHmmssX")

			.put("date", "yyyy-MM-dd")
			.put("datehour", "yyyy-MM-dd'T'HH")
			.put("datehourminute", "yyyy-MM-dd'T'HH:mm")
			.put("datehourminutesecond", "yyyy-MM-dd'T'HH:mm:ss")
			.put("datehourminutesecondfraction", "yyyy-MM-dd'T'HH:mm:ss.SSS")

			.put("datehourminutesecondmillis", "yyyy-MM-dd'T'HH:mm:ss.SSS")
			.put("dateoptionaltime", "yyyy-MM-dd'T'HH:mm:ss.SSSXX")
			.put("datetime", "yyyy-MM-dd'T'HH:mm:ss.SSSX")
			.put("datetimenomillis", "yyyy-MM-dd'T'HH:mm:ssX")

			.put("hour", "HH")
			.put("hourminute", "HH:mm")
			.put("hourminutesecond", "HH:mm:ss")
			.put("hourminutesecondfraction", "HH:mm:ss.SSS")
			.put("hourminutesecondmillis", "HH:mm:ss.SSS")
			.put("ordinaldate", "yyyy-DDD")
			.put("ordinaldatetime", "yyyy-DDD'T'HH:mm:ss.SSSX")
			.put("ordinaldatetimenomillis", "yyyy-DDD'T'HH:mm:ssX")
			.put("time", "HH:mm:ss.SSSX")
			.put("timenomillis", "HH:mm:ssX")
			.put("ttime", "'T'HH:mm:ss.SSSX")
			.put("ttimenomillis", "'T'HH:mm:ssX")
			.put("year", "yyyy")
			.put("yearmonth", "yyyy-MM")
			.put("yearmonthday", "yyyy-MM-dd")

			.put("strictdateoptionaltime", "yyyy-MM-dd'T'HH:mm:ss.SSSX")
			.build();

	private DateStatsParserConstants() {

	}
}
