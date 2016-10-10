package com.qiu.photo.filters;

public class EdgeMask extends MatrixMask {
	public EdgeMask()
	{
		filterWidth = 3;
		filterHeight = 3;
		
		filter = new double[][]
		{
		     {-1,  -1,  -1},
			 {-1,  8,   -1},
			 {-1,  -1,  -1}
		};

		factor = 1.0;
		bias = 0.0;
		
	}
}
