package com.humoule.customcal.callback;

import java.util.Date;

import android.view.View;

public abstract class CustomCalListener
{
	/**
	 * Inform client user has clicked on a date
	 *
	 * @param date
	 * @param view
	 */
	public abstract void onSelectDate(Date date, View view);

	/**
	 * Inform client user has long clicked on a date
	 *
	 * @param date
	 * @param view
	 */
	public void onLongClickDate(Date date, View view)
	{

	}

	/**
	 * Inform client that calendar has changed month
	 *
	 * @param month
	 * @param year
	 */
	public void onChangeMonth(int month, int year)
	{

	}

	/**
	 * Inform client that CustomCalFragment view has been created and views are no longer null. 
	 */
	public void onCustomCalViewCreated()
	{

	}
}
