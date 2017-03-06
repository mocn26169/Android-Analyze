package com.mwf.analyze.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.mwf.analyze.Constant;
import com.mwf.analyze.R;
import com.mwf.analyze.bean.CloudResultPlainParse;
import com.mwf.analyze.bean.FamousInfoReq;
import com.mwf.analyze.model.FamousInfoModel;
import com.mwf.analyze.utils.FileUtils;

import java.io.File;
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
            parseFile();
        }
    }

    private FamousInfoReq initParams(String text) {
        FamousInfoReq mFamousInfoReq = null;
        mFamousInfoReq = new FamousInfoReq();
        mFamousInfoReq.api_key = Constant.APIKEY;
        mFamousInfoReq.text = text;
        mFamousInfoReq.pattern = "ws";
        mFamousInfoReq.format = "plain";
        return mFamousInfoReq;
    }

    private void parseEditext() {
        String text = mEditKeyWord.getText().toString();

        famousInfoModel.queryLookUp(initParams(text)).enqueue(new Callback<String>() {
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
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        FilePickerDialog dialog = new FilePickerDialog(MainActivity.this, properties);
        dialog.setTitle("Select a File");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                String string = FileUtils.readTxtFile(files[0]);
                if (!TextUtils.isEmpty(string)) {
                    Log.e(TAG, string);
                    mEditKeyWord.setText(string);
                    parseF(string);
                }

            }
        });
        dialog.show();
    }

    private void parseF(String text) {
        famousInfoModel.queryLookUp(initParams(text)).enqueue(new Callback<String>() {
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
}
