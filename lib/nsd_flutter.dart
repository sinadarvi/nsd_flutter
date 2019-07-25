import 'dart:async';

import 'package:flutter/services.dart';
import 'package:nsd_flutter/nsd_type.dart';

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

  static Future<String> get initializeNsdHelper async {
    final String nsd = await _channel.invokeMethod('initializeNsdHelper');
    return nsd;
  }

  static Future<void> register(
      String desiredServiceName, String nsdType) async {
    try {
      return _channel.invokeMethod('register', <String, dynamic>{
        'desiredServiceName': desiredServiceName,
        'type': nsdType
      });
    } on PlatformException catch (e) {
      throw 'Unable to register: ${e.message}';
    }
  }

  static Future<String> get unregister async {
    final String reg = await _channel.invokeMethod('unregister');
    return reg;
  }
}
