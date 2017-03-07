package com.mwf.analyze.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.mwf.analyze.R;
import com.mwf.analyze.bean.AnalyzeBean;
import com.mwf.analyze.dao.AnalyzeDao;
import com.mwf.analyze.services.SqlService;
import com.mwf.analyze.utils.FileUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 1、打开文件
 * 2、开启子线程读取文件数据并分组
 * 3、开启IntentService对每组请求数据
 * 4、对返回数据进行处理
 */
public class AnalyzePoemActivity extends AppCompatActivity implements View.OnClickListener, SqlService.IUpdateUI, SqlService.ILoadFinish {

    public final String TAG = this.getClass().getName();

    /**
     * 存入sql的列表
     */
    private ArrayList<String> sqlList = new ArrayList<>();

    @BindView(R.id.button_terms)
    Button button_terms;
    @BindView(R.id.button_word)
    Button button_word;
    @BindView(R.id.button_excel)
    Button button_excel;
    @BindView(R.id.txt_content)
    TextView mTxtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_poem);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        mTxtContent.setText("");
        AnalyzeDao dao = new AnalyzeDao(AnalyzePoemActivity.this);
        List<AnalyzeBean> list = dao.queryAll(50);
        String text;
        for (int i = 0; i < list.size(); i++) {
            text = mTxtContent.getText().toString();
            text += (list.get(i).getName() + "       ==" + list.get(i).getAmount() + "个\n");
            mTxtContent.setText(text);
        }
    }

    private void openFile(DialogSelectionListener listener) {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        FilePickerDialog dialog = new FilePickerDialog(AnalyzePoemActivity.this, properties);
        dialog.setTitle("选择一个文件");
        dialog.setDialogSelectionListener(listener);
        dialog.show();
    }

    /**
     * 词统计
     */
    private void parseTerms() {
        DialogSelectionListener listener = new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length >= 0) {
                    readTermThread(files[0]);
                    //清空内容
                    mTxtContent.setText("");
                    //清空数据库
//                    new AnalyzeDao(AnalyzePoemActivity.this).deletedAll();
                }
            }
        };
        openFile(listener);
    }

    /**
     * 开启线程解析文件
     *
     * @param path 文件路径
     */
    private void readTermThread(final String path) {
        //每组文字的长度
        final int arrayLength = 50;

        Thread mThread = new Thread() {
            @Override
            public void run() {
                String string = FileUtils.readTxtFile(path);
                if (!TextUtils.isEmpty(string)) {
                    double total = string.length();
                    double d = new Double(Math.round(total / arrayLength));
                    int size = (int) (Math.ceil(d));
                    Log.e(TAG, "总长度=" + total);
                    Log.e(TAG, "d==" + d);
                    Log.e(TAG, "分组=" + size);
                    Intent intent = new Intent(AnalyzePoemActivity.this, SqlService.class);
                    SqlService.setUpdateUI(AnalyzePoemActivity.this);
                    SqlService.setLoadFinish(AnalyzePoemActivity.this);
                    //分组查询文字
                    for (int i = 0; i < size; i++) {
                        String singleString = "";
                        if (i == size - 1) {
                            //最后一组长度为总长度
                            singleString = string.substring(i * arrayLength, string.length());
                        } else {
                            //截取每一组的文字
                            singleString = string.substring(i * arrayLength, (i + 1) * arrayLength);
                        }

                        Log.e(TAG, "第" + i + "组");
                        Log.e(TAG, singleString);
                        intent.putExtra("poemString", singleString);
                        intent.putExtra("number", i);
                        intent.putExtra("total", size);
                        startService(intent);
                    }

                }
            }
        };
        mThread.start();
    }

    @Override
    @OnClick({R.id.button_terms, R.id.button_word, R.id.button_excel})
    public void onClick(View view) {
        if (view.getId() == R.id.button_terms) {
            parseTerms();
        } else if (view.getId() == R.id.button_word) {

        } else if (view.getId() == R.id.button_excel) {

        }
    }

    @Override
    public void updateUI(Message message) {
        mUIHandler.sendMessage(message);
    }

    private final Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String something = bundle.getString("something");
            double total = bundle.getInt("total", 0);
            double number = bundle.getInt("number", 0);
            Log.e(TAG, "total=" + total + "   number" + number);
            String text = mTxtContent.getText().toString() + something;
            double lastPercent = number / total * 100;
            DecimalFormat df = new DecimalFormat("#.##");
            mTxtContent.setText("完成比例：  " +  df.format(lastPercent) + "%，请耐心等候！");
//            Log.e(TAG, something);
        }
    };

    @Override
    public void loadFinsh() {
        loadFinishHandler.sendEmptyMessage(0);
    }

    private final Handler loadFinishHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mTxtContent.setText("");
            AnalyzeDao dao = new AnalyzeDao(AnalyzePoemActivity.this);
            List<AnalyzeBean> list = dao.queryAll(50);
            String text;
            for (int i = 0; i < list.size(); i++) {
                text = mTxtContent.getText().toString();
                text += (list.get(i).getName() + "       ==" + list.get(i).getAmount() + "个\n");
                mTxtContent.setText(text);
            }
        }
    };
}
