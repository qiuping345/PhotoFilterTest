package com.qiu.photo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.qiu.photo.filters.CrossMotionBlurMask;
import com.qiu.photo.filters.EdgeMask;
import com.qiu.photo.filters.EmbossMask;
import com.qiu.photo.filters.HorizontalEdgeMask;
import com.qiu.photo.filters.MatrixMask;
import com.qiu.photo.filters.MediumBlurMask;
import com.qiu.photo.filters.SharpenMask;
import com.qiu.photo.filters.SlightBlurMask;
import com.qiu.photo.filters.Soble;
import com.qiu.photo.filters.VerticalEdgeMask;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.opengl.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;

public class FilterMask {
	
	//黑白效果
	public static float[] grayFilter = new float[]{0.3086f, 0.6094f, 0.0820f, 0, 0, 
												   0.3086f, 0.6094f, 0.0820f, 0, 0,
												   0.3086f, 0.6094f, 0.0820f, 0, 0,
												   0,       0,       0,       1, 0};
	//颜色值翻转
	public static float[] invertFilter = new float[]{-1,  0,   0,   0,   255,
													 0,   -1,  0,   0,   255,
													 0,   0,   -1,  0,   255,
													 0,   0,   0,   1,   0};
	
	//红绿值交换
	public static float[] rgSwapFilter = new float[]{0, 1, 0, 0, 0,
													 1, 0, 0, 0, 0,
													 0, 0, 1, 0, 0,
													 0, 0, 0, 1, 0};
	
	//红蓝值交换
	public static float[] rbSwapFilter = new float[]{0, 0, 1, 0, 0,
												     0, 1, 0, 0, 0,
													 1, 0, 0, 0, 0,
													 0, 0, 0, 1, 0};
		
	//蓝绿值交换
	public static float[] gbSwapFilter = new float[]{1, 0, 0, 0, 0,
													 0, 0, 1, 0, 0,
													 0, 1, 0, 0, 0,
													 0, 0, 0, 1, 0};
	
	public static float[] brightFilter = new float[]{3.4f, 0,  0,  0,  0,
													 0,  3.4f, 0,  0,  0,
													 0,  0,  3.4f, 0,  0,
													 0,  0,    0,  1,  0};
	
			
	
	
	private int imageWidth = 0; 
	private int imageHeight = 0; 
	int result[][];	
	public static Soble soble = new Soble();
	public static SlightBlurMask slightBlurMask = new SlightBlurMask();
	public static MediumBlurMask mediumBlurMask = new MediumBlurMask();
	public static CrossMotionBlurMask crossBlurMask = new CrossMotionBlurMask();
	public static HorizontalEdgeMask horizontalEdgeMask = new HorizontalEdgeMask();
	public static VerticalEdgeMask verticalEdgeMask = new VerticalEdgeMask();
	public static EdgeMask edgeMask = new EdgeMask();
	public static SharpenMask sharpenMask = new SharpenMask();
	public static EmbossMask embossMask = new EmbossMask();
	
	public FilterMask()
	{
		//do nothing.
	}
	

	public void applyConvolutionFilter(Bitmap bmp, MatrixMask currMask) 
	{ 
		if(null == bmp || null == currMask)
		{
			return;
		}
		
		imageWidth = bmp.getWidth();
		imageHeight = bmp.getHeight();
		
		result = new int[imageWidth][imageHeight];
		for(int i = 0; i < imageWidth; i++)
		{
			result[i] = new int[imageHeight];
		}

		//apply the filter 
	    for(int x = 0; x < imageWidth; x++) 
	    {
		    for(int y = 0; y < imageHeight; y++) 
		    { 
		        double red = 0.0, green = 0.0, blue = 0.0; 
		         
		        //multiply every value of the filter with corresponding image pixel 
		        for(int filterX = 0; filterX < currMask.getFilterWidth(); filterX++) 
		        {
			        for(int filterY = 0; filterY < currMask.getFilterHeight(); filterY++) 
			        { 
			            int imageX = (x - currMask.getFilterWidth() / 2 + filterX + imageWidth) % imageWidth; 
			            int imageY = (y - currMask.getFilterHeight() / 2 + filterY + imageHeight) % imageHeight; 
			            int pixVal = bmp.getPixel(imageX, imageY);
			            double filterVal = currMask.getFilter()[filterX][filterY];
			            red += Color.red(pixVal) * filterVal; 
			            green += Color.green(pixVal) * filterVal; 
			            blue += Color.blue(pixVal) * filterVal; 
			        } 
		        }
		        //truncate values smaller than zero and larger than 255 
		        int redVal = calcColorChannel(red, currMask.getFactor(), currMask.getBias()); 
		        int greenVal = calcColorChannel(green, currMask.getFactor(), currMask.getBias()); 
		        int blueVal = calcColorChannel(blue, currMask.getFactor(), currMask.getBias());
		        result[x][y] = Color.rgb(redVal, greenVal, blueVal);
		    }
	    }
	    //draw the result buffer to the screen 
	    for(int x = 0; x < imageWidth; x++)
	    {
		    for(int y = 0; y < imageHeight; y++) 
		    { 
		        bmp.setPixel(x, y, result[x][y]); 
		    } 
	    }
	}
	
