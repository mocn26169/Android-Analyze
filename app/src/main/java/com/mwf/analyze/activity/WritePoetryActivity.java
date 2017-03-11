package com.mwf.analyze.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mwf.analyze.R;
import com.mwf.analyze.bean.AnalyzeBean;
import com.mwf.analyze.dao.AnalyzeDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

import static android.R.id.list;

/**
 * 我用代码写首诗
 */
public class WritePoetryActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

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
                Log.e(TAG, "一个字总量=====>   " + oneWordsList.size());
                Log.e(TAG, "两个字总量=====>   " + twoWordsList.size());
                Log.e(TAG, "三个字总量=====>   " + threeWordsList.size());
                for (int i = 0; i < oneWordsList.size(); i++) {
//                    Log.e(TAG, "一个字=====>   " + oneWordsList.get(i).getName());

                    if (oneWordsList.get(i).getName().equals("·")){
                    Log.e(TAG, "一个字=====>   " + oneWordsList.get(i).getName());
                        dao.delete(oneWordsList.get(i));
                    }
                }
//                for (int i = 0; i < twoWordsList.size(); i++) {
//                    Log.e(TAG, "两个字=====>   " + twoWordsList.get(i).getName());
//                }
//                for (int i = 0; i < threeWordsList.size(); i++) {
//                    Log.e(TAG, "三个字=====>   " + threeWordsList.get(i).getName());
//                }


                randoOutput();
//                initUIHandler.sendEmptyMessage(0);
            }
        }).start();

    }

    /**
     * 随机输出
     */
    private void randoOutput(){
        int oneWordsSize = oneWordsList.size();
        Random random = new Random();
        int randomTotal = 1000;
        //单字 + 单字 + 单字
//        for (int i = 0; i < randomTotal; i++) {
//            int number1 = random.nextInt(oneWordsSize);
//            int number2 = random.nextInt(oneWordsSize);
//            int number3 = random.nextInt(oneWordsSize);
//            Log.e(TAG, "" + oneWordsList.get(number1).getName() + oneWordsList.get(number2).getName() + oneWordsList.get(number3).getName());
//        }
        //双字 + 单字 + 双字
        for (int i = 0; i < randomTotal; i++) {
            int number1 = random.nextInt(oneWordsSize);
            int number2 = random.nextInt(oneWordsSize);
            int number3 = random.nextInt(oneWordsSize);
            Log.e(TAG, ""  + twoWordsList.get(number1).getName()+ oneWordsList.get(number2).getName() + twoWordsList.get(number3).getName());
        }
    }
    public void onClickWrite(View view) {
        randoOutput();
    }

    public void onClickClear(View view) {

    }

    public void onClickExport(View view) {

    }

}
