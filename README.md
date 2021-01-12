# qr_scanner

二维码、条形码扫描插件

## 安装

pubspec.yaml
```yaml
dependencies:
  qr_scanner:
    git:
      url: https://github.com/lytian/qr_scanner.git
```

## Android权限配置

```xml
    <!--相机-->
    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.VIBRATE"/>
    <uses-permission android:name="android.permission.FLASHLIGHT"/>
    
    <!--存储-->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

## 使用

1. 开启扫描
```dart
  QrScanner.scan();
```

2. 选择图片识别
```dart
  QrScanner.pickImage();
```

3. 识别图片文件地址
```dart
  QrScanner.scanPath(String imgPath);
```

4. 识别图片数据流
```dart
  QrScanner.scanBitmap(Uint8List uint8list);
```

5 开启扫描
```dart
  /// 生成二维码
  /// - [code] 码
  /// - [width] 二维码宽高
  /// - [color] 16进制颜色
  QrScanner.createQRCode(String code, {
    int width = 200,
    int color = 0xFF000000
  });
```

6. 生成条形码
```dart
  /// 生成条形码
  /// - [code] 码
  /// - [width] 条形码的宽
  /// - [height] 条形码的高
  /// - [showText] 是否显示文字
  /// - [fontSize] 文字大小
  /// - [color] 16进制颜色
  QrScanner.createBarCode(String code, {
      int width = 200,
      int height = 200,
      bool showText = false,
      int fontSize = 14,
      int color = 0xFF000000
    });
```