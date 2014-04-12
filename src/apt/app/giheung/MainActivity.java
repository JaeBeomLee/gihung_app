package apt.app.giheung;

//IO관련
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.tools.UnitConv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//급식표 출력
//바코드 제작
//안드로이드
//시간표
//바코드 이미지 출력

public class MainActivity extends Activity {

	private SQLiteDatabase db;// 시간표 데이터베이스

	private TextView timetable;// 시간표

	private TextView hakban;// 90도 기울어진 학년 반
	private TextView irum1; // 이름
	private TextView yoil;
	private TextView memo2;
	private TextView settingPlease;
	private TextView bab_day;
	private Button yesterday_btn;
	private CheckBox check;

	private boolean hakbanChange;
	
	
	Calendar calendar = new GregorianCalendar();

	Date date = new Date();
	@SuppressWarnings("deprecation")
	int totalYear = date.getYear();
	// calendar.get(calendar.YEAR);
	@SuppressWarnings("deprecation")
	int totalMonth = date.getMonth() + 1;
	// calendar.get(calendar.MONTH)+1;
	@SuppressWarnings("deprecation")
	int totalDate = date.getDate();
	// calendar.get(calendar.DATE);
	@SuppressWarnings("deprecation")
	int totalDay = date.getDay();
	// calendar.get(Calendar.DAY_OF_WEEK)-1;

	String lunchYear = Integer.toString(totalYear + 1900);
	String lunchMonth = Integer.toString(totalMonth);
	String lunchDate = Integer.toString(totalDate);

	String DinnerYear = Integer.toString(totalYear + 1900);
	String DinnerMonth = Integer.toString(totalMonth);
	String DinnerDate = Integer.toString(totalDate);

	ImageView image; // 바코드 이미지

	ImageButton imageButton;
	Context context;

	LayoutInflater inflater; // 다이얼
	final static int DIALOG_1 = 0;
	final static int DIALOG_2 = 1;
	final static int DIALOG_3 = 2;
	final static int DIALOG_4 = 3;

	String ban = null;
	String irum = null;

	String ban2 = null;
	String name2 = null;
	String barContent = null;

	String link = "https://play.google.com/store/apps/details?id=apt.app.giheung";
	String version = null;

	DBHelper dbHelper = new DBHelper(context); // 데이터베이스 클래스 소환

	static int Day;
	private String lunchResult;
	private String dinnerResult;

	private SlidingDrawer mDialerDrawer;

	protected String versionResult;

	// 메인에 보여줄 것

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		// Version();
		Day = totalDay;
		menu();
		SlideMenu();
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		timetable = (TextView) findViewById(R.id.timetable);
		imageButton = (ImageButton) findViewById(R.id.barcodebutton);
		memo2 = (TextView) findViewById(R.id.memo2);
		memo2.setText(sharedPreferences.getString("MEMO", "Memo"));
		check = (CheckBox) findViewById(R.id.check);
		bab_day = (TextView) findViewById(R.id.bab_day);

		LoadPreference();
		if (ban2 == "") {
			// 새로 설치할 때
			Dial(DIALOG_1);
		} else {
			// 신학기 설정

			hakbanChange = LoadPreferenceBool();
			if (hakbanChange == true) {

			} else if (hakbanChange == false) {

				hakbanchange();
			}
			if (hakbanChange == false) {
				Dial(DIALOG_4);// 새해
			}

			Cursor();
			LoadPreference();
			createBarcodeWithBarcode4j(barContent);
		}

