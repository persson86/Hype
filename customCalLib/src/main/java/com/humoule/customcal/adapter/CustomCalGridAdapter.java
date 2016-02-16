package com.humoule.customcal.adapter;

import hirondelle.date4j.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.humoule.customcal.CustomCalFragment;
import com.humoule.customcal.R;
import com.humoule.customcal.util.CalendarHelper;

/**
 * The CustomCalGridAdapter provides customized view for the dates gridview
 *
 * @author Farouk Touzi
 */
public class CustomCalGridAdapter extends BaseAdapter
{
	protected ArrayList<DateTime> datetimeList;
	protected int month;
	protected int year;
	protected Context context;
	protected ArrayList<DateTime> disableDates;
	protected ArrayList<DateTime> selectedDates;

	protected HashMap<DateTime, Integer> disableDatesMap = new HashMap<DateTime, Integer>();
	protected HashMap<DateTime, Integer> selectedDatesMap = new HashMap<DateTime, Integer>();

	protected DateTime minDateTime;
	protected DateTime maxDateTime;
	protected DateTime today;
	protected int startDayOfWeek;
	protected boolean fiveWeeksInCalendar;
	protected boolean squareTextViewCell;
	protected Resources resources;

	public static View selected;
	public static Date selectedDate;

	protected HashMap<String, Object> customCalData; 
	protected HashMap<String, Object> extraData;

	public void setAdapterDateTime(DateTime dateTime)
	{
		this.month = dateTime.getMonth();
		this.year = dateTime.getYear();
		this.datetimeList = CalendarHelper.getFullWeeks(this.month, this.year, startDayOfWeek, fiveWeeksInCalendar);
	}

	public ArrayList<DateTime> getDatetimeList()
	{
		return datetimeList;
	}

	public DateTime getMinDateTime()
	{
		return minDateTime;
	}

	public void setMinDateTime(DateTime minDateTime)
	{
		this.minDateTime = minDateTime;
	}

	public DateTime getMaxDateTime()
	{
		return maxDateTime;
	}

	public void setMaxDateTime(DateTime maxDateTime)
	{
		this.maxDateTime = maxDateTime;
	}

	public ArrayList<DateTime> getDisableDates()
	{
		return disableDates;
	}

	public void setDisableDates(ArrayList<DateTime> disableDates)
	{
		this.disableDates = disableDates;
	}

	public ArrayList<DateTime> getSelectedDates()
	{
		return selectedDates;
	}

	public void setSelectedDates(ArrayList<DateTime> selectedDates)
	{
		this.selectedDates = selectedDates;
	}

	public HashMap<String, Object> getCustomCalData()
	{
		return customCalData;
	}

	public void setCustomCalData(HashMap<String, Object> customCalData)
	{
		this.customCalData = customCalData;

		populateFromCustomCalViewData();
	}

	public HashMap<String, Object> getExtraData()
	{
		return extraData;
	}

	public void setExtraData(HashMap<String, Object> extraData)
	{
		this.extraData = extraData;
	}

	public static void desableSelectedDate()
	{
		if (selected != null)
		{
			selected.findViewById(R.id.calendar_iv).setVisibility(View.INVISIBLE);
		}
	}

	public static void setSelectedView(View v)
	{
		selected = v;
	}

	public static void setSelectedDate(Date d)
	{
		selectedDate = d;
	}

	/**
	 * Constructor
	 *
	 * @param context
	 * @param month
	 * @param year
	 * @param customCalData
	 * @param extraData
	 */
	public CustomCalGridAdapter(Context context, int month, int year, HashMap<String, Object> customCalData,
			HashMap<String, Object> extraData) {
		super();
		this.month = month;
		this.year = year;
		this.context = context;
		this.customCalData = customCalData;
		this.extraData = extraData;
		this.resources = context.getResources();

		// Get data from customCalData
		populateFromCustomCalViewData();
	}

