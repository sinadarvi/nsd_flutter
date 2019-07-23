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
  String _isItRegistered = 'Not Registered';
  String _initializeNsdHelper = 'NSD Helper Not Initialized';
  String _registerIt = 'Normal';
  String _unregisterIt = 'Normal';

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
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text('Running on: $_platformVersion\n'),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: <Widget>[
                Text(_initializeNsdHelper),
                RaisedButton(
                  child: Text("Initialize NSD helper"),
                  onPressed: () async {
                    String result = await NsdFlutter.initializeNsdHelper;
                    setState(() {
                      _initializeNsdHelper = result == 'true'
                          ? 'NSD Helper Initilized'
                          : 'NSD Helper Not Initialized';
                    });
                  },
                )
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: <Widget>[
                Text(_registerIt),
                RaisedButton(
                  child: Text("Register"),
                  onPressed: () async {
                    String result = await NsdFlutter.registerIt;
                    setState(() {
                      _registerIt =
                          result == 'true' ? 'Registering...' : 'Normal';
                    });
                  },
                )
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: <Widget>[
                StreamBuilder(
                  stream: NsdFlutter.registeredStream,
                  builder:
                      (BuildContext context, AsyncSnapshot<bool> snapshot) {
                    if (snapshot.hasData) {
                      String value =
                          snapshot.data == true ? 'registered' : 'unregister';
                      return Text("$value");
                    }
                    return Text("noData");
                  },
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: <Widget>[
                Text(_unregisterIt),
                RaisedButton(
                  child: Text("Unregister"),
                  onPressed: () async {
                    String result = await NsdFlutter.unregisterIt;
                    _unregisterIt =
                        result == 'true' ? 'UnRegistering' : 'Normal';
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
