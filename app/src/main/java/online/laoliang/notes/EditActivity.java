package online.laoliang.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

import online.laoliang.MainActivity;
import online.laoliang.simpleweather.R;
import online.laoliang.simpleweather.activity.WeatherActivity;

public class EditActivity extends Activity {
    private String weather;
    private Button back;
    private TextView title_text;
    private TextView tv_weather;
    private EditText et_mood;
    private Button btn_save;
    private TextView tv_now;
    private EditText et_title;
    private EditText et_content;
    //记录当前编辑的笔记对象（用于比对是否改变）
    private NoteInfo currentNote;
    //记录是否是插入状态 （因为也可能是更新（编辑）状态）
    private boolean insertFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_edit);

        SharedPreferences prefs = getSharedPreferences("data_setting", MODE_PRIVATE);
        int chooseTheme = prefs.getInt("choose_theme", 1);
        switch (chooseTheme) {
            case 1:
                setTheme(R.style.AppTheme1);
                break;
            case 2:
                setTheme(R.style.AppTheme2);
                break;
            case 3:
                setTheme(R.style.AppTheme3);
                break;
            case 4:
                setTheme(R.style.AppTheme4);
                break;
            case 5:
                setTheme(R.style.AppTheme5);
                break;
            case 6:
                setTheme(R.style.AppTheme6);
                break;
            case 7:
                setTheme(R.style.AppTheme7);
                break;
            case 8:
                setTheme(R.style.AppTheme8);
                break;
            case 9:
                setTheme(R.style.AppTheme9);
                break;
            case 10:
                setTheme(R.style.AppTheme10);
                break;
            case 11:
                setTheme(R.style.AppTheme11);
                break;
            case 12:
                setTheme(R.style.AppTheme12);
                break;
        }
        weather = WeatherActivity.commonWeather;
        initView();
        setListener();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //主界面点击ListView中的一个Item跳转时
        if(bundle != null){
            currentNote = (NoteInfo) bundle.getSerializable("noteInfo");
            et_title.setText(currentNote.getTitle());
            et_content.setText(currentNote.getContent());
            et_mood.setText(currentNote.getMood());
            tv_weather.setText(currentNote.getWeather());
            tv_now.setText(currentNote.getDate());
            insertFlag = false;
        }
    }
    //初始化视图
    private void initView(){
        tv_weather = findViewById(R.id.tv_weather);
        et_mood = findViewById(R.id.edit_mood);
        tv_weather.setText(weather);

        back = (Button) findViewById(R.id.edit_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditActivity.this, NotesActivity.class);
                startActivity(intent);
                EditActivity.this.finish();
            }
        });
        title_text = (TextView) findViewById(R.id.edit_title_text);
        title_text.setText("日记编辑");


        btn_save = findViewById(R.id.btn_save);
        tv_now = findViewById(R.id.tv_now);
        et_content = findViewById(R.id.edit_content);
        et_title = findViewById(R.id.edit_title);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        tv_now.setText(sdf.format(date));

    }
    //设置监听器
    private void setListener(){
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( et_title.getText().toString().equals("") ||
                        et_content.getText().toString().equals("") ||
                et_mood.getText().toString().equals("")){
                    Toast.makeText(EditActivity.this,"内容为空！",Toast.LENGTH_LONG).show();
                }else {
                    saveNote();
                    Intent intent = new Intent(EditActivity.this, NotesActivity.class);
                    startActivity(intent);
                    EditActivity.this.finish();
                }
            }
        });
    }
    //保存笔记到数据库 判断是新建还是更新
    private void saveNote(){
        NoteDataBaseHelper dbHelper = NotesActivity.getDbHelper();

        ContentValues values = new ContentValues();
        values.put(Note.title,et_title.getText().toString());
        values.put(Note.content,et_content.getText().toString());
        values.put(Note.time,tv_now.getText().toString());
        values.put(Note.mood,et_mood.getText().toString());
        values.put(Note.weather,tv_weather.getText().toString());
        if (insertFlag){
            Note.insertNote(dbHelper,values);
        }else{
            Note.updateNote(dbHelper,Integer.parseInt(currentNote.getId()),values);
        }
    }
    //重写手机上返回按键处理函数，如果更改了提示保存 否则直接返回主界面
    @Override
    public void onBackPressed() {
        boolean display = false;
        if (insertFlag){
            if( !et_title.getText().toString().equals("") &&
                    !et_content.getText().toString().equals("")){
                display = true;
            }
        }else{
            if( !et_title.getText().toString().equals(currentNote.getTitle()) ||
                    !et_content.getText().toString().equals(currentNote.getContent())){
                display = true;
            }
        }
        if (display){
            String title = "警告：";
            new AlertDialog.Builder(EditActivity.this)
                    .setTitle(title)
                    .setMessage("是否保存当前内容?")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveNote();
                            Toast.makeText(EditActivity.this,"保存成功！",Toast.LENGTH_LONG).show();
                            //更新当前Note对象的值 防止选择保存后按返回仍显示此警告对话框
                            currentNote.setTitle(et_title.getText().toString());
                            currentNote.setContent(et_content.getText().toString());
                            currentNote.setWeather(tv_weather.getText().toString());
                            currentNote.setMood(et_mood.getText().toString());
                            currentNote.setDate(tv_now.getText().toString());
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(EditActivity.this, NotesActivity.class);
                            intent.putExtra("weather",weather);
                            startActivity(intent);
                            EditActivity.this.finish();
                        }
                    }).create().show();
        }else{
            Intent intent = new Intent(EditActivity.this, NotesActivity.class);
            intent.putExtra("weather",weather);
            startActivity(intent);
            EditActivity.this.finish();
        }
    }
}

