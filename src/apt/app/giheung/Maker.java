package apt.app.giheung;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;



public class Maker extends Activity{

	TextView app_version;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maker);
		
		app_version = (TextView)findViewById(R.id.version);
		final String link = "https://docs.google.com/forms/d/1XRxI6IKJa01ENEARTtfqIdhoeoMLWACn3WLu1LgCRDk/viewform";
		String version = null;
        try 
        {
         Context context = getBaseContext();
         PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
         version = i.versionName;
         Log.d("tag", "version:"+version);
        } 
        catch(NameNotFoundException e) 
        { }
		app_version.setText("v"+version);
		TextView sign = (TextView)findViewById(R.id.link);
		
		sign.setText(Html.fromHtml("<a href = \""+ link+"\"> 불만&의견"));
		sign.setMovementMethod(LinkMovementMethod.getInstance());
		sign.setLinkTextColor(Color.WHITE);
		
	}
	public static void getAppVersion() {  
		 
	
	}  
}
