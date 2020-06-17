package online.laoliang.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import online.laoliang.simpleweather.R;
import online.laoliang.simpleweather.activity.WeatherActivity;

public class LoginFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //mxl文件
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Button login = (Button) getActivity().findViewById(R.id.bt_loginactivity_login);
        final EditText mEtLoginactivityUsername=getActivity().findViewById(R.id.et_loginactivity_username);
        final EditText mEtLoginactivityPassword=getActivity().findViewById(R.id.et_loginactivity_password);
        final CheckBox rememberPass=getActivity().findViewById(R.id.remember_pwd);
        final Boolean isRemember;
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());;
        final SharedPreferences.Editor editor = pref.edit();
        final DBOpenHelper mDBOpenHelper = new DBOpenHelper(getContext());

        isRemember = pref.getBoolean("remember_password",false);
        if (isRemember) {
            String name = pref.getString("name","");
            String password = pref.getString("password","");
            mEtLoginactivityUsername.setText(name);
            mEtLoginactivityPassword.setText(password);
            rememberPass.setChecked(true);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mEtLoginactivityUsername.getText().toString().trim();
                String password = mEtLoginactivityPassword.getText().toString().trim();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {
                    if (rememberPass.isChecked()) {
                        editor.putBoolean("remember_password",true);
                        editor.putString("name",name);
                        editor.putString("password",password);
                    } else {
                        editor.clear();
                    }
                    editor.commit();
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name",name);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().finish();//销毁此Activity
                } else {
                    Toast.makeText(getContext(), "请输入你的用户名或密码", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
