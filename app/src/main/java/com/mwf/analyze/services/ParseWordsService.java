package com.mwf.analyze.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.mwf.analyze.Constant;
import com.mwf.analyze.activity.AnalyzePoemActivity;
import com.mwf.analyze.bean.CloudResultPlainParse;
import com.mwf.analyze.bean.FamousInfoReq;
import com.mwf.analyze.dao.AnalyzeDao;
import com.mwf.analyze.model.FamousInfoModel;
import com.mwf.analyze.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 处理词的服务类
 */
public class ParseWordsService extends IntentService {

    public final String TAG = this.getClass().getName();

    /**
     * 结束加载回调
     */
    private static ILoadWorsdFinish iLoadFinish;

    /**
     * 数据库访问
     */
    private AnalyzeDao dao;

    public ParseWordsService() {
        super("com.mwf.analyze.services.ParseWordsService");
    }

    public static void setLoadFinish(ILoadWorsdFinish iLoadWordsFinishInterface) {
        iLoadFinish = iLoadWordsFinishInterface;
    }

    /**
     * 结束加载回调接口
     */
    public interface ILoadWorsdFinish {
        void LoadWorsdFinish();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent");
        dao = new AnalyzeDao(ParseWordsService.this);
        //获取文件地址
        String filePath = intent.getStringExtra("filePath");
        if (!TextUtils.isEmpty(filePath)) {
            String string = FileUtils.readTxtFile(filePath);
            //转单个字符
            char[] chars = new char[string.length()];
            chars = string.toCharArray();
            for (int i = 0; i < string.length(); i++) {
                String text = String.valueOf(chars[i]);
//                Log.i(TAG, text);
                dbSave(text);
            }
        }
    }

    public void onDestroy() {
        Log.i(TAG, " ParseTermService  destroy");
        iLoadFinish.LoadWorsdFinish();
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
