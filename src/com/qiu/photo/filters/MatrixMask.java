package com.qiu.photo.filters;

public abstract class MatrixMask {
	
	protected int filterWidth; 
	protected int filterHeight;	
	protected double filter[][];	       
	protected double factor; 
	protected double bias; 
	
	public int getFilterWidth() {
		return filterWidth;
	}
	public int getFilterHeight() {
		return filterHeight;
	}
	public double[][] getFilter() {
		return filter;
	}
	public double getFactor() {
		return factor;
	}
	public double getBias() {
		return bias;
	}

}
