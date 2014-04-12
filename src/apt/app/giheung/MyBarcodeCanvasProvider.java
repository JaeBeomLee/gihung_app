package apt.app.giheung;

import java.io.IOException;
import java.io.OutputStream;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.output.eps.EPSCanvasProvider;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class MyBarcodeCanvasProvider extends EPSCanvasProvider
{
	Bitmap bm;
	Canvas canvas;
	Paint paint;
	OutputStream out;
	
	public MyBarcodeCanvasProvider(OutputStream out, int direction) throws IOException
	{
		super(out, direction);
		
		this.out = out;
		initialize();
	}
	
	public Bitmap getBarcodeBitmap()
	{
		return bm;
	}
	
	protected void initialize()
	{
		paint = new Paint();
		paint.setARGB(255, 0, 0, 0);
		paint.setStyle(Paint.Style.FILL);
	}

	@Override
	public void deviceFillRect(double x, double y, double w, double h) 
	{
		float left = (float)(x*14.5);
		float top = (float)(y*14.5);
		float right = (float)(x*14.5 + w*14.5);
		float bottom = (float)(y*14.5 + h*14.5);
		canvas.drawRect(left, top, right, bottom, paint);
	}

	@Override
	public void deviceText(String arg0, double arg1, double arg2, double arg3,
			String arg4, double arg5, TextAlignment arg6) {
		
	}

	@Override
	public void establishDimensions(BarcodeDimension dim) {
		int width = (int) (dim.getWidth()*15);
		int height = (int) (dim.getHeight()*10);
		
		Log.i("ghi", "width : " + width + " , height : " + height);
		
		bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bm);
		
		// paint background
		Paint dp = new Paint();
		dp.setARGB(255, 255, 255, 255);
		dp.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, width, height, dp);
	}

	@Override
	public void finish() throws IOException {
		bm.compress(CompressFormat.PNG, 100, out);
	}

}
