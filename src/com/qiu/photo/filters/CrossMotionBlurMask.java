package com.qiu.photo.filters;

public class CrossMotionBlurMask extends MatrixMask {
	public CrossMotionBlurMask()
	{
		filterWidth = 9;
		filterHeight = 9;
		
		filter = new double[][] 
		{
			{1, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 1, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 1, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 1, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 1, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 1, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 1, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 1, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 1}
		};

		factor = 1.0 / 9.0;
		bias = 0.0;
		
	}
}
