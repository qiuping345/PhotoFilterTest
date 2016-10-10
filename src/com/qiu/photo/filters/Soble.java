package com.qiu.photo.filters;

public class Soble extends MatrixMask {
	public Soble()
	{
		filterWidth = 3; 
		filterHeight = 3; 
		
		filter =  new double[][]
		{ 
		     {-1, -2, -1}, 
		     {0,  0,  0}, 
		     {1,  2,  1} 
		}; 
		       
		factor = 1.0; 
		bias = 0.0;		
	}

}
