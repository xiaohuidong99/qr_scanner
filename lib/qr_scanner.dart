
import 'dart:async';
import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class QrScanner {
  static const MethodChannel _channel =
      const MethodChannel('vincent/qr_scanner');

  /// 开启扫描
  static Future<String> scan() async {
    return await _channel.invokeMethod('scan');
  }

  /// 选择图片识别
  static Future<String> pickImage() async {
    return await _channel.invokeMethod('pickImage');
  }

  /// 识别图片文件地址
  static Future<String> scanPath(String imgPath) async {
    assert(imgPath != null && File(imgPath).existsSync());
    return await _channel.invokeMethod('scanPath', {'path': imgPath});
  }

  /// 识别图片数据流
  static Future<String> scanBitmap(Uint8List uint8list) async {
    assert(uint8list != null && uint8list.isNotEmpty);
    return await _channel.invokeMethod('scanBitmap', {'bytes' : uint8list});
  }

  /// 生成二维码
  /// - [code] 码
  /// - [width] 二维码宽高
  /// - [color] 16进制颜色
  static Future<Uint8List> createQRCode(String code, {
    int width = 200,
    int color = 0xFF000000
  }) async {
    return await _channel.invokeMethod('createQRCode', {
      'code': code,
      'width': width,
      'color': color
    });
  }

  /// 生成条形码
  /// - [code] 码
  /// - [width] 条形码的宽
  /// - [height] 条形码的高
  /// - [showText] 是否显示文字
  /// - [fontSize] 文字大小
  /// - [color] 16进制颜色
  static Future<Uint8List> createBarCode(String code, {
    int width = 200,
    int height = 200,
    bool showText = false,
    int fontSize = 14,
    int color = 0xFF000000
  }) async {
    return await _channel.invokeMethod('createBarCode', {
      'code': code,
      'width': width,
      'height': height,
      'showText': showText,
      'fontSize': fontSize,
      'color': color
    });
  }
}
