package com.humoule.customcal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.humoule.customcal.adapter.CustomCalGridAdapter;

public class DateGridFragment extends Fragment
{
	private GridView gridView;
	private CustomCalGridAdapter gridAdapter;
	private OnItemClickListener onItemClickListener;
	private OnItemLongClickListener onItemLongClickListener;
	private int gridViewRes = 0;

	public OnItemClickListener getOnItemClickListener()
	{
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener)
	{
		this.onItemClickListener = onItemClickListener;
	}

	public OnItemLongClickListener getOnItemLongClickListener()
	{
		return onItemLongClickListener;
	}

	public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener)
	{
		this.onItemLongClickListener = onItemLongClickListener;
	}

	public CustomCalGridAdapter getGridAdapter()
	{
		return gridAdapter;
	}

	public void setGridAdapter(CustomCalGridAdapter gridAdapter)
	{
		this.gridAdapter = gridAdapter;
	}

	public GridView getGridView()
	{
		return gridView;
	}

	public void setGridViewRes(int gridViewRes)
	{
		this.gridViewRes = gridViewRes;
	}

	private void setupGridView()
	{
		if (gridAdapter != null)
		{
			gridView.setAdapter(gridAdapter);
		}

		if (onItemClickListener != null)
		{
			gridView.setOnItemClickListener(onItemClickListener);
		}
		if (onItemLongClickListener != null)
		{
			gridView.setOnItemLongClickListener(onItemLongClickListener);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if (gridViewRes == 0)
		{
			gridViewRes = R.layout.date_grid_fragment;
		}

		if (gridView == null)
		{
			gridView = (GridView) inflater.inflate(gridViewRes, container, false);
			setupGridView();
		}
		else
		{
			ViewGroup parent = (ViewGroup) gridView.getParent();
			if (parent != null)
			{
				parent.removeView(gridView);
			}
		}

		return gridView;
	}

}
