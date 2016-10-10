package com.qiu.photo.filters;

public class EmbossMask extends MatrixMask {
	public EmbossMask()
	{
		filterWidth = 3;
		filterHeight = 3;
		
		filter = new double[][]
		{
//			    {1, 1, 0},
//				{1, 0, -1},
//				{0, -1, -1}

		    {-1, -1, 0},
			{-1, 0,  1},
			{0,  1,  1}
		};

		factor = 1.0;
		bias = 128.0;
	}
}
