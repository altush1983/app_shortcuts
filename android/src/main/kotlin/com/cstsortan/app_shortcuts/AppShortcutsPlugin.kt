package com.cstsortan.app_shortcuts

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlin.concurrent.thread


/** AppShortcutsPlugin */
class AppShortcutsPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private var shortcutManager: ShortcutManager? = null
  private lateinit var activity: Activity

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "app_shortcuts")
    channel.setMethodCallHandler(this)
    Log.d("TAG", "onAttachedToEngine: engine attached!")
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "createAndroidShortcut") {
      val id: String = call.argument<String>("id").toString()
      val title: String = call.argument<String>("title").toString();
      val imageBase64: String = call.argument<String>("imageBase64").toString()

      thread {
        activity.runOnUiThread {
          val link: String = call.argument<String>("link").toString()
          if (shortcutManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && shortcutManager!!.isRequestPinShortcutSupported) {

            val imageData = Base64.decode(imageBase64, Base64.DEFAULT)
            val image = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)

            val shortcutInfo = ShortcutInfo.Builder(activity, id)
                    .setShortLabel(title)
                    .setLongLabel(title)
                    .setIcon(Icon.createWithBitmap(image))
                    .setIntent(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                    .build()

            val pinnedShortcutCallbackIntent = shortcutManager?.createShortcutResultIntent(shortcutInfo)
            val successCallback = PendingIntent.getBroadcast(activity, /* request code */ 0,
                    pinnedShortcutCallbackIntent, PendingIntent.FLAG_IMMUTABLE)
            shortcutManager!!.requestPinShortcut(shortcutInfo, successCallback.intentSender)
            result.success("done")
          } else {
            result.error("not-supported", "Pinned shortcuts are not supported in this device", null)
          }
        }
      }
    } else {
      result.notImplemented()
    }
  }



  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    Log.d("TAG", "onDetachedFromEngine: detached engine!")
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    Log.d("TAG", "onAttachedToActivity: attached!")
    activity = binding.activity
    shortcutManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      activity.getSystemService(ShortcutManager::class.java)
    } else {
      null
    }
  }

  override fun onDetachedFromActivityForConfigChanges() {
    Log.d("TAG", "onDetachedFromActivityForConfigChanges: detached for config changes!")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    Log.d("TAG", "onReattachedToActivityForConfigChanges: reattached!")
  }

  override fun onDetachedFromActivity() {
    Log.d("TAG", "onDetachedFromActivity: detached!")
  }
}
