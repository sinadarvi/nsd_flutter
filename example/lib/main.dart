import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:nsd_flutter/nsd_flutter.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String _isItRegistered = 'Unknown';
  String _initializeNsdHelper = 'Unknown';
  String _registerIt = 'Unknown';
  String _unregisterIt = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await NsdFlutter.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: <Widget>[
            Text('Running on: $_platformVersion\n'),
            Row(
              children: <Widget>[
                Text(_isItRegistered),
                RaisedButton(
                  child: Text("isItRegistered"),
                  onPressed: () async {
                    String result = await NsdFlutter.isItRegistered;
                    try {
                      setState(() {
                        _isItRegistered = result;
                      });
                    } on PlatformException {
                      result = 'Failed to get isItRegistered.';
                      setState(() {
                        _isItRegistered = result;
                      });
                    }
                  },
                )
              ],
            ),
            Row(
              children: <Widget>[
                Text(_initializeNsdHelper),
                RaisedButton(
                  child: Text("initializeNsdHelper"),
                  onPressed: () async {
                    String result = await NsdFlutter.initializeNsdHelper;
                    try {
                      setState(() {
                        _initializeNsdHelper = result;
                      });
                    } on PlatformException {
                      result = 'Failed to get initializeNsdHelper.';
                      setState(() {
                        _initializeNsdHelper = result;
                      });
                    }
                  },
                )
              ],
            ),
            Row(
              children: <Widget>[
                Text(_registerIt),
                RaisedButton(
                  child: Text("registerIt"),
                  onPressed: () async {
                    String result = await NsdFlutter.registerIt;
                    try {
                      setState(() {
                        _registerIt = result;
                      });
                    } on PlatformException {
                      result = 'Failed to get registerIt.';
                      setState(() {
                        _registerIt = result;
                      });
                    }
                  },
                )
              ],
            ),
            Row(
              children: <Widget>[
                Text(_unregisterIt),
                RaisedButton(
                  child: Text("unregisterIt"),
                  onPressed: () async {
                    String result = await NsdFlutter.unregisterIt;
                    try {
                      setState(() {
                        _unregisterIt = result;
                      });
                    } on PlatformException {
                      result = 'Failed to get unregisterIt.';
                      setState(() {
                        _unregisterIt = result;
                      });
                    }
                  },
                )
              ],
            )
          ],
        ),
      ),
    );
  }
}