	public void applyGrayFilter(Bitmap bmp) 
	{ 
		if(null == bmp)
		{
			return;
		}
		
		imageWidth = bmp.getWidth();
		imageHeight = bmp.getHeight();
		
		//apply the filter 
	    for(int x = 0; x < imageWidth; x++) 
	    {
		    for(int y = 0; y < imageHeight; y++) 
		    { 
		    	int pixVal = bmp.getPixel(x, y);
		    	
		    	int rVal = Color.red(pixVal);
		    	int gVal = Color.green(pixVal);
		    	int bVal = Color.blue(pixVal);
		    	int averageVal = (int)(0.3086 * rVal + 0.6094 * gVal + 0.0820 * bVal);
		    	int colorVal = Color.argb(Color.alpha(pixVal), averageVal, averageVal, averageVal);
		    	bmp.setPixel(x, y, colorVal);
		    }
	    }
	}
	
	public void applyRevert(Bitmap bmp)
	{
		if(null == bmp)
		{
			return;
		}
		
		imageWidth = bmp.getWidth();
		imageHeight = bmp.getHeight();
		
		//apply the filter 
	    for(int x = 0; x < imageWidth; x++) 
	    {
		    for(int y = 0; y < imageHeight; y++) 
		    { 
		    	int pixVal = bmp.getPixel(x, y);
		    	
		    	int rVal = Color.red(pixVal);
		    	int gVal = Color.green(pixVal);
		    	int bVal = Color.blue(pixVal);
		    	int colorVal = Color.argb(Color.alpha(pixVal), (255 - rVal), (255-gVal), (255-bVal));
		    	bmp.setPixel(x, y, colorVal);
		    }
	    }
	}
	
	public void applySaturation(Bitmap bmp, float saturationArg)
	{
	    if(null == bmp)
		{
			return;
		}
	    
		/*
		float mat[4][4] = {
		        a,      b,      c,      0.0,
		        d,      e,      f,      0.0,
		        g,      h,      i,      0.0,
		        0.0,    0.0,    0.0,    1.0,
		    };
		    
		a = (1.0-s)*rwgt + s;
	    b = (1.0-s)*rwgt;
	    c = (1.0-s)*rwgt;
	    d = (1.0-s)*gwgt;
	    e = (1.0-s)*gwgt + s;
	    f = (1.0-s)*gwgt;
	    g = (1.0-s)*bwgt;
	    h = (1.0-s)*bwgt;
	    i = (1.0-s)*bwgt + s;
		*/
		
	    double a = (1.0 - saturationArg) * 0.3086 + saturationArg;
	    double b = (1.0 - saturationArg) * 0.3086;
	    double c = b;
	    double d = (1.0 - saturationArg) * 0.6094;
	    double e = (1.0 - saturationArg) * 0.6094 + saturationArg;
	    double f = d;
	    double g = (1.0 - saturationArg) * 0.0820;
	    double h = g;
	    double i = (1.0 - saturationArg) * 0.0820 + saturationArg;
		
		imageWidth = bmp.getWidth();
		imageHeight = bmp.getHeight();
		
		//apply the filter 
	    for(int x = 0; x < imageWidth; x++) 
	    {
		    for(int y = 0; y < imageHeight; y++) 
		    { 
		    	int pixVal = bmp.getPixel(x, y);
		    	
		    	int rVal = Color.red(pixVal);
		    	int gVal = Color.green(pixVal);
		    	int bVal = Color.blue(pixVal);
		    	int red =   Math.max(0, Math.min(255, (int)(a * rVal + d * gVal + g * bVal)));
		    	int green = Math.max(0, Math.min(255, (int)(b * rVal + e * gVal + h * bVal)));
		    	int blue =  Math.max(0, Math.min(255, (int)(c * rVal + f * gVal + i * bVal)));
		    	int colorVal = Color.argb(Color.alpha(pixVal), red, green, blue);
		    	bmp.setPixel(x, y, colorVal);
		    }
	    }
	}
	
