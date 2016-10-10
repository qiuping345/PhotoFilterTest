package com.qiu.photo.filters;

public class HorizontalEdgeMask extends MatrixMask {
	public HorizontalEdgeMask()
	{
		filterWidth = 5;
		filterHeight = 5;
		
		filter = new double[][]
		{
		     {0,  0,  0,  0,  0},
			 {0,  0,  0,  0,  0},
			 {-1, -1, 2,  0,  0},
			 {0,  0,  0,  0,  0},
			 {0,  0,  0,  0,  0}
		};

		factor = 1.0;
		bias = 0.0;
	}
}
