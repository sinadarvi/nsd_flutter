import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:nsd_flutter/nsd_flutter.dart';
import 'package:nsd_flutter/nsd_type.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  NsdFlutter nsdFlutter;

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
          title: const Text('NSD Plugin Example'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Text('Running on: $_platformVersion\n'),
              // Text(_initializeNsdHelper),

              StreamBuilder(
                stream: NsdFlutter.registeredStream,
                builder: (BuildContext context, AsyncSnapshot<bool> snapshot) {
                  if (snapshot.hasData) {
                    String value =
                        snapshot.data == true ? 'Registered' : 'Unregistered';
                    return Text("$value");
                  }
                  return Text("Registering Status");
                },
              ),
              RaisedButton(
                child: Text("Initialize NSD helper"),
                onPressed: () async {
                  this.nsdFlutter = NsdFlutter(
                    isLogEnabled: true,
                    isAutoResolveEnabled: true,
                    discoveryTimeout: 30,
                  );
                },
              ),
              RaisedButton(
                child: Text("Register"),
                onPressed: () async {
                  await nsdFlutter.register("MyApp", NsdType.HTTP);
                },
              ),
              RaisedButton(
                child: Text("Unregister"),
                onPressed: () async {
                  await nsdFlutter.unregister;
                },
              ),
              RaisedButton(
                child: Text("startDiscovery"),
                onPressed: () {
                  NsdFlutter.startDiscovery;
                },
              ),
              RaisedButton(
                child: Text("stopDiscovery"),
                onPressed: () {
                  NsdFlutter.stopDiscovery;
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
