package com.mwf.analyze.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import static android.R.id.list;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final String TAG = this.getClass().getName();
    private FamousInfoModel famousInfoModel;
    /**
     * 存入sql的列表
     */
    private ArrayList<String> sqlList = new ArrayList<>();
    ArrayList<String> singleList = new ArrayList<>();
    private boolean quit = false;
    private int total;
    private int count;
    Thread thread;
    @BindView(R.id.edit_keyword)
    EditText mEditKeyWord;
    @BindView(R.id.button_search)
    Button mSerachBtn;
    @BindView(R.id.button_file)
    Button button_file;
    @BindView(R.id.button_sql)
    Button button_sql;
    @BindView(R.id.button_excel)
    Button button_excel;
    @BindView(R.id.txt_content)
    TextView mTxtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        famousInfoModel = FamousInfoModel.getInstance(this);
        getSupportActionBar().hide();
    }

    @Override
    @OnClick({R.id.button_search, R.id.button_file, R.id.button_sql, R.id.button_excel})
    public void onClick(View view) {
        if (view.getId() == R.id.button_search) {
            parseEditext();
        } else if (view.getId() == R.id.button_file) {
            parseFile();
        } else if (view.getId() == R.id.button_sql) {
            parseFileToSQL();
        } else if (view.getId() == R.id.button_excel) {

        }
    }

    /**
     * 初始化请求参数
     *
     * @param text
     * @return
     */
    private FamousInfoReq initParams(String text) {
        FamousInfoReq mFamousInfoReq = null;
        mFamousInfoReq = new FamousInfoReq();
        mFamousInfoReq.api_key = Constant.APIKEY;
        mFamousInfoReq.text = text;
        mFamousInfoReq.pattern = "ws";
        mFamousInfoReq.format = "plain";
        return mFamousInfoReq;
    }

    /**
     * 解析输入框内容
     */
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

    private void openFile(DialogSelectionListener listener) {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        FilePickerDialog dialog = new FilePickerDialog(MainActivity.this, properties);
        dialog.setTitle("选择一个文件");
        dialog.setDialogSelectionListener(listener);
        dialog.show();
    }

    /**
     * 选择文件解析
     */
    private void parseFile() {
        DialogSelectionListener listener = new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                String string = FileUtils.readTxtFile(files[0]);
                if (!TextUtils.isEmpty(string)) {
                    Log.e(TAG, string);
                    mEditKeyWord.setText(string);

                    famousInfoModel.queryLookUp(initParams(string)).enqueue(new Callback<String>() {
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
        };
        openFile(listener);
    }

    /**
     * 文件解析并保存到sql
     */
    private void parseFileToSQL() {

        //每组文字的长度
        final int arrayLength = 80;

        //请求间隔时间
        final long requestTime = 10000;

        DialogSelectionListener listener = new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                String string = FileUtils.readTxtFile(files[0]);
                if (!TextUtils.isEmpty(string)) {

                    double total = string.length();
                    double d = new Double(Math.round(total / arrayLength));//这样为保持3位
                    int size = (int) (Math.ceil(d));
                    Log.e(TAG, "总长度=" + total);
                    Log.e(TAG, "d==" + d);
                    Log.e(TAG, "分组=" + size);
                    singleList = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        String singleString = "";
                        if (i == size - 1) {
                            singleString = string.substring(i * arrayLength, string.length());
                        } else {
                            singleString = string.substring(i * arrayLength, (i + 1) * arrayLength);
                        }
                        singleList.add(singleString);
                    }
                    for (int i = 0; i < singleList.size(); i++) {
                        Log.e(TAG, "第" + i + "组");
                        Log.e(TAG, singleList.get(i));
                    }
                    //启动一个线程间隔
                    startThread(singleList.size(), requestTime);
                }
            }
        };
        openFile(listener);
    }


    /**
     * 初始化一个定时器
     */
    private void startThread(int t, final long time) {
        quit = false;
        count = 0;
        total = t;
        mTxtContent.setText("");
        thread = new Thread() {
            @Override
            public void run() {
                while (!quit) {
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timeHandler.sendEmptyMessage(0);
                }
            }
        };
        thread.start();
    }

    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "发送消息：" + count);
            if (count >= singleList.size()) {
                Log.i(TAG, "发送完毕！");
                quit = true;
                thread.interrupted();
            } else {
                Log.i(TAG, "解析中：" + count);

                String requstStr = singleList.get(count);
//                String text = mTxtContent.getText().toString()+requstStr;
//                mTxtContent.setText(text);

                parse(requstStr);
                count++;

            }
            super.handleMessage(msg);
        }
    };

    /**
     * 解析输入框内容
     */
    private void parse(String text) {

        famousInfoModel.queryLookUp(initParams(text)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body().trim();
                CloudResultPlainParse parse = new CloudResultPlainParse();
                ArrayList<String> list = parse.parse(result);
                sqlList.addAll(list);

                for (int i = 0; i <sqlList.size() ; i++) {
                    String text = mTxtContent.getText().toString()+sqlList.get(i);
                    mTxtContent.setText(text);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}
