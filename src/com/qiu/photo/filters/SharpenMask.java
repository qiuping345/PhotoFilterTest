package com.qiu.photo.filters;

public class SharpenMask extends MatrixMask {
	public SharpenMask()
	{
		filterWidth = 3;
		filterHeight = 3;
		
		filter = new double[][]
		{
		     {-1,  -1,  -1},
			 {-1,  9,   -1},
			 {-1,  -1,  -1}
		};

		factor = 1.0;
		bias = 0.0;
		
	}
}
