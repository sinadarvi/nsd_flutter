#import "NsdFlutterPlugin.h"
#import <nsd_flutter/nsd_flutter-Swift.h>

@implementation NsdFlutterPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftNsdFlutterPlugin registerWithRegistrar:registrar];
}
@end
