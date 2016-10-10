package com.qiu.photo.filters;

public class SlightBlurMask extends MatrixMask {
	public SlightBlurMask()
	{
		filterWidth = 3; 
		filterHeight = 3; 
		
		filter =  new double[][]
		{ 
		     {0.0, 0.2,  0.0}, 
		     {0.2, 0.2,  0.2}, 
		     {0.0, 0.2,  0.0} 
		}; 
		       
		factor = 1.0; 
		bias = 0.0;		
	}
}
