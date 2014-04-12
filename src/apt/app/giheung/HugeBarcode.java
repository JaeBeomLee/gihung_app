package apt.app.giheung;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class HugeBarcode extends Activity{

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hugebarcode);
		File file =HugeBarcode.this.getExternalFilesDir(Environment.getExternalStorageDirectory()+"/Android/barcode.png");
		
		Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/Android/barcode.png");
		ImageView HugeButton = (ImageView)findViewById(R.id.hugebarcode);
		HugeButton.setImageBitmap(bitmap);
		HugeButton.setScaleType(ScaleType.FIT_XY);
		
		Matrix matrix = new Matrix();
		
		matrix.postRotate(90);
		
		
	}
}
