package apt.app.giheung;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;

public class upDate extends Activity{

	Button upButton;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		upButton = (Button)findViewById(R.id.upbutton);
		
		
	}
	public void Link(View v){
		if(v.getId() == R.id.upbutton){
			Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=apt.app.giheung");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.addCategory(Intent.CATEGORY_BROWSABLE);
			startActivity(intent);
		}
	}
}
