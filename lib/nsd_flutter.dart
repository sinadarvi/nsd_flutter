import 'dart:async';

import 'package:flutter/services.dart';

class NsdFlutter {
  static const MethodChannel _channel = const MethodChannel('nsd_flutter');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> get isItRegistered async {
    final String reg = await _channel.invokeMethod('isItRegistered');
    return reg;
  }

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
