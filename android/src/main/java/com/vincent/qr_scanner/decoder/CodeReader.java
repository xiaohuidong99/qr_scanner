package com.vincent.qr_scanner.decoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import java.util.Map;

/**
 * 条形码、二维码读取器
 * @Author: Vincent
 * @CreateAt: 2021/01/11 17:29
 */
public class CodeReader {

    public static final int DEFAULT_REQ_WIDTH = 480;
    public static final int DEFAULT_REQ_HEIGHT = 640;

    /**
     * 解析二维码图片
     * @param bitmapPath
     * @return
     */
    public static String parseQRCode(String bitmapPath){
        Result result = parseQRCodeResult(bitmapPath);
        if(result != null){
            return result.getText();
        }
        return null;
    }

    /**
     * 解析二维码图片
     * @param bitmapPath
     * @return
     */
    public static Result parseQRCodeResult(String bitmapPath){
        return parseQRCodeResult(bitmapPath,DEFAULT_REQ_WIDTH,DEFAULT_REQ_HEIGHT);
    }

    /**
     * 解析二维码图片
     * @param bitmapPath
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Result parseQRCodeResult(String bitmapPath,int reqWidth,int reqHeight){
        return parseCodeResult(bitmapPath,reqWidth,reqHeight, DecodeFormatManager.QR_CODE_HINTS);
    }

    /**
     * 解析一维码/二维码图片
     * @param bitmapPath
     * @return
     */
    public static String parseCode(String bitmapPath){
        return parseCode(bitmapPath, DecodeFormatManager.ALL_HINTS);
    }

    /**
     * 解析一维码/二维码图片
     * @param bitmapPath
     * @param hints 解析编码类型
     * @return
     */
    public static String parseCode(String bitmapPath, Map<DecodeHintType,Object> hints){
        Result result = parseCodeResult(bitmapPath,hints);
        if(result != null){
            return result.getText();
        }
        return null;
    }

    /**
     * 解析一维码/二维码图片
     * @param bitmapPath
     * @param hints 解析编码类型
     * @return
     */
    public static Result parseCodeResult(String bitmapPath, Map<DecodeHintType,Object> hints){
        return parseCodeResult(bitmapPath,DEFAULT_REQ_WIDTH,DEFAULT_REQ_HEIGHT,hints);
    }

    /**
     * 解析一维码/二维码图片
     * @param bitmap
     * @return
     */
    public static String parseCode(Bitmap bitmap) {
        Result result = parseCodeResult(bitmap, DecodeFormatManager.ALL_HINTS);
        if(result != null){
            return result.getText();
        }
        return null;
    }

    /**
     * 解析一维码/二维码图片
     * @param bitmap
     * @param hints 解析编码类型
     * @return
     */
    public static Result parseCodeResult(Bitmap bitmap, Map<DecodeHintType,Object> hints){
        Result result = null;
        MultiFormatReader reader = new MultiFormatReader();
        try{
            reader.setHints(hints);
            RGBLuminanceSource source = getRGBLuminanceSource(bitmap);
            result = decodeInternal(reader,source);
            if(result == null){
                result = decodeInternal(reader,source.invert());
            }
            if(result == null && source.isRotateSupported()){
                result = decodeInternal(reader,source.rotateCounterClockwise());
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            reader.reset();
        }

        return result;
    }


    /**
     * 解析一维码/二维码图片
     * @param bitmapPath
     * @param reqWidth
     * @param reqHeight
     * @param hints 解析编码类型
     * @return
     */
    public static Result parseCodeResult(String bitmapPath,int reqWidth,int reqHeight, Map<DecodeHintType,Object> hints){
        Result result = null;
        MultiFormatReader reader = new MultiFormatReader();
        try{
            reader.setHints(hints);
            RGBLuminanceSource source = getRGBLuminanceSource(compressBitmap(bitmapPath,reqWidth,reqHeight));
            result = decodeInternal(reader,source);
            if(result == null){
                result = decodeInternal(reader,source.invert());
            }
            if(result == null && source.isRotateSupported()){
                result = decodeInternal(reader,source.rotateCounterClockwise());
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            reader.reset();
        }

        return result;
    }

    private static Result decodeInternal(MultiFormatReader reader, LuminanceSource source){
        Result result = null;
        try{
            try{
                //采用HybridBinarizer解析
                result = reader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
            }catch (Exception e){

            }
            if(result == null){
                //如果没有解析成功，再采用GlobalHistogramBinarizer解析一次
                result = reader.decodeWithState(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 压缩图片
     * @param path
     * @return
     */
    private static Bitmap compressBitmap(String path, int reqWidth, int reqHeight){
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;//获取原始图片大小
        BitmapFactory.decodeFile(path, newOpts);// 此时返回bm为空
        float width = newOpts.outWidth;
        float height = newOpts.outHeight;
        // 缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int wSize = 1;// wSize=1表示不缩放
        if (width > reqWidth) {// 如果宽度大的话根据宽度固定大小缩放
            wSize = (int) (width / reqWidth);
        }
        int hSize = 1;// wSize=1表示不缩放
        if (height > reqHeight) {// 如果高度高的话根据宽度固定大小缩放
            hSize = (int) (height / reqHeight);
        }
        int size = Math.max(wSize,hSize);
        if (size <= 0)
            size = 1;
        newOpts.inSampleSize = size;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, newOpts);
    }


    /**
     * 获取RGBLuminanceSource
     * @param bitmap
     * @return
     */
    private static RGBLuminanceSource getRGBLuminanceSource(@NonNull Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return new RGBLuminanceSource(width, height, pixels);

    }
}
