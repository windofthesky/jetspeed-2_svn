package org.apache.cornerstone.framework.demo.service;

import java.util.*;
import java.text.*;
import org.apache.cornerstone.framework.api.service.ServiceException;
import org.apache.cornerstone.framework.service.BaseService;

public class DateService extends BaseService
{
	public static final String REVISION = "$Revision$";

	public static final String INPUT_TIME_ZONE = "timeZone";
	public static final String INPUT_DATE_FORMAT = "dateFormat";
	public static final String OUTPUT_DATE = "date";

	public static final String DATE_FORMAT_FULL = "FULL";
	public static final String DATE_FORMAT_SHORT = "SHORT";

	public static final String CONFIG_DATE_FORMAT_PATTERN = "dateFormatPattern";

	/**
	 * Object configuration metadata
	 */
	public static final String CONFIG_PARAMS = CONFIG_DATE_FORMAT_PATTERN;

	/**
	 * Service metadata
	 */
	public static final String INVOKE_DIRECT_INPUTS = INPUT_TIME_ZONE + "," + INPUT_DATE_FORMAT;
	public static final String INVOKE_DIRECT_OUTPUT = OUTPUT_DATE;

	/**
	 * Gets current date in time zone
	 * @param timeZone String E.g. "GMT-0800"
	 * @param dateStyle String DATE_FORMAT_FULL or DATE_FORMAT_SHORT
	 * @return Current date in time zone.
	 */
	public String invokeDirect(String timeZone, String dateStyle) throws ServiceException
	{
		if (timeZone == null) timeZone = "GMT-0000";
		if (dateStyle == null) dateStyle = DATE_FORMAT_FULL;

		int dateStyleInt = DateFormat.FULL;
		if (dateStyle.equals(DATE_FORMAT_FULL))
			dateStyleInt = DateFormat.FULL;
		else if (dateStyle.equals(DATE_FORMAT_SHORT))
			dateStyleInt = DateFormat.SHORT;

		String dateFormatPattern = getConfigProperty(CONFIG_DATE_FORMAT_PATTERN);
		DateFormat df =
			dateFormatPattern == null ?
			DateFormat.getDateInstance(dateStyleInt) :
			new SimpleDateFormat(dateFormatPattern);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));

		Date date = new Date();
		String dateString = df.format(date); 

		return dateString;
	}
}