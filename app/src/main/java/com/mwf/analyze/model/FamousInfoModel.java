package com.mwf.analyze.model;

import android.content.Context;

import com.mwf.analyze.Constant;
import com.mwf.analyze.RetrofitWrapper;
import com.mwf.analyze.bean.FamousInfoReq;
import com.mwf.analyze.intf.IFamousInfo;

import retrofit2.Call;

public class FamousInfoModel {
    private static FamousInfoModel famousInfoModel;
    private IFamousInfo mIFamousInfo;

    public FamousInfoModel(Context context) {
        mIFamousInfo = RetrofitWrapper.getInstance(Constant.BASEURL).create(IFamousInfo.class);
    }

    public static FamousInfoModel getInstance(Context context){
        if(famousInfoModel == null) {
            famousInfoModel = new FamousInfoModel(context);
        }
        return famousInfoModel;
    }

    public Call<String> queryLookUp(FamousInfoReq famousInfoReq) {
        Call<String > infoCall = mIFamousInfo.getFamousResult(famousInfoReq.api_key, famousInfoReq.text, famousInfoReq.pattern, famousInfoReq.format);
        return infoCall;
    }
}
