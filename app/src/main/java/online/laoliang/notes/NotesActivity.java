package online.laoliang.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import online.laoliang.simpleweather.R;
import online.laoliang.simpleweather.activity.WeatherActivity;

public class NotesActivity extends Activity {
    private Button back;
    private TextView title_text;

    // 搜索相关声明
    private String searchCondition;
    private Spinner search_spinner;
    private Button search_btn;
    private EditText search_input;
    private Map<String,String> searchMap;
    private Button search_fanwei;
    private EditText start_time;
    private EditText end_time;

    private ListView noteListView;
    private Button addBtn;
    private List<NoteInfo> noteList = new ArrayList<>();
    private ListAdapter mListAdapter;

    private static NoteDataBaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setContentView(R.layout.activity_edit);
        dbHelper = new NoteDataBaseHelper(this,"MyNote.db",null,1);

        //先测试添加一条数据
        /*ContentValues values = new ContentValues();
        values.put(Note.title,"测试笔记");
        values.put(Note.content,"以下为测试内容！！！");
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        values.put(Note.time,sdf.format(date));
        Note.insertNote(dbHelper,values);*/

        initView();
        setListener();

        //跳转回主界面 刷新列表
        Intent intent = getIntent();
        if (intent != null){
            getNoteList();
            mListAdapter.refreshDataSet();
        }
    }
    //初始化视图
    private void initView(){
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(NotesActivity.this, WeatherActivity.class);
                //startActivity(intent);
                NotesActivity.this.finish();
            }
        });
        title_text = (TextView) findViewById(R.id.title_text);
        title_text.setText("日记");

        search_fanwei = findViewById(R.id.btn_search_fanwei);
        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        search_fanwei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start_time.getText().toString().equals("") || end_time.getText().toString().equals("")) {
                    Toast.makeText(NotesActivity.this,"请输入起始时间！",Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO 开始查询
                searchFanWeiNote(Note.time,start_time.getText().toString(),end_time.getText().toString());
            }
        });

        searchMap = new HashMap<>();
        searchMap.put("按主题",Note.title);
        searchMap.put("按内容",Note.content);
        searchMap.put("按天气",Note.weather);
        searchMap.put("按日期",Note.time);
        searchMap.put("按心情",Note.mood);

        searchCondition = "按主题";
        search_spinner = findViewById(R.id.search_spinner);
        search_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                searchCondition = (String) search_spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        search_btn = findViewById(R.id.btn_search);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search_input.getText().toString().trim().equals("")) {
                    Toast.makeText(NotesActivity.this,"请输入查询信息！",Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO 开始查询
                searchNote(searchMap.get(searchCondition),search_input.getText().toString());
            }
        });
        search_input = findViewById(R.id.search_input);

        noteListView = findViewById(R.id.note_list);
        addBtn = findViewById(R.id.btn_add);
        //获取noteList
        getNoteList();
        mListAdapter = new ListAdapter(NotesActivity.this,noteList);
        noteListView.setAdapter(mListAdapter);
    }

    // 开始查询
    private void searchNote(String condition,String input) {
        noteList.clear();
        Cursor allNotes = Note.getSearchNotes(dbHelper,condition,input);
        for (allNotes.moveToFirst(); !allNotes.isAfterLast(); allNotes.moveToNext()){
            NoteInfo noteInfo = new NoteInfo();
            noteInfo.setId(allNotes.getString(allNotes.getColumnIndex(Note._id)));
            noteInfo.setTitle(allNotes.getString(allNotes.getColumnIndex(Note.title)));
            noteInfo.setContent(allNotes.getString(allNotes.getColumnIndex(Note.content)));
            noteInfo.setDate(allNotes.getString(allNotes.getColumnIndex(Note.time)));
            noteInfo.setMood(allNotes.getString(allNotes.getColumnIndex(Note.mood)));
            noteInfo.setWeather(allNotes.getString(allNotes.getColumnIndex(Note.weather)));
            noteList.add(noteInfo);
        }
        mListAdapter = new ListAdapter(NotesActivity.this,noteList);
        noteListView.setAdapter(mListAdapter);
    }

    // 时间范围查询
    private void searchFanWeiNote(String condition,String start,String end) {
        noteList.clear();
        Cursor allNotes = Note.getSearchFanWeiNotes(dbHelper,condition,start,end);
        for (allNotes.moveToFirst(); !allNotes.isAfterLast(); allNotes.moveToNext()){
            NoteInfo noteInfo = new NoteInfo();
            noteInfo.setId(allNotes.getString(allNotes.getColumnIndex(Note._id)));
            noteInfo.setTitle(allNotes.getString(allNotes.getColumnIndex(Note.title)));
            noteInfo.setContent(allNotes.getString(allNotes.getColumnIndex(Note.content)));
            noteInfo.setDate(allNotes.getString(allNotes.getColumnIndex(Note.time)));
            noteInfo.setMood(allNotes.getString(allNotes.getColumnIndex(Note.mood)));
            noteInfo.setWeather(allNotes.getString(allNotes.getColumnIndex(Note.weather)));
            noteList.add(noteInfo);
        }
        mListAdapter = new ListAdapter(NotesActivity.this,noteList);
        noteListView.setAdapter(mListAdapter);
    }


    //设置监听器
    private void setListener(){
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotesActivity.this,EditActivity.class);
                //intent.putExtra("weather",weather);
                startActivity(intent);
                NotesActivity.this.finish();
            }
        });

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoteInfo noteInfo = noteList.get(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("noteInfo",(Serializable)noteInfo);
                intent.putExtras(bundle);
                intent.setClass(NotesActivity.this, EditActivity.class);
                startActivity(intent);
                NotesActivity.this.finish();
            }
        });

        noteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final NoteInfo noteInfo = noteList.get(position);
                String title = "警告";
                new AlertDialog.Builder(NotesActivity.this)
                        .setTitle(title)
                        .setMessage("确定要删除吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Note.deleteNote(dbHelper,Integer.parseInt(noteInfo.getId()));
                                noteList.remove(position);
                                mListAdapter.refreshDataSet();
                                Toast.makeText(NotesActivity.this,"删除成功！",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
                return true;
            }
        });
    }
    //从数据库中读取所有笔记 封装成List<NoteInfo>
    private void getNoteList(){
        noteList.clear();
        Cursor allNotes = Note.getAllNotes(dbHelper);
        for (allNotes.moveToFirst(); !allNotes.isAfterLast(); allNotes.moveToNext()){
            NoteInfo noteInfo = new NoteInfo();
            noteInfo.setId(allNotes.getString(allNotes.getColumnIndex(Note._id)));
            noteInfo.setTitle(allNotes.getString(allNotes.getColumnIndex(Note.title)));
            noteInfo.setContent(allNotes.getString(allNotes.getColumnIndex(Note.content)));
            noteInfo.setDate(allNotes.getString(allNotes.getColumnIndex(Note.time)));
            noteInfo.setMood(allNotes.getString(allNotes.getColumnIndex(Note.mood)));
            noteInfo.setWeather(allNotes.getString(allNotes.getColumnIndex(Note.weather)));
            noteList.add(noteInfo);
        }
    }
    //重写返回按钮处理事件
    @Override
    public void onBackPressed() {
        //Intent intent = new Intent(NotesActivity.this, WeatherActivity.class);
        //startActivity(intent);
        NotesActivity.this.finish();
    }
    //给其他类提供dbHelper
    public static NoteDataBaseHelper getDbHelper() {
        return dbHelper;
    }
}

