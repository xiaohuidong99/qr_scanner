package com.vincent.qr_scanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.vincent.qr_scanner.camera.CameraScan;
import com.vincent.qr_scanner.decoder.CodeReader;
import com.vincent.qr_scanner.encoder.CodeCreator;
import com.vincent.qr_scanner.utils.PermissionUtils;
import com.vincent.qr_scanner.utils.UriUtils;

import java.io.ByteArrayOutputStream;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/** QrScannerPlugin */
public class QrScannerPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
  private static final int REQUEST_CODE = 10010;
  private static final int REQUEST_IMAGE = 10011;

  private MethodChannel channel;
  private Activity activity;
  private Result result;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "vincent/qr_scanner");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "scan":
        Intent codeIntent = new Intent(activity, CaptureActivity.class);
        activity.startActivityForResult(codeIntent, REQUEST_CODE);
        this.result = result;
        break;
      case "pickImage":
        if(!PermissionUtils.checkPermission(activity.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)){
          PermissionUtils.requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, 1);
          result.success(null);
          return;
        }
        Intent imgIntent = new Intent();
        imgIntent.setAction(Intent.ACTION_PICK);
        imgIntent.setType("image/*");
        activity.startActivityForResult(imgIntent, REQUEST_IMAGE);
        this.result = result;
        break;
      case "scanPath":
        if(!PermissionUtils.checkPermission(activity.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)){
          PermissionUtils.requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, 1);
          result.success(null);
          return;
        }
        String path = call.argument("path");
        result.success(CodeReader.parseCode(path));
        break;
      case "scanBitmap":
        byte[] bytes = call.argument("bytes");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes != null ? bytes.length : 0);
        result.success(CodeReader.parseCode(bitmap));
        break;
      case "createQRCode":
        String qrCode = call.argument("code");
        int qrWidth = call.argument("width");
        long qrColor =  call.argument("color");
        Bitmap qrBt = CodeCreator.createQRCode(qrCode, qrWidth, (int) qrColor);
        if (qrBt != null) {
          ByteArrayOutputStream qrBo = new ByteArrayOutputStream();
          qrBt.compress(Bitmap.CompressFormat.JPEG, 90, qrBo);
          result.success(qrBo.toByteArray());
          return;
        }
        result.success(null);
        break;
      case "createBarCode":
        String barCode = call.argument("code");
        int barWidth = call.argument("width");
        int barHeight = call.argument("height");
        boolean showText = call.argument("showText");
        int fontSize = call.argument("fontSize");
        long barColor =  call.argument("color");
        Bitmap barBt = CodeCreator.createBarCode(barCode, BarcodeFormat.CODE_128, barWidth, barHeight, null, showText, fontSize, (int) barColor);
        if (barBt != null) {
          ByteArrayOutputStream barBo = new ByteArrayOutputStream();
          barBt.compress(Bitmap.CompressFormat.JPEG, 90, barBo);
          result.success(barBo.toByteArray());
          return;
        }
        result.success(null);
        break;
      default:
        result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK && data != null) {
      if (requestCode == REQUEST_CODE) {
        String result = CameraScan.parseScanResult(data);
        this.result.success(result);
        return true;
      } else if (requestCode == REQUEST_IMAGE) {
        final String path = UriUtils.getImagePath(activity.getApplicationContext(), data);
        String code = CodeReader.parseCode(path);
        result.success(code);
        return true;
      }
    }
    return false;
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    this.activity = binding.getActivity();
    binding.addActivityResultListener(this);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }
}
