package com.qiu.photo.filters;

public class MediumBlurMask extends MatrixMask {

	public MediumBlurMask()
	{
		filterWidth = 5; 
		filterHeight = 5;
		
		filter = new double[][]
		{
			{0, 0, 1, 0, 0},
			{0, 1, 1, 1, 0},
			{1, 1, 1, 1, 1},
			{0, 1, 1, 1, 0},
			{0, 0, 1, 0, 0},
		};

		factor = 1.0 / 13.0;
		bias = 0.0;
	}
}
