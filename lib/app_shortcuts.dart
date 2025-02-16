import 'dart:io';
import 'dart:convert';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class AppShortcuts {
  static const MethodChannel _channel = const MethodChannel('app_shortcuts');

  // static void _openIOSShortcutGenerator(
  //   String link,
  //   String title,
  //   String imageUrl, {
  //   String description,
  //   String shareButtonDescription,
  //   String addToHomescreenButtonDescription,
  // }) async {
  //   var html = getIosTutorialHTML(link, title, description, imageUrl,
  //       shareButtonDescription: shareButtonDescription,
  //       addToHomescreenButtonDescription: addToHomescreenButtonDescription);
  //   var address = 'localhost';
  //   var diaApp = App();
  //   // ignore: missing_return
  //   diaApp.use((ctx, next) {
  //     var uri = Uri.dataFromString(html,
  //         mimeType: "text/html", base64: true, encoding: Utf8Codec());
  //     ctx.response.redirect(uri);
  //     diaApp.close();
  //   });
  //   await diaApp
  //       .listen(address, 8080)
  //       .then((value) => print('Server started!'))
  //       .catchError(print);
  //   try {
  //     await launch('http://$address:8080/', forceSafariVC: false);
  //   } catch (error) {
  //     print(error);
  //   }
  // }

  static void _createAndroidShortcut(
      String id, String link, String title, Uint8List image) async {
    return _channel.invokeMethod('createAndroidShortcut', {
      'title': title,
      'id': id,
      'link': link,
      'imageBase64': base64Encode(image as List<int>),
    });
  }

  static void createShortcut({
    /// the name of the shortcut in the home screen
    required String title,

    /// the link for the shortcut to open, most likely a deep link
    required String link,

    /// a string identifier used to let android identify the shortcut
    required String id,

    /// an PDF or JPEG (?) image for the shortcut
    /// it will also be used in the tutorial page on iOS
    required Uint8List imageData,

    // /// a description used for the tutorial page on iOS
    // String description = '',
    //
    // /// a description used to prompt the user to click the share button on safari
    // String shareButtonDescription = '',
    //
    // /// a description used to prompt the user to click on the 'Add to homescreen' button on safari
    // String addToHomescreenButtonDescription = '',
  }) {
    if (Platform.isAndroid) {
      return _createAndroidShortcut(id, link, title, imageData);
    // } else if (Platform.isIOS) {
    //   return _openIOSShortcutGenerator(
    //     link,
    //     title,
    //     imageUrl,
    //     description: description,
    //     shareButtonDescription: shareButtonDescription,
    //     addToHomescreenButtonDescription: addToHomescreenButtonDescription,
    //   );
    } else {
      throw Exception('Platform not supported');
    }
  }
}
