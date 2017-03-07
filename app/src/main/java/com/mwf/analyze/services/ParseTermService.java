package com.mwf.analyze.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.mwf.analyze.Constant;
import com.mwf.analyze.bean.CloudResultPlainParse;
import com.mwf.analyze.bean.FamousInfoReq;
import com.mwf.analyze.dao.AnalyzeDao;
import com.mwf.analyze.model.FamousInfoModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 处理词的服务类
 */
public class ParseTermService extends IntentService {

    public final String TAG = this.getClass().getName();
    /**
     * 更新界面回调
     */
    private static IUpdateUI iUpdateUI;

    /**
     * 结束加载回调
     */
    private static ILoadFinish iLoadFinish;

    /**
     * 网络请求
     */
    private FamousInfoModel famousInfoModel;

    /**
     * 数据库访问
     */
    private AnalyzeDao dao;

    public ParseTermService() {
        super("com.mwf.analyze.services.ParseTermService");
        famousInfoModel = FamousInfoModel.getInstance(this);
    }

    public static void setUpdateUI(IUpdateUI iUpdateUIInterface) {
        iUpdateUI = iUpdateUIInterface;
    }

    public static void setLoadFinish(ILoadFinish iLoadFinishInterface) {
        iLoadFinish = iLoadFinishInterface;
    }

    /**
     * 更新界面回调接口
     */
    public interface IUpdateUI {
        void updateUI(Message message);
    }

    /**
     * 结束加载回调接口
     */
    public interface ILoadFinish {
        void loadFinsh();
    }

    /**
     * 初始化请求参数
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

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //语言云每秒最大调用次数200次，防止调用过多
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dao = new AnalyzeDao(ParseTermService.this);
        //获取需要请求的文字
        String requestText = intent.getStringExtra("poemString");
        //获取总共的组数
        int total = intent.getIntExtra("total", 0);
        //当前的组数
        int number = intent.getIntExtra("number", 0);

// // 异步
//        famousInfoModel.queryLookUp(initParams(requestText)).enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                Log.i(TAG, "请求成功！");
//                String result = response.body().trim();
//                CloudResultPlainParse parse = new CloudResultPlainParse();
//                ArrayList<String> list = parse.parse(result);
//                String strList = "";
//                for (int i = 0; i < list.size(); i++) {
////                    strList+=   list.get(i)+"\n";
//                    strList += list.get(i);
//                }
//                Message message = new Message();
//                Bundle bundle = new Bundle();
//                bundle.putString("something", strList);
//                message.setData(bundle);
//                iUpdateUI.updateUI(message);
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.i(TAG, "请求失败！");
//            }
//        });

        // 同步请求可以根据顺序来
        Call<String> infoCall = famousInfoModel.queryLookUp(initParams(requestText));
        try {
            Response<String> response = infoCall.execute();
            String result = response.body().trim();
            //解析数据
            CloudResultPlainParse parse = new CloudResultPlainParse();
            ArrayList<String> list = parse.parse(result);
            String strList = "";
            for (int i = 0; i < list.size(); i++) {
                strList += list.get(i) + "\n";
                String word = list.get(i);
                dbSave(word);
            }
            //通知界面进行更新
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("something", strList);
            bundle.putInt("number", number);
            bundle.putInt("total", total);
            message.setData(bundle);
            iUpdateUI.updateUI(message);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "请求失败！");
        }
    }

    public void onDestroy() {
        Log.i(TAG, " ParseTermService  destroy");
        iLoadFinish.loadFinsh();
        super.onDestroy();
    }

    /**
     * 数字判断
     *
     * @param str
     * @return
     */
    public boolean isNumeric(String str) {
        if (str.matches("\\d*")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 标点符号判断
     *
     * @param str
     * @return
     */
    public boolean isPunc(String str) {
        Pattern patPunc = Pattern.compile("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]$");
        Matcher matcher = patPunc.matcher(str);

        if (matcher.find()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存到数据库
     */
    private void dbSave(String word) {
        //空判断
        if (TextUtils.isEmpty(word)) {
            return;
        }
        //数字判断
        if (isNumeric(word)) {
            return;
        }
        //标点判断
        if (isPunc(word)) {
            return;
        }
        //保存到数据库
        dao.checkAndCreate(word);
    }

}
