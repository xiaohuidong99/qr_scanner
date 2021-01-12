
import 'dart:async';
import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class QrScanner {
  static const MethodChannel _channel =
      const MethodChannel('vincent/qr_scanner');

  static Future<String> scan() async {
    return await _channel.invokeMethod('scan');
  }

  static Future<String> pickImage() async {
    return await _channel.invokeMethod('pickImage');
  }

  static Future<String> scanPath(String path) async {
    assert(path != null && File(path).existsSync());
    return await _channel.invokeMethod('scanPath', {'path': path});
  }

  static Future<String> scanBitmap(Uint8List uint8list) async {
    assert(uint8list != null && uint8list.isNotEmpty);
    return await _channel.invokeMethod('scanBitmap', {'bytes' : uint8list});
  }

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
