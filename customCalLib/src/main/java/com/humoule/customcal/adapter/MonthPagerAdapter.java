package com.humoule.customcal.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.humoule.customcal.CustomCalFragment;
import com.humoule.customcal.DateGridFragment;

public class MonthPagerAdapter extends FragmentPagerAdapter
{

	private ArrayList<DateGridFragment> fragments;

	public ArrayList<DateGridFragment> getFragments()
	{
		if (fragments == null)
		{
			fragments = new ArrayList<DateGridFragment>();
			for (int i = 0; i < getCount(); i++)
			{
				fragments.add(new DateGridFragment());
			}
		}
		return fragments;
	}

	public void setFragments(ArrayList<DateGridFragment> fragments)
	{
		this.fragments = fragments;
	}

	public MonthPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position)
	{
		DateGridFragment fragment = getFragments().get(position);
		return fragment;
	}

	@Override
	public int getCount()
	{
		return CustomCalFragment.NUMBER_OF_PAGES;
	}

}