		time(); // 시간 출력
		lunch();// 점심 출력
		dinner();// 저녁 출력
		// SurveyDialog();

	}

	protected void hakbanchange() {
		if (totalMonth >= 3 && totalDate >= 1) {
			hakbanChange = false;
			SavePreference("hakbanChange", hakbanChange);

		}
	}

	// 설정창 다시 설정을 위해 사용
	@SuppressWarnings("deprecation")
	protected void Dial(int a) {
		showDialog(a);
	}

	// 점심 시간표
	protected void lunch() {
		((TextView) findViewById(R.id.lunchtxt)).setText("불러오는 중..");
		new Thread() {
			@SuppressWarnings("deprecation")
			public void run() {
				Document document;
				try {

					String url = "http://www.giheung.hs.kr/?_page=45&_action=view&_view=view&"
							+ "yy="
							+ lunchYear
							+ "&mm="
							+ lunchMonth
							+ "&dd="
							+ lunchDate;
					document = (Document) Jsoup.connect(url).get();
					Elements dd = document.select("dd");

					if (dd.size() > 0) {
						lunchResult = dd.get(0).text() + "\n";

					}
				} catch (IOException e) {
					lunchResult = "인터넷을 연결 해 주세요";
					e.printStackTrace();
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						((TextView) findViewById(R.id.lunchtxt))
								.setText("점심 : \n" + lunchResult);
						if (lunchResult == "") {
							((TextView) findViewById(R.id.lunchtxt))
									.setText("오늘은 점심이 없습니다.");
						} else if (lunchResult == "\n") {
							((TextView) findViewById(R.id.lunchtxt))
									.setText("오늘은 점심이 없습니다.");
						}

					}
				});

			}
		}.start();

	}

	// 저녁 시간표
	protected void dinner() {
		final TextView t = (TextView) findViewById(R.id.dinnertxt);
		t.setTypeface(Typeface.createFromAsset(getAssets(),
				"NanumBarunGothic.ttf"));
		t.setText("불러오는 중..");
		new Thread() {
			@SuppressWarnings("deprecation")
			public void run() {
				Document document;
				try {
					Date date = new Date();

					String url = "http://www.giheung.hs.kr/?_page=45&_action=view&_view=view&yy="
							+ DinnerYear
							+ "&mm="
							+ DinnerMonth
							+ "&dd="
							+ DinnerDate;

					document = (Document) Jsoup.connect(url).get();
					Elements dd = document.select("dd");

					if (dd.size() > 0) {
						dinnerResult = dd.get(1).text();

					}
				} catch (IOException e) {

					dinnerResult = "인터넷을 연결 해 주세요";
					e.printStackTrace();
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						t.setText("석식 : \n" + dinnerResult);
						if (dinnerResult == "") {
							t.setText("오늘은 석식이 없습니다.");
						} else if (dinnerResult == "\n") {
							t.setText("오늘은 석식이 없습니다.");
						}

					}
				});

			}
		}.start();

	}

	public void yesterday_bab(View v) {
		if (v.getId() == R.id.Yesterday_bab) {
			totalDate = totalDate - 1;
			lunchDate = Integer.toString(totalDate);
			DinnerDate = Integer.toString(totalDate);

			if (totalDate == 0) {
				totalMonth = totalMonth - 1;
				if (totalMonth == 0) {
					totalYear = totalYear - 1;
					lunchYear = Integer.toString(totalYear + 1900);
					DinnerYear = Integer.toString(totalYear + 1900);
					totalMonth = 12;
				}
				lunchMonth = Integer.toString(totalMonth);
				DinnerMonth = Integer.toString(totalMonth);
				if (totalMonth == 1 || totalMonth == 3 || totalMonth == 5
						|| totalMonth == 7 || totalMonth == 8
						|| totalMonth == 10 || totalMonth == 12) {
					totalDate = 31;
				} else if (totalMonth == 4 || totalMonth == 6
						|| totalMonth == 9 || totalMonth == 11) {
					totalDate = 30;
				} else if (totalMonth == 2) {

					if (totalYear % 4 == 0) {
						if (totalYear % 100 == 0) {
							if (totalYear % 400 == 0) {
								totalDate = 29;
							}
							totalDate = 28;
						}
						totalDate = 29;
					} else {
						totalDate = 28;
					}
				}
			}

			bab_day.setText(totalMonth + "." + totalDate);
			lunch();
			dinner();

		}
	}

	public void tomorrow_bab(View v) {
		if (v.getId() == R.id.tomorrow_bab) {
			totalDate = totalDate + 1;

			if (totalMonth == 1 || totalMonth == 3 || totalMonth == 5
					|| totalMonth == 7 || totalMonth == 8 || totalMonth == 10
					|| totalMonth == 12) {
				if (totalDate == 32) {
					totalDate = 1;
					totalMonth = totalMonth + 1;
					lunchMonth = Integer.toString(totalMonth);
					DinnerMonth = Integer.toString(totalMonth);

				}
			} else if (totalMonth == 4 || totalMonth == 6 || totalMonth == 9
					|| totalMonth == 11) {
				if (totalDate == 31) {
					totalDate = 1;
					totalMonth = totalMonth + 1;
					lunchMonth = Integer.toString(totalMonth);
					DinnerMonth = Integer.toString(totalMonth);
				}

			} else if (totalMonth == 2) {
				if (totalYear % 4 == 0) {
					if (totalYear % 100 == 0) {
						if (totalYear % 400 == 0) {

							if (totalDate == 30) {
								totalDate = 1;
								totalMonth = totalMonth + 1;
								lunchMonth = Integer.toString(totalMonth);
								DinnerMonth = Integer.toString(totalMonth);
							}
						}

						if (totalDate == 29) {
							totalDate = 1;
							totalMonth = totalMonth + 1;
							lunchMonth = Integer.toString(totalMonth);
							DinnerMonth = Integer.toString(totalMonth);
						}
					}
					if (totalDate == 30) {
						totalDate = 1;
						totalMonth = totalMonth + 1;
						lunchMonth = Integer.toString(totalMonth);
						DinnerMonth = Integer.toString(totalMonth);
					}
				} else {
					if (totalDate == 29) {
						totalDate = 1;
						totalMonth = totalMonth + 1;
						lunchMonth = Integer.toString(totalMonth);
						DinnerMonth = Integer.toString(totalMonth);
					}
				}

			}

			if (totalMonth == 13) {
				totalMonth = 1;
				totalYear = totalYear + 1;
				lunchMonth = Integer.toString(totalMonth);
				DinnerMonth = Integer.toString(totalMonth);
				lunchYear = Integer.toString(totalYear + 1900);
				DinnerYear = Integer.toString(totalYear + 1900);
			}
			lunchDate = Integer.toString(totalDate);
			DinnerDate = Integer.toString(totalDate);

			bab_day.setText(totalMonth + "." + totalDate);
			lunch();
			dinner();
		}
	}

	public void today_bab(View v) {
		if (v.getId() == R.id.today_bab) {
			totalDate = date.getDate();
			totalMonth = date.getMonth() + 1;
			totalYear = date.getYear();

			lunchMonth = Integer.toString(totalMonth);
			DinnerMonth = Integer.toString(totalMonth);
			lunchYear = Integer.toString(totalYear + 1900);
			DinnerYear = Integer.toString(totalYear + 1900);
			lunchDate = Integer.toString(totalDate);
			DinnerDate = Integer.toString(totalDate);

			bab_day.setText(totalMonth + "." + totalDate);
			lunch();
			dinner();
		}
	}

	// 시간
	@SuppressWarnings("deprecation")
	protected void time() {

		TextView Time = (TextView) findViewById(R.id.time);
		String DayStr = "";

		int month = totalMonth;
		int Date = totalDate;
		int day = totalDay;

		switch (day) {
		case 1:
			DayStr = "Mon";
			break;
		case 2:
			DayStr = "Tue";
			break;
		case 3:
			DayStr = "Wed";
			break;
		case 4:
			DayStr = "Thu";
			break;
		case 5:
			DayStr = "Fri";
			break;
		case 6:
			DayStr = "Sat";
			break;
		case 0:
			DayStr = "Sun";
			break;
		}

		String Month = Integer.toString(month);

		Time.setText(Month + "/ " + Date + "/ " + DayStr);
		bab_day.setText(totalMonth + "." + totalDate);
	}

	// 학년반이름 설정
	private void DialogSetting() {
		final LinearLayout linear = (LinearLayout) inflater.inflate(
				R.layout.settext, null);

		AlertDialog alt_bld = new AlertDialog.Builder(this)
				.setTitle("설정")
				.setView(linear)
				.setCancelable(false)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						EditText editText = (EditText) linear
								.findViewById(R.id.editban);
						EditText editText2 = (EditText) linear
								.findViewById(R.id.editname);
						irum = editText2.getText().toString();
						ban = editText.getText().toString();

						if (ban.equals("") || irum.equals("")) {

						} else {
							SavePreference("ban", ban);
							SavePreference("name", irum);
							hakbanChange = true;
							SavePreference("hakbanChange", hakbanChange);
							Cursor();

						}

					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						android.os.Process.killProcess(android.os.Process
								.myPid());

					}
				}).show();

		android.view.WindowManager.LayoutParams params = alt_bld.getWindow()
				.getAttributes();
		params.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
		params.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
		alt_bld.getWindow().setAttributes(params);
	}

	// 메모 설정
	private void DialogMemo() {
		final LinearLayout MemoLayout = (LinearLayout) inflater.inflate(
				R.layout.memo, null);
		AlertDialog alt_bld = new AlertDialog.Builder(this).setTitle("메모")
				.setView(MemoLayout).setCancelable(false)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						EditText setMemo = (EditText) MemoLayout
								.findViewById(R.id.setmemo);
						String getmemo = setMemo.getText().toString();
						editor.putString("MEMO", getmemo);
						editor.commit();
						String saveMemo = sharedPreferences.getString("MEMO",
								"");
						memo2.setTypeface(Typeface.createFromAsset(getAssets(),
								"NanumBarunGothic.ttf"));
						memo2.setText(saveMemo);

					}
				}).setNegativeButton("취소", null).show();
		android.view.WindowManager.LayoutParams params = alt_bld.getWindow()
				.getAttributes();
		params.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
		params.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
		alt_bld.getWindow().setAttributes(params);
	}

	// 새해 학년반 설정
	private void DialogNewYearSetting() {
		final LinearLayout NewYearSetting = (LinearLayout) inflater.inflate(
				R.layout.new_year_of_set_text, null);

		AlertDialog alt_bld = new AlertDialog.Builder(this)
				.setTitle("신학기 설정")
				.setView(NewYearSetting)
				.setCancelable(false)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						EditText editText = (EditText) NewYearSetting
								.findViewById(R.id.editban_new_year);
						if (editText.getText().toString() == null) {
							showDialog(DIALOG_4);
						}

						ban = editText.getText().toString();

						if (ban == "") {

						} else {
							SavePreference("ban", ban);
							hakbanChange = true;
							SavePreference("hakbanChange", hakbanChange);
							Cursor();

						}
					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						android.os.Process.killProcess(android.os.Process
								.myPid());

					}
				}).show();

		android.view.WindowManager.LayoutParams params = alt_bld.getWindow()
				.getAttributes();
		params.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
		params.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
		alt_bld.getWindow().setAttributes(params);
	}

	// 다이얼로그 설정창
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_1:
			DialogSetting();
			break;
		case DIALOG_3:
			DialogMemo();
			break;
		case DIALOG_4:
			DialogNewYearSetting();
			break;
		}

		return null;

	}

	// 학교 시간표
	@SuppressWarnings("deprecation")
	public void Cursor() {
		yoil = (TextView) findViewById(R.id.yoil);
		String HakBan = "";
		LoadPreference();

		Day = totalDay;

		if (Integer.parseInt(ban2.substring(1, 3)) < 10) {
			HakBan = ban2.substring(0, 1) + "-" + ban2.substring(2, 3);
		} else if (Integer.parseInt(ban2.substring(1, 3)) >= 10) {
			HakBan = ban2.substring(0, 1) + "-" + ban2.substring(1, 3);
		}

		hakban = (TextView) findViewById(R.id.hakban);
		hakban.setText(HakBan);
		irum1 = (TextView) findViewById(R.id.irum);
		irum1.setTypeface(Typeface.createFromAsset(getAssets(),
				"NanumBarunGothic.ttf"));
		irum1.setText(name2);

		DBHelper dbHelper = new DBHelper(this);

		String StrDay = null;
		String yoil2 = null;
		if (Day == 1) {
			StrDay = "monday";
			yoil2 = "월";
		} else if (Day == 2) {
			StrDay = "tuesday";
			yoil2 = "화";
		} else if (Day == 3) {
			StrDay = "wednesday";
			yoil2 = "수";
		} else if (Day == 4) {
			StrDay = "thursday";
			yoil2 = "목";
		} else if (Day == 5) {
			StrDay = "friday";
			yoil2 = "금";
		} else {
			StrDay = "monday";
			yoil2 = "월";
		}
		try {

			db = dbHelper.getReadableDatabase();
			String sql = "Select ban" + ban2 + " from " + StrDay;
			android.database.Cursor c = db.rawQuery(sql, null);

			if (c.getCount() == 7) {
				c.moveToFirst();
			}
			String num;
			timetable.setText("");
			yoil.setText(yoil2);

			do {
				int i = c.getPosition() + 1;
				String j = Integer.toString(i);
				num = c.getString(0);
				timetable.append(j + " " + num + "\n");
			} while (c.moveToNext());
			timetable.setTextColor(0xFFFFFFFF);
			c.close();
			dbHelper.close();
			db.close();

		} catch (SQLException e) {
			Log.e("", e.getMessage());
		}

	}

	// 어제 시간표
	public void Back(View view) {
		if (view.getId() == R.id.back) {
			yoil = (TextView) findViewById(R.id.yoil);
			if (Day == 0) {
				Day = 5;

			}

			Day = Day - 1;

			if (Day == 0) {
				Day = 5;

			}
			DBHelper dbHelper = new DBHelper(this);

			String StrDay = null;
			String yoil2 = null;
			if (Day == 1) {
				StrDay = "monday";
				yoil2 = "월";
			} else if (Day == 2) {
				StrDay = "tuesday";
				yoil2 = "화";
			} else if (Day == 3) {
				StrDay = "wednesday";
				yoil2 = "수";
			} else if (Day == 4) {
				StrDay = "thursday";
				yoil2 = "목";
			} else if (Day == 5) {
				StrDay = "friday";
				yoil2 = "금";
			} else {
				StrDay = "monday";
				yoil2 = "월";
			}
			try {

				db = dbHelper.getReadableDatabase();
				String sql = "Select ban" + ban2 + " from " + StrDay;
				android.database.Cursor c = db.rawQuery(sql, null);

				if (c.getCount() == 7) {
					c.moveToFirst();
				}
				String num;
				timetable.setText("");
				yoil.setText(yoil2);
				do {
					int i = c.getPosition() + 1;
					String j = Integer.toString(i);
					num = c.getString(0);
					timetable.append(j + " " + num + "\n");
				} while (c.moveToNext());
				timetable.setTextColor(0xFFFFFFFF);
				c.close();
				dbHelper.close();
				db.close();

			} catch (SQLException e) {
				Log.e("", e.getMessage());

			}

		}
	}

	// 내일 시간표
	public void Forward(View view) {
		if (view.getId() == R.id.forward) {
			if (Day == 0 || Day == 6 || Day == 7) {
				Day = 1;

			}
			Day = Day + 1;

			DBHelper dbHelper = new DBHelper(this);

			String StrDay = null;
			String yoil2 = null;

			if (Day == 1) {
				StrDay = "monday";
				yoil2 = "월";
			} else if (Day == 2) {
				StrDay = "tuesday";
				yoil2 = "화";
			} else if (Day == 3) {
				StrDay = "wednesday";
				yoil2 = "수";
			} else if (Day == 4) {
				StrDay = "thursday";
				yoil2 = "목";
			} else if (Day == 5) {
				StrDay = "friday";
				yoil2 = "금";
			} else {
				StrDay = "monday";
				yoil2 = "월";
			}
			try {

				db = dbHelper.getReadableDatabase();
				String sql = "Select ban" + ban2 + " from " + StrDay;
				android.database.Cursor c = db.rawQuery(sql, null);

				if (c.getCount() == 7) {
					c.moveToFirst();
				}
				String num;
				timetable.setText("");
				yoil.setText(yoil2);

				do {
					int i = c.getPosition() + 1;
					String j = Integer.toString(i);
					num = c.getString(0);
					timetable.append(j + " " + num + "\n");
				} while (c.moveToNext());
				timetable.setTextColor(0xFFFFFFFF);
				c.close();
				dbHelper.close();
				db.close();
			} catch (SQLException e) {
				Log.e("", e.getMessage());

			}

		}
	}

	// 오늘 시간표 리셋
	public void Reset(View view) {
		if (view.getId() == R.id.reset) {
			Day = totalDay;

			DBHelper dbHelper = new DBHelper(this);

			String Day = null;
			String yoil2 = null;
			if (MainActivity.Day == 1) {
				Day = "monday";
				yoil2 = "월";
			} else if (MainActivity.Day == 2) {
				Day = "tuesday";
				yoil2 = "화";
			} else if (MainActivity.Day == 3) {
				Day = "wednesday";
				yoil2 = "수";
			} else if (MainActivity.Day == 4) {
				Day = "thursday";
				yoil2 = "목";
			} else if (MainActivity.Day == 5) {
				Day = "friday";
				yoil2 = "금";
			} else {
				Day = "monday";
				yoil2 = "월";
			}
			try {

				db = dbHelper.getReadableDatabase();
				String sql = "Select ban" + ban2 + " from " + Day;
				android.database.Cursor c = db.rawQuery(sql, null);

				if (c.getCount() == 7) {
					c.moveToFirst();
				}
				String num;
				timetable.setText("");
				yoil.setText(yoil2);
				do {
					int i = c.getPosition() + 1;
					String j = Integer.toString(i);
					num = c.getString(0);
					timetable.append(j + " " + num + "\n");
				} while (c.moveToNext());
				timetable.setTextColor(0xFFFFFFFF);

			} catch (SQLException e) {
				Log.e("", e.getMessage());

			}

		}
	}

	public void Linear(View view) {
		if (view.getId() == R.id.Memo) {
			showDialog(DIALOG_3);
		}
	}

	// 바코드 설정
	private void createBarcodeWithBarcode4j(String s) {
		try {
			final int dpi = 70;
			Code39Bean bean = new Code39Bean();
			bean.setModuleWidth(UnitConv.in2mm(1f / dpi));
			bean.setWideFactor(3);
			bean.doQuietZone(true);
			bean.setQuietZone(1.1);

			File outputFile = new File(
					Environment.getExternalStorageDirectory()
							+ "/Android/barcode.png");

			OutputStream out = new FileOutputStream(outputFile);
			try {
				MyBarcodeCanvasProvider canvas = new MyBarcodeCanvasProvider(
						out, 0);
				bean.generateBarcode(canvas, s);
				displayBarcode(canvas.getBarcodeBitmap());

				canvas.finish();
			} finally {
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 바코드 제작
	protected void displayBarcode(Bitmap bm) {
		imageButton.setImageBitmap(bm);
		settingPlease = (TextView) findViewById(R.id.settingplease);
		settingPlease.setText("");
		try {
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/Android/barcode.png");
			FileOutputStream ostream = new FileOutputStream(file);
			bm.compress(CompressFormat.PNG, 100, ostream);
			ostream.close();
		} catch (Exception e) {

		}
	}

	// 문자값저장
	public void SavePreference(String key, String value) {
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	// 참값 저장
	public void SavePreference(String key, Boolean value) {
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();

	}

	// 저장 불러오기
	private void LoadPreference() {
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		String strName = sharedPreferences.getString("name", "");
		String strBan = sharedPreferences.getString("ban", "");
		String strBarcode = sharedPreferences.getString("barcode", "");
		name2 = strName;
		ban2 = strBan;
		barContent = strBarcode;
	}

	// 참값 불러오기
	private boolean LoadPreferenceBool() {
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		boolean boolCheck = sharedPreferences.getBoolean("hakbanChange", false);
		return boolCheck;
	}

	public void BarcodeClick(View v) {
		if (v.getId() == R.id.scan_Button) {
			IntentIntegrator scaninIntegrator = new IntentIntegrator(this);
			scaninIntegrator.initiateScan();
		}
	}

	public void BarcodeClick2(View v) {
		if (v.getId() == R.id.scan_Button2) {
			IntentIntegrator scaninIntegrator = new IntentIntegrator(this);
			scaninIntegrator.initiateScan();
		}
	}

	public void BarcodeSetting(View v) {
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		String barcodeNum = sharedPreferences.getString("barcode", "");
		if (v.getId() == R.id.barcodebutton) {

			// 이미지 버튼이 이미지를 적용했는지 유무 판단이 관건
			if (barcodeNum == "") {
				IntentIntegrator scaninIntegrator = new IntentIntegrator(this);
				scaninIntegrator.initiateScan();
			} else {
				Intent intent = new Intent(MainActivity.this, HugeBarcode.class);
				startActivity(intent);
			}

		}
	}

	public void WholeLayout(View v) {
		mDialerDrawer.animateClose();
	}

	public void resetlunch(View v) {
		if (v.getId() == R.id.resetlunch) {
			lunch();
		}

	}

	@SuppressWarnings("unused")
	public void resetdinner(View v) {
		if (v.getId() == R.id.resetdinner) {
			dinner();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);

		if (scanningResult != null) {
			String scanContent = scanningResult.getContents();
			SavePreference("barcode", scanContent);
			LoadPreference();
			createBarcodeWithBarcode4j(barContent);

		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}

	}

	@SuppressWarnings("deprecation")
	public void SlideMenu() {

		mDialerDrawer = (SlidingDrawer) findViewById(R.id.slide);
		mDialerDrawer
				.setOnDrawerScrollListener(new SlidingDrawer.OnDrawerScrollListener() {
					@Override
					public void onScrollStarted() {
						Log.v("SlidingDrawerActivity", "onScrollStarted()");
					}

					@Override
					public void onScrollEnded() {
						Log.v("SlidingDrawerActivity", "onScrollEnded()");
					}
				});

		mDialerDrawer
				.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
					@Override
					public void onDrawerOpened() {
						// edit.clearListSelection();
						Log.v("SlidingDrawerActivity", "onDrawerOpened()");
					}
				});

		mDialerDrawer
				.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
					@Override
					public void onDrawerClosed() {
						Log.v("SlidingDrawerActivity", "onDrawerClosed()");
					}
				});
	}

	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { // 백 버튼
			if (mDialerDrawer.isOpened()) {

				mDialerDrawer.animateClose();

			} else {
				finish();
			}

		}
		return true;
	}

	public void handle(View v) {
		if (v.getId() == R.id.handlelayout) {
			mDialerDrawer.animateOpen();
		}
	}

	public void menu() {
		final ArrayList<String> list = new ArrayList<String>();
		list.add("메인");
		list.add("제작");
        list.add("학사 일정");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		ListView listView = (ListView) findViewById(R.id.list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 1) {
					Intent intent = new Intent(MainActivity.this, Maker.class);
					startActivity(intent);

					mDialerDrawer.animateClose();
				}else if(position == 2){
                    Intent intent = new Intent(MainActivity.this, haksa.class);
                    startActivity(intent);
                }

			}
		});

	}

	// protected void Version() {
	//
	// ConnectivityManager cManager;
	// NetworkInfo mobile;
	// NetworkInfo wifi;
	//
	// cManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	// mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	// wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	//
	// if(!mobile.isConnected() && !wifi.isConnected())
	// {
	//
	// }else if(mobile.isConnected() || wifi.isConnected()){
	// new Thread() {
	// @SuppressWarnings("deprecation")
	// public void run() {
	// Document document;
	// try
	// {
	// Context context = getBaseContext();
	// PackageInfo i =
	// context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	// version ="현재 버전 "+ i.versionName;
	//
	// Log.d("tag", "version:"+version);
	// }
	// catch(NameNotFoundException e1)
	// { }
	//
	// try {
	//
	// String url
	// ="https://play.google.com/store/apps/details?id=apt.app.giheung";
	//
	// document = (Document) Jsoup.connect(url).get();
	// Elements div = document.select("div");
	// String str2 = document.toString();
	// str2 =
	// str2.split("<div class=\"content\" itemprop=\"softwareVersion\">")[1];
	// str2 = str2.split("</div>")[0];
	//
	//
	// String now = "현재";
	// String sub2 = versionResult.substring(0, 2);
	// try {
	// if (!(sub2.hashCode() == now.hashCode())){
	// Log.d("tag", "일치안함");
	// versionResult = version;
	// Log.d("tag", "version:"+versionResult);
	// }
	//
	//
	// } catch (PatternSyntaxException e) {
	// System.err.println(e);
	// System.exit(1);
	// }
	// } catch (IOException e) {
	//
	//
	//
	// e.printStackTrace();
	// }
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	//
	// if(version.hashCode() != versionResult.hashCode()){
	// Dial(DIALOG_4);
	// }else if(version.hashCode() == versionResult.hashCode()){
	//
	// }
	//
	//
	// }
	// });
	//
	// }
	// }.start();
	// }
	//
	//
	// }
	// public void Link(View v){
	// if(v.getId() == R.id.upbutton){
	// Uri uri =
	// Uri.parse("https://play.google.com/store/apps/details?id=apt.app.giheung");
	// Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	// intent.addCategory(Intent.CATEGORY_BROWSABLE);
	// startActivity(intent);
	// }
	// }

	// public void SurveyDialog(){
	// SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
	// boolean usecheck = sharedPreferences.getBoolean("surveyboolean", false);
	//
	// if(usecheck == false){
	// showDialog(DIALOG_4);
	// }else if(usecheck == true){
	//
	// }
	//
	// if(check.isChecked()){
	//
	//
	// SharedPreferences.Editor editor = sharedPreferences.edit();
	// editor.putBoolean("surveyboolean", check.isChecked());
	// editor.commit();
	//
	// }
	//
	//
	//
	//
	//
	// }

}
