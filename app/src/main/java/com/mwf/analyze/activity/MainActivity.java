package com.mwf.analyze.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mwf.analyze.Constant;
import com.mwf.analyze.R;
import com.mwf.analyze.bean.CloudResultPlainParse;
import com.mwf.analyze.bean.FamousInfoReq;
import com.mwf.analyze.model.FamousInfoModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final String TAG = this.getClass().getName();
    private FamousInfoModel famousInfoModel;

    @BindView(R.id.edit_keyword)
    EditText mEditKeyWord;
    @BindView(R.id.button_search)
    Button mSerachBtn;
    @BindView(R.id.button_file)
    Button button_file;
    @BindView(R.id.txt_content)
    TextView mTxtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        famousInfoModel = FamousInfoModel.getInstance(this);
    }

    @Override
    @OnClick({R.id.button_search, R.id.button_file})
    public void onClick(View view) {
        if (view.getId() == R.id.button_search) {
            parseEditext();
        } else if (view.getId() == R.id.button_file) {
            parseEditext();
        }
    }

    private FamousInfoReq initParams() {
        FamousInfoReq mFamousInfoReq = null;
        mFamousInfoReq = new FamousInfoReq();
        mFamousInfoReq.api_key = Constant.APIKEY;
        mFamousInfoReq.text = mEditKeyWord.getText().toString();
        mFamousInfoReq.pattern = "ws";
        mFamousInfoReq.format = "plain";
        return mFamousInfoReq;
    }

    private void parseEditext() {
        famousInfoModel.queryLookUp(initParams()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body().trim();
                CloudResultPlainParse parse = new CloudResultPlainParse();
                ArrayList<String> list = parse.parse(result);
//                    Log.e(TAG, "result====" + result);
                String string = "";
                for (String tmp : list) {
                    string += tmp + "\n";
                    Log.e(TAG, tmp.toString());
                }
                mTxtContent.setText(string);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 选择文件解析
     */
    private void parseFile() {

    }
}