	/**
	 * Retrieve internal parameters from custom calendar data
	 */
	@SuppressWarnings("unchecked")
	private void populateFromCustomCalViewData()
	{
		disableDates = (ArrayList<DateTime>) customCalData.get(CustomCalFragment.DISABLE_DATES);
		if (disableDates != null)
		{
			disableDatesMap.clear();
			for (DateTime dateTime : disableDates)
			{
				disableDatesMap.put(dateTime, 1);
			}
		}

		selectedDates = (ArrayList<DateTime>) customCalData.get(CustomCalFragment.SELECTED_DATES);
		if (selectedDates != null)
		{
			selectedDatesMap.clear();
			for (DateTime dateTime : selectedDates)
			{
				selectedDatesMap.put(dateTime, 1);
			}
		}

		minDateTime = (DateTime) customCalData.get(CustomCalFragment._MIN_DATE_TIME);
		maxDateTime = (DateTime) customCalData.get(CustomCalFragment._MAX_DATE_TIME);
		startDayOfWeek = (Integer) customCalData.get(CustomCalFragment.START_DAY_OF_WEEK);
		fiveWeeksInCalendar = (Boolean) customCalData.get(CustomCalFragment.FIVE_WEEKS_IN_CALENDAR);

		this.datetimeList = CalendarHelper.getFullWeeks(this.month, this.year, startDayOfWeek, fiveWeeksInCalendar);
	}

	public void updateToday()
	{
		today = CalendarHelper.convertDateToDateTime(new Date());
	}

	protected DateTime getToday()
	{
		if (today == null)
		{
			today = CalendarHelper.convertDateToDateTime(new Date());
		}
		return today;
	}

	@SuppressWarnings("unchecked")
	protected void setCustomResources(DateTime dateTime, View backgroundView, TextView textView)
	{

		// Set custom text color
		HashMap<DateTime, Integer> textColorForDateTimeMap = (HashMap<DateTime, Integer>) customCalData
				.get(CustomCalFragment._TEXT_COLOR_FOR_DATETIME_MAP);
		if (textColorForDateTimeMap != null)
		{
			// Get textColor for the dateTime
			Integer textColorResource = textColorForDateTimeMap.get(dateTime);

			// Set it
			if (textColorResource != null)
			{
				textView.setTextColor(resources.getColor(textColorResource.intValue()));
			}
		}
	}

	/**
	 * Customize colors of text and background based on states of the cell (disabled, active, selected, etc)
	 * <p/>
	 * To be used only in getView method
	 *
	 * @param position
	 * @param cellView
	 */
	protected void customizeTextView(int position, TextView cellView, View view)
	{
		cellView.setTextColor(Color.WHITE);

		// Get the padding of cell so that it can be restored later
		int topPadding = view.getPaddingTop();
		int leftPadding = view.getPaddingLeft();
		int bottomPadding = view.getPaddingBottom();
		int rightPadding = view.getPaddingRight();

		// Get dateTime of this cell
		DateTime dateTime = this.datetimeList.get(position);

		// Set color of the dates in previous / next month
		if (dateTime.getMonth() != month)
		{
			cellView.setTextColor(resources.getColor(R.color.blue_light));
		}

		Date selectedDateConverted = CalendarHelper.convertDateTimeToDate(dateTime);

		if (selectedDate != null && selectedDateConverted.equals(selectedDate))
		{
			view.findViewById(R.id.calendar_iv).setVisibility(View.VISIBLE);
		}
		else
		{
			// Customize for today
			if (dateTime.equals(getToday()) && selectedDate == null)
			{
				view.findViewById(R.id.calendar_iv).setVisibility(View.VISIBLE);
				selected = view;

			}
			else
			{
				view.findViewById(R.id.calendar_iv).setVisibility(View.INVISIBLE);
			}

		}

		// Set text
		cellView.setText("" + dateTime.getDay());

		// Set custom color if required
		setCustomResources(dateTime, view, cellView);

		// This is to recover the padding
		cellView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
	}

	@Override
	public int getCount()
	{
		return this.datetimeList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return datetimeList.get(position);
	}

	@Override
	public long getItemId(int arg0)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View mainView;
		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mainView = inflater.inflate(R.layout.normal_date_cell, null);

		}
		else
		{
			mainView = (View) convertView;
		}

		TextView cellView = (TextView) mainView.findViewById(R.id.calendar_tv);
		customizeTextView(position, cellView, mainView);

		return mainView;
	}

}