	public void applyBrightness(Bitmap bmp, float redPortion, float greenPortion, float bluePortion)
	{
		if(null == bmp)
		{
			return;
		}
		
		imageWidth = bmp.getWidth();
		imageHeight = bmp.getHeight();
		
		//apply the filter 
	    for(int x = 0; x < imageWidth; x++) 
	    {
		    for(int y = 0; y < imageHeight; y++) 
		    { 
		    	int pixVal = bmp.getPixel(x, y);
		    	
		    	int rVal = Color.red(pixVal);
		    	int gVal = Color.green(pixVal);
		    	int bVal = Color.blue(pixVal);
		    	
//		    	int color = Color.rgb((int)(rVal * redPortion), 
//						  (int)(gVal * greenPortion), 
//						  (int)(bVal * bluePortion));
		    	
		    	int color = Color.rgb(Math.min(255, (int)(rVal * redPortion)), 
		    						  Math.min(255, (int)(gVal * greenPortion)), 
		    						  Math.min(255, (int)(bVal * bluePortion)));
		    	bmp.setPixel(x, y, color);
		    }
	    }
	}
	
	public void applyContrast(Bitmap bmp, int contrastArg)
	{
		if(null == bmp)
		{
			return;
		}
		
		if (contrastArg < -100 || contrastArg > 100)
		{
			return;
		}
		
		double contrast = (100.0 + contrastArg) / 100.0;
		contrast *= contrast;
		
		imageWidth = bmp.getWidth();
		imageHeight = bmp.getHeight();
		
		//apply the filter 
	    for(int x = 0; x < imageWidth; x++) 
	    {
		    for(int y = 0; y < imageHeight; y++) 
		    { 
		    	int pixVal = bmp.getPixel(x, y);
		    	
		    	int rVal = Color.red(pixVal);
		    	int gVal = Color.green(pixVal);
		    	int bVal = Color.blue(pixVal);
		    	int color = Color.rgb(contrastVal(rVal, contrast), 
					    			contrastVal(gVal, contrast), 
					    			contrastVal(bVal, contrast));
		    	bmp.setPixel(x, y, color);
		    }
	    }		
	}
	
	private int contrastVal(int value, double contrast)
	{
		double pixel = value / 255.0;
		pixel -= 0.5;
		pixel *= contrast;
		pixel += 0.5;
		pixel *= 255;
		if (pixel < 0) 
		{
			pixel = 0;
		}
		if (pixel > 255) 
		{
			pixel = 255;
		}
		return (int)pixel;
	}
	
	public static Bitmap layertest(Bitmap src, Bitmap mask)
	{
		
		int radius = (int)(Math.min(src.getHeight(), src.getWidth()) * 0.90) / 2;
		
		ColorMatrixColorFilter brightMatrix = getScaleBrightFilter(1.0f, 0.95f, 1.5f);
		ColorMatrixColorFilter contrastMatrix = getContrastFilter(1f, 0.20f, -0.50f);
		ColorMatrixColorFilter saturationMatrix = getSaturationFilter(1.2f);
		BlurMaskFilter blurFilter = new BlurMaskFilter(7, BlurMaskFilter.Blur.NORMAL);
		EmbossMaskFilter embossFilter = new EmbossMaskFilter(new float[]{2, 4, 8}, 0.72f, 8f, 3f);
		//return layer(src, brightMatrix, null, null, mask);
		//return layer(src, contrastMatrix, null, null, mask);
		//return layer(src, saturationMatrix, null, null, mask);
		//return layer(src, brightMatrix, contrastMatrix, null, mask);
		ColorMatrixColorFilter[] filters = new ColorMatrixColorFilter[]{ contrastMatrix, saturationMatrix, brightMatrix};
		//return layer(src, filters, 	mask, new PorterDuffXfermode(Mode.SCREEN));
		return layer(src, filters, 	null, null);
		
		//return layer(src, null, null, null);
		
		//return layer(src, filters, null, null);

		//return layer(src, contrastMatrix, brightMatrix, null, mask);
		//return layer(src, brightMatrix, saturationMatrix, null, mask);
		//return layer(src, contrastMatrix, saturationMatrix, null, mask);
		//return layer(src, contrastMatrix, saturationMatrix, null, mask);
		//return layer(src, getSaturationFilter(1.02f), null, null, blurFilter, mask);
		//return layer(src, contrastMatrix, null, null, embossFilter, mask);

		//return layer(src, brightMatrix, contrastMatrix, saturationMatrix, mask);
		//return layer(src, contrastMatrix, brightMatrix, saturationMatrix, mask);		
	}
	
	
	public static Bitmap layer(Bitmap src, ColorMatrixColorFilter[] filters, 
								Bitmap mask, Xfermode mode)  
    {  
		// 创建一个新的和SRC长度宽度一样的位图  
        Bitmap newb = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(newb);  
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        
        //fiter处理
        for(int i = 0; filters != null && i < filters.length; i++)
        {
        	if(filters[i] != null)
        	{
    	        paint.setColorFilter(filters[i]);
    	        canvas.drawBitmap(newb, 0, 0, paint);// 在 0，0坐标开始画入原图片src    	        
        	}
        }
        
        //上层mask处理
        if(mask != null)
        {
    		paint.setXfermode(mode);
    		canvas.drawBitmap(mask, new Rect(0, 0, mask.getWidth(), mask.getHeight()), 
    								new Rect(0, 0, newb.getWidth(), newb.getHeight()), paint);
        }
        
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newb;  
    }
	
	/**
	 * 计算rgb中的一个r或g或b的值。
	 * @param val
	 * @return
	 */
	private int calcColorChannel(double val, double factor, double bias)
	{
		return Math.min(Math.max((int)(factor * val + bias), 0), 255);
	}
	
	public static ColorMatrixColorFilter getSaturationFilter(float saturation)
	{
		float[] defaultArr = new float[]{1, 0, 0, 0, 0,
										 0, 1, 0, 0, 0,
										 0, 0, 1, 0, 0,
										 0, 0, 0, 1, 0};
		ColorMatrix matrix = new ColorMatrix(defaultArr);
		matrix.setSaturation(saturation);
		ColorMatrixColorFilter result = new ColorMatrixColorFilter(matrix);
		return result;
	}
	
	public static ColorMatrixColorFilter getScaleBrightFilter(float rScale, float gScale, float bScale)
	{
		float[] arr = new float[]{rScale, 0, 0, 0, 0,
						   0, gScale, 0, 0, 0,
						   0, 0, bScale, 0, 0,
						   0, 0, 0, 1, 0 };
		return new ColorMatrixColorFilter(arr);
	}
	

	
	public static ColorMatrixColorFilter getContrastFilter(float rContrast, float gContrast, float bContrast)
	{
		final int dimen = 3;
		float[] contrasts = new float[]{rContrast, gContrast, bContrast};
		float[] scales = new float[dimen];
		float[] translates = new float[dimen];
		
		for(int i = 0; i < dimen; i++)
		{
			scales[i] = contrasts[i] + 1.f;
			scales[i] *= scales[i];
	        translates[i] = (-.5f * scales[i] + .5f) * 255.f;
		}
		
        float[] arr =  new float[]{
        		scales[0], 0, 0, 0, translates[0], 
        		0, scales[1], 0, 0, translates[1], 
        		0, 0, scales[2], 0, translates[2], 
        		0, 0, 0, 1, 0};
        
        return new ColorMatrixColorFilter(arr);
	}
	
	
	
	public static void dumpArray( float[] arr)
	{
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<arr.length; i++)
		{
			sb.append(arr[i] + ",");
		}
		android.util.Log.d("FilterMask", sb.toString());
	}
	
	private static void save(Bitmap bmp)
	{
        File f = new File("/mnt/sdcard/phototext.jpg");
        try
        {
        	if(!f.exists())
        	{
        		f.createNewFile();
        	}
        	
            FileOutputStream fos = new FileOutputStream(f);
          //  imgDraw.getBitmap().
            bmp.compress(CompressFormat.JPEG, 100, fos);

        }
        catch(IOException ioe)
        {
        	ioe.printStackTrace();
        }
	}
}


