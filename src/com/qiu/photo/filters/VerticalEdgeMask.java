package com.qiu.photo.filters;

public class VerticalEdgeMask extends MatrixMask {
	public VerticalEdgeMask()
	{
		filterWidth = 5;
		filterHeight = 5;
		
		filter = new double[][]
		{
		     {0,  0,  -1, 0,  0},
			 {0,  0,  -1, 0,  0},
			 {0,  0,  4,  0,  0},
			 {0,  0,  -1, 0,  0},
			 {0,  0,  -1, 0,  0}
		};

		factor = 1.0;
		bias = 0.0;
		
		
	}
}
