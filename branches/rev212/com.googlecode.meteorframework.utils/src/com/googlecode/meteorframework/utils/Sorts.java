package com.googlecode.meteorframework.utils;

import java.util.Comparator;

public class Sorts 
{
	public static <T> void bubbleSort(T[] items, Comparator<T> comparator) 
	{
		int n= items.length;
		boolean swapped;
		do 
		{
			swapped= false;
			n--;
			for (int i= 0; i < n; i++) 
			{
				if (0 < comparator.compare(items[i], items[i+1])) 
				{
					T t= items[i];
					items[i]= items[i+1];
					items[i+1]= t;
					swapped= true;
				}
			}
		} while (swapped);
	}
	
	
	public static <T> void combSort11(T[] items, Comparator<T> comparator)
	{
		int gap = items.length;

		boolean swapped = false;
		while (1 < gap || swapped)
		{
			// update the gap value for a next comb
			if (gap > 1)
			{
				gap /= 1.3;
				if (gap == 10 || gap == 9)
					gap = 11;
			}
			swapped = false;
			for (int i = 0; i + gap < items.length; i++)
			{
				if (0 < comparator.compare(items[i], items[i + gap]))
				{
					T t = items[i];
					items[i] = items[i + gap];
					items[i + gap] = t;
					swapped = true;
				}
			}
		}
	}

}
