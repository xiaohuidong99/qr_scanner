import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:qr_scanner/qr_scanner.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _result;
  Uint8List _bytes;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('qr_scanner'),
        ),
        body: Padding(
          padding: EdgeInsets.all(20),
          child: Column(
            children: [
              SizedBox(
                width: double.infinity,
                height: 40,
                child: RaisedButton(
                  onPressed: () async {
                    _result = await QrScanner.scan();
                    setState(() {});
                  },
                  color: Theme.of(context).primaryColor,
                  textColor: Colors.white,
                  child: Text('扫描'),
                ),
              ),
              SizedBox(height: 10,),
              SizedBox(
                width: double.infinity,
                height: 40,
                child: RaisedButton(
                  onPressed: () async {
                    _result = await QrScanner.pickImage();
                    // _result = await QrScanner.scanPath('/storage/emulated/0/Pictures/WeiXin/1mmexport30422518a2abd975f99a061b1a05b57c.png');
                    setState(() {});
                  },
                  color: Theme.of(context).primaryColor,
                  textColor: Colors.white,
                  child: Text('扫描文件'),
                ),
              ),
              SizedBox(height: 10,),
              SizedBox(
                width: double.infinity,
                height: 40,
                child: RaisedButton(
                  onPressed: () async {
                    _bytes = await QrScanner.createBarCode('123456789', width: 400, height: 40, showText: false);
                    setState(() {});
                  },
                  color: Theme.of(context).primaryColor,
                  textColor: Colors.white,
                  child: Text('生成条形码'),
                ),
              ),
              SizedBox(height: 10,),
              SizedBox(
                width: double.infinity,
                height: 40,
                child: RaisedButton(
                  onPressed: () async {
                    _bytes = await QrScanner.createQRCode('qr_scanner');
                    setState(() {});
                  },
                  color: Theme.of(context).primaryColor,
                  textColor: Colors.white,
                  child: Text('生成二维码'),
                ),
              ),
              SizedBox(height: 20,),
              Text('扫描结果：${_result ?? ''}'),
              SizedBox(height: 20,),
              _bytes != null && _bytes.isNotEmpty ? Image.memory(_bytes, width: double.infinity,) : Container()
            ],
          ),
        )
      ),
    );
  }
}
