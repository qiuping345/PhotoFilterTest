package com.qiu.photo;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoFilterTestActivity extends Activity {
	
	private ImageView imgView;
	private Button applyButton;
	private GridView optionsGrid;
	private int currRes = R.drawable.photo2;
	private ProgressDialog pd;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        optionsGrid = (GridView) findViewById(R.id.options_grid);
        optionsGrid.setAdapter(OptionsAdapter);
        optionsGrid.setOnItemClickListener(filtersItemListener);
        
        applyButton = (Button) findViewById(R.id.apply_button);
        applyButton.setOnClickListener(applyFilterListener);
        
        imgView = (ImageView) findViewById(R.id.imgView);
	    setImage();
	    
	    pd = new ProgressDialog(this);
	    pd.setTitle("计算中");
	    pd.setMessage("正在处理，请稍候");
	    pd.setCancelable(false);
	    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    
    }
    
    private void setImage()
    {
        Bitmap img = BitmapFactory.decodeResource(getResources(), currRes);
        final BitmapDrawable imgDraw = new BitmapDrawable(img);
        imgDraw.setFilterBitmap(true);

        imgView.setImageBitmap(img);
        Bitmap lomoMask = BitmapFactory.decodeResource(getResources(), R.drawable.lomo_yellow);
        android.util.Log.d("PhotoFilter", "before filter: [" + System.currentTimeMillis() + "]");
        final Bitmap layeredBmp = FilterMask.layertest(img, lomoMask); //FilterMask.layer(img, lomoMask);
        android.util.Log.d("PhotoFilter", "after filter: [" + System.currentTimeMillis() + "]");
        imgView.setImageBitmap(layeredBmp);
        android.util.Log.d("PhotoFilter", "after setImageBitmap:[" + System.currentTimeMillis() + "]");
        
        uiHandler.postDelayed(new Runnable(){
        	public void run()
        	{
        		save(layeredBmp);
        	}
        }, 3000);
    }
    
    View.OnClickListener applyFilterListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			setImage();
			
		}
	};
	
	
	
	
	private void save(BitmapDrawable imgDraw)
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
            imgDraw.getBitmap().compress(CompressFormat.JPEG, 100, fos);

        }
        catch(IOException ioe)
        {
        	ioe.printStackTrace();
        }

	}
	
	private void save(Bitmap bmp)
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
	
	
	public static final int MSG_SHOW_PROGRESS_DLG = 0;
	public static final int MSG_HIDE_PROGRESS_DLG = 1;
	
	public static final int FILTER_BLACK_WIHTE = 0;
	public static final int FILTER_EMBOSS = 1;
	public static final int FILTER_EDGE = 2;
	public static final int FILTER_SHARPEN = 3;
	public static final int FILTER_BRIGHTNESS_INCREASE = 4;
	public static final int FILTER_BRIGHTNESS_DECREASE = 5;
	public static final int FILTER_CONTRAST_INCREASE = 6;
	public static final int FILTER_CONTRAST_DECREASE = 7;
	public static final int FILTER_REVERT = 8;
	public static final int FILTER_SATUARATION_INCREASE = 9;
	public static final int FILTER_SATUARATION_DECREASE = 10;
	public static final int FILTER_BLUR_SLIGHT = 11;
	public static final int FILTER_BLUR_MEDIUM = 12;
	public static final int FILTER_BLUR_CROSS_MOTION = 13;
	public static final int FILTER_SOBLE = 14;
	
	int[]    optionsArray = new int[]{FILTER_BLACK_WIHTE, FILTER_EMBOSS, FILTER_EDGE, FILTER_SHARPEN, 
			FILTER_BRIGHTNESS_INCREASE, FILTER_BRIGHTNESS_DECREASE, FILTER_CONTRAST_INCREASE,
			FILTER_CONTRAST_DECREASE, FILTER_REVERT, FILTER_SATUARATION_INCREASE, FILTER_SATUARATION_DECREASE,
			FILTER_BLUR_SLIGHT, FILTER_BLUR_MEDIUM, FILTER_BLUR_CROSS_MOTION, FILTER_SOBLE};	
	String[] optionsTags = new String[]{"黑白", "浮雕", "边缘", "锐化", 
			"亮度增", "亮度减", "对比增", "对比减", 
			"胶片", "饱和增", "饱和减",
			"模糊轻", "模糊中", "动态模糊", "soble"};
	int[]    optionsIconId = new int[]{};
	
	BaseAdapter OptionsAdapter = new BaseAdapter()
	{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return optionsArray.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return optionsArray[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			int tagConvert  = -1;
			if(convertView != null)
			{
				tagConvert = ((Integer)convertView.getTop()).intValue();
			}
			
			if(optionsArray[position] == tagConvert)
			{
				return convertView;
			}
			
			TextView aView = new TextView(PhotoFilterTestActivity.this);
			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
															AbsListView.LayoutParams.WRAP_CONTENT, 
															AbsListView.LayoutParams.WRAP_CONTENT);
			aView.setLayoutParams(layoutParams);
			aView.setText(optionsTags[position]);
			aView.setTag(optionsArray[position]);
			aView.setTextColor(Color.BLACK);
			aView.setBackgroundColor(Color.WHITE);
			aView.setGravity(Gravity.CENTER);
			aView.setWidth(120);
			aView.setHeight(40);
			
			return aView;
		}
		
		
	};
	
	float saturationVal = 0.0f;
	
	AdapterView.OnItemClickListener filtersItemListener = new AdapterView.OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		{
			final FilterMask filter = new FilterMask();
			Bitmap bmp = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
			final Bitmap bmpMutable = bmp.copy(Config.ARGB_8888, true);
			
			pdHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_DLG);
			
			final int filterIdx = ((Integer)view.getTag()).intValue();
			new Thread()
			{
				public void run()
				{
					switch(filterIdx)
					{
					case FILTER_BLACK_WIHTE:
						filter.applyGrayFilter(bmpMutable);	
						break;
					case FILTER_EMBOSS:
						filter.applyConvolutionFilter(bmpMutable, FilterMask.embossMask);
						break;
					case FILTER_EDGE:
						filter.applyConvolutionFilter(bmpMutable, FilterMask.edgeMask);
						break;
					case FILTER_SHARPEN:
						filter.applyConvolutionFilter(bmpMutable, FilterMask.sharpenMask);
						break;
					case FILTER_BRIGHTNESS_INCREASE:
						//如果这里几个颜色的比例值不同，会是什么样的效果？
						filter.applyBrightness(bmpMutable, 1.1f, 1.1f, 1.1f);
						break;
					case FILTER_BRIGHTNESS_DECREASE:
						filter.applyBrightness(bmpMutable, 0.91f, 0.91f, 0.91f);
						break;
					case FILTER_CONTRAST_INCREASE:
						filter.applyContrast(bmpMutable, 20);
						break;
					case FILTER_CONTRAST_DECREASE:
						filter.applyContrast(bmpMutable, -20);
						break;
					case FILTER_REVERT:
						filter.applyRevert(bmpMutable);
						break;
					case FILTER_SATUARATION_INCREASE:
						saturationVal += 0.5;
						filter.applySaturation(bmpMutable, 1.1f);//saturationVal);
						break;
					case FILTER_SATUARATION_DECREASE:
						saturationVal -= 0.5;
						filter.applySaturation(bmpMutable, 0.6f);
						break;
					case FILTER_BLUR_SLIGHT:
						filter.applyConvolutionFilter(bmpMutable, FilterMask.slightBlurMask);
						break;
					case FILTER_BLUR_MEDIUM:
						filter.applyConvolutionFilter(bmpMutable, FilterMask.mediumBlurMask);
						break;
					case FILTER_BLUR_CROSS_MOTION:
						filter.applyConvolutionFilter(bmpMutable, FilterMask.crossBlurMask);
						break;
					case FILTER_SOBLE:
						filter.applyConvolutionFilter(bmpMutable, FilterMask.soble);
						break;
					default:
						break;
					}
					
					Message msg = uiHandler.obtainMessage(0, bmpMutable);
					uiHandler.sendMessage(msg);
					pdHandler.sendEmptyMessage(MSG_HIDE_PROGRESS_DLG);

				}
			}.start();
		}
	};
	
	
	final Handler uiHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			Bitmap bmp = (Bitmap)msg.obj;
			imgView.setImageBitmap(bmp);
			imgView.invalidate();		
		}
	};
	
	Handler pdHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case MSG_SHOW_PROGRESS_DLG :
				pd.show();
				break;
			case MSG_HIDE_PROGRESS_DLG:
				pd.dismiss();
				break;
			default:
				break;
			}
		}
	};
	
}