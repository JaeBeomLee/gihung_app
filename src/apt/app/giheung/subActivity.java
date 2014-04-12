package apt.app.giheung;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.TextView;

public class subActivity extends Activity implements Runnable{

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE
				);
		setContentView(R.layout.start);

		(new Thread(this)).start();
		
	}
	
	public void run(){
		try{
			Thread.sleep(1000);
		}catch(Exception e){}
		
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
		
		finish();
	}
}
