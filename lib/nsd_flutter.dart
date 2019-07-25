import 'dart:async';

import 'package:flutter/services.dart';

class NsdFlutter {
  final bool isLogEnabled;
  final bool isAutoResolveEnabled;
  final int discoveryTimeout;

  NsdFlutter(
      {this.isLogEnabled = false,
      this.isAutoResolveEnabled = true,
      this.discoveryTimeout = 15})
      : assert(isLogEnabled != null) {
    initializeNsdHelper(isLogEnabled, isAutoResolveEnabled, discoveryTimeout);
  }

  static const MethodChannel _channel = const MethodChannel('nsdMethodChannel');

  static const EventChannel _registeredEventChannel =
      const EventChannel('registeredEventChannel');

  static Stream<bool> _registeredStream;

  static Stream<bool> get registeredStream {
    if (_registeredStream == null) {
      _registeredStream = _registeredEventChannel
          .receiveBroadcastStream()
          .map<bool>((value) => value);
    }
    return _registeredStream;
  }

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  Future<void> initializeNsdHelper(bool isLogEnabled, bool isAutoResolveEnabled,
      int discoveryTimeout) async {
    try {
      return _channel.invokeMethod('initializeNsdHelper', <String, dynamic>{
        'isLogEnabled': isLogEnabled,
        'isAutoResolveEnabled': isAutoResolveEnabled,
        'discoveryTimeout': discoveryTimeout,
      });
    } on PlatformException catch (e) {
      throw 'Unable to register: ${e.message}';
    }
  }

  Future<void> register(String desiredServiceName, String nsdType) async {
    try {
      return _channel.invokeMethod('register', <String, dynamic>{
        'desiredServiceName': desiredServiceName,
        'type': nsdType
      });
    } on PlatformException catch (e) {
      throw 'Unable to register: ${e.message}';
    }
  }

  Future<bool> get unregister async {
    return await _channel.invokeMethod('unregister');
  }
}
