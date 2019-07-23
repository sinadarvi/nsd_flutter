import 'dart:async';

import 'package:flutter/services.dart';

class NsdFlutter {
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

  // static Future<String> get isItRegistered async {
  //   final String reg = await _channel.invokeMethod('isRegistered');
  //   return reg;
  // }

  static Future<String> get initializeNsdHelper async {
    final String nsd = await _channel.invokeMethod('initializeNsdHelper');
    return nsd;
  }

  static Future<String> get registerIt async {
    final String reg = await _channel.invokeMethod('registerIt');
    return reg;
  }

  static Future<String> get unregisterIt async {
    final String reg = await _channel.invokeMethod('unregisterIt');
    return reg;
  }
}
