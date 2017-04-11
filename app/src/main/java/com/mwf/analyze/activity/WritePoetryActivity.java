package com.mwf.analyze.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mwf.analyze.R;
import com.mwf.analyze.bean.AnalyzeBean;
import com.mwf.analyze.dao.AnalyzeDao;
import com.mwf.analyze.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

/**
 * 我用代码写首诗
 */
public class WritePoetryActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();
    private StringBuilder poems = new StringBuilder();
    @BindView(R.id.tv_content)
    TextView tv_content;

    private ProgressDialog progressDialog;

    private ArrayList<AnalyzeBean> oneWordsList = new ArrayList<AnalyzeBean>();
    private ArrayList<AnalyzeBean> twoWordsList = new ArrayList<AnalyzeBean>();
    private ArrayList<AnalyzeBean> threeWordsList = new ArrayList<AnalyzeBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_poetry);
        init();
    }

    private void showProgress(String content) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(content);
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog == null) {
            return;
        }
        progressDialog.dismiss();
    }

    /**
     * 初始化完毕
     */
    private final Handler initUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissProgress();
        }
    };

    /**
     * 初始化
     */
    private void init() {
//        showProgress("正在初始化");
        final AnalyzeDao dao = new AnalyzeDao(WritePoetryActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AnalyzeBean> list = dao.queryAll();
                AnalyzeBean bean;
                for (int i = 0; i < list.size(); i++) {
                    bean = list.get(i);
                    if (bean.getLength() == 1) {
//                        Log.e(TAG, "一个字=====>   " + bean.getName());
                        oneWordsList.add(bean);
                    }
                    if (bean.getLength() == 2) {
//                        Log.e(TAG, "两个字=====>   " + bean.getName());
                        twoWordsList.add(bean);
                    }
                    if (bean.getLength() == 3) {
//                        Log.e(TAG, "三个字=====>   " + bean.getName());
                        threeWordsList.add(bean);
                    }
                }
//                Log.e(TAG, "一个字总量=====>   " + oneWordsList.size());
//                Log.e(TAG, "两个字总量=====>   " + twoWordsList.size());
//                Log.e(TAG, "三个字总量=====>   " + threeWordsList.size());

                //去掉多余的字
                for (int i = 0; i < oneWordsList.size(); i++) {
//                    Log.e(TAG, "一个字=====>   " + oneWordsList.get(i).getName());
                    if (oneWordsList.get(i).getName().equals("纟") || oneWordsList.get(i).getName().equals("阝") || oneWordsList.get(i).getName().equals("」")
                            || oneWordsList.get(i).getName().equals("扌") || oneWordsList.get(i).getName().equals("～") || oneWordsList.get(i).getName().equals("「")
                            || oneWordsList.get(i).getName().equals("衤") || oneWordsList.get(i).getName().equals("」") || oneWordsList.get(i).getName().equals("·")
                            || oneWordsList.get(i).getName().equals("〕") || oneWordsList.get(i).getName().equals("＜") || oneWordsList.get(i).getName().equals("〔")
                            || oneWordsList.get(i).getName().equals("〕") || oneWordsList.get(i).getName().equals("＜") || oneWordsList.get(i).getName().equals("〔")) {

                        Log.e(TAG, "一个字=====>   " + oneWordsList.get(i).getName());
                        dao.delete(oneWordsList.get(i));
                    }
                }
                for (int i = 0; i < twoWordsList.size(); i++) {
                    if (twoWordsList.get(i).getName().equals("氵邑") || twoWordsList.get(i).getName().equals("扌戚") || twoWordsList.get(i).getName().equals("氵爰")
                            || twoWordsList.get(i).getName().equals("鹿阝") || twoWordsList.get(i).getName().equals("灌「") || twoWordsList.get(i).getName().equals("氵项")
                            || twoWordsList.get(i).getName().equals("豸区") || twoWordsList.get(i).getName().equals("囗囗") || twoWordsList.get(i).getName().equals("纟墨")) {

                        Log.e(TAG, "两个字=====>   " + twoWordsList.get(i).getName());
                        dao.delete(twoWordsList.get(i));
                    }
                }
                randoOutput();
//                initUIHandler.sendEmptyMessage(0);
            }
        }).start();

    }

    /**
     * 随机输出
     */
    private void randoOutput() {
        if (oneWordsList.size()<=0){
            return;
        }
//        write111();
        write12();
//        write212();
//        write221();
//        write223();
//        write2221();
    }

    private void write2221() {
        int size1 = twoWordsList.size();
        int size4 = oneWordsList.size();

        Random random = new Random();
        int randomTotal = 1000;

        //双字 + 双字 + 双字+单字
        //月落 乌啼 霜满 天，江枫 渔火 对愁 眠
        for (int i = 0; i < randomTotal; i++) {
            int number1 = random.nextInt(size1);
            int number2 = random.nextInt(size1);
            int number3 = random.nextInt(size1);
            int number4 = random.nextInt(size4);
            String str = twoWordsList.get(number1).getName() + twoWordsList.get(number2).getName() + twoWordsList.get(number3).getName() + oneWordsList.get(number4).getName();
            Log.e(TAG, "" + str);

            if (i % 10 == 0) {
                poems.append(str + "\n");
            } else {
                poems.append(str + "   ");
            }
        }
    }

    private void write223() {
        int size1 = twoWordsList.size();
        int size2 = twoWordsList.size();
        int size3 = threeWordsList.size();

        Random random = new Random();
        int randomTotal = 1000;

        //双字 + 双字 + 三字
        //月落 乌啼 霜满天，江枫 渔火 对愁眠
        for (int i = 0; i < randomTotal; i++) {
            int number1 = random.nextInt(size1);
            int number2 = random.nextInt(size2);
            int number3 = random.nextInt(size3);
            String str = twoWordsList.get(number1).getName() + twoWordsList.get(number2).getName() + threeWordsList.get(number3).getName();
            Log.e(TAG, "" + str);

            if (i % 10 == 0) {
                poems.append(str + "\n");
            } else {
                poems.append(str + "   ");
            }
        }
    }

    private void write221() {
        int size1 = oneWordsList.size();
        int size2 = twoWordsList.size();
        int size3 = oneWordsList.size();

        Random random = new Random();
        int randomTotal = 1000;

        //双字 + 双字 + 单字
        //浮云 游子 意，落日 故人 情
        for (int i = 0; i < randomTotal; i++) {
            int number1 = random.nextInt(size2);
            int number2 = random.nextInt(size2);
            int number3 = random.nextInt(size1);
            String str = twoWordsList.get(number1).getName() + twoWordsList.get(number2).getName() + oneWordsList.get(number3).getName();
            Log.e(TAG, "" + str);

            if (i % 10 == 0) {
                poems.append(str + "\n");
            } else {
                poems.append(str + "   ");
            }
        }
    }

    private void write212() {
        int size1 = oneWordsList.size();
        int size2 = twoWordsList.size();
        int size3 = threeWordsList.size();

        Random random = new Random();
        int randomTotal = 1000;


        //双字 + 单字 + 双字
        //千山 鸟 飞绝，万径 人 踪灭
        for (int i = 0; i < randomTotal; i++) {
            int number1 = random.nextInt(size2);
            int number2 = random.nextInt(size1);
            int number3 = random.nextInt(size2);
            String str = twoWordsList.get(number1).getName() + oneWordsList.get(number2).getName() + twoWordsList.get(number3).getName();
            Log.e(TAG, "" + str);

            if (i % 10 == 0) {
                poems.append(str + "\n");
            } else {
                poems.append(str + "   ");
            }
        }
    }

    private void write111() {
        int size1 = oneWordsList.size();

        Random random = new Random();
        int randomTotal = 1000;
        //单字 + 单字 + 单字
        for (int i = 0; i < randomTotal; i++) {
            int number1 = random.nextInt(size1);
            int number2 = random.nextInt(size1);
            int number3 = random.nextInt(size1);
            Log.e(TAG, "" + oneWordsList.get(number1).getName() + oneWordsList.get(number2).getName() + oneWordsList.get(number3).getName());
        }
    }

    private void write12() {
        int size1 = oneWordsList.size();
        int size2 = twoWordsList.size();


        Random random = new Random();
        int randomTotal = 1000;
        //单字 + 单字 + 单字
        for (int i = 0; i < randomTotal; i++) {
            int number1 = random.nextInt(size1);
            int number2 = random.nextInt(size2);
//            int number1 = random.nextInt(size2);
//            int number2 = random.nextInt(size2);

            Log.e(TAG, "" + oneWordsList.get(number1).getName() + twoWordsList.get(number2).getName());
//            Log.e(TAG, "" + oneWordsList.get(number1).getName() + oneWordsList.get(number2).getName());
//            Log.e(TAG, "" + twoWordsList.get(number1).getName() + twoWordsList.get(number2).getName());
        }
    }

    /**
     * 导出完成更新UI
     */
    private final Handler exportFinishHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissProgress();
            Toast.makeText(WritePoetryActivity.this, "导出成功", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 导出数据
     */
    private void export() {

        if (TextUtils.isEmpty(poems.toString())) {
            Toast.makeText(this, "查询不到数据", Toast.LENGTH_LONG).show();
            return;
        }

        //显示一个输入导出的文件名的对话框
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_export, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(true);
        alertDialog.show();
        alertDialog.setContentView(view);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        final EditText editText = (EditText) view.findViewById(R.id.editText);

        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        Button btn_commit = (Button) view.findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                showProgress("正在导出，请稍后......");

                String name = editText.getText().toString();
                Log.e(TAG, "name=" + name);
                if (TextUtils.isEmpty(name)) {
                    name = editText.getHint().toString();
                    name = name.substring(5, name.length());
                }

                final String finalName = name;

                //获取SDCard路径
                final File file = Environment.getExternalStorageDirectory();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //保存文件
//                        FileUtils.saveTxt(text, AnalyzePoemActivity.this, file.getAbsolutePath() + File.separator + "Download", finalName + ".csv");
                        FileUtils.saveTxt(poems.toString(), WritePoetryActivity.this, file.getAbsolutePath() + File.separator + "Download", finalName + ".txt");
                        exportFinishHandler.sendEmptyMessage(0);
                    }
                }).start();

            }
        });

    }

    public void onClickWrite(View view) {
        randoOutput();
    }

    public void onClickClear(View view) {

    }

    public void onClickExport(View view) {
        poems = new StringBuilder();
        randoOutput();
        export();
    }

}
