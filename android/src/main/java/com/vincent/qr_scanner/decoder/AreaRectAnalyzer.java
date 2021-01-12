package com.vincent.qr_scanner.decoder;

import android.graphics.Rect;

import androidx.annotation.Nullable;

import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

import java.util.Map;

/**
 * 矩形区域分析器
 * @Author: Vincent
 * @CreateAt: 2021/01/11 17:03
 */
public abstract class AreaRectAnalyzer extends ImageAnalyzer {

    DecodeConfig mDecodeConfig;
    Map<DecodeHintType,?> mHints;
    boolean isMultiDecode = true;
    private float mAreaRectRatio = DecodeConfig.DEFAULT_AREA_RECT_RATIO;
    private int mAreaRectHorizontalOffset = 0;
    private int mAreaRectVerticalOffset = 0;

    public AreaRectAnalyzer(@Nullable DecodeConfig config){
        this.mDecodeConfig = config;
        if(config != null){
            mHints = config.getHints();
            isMultiDecode = config.isMultiDecode();
            mAreaRectRatio = config.getAreaRectRatio();
            mAreaRectHorizontalOffset = config.getAreaRectHorizontalOffset();
            mAreaRectVerticalOffset = config.getAreaRectVerticalOffset();
        }else{
            mHints = DecodeFormatManager.DEFAULT_HINTS;
        }

    }

    @Nullable
    @Override
    public Result analyze(byte[] data, int width, int height) {
        if(mDecodeConfig != null){
            if(mDecodeConfig.isFullAreaScan()){
                //mDecodeConfig为空或者支持全区域扫码识别时，直接使用全区域进行扫码识别
                return analyze(data,width,height,0,0,width,height);
            }

            Rect rect = mDecodeConfig.getAnalyzeAreaRect();
            if(rect != null){//如果分析区域不为空，则使用指定的区域进行扫码识别
                return analyze(data,width,height,rect.left,rect.top,rect.width(),rect.height());
            }
        }

        //如果分析区域为空，则通过识别区域比例和相关的偏移量计算出最终的区域进行扫码识别
        int size = (int)(Math.min(width,height) * mAreaRectRatio);
        int left = (width-size)/2 + mAreaRectHorizontalOffset;
        int top = (height-size)/2 + mAreaRectVerticalOffset;

        return analyze(data,width,height,left,top,size,size);

    }

    abstract Result analyze(byte[] data, int dataWidth, int dataHeight,int left,int top,int width,int height);
}
