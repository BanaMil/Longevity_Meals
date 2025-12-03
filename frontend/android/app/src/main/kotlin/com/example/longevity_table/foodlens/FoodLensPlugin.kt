package com.example.frontend.foodlens

import android.app.Activity
import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

class FoodLensPlugin : FlutterPlugin, ActivityAware, MethodCallHandler {
    private lateinit var channel: MethodChannel
    private var appContext: Context? = null
    private var activity: Activity? = null

    // 예: 실제 SDK 인스턴스
    // private var sdk: FoodLensSdk? = null

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        appContext = binding.applicationContext
        channel = MethodChannel(binding.binaryMessenger, "foodlens_bridge")
        channel.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        // sdk = null
        appContext = null
    }

    // ActivityAware
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        // 권한 콜백 필요 시 binding.addRequestPermissionsResultListener(...)
    }
    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }
    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }
    override fun onDetachedFromActivity() {
        activity = null
    }

    // MethodChannel
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "initFoodlens" -> handleInit(call, result)
            "analyzeImage" -> handleAnalyze(call, result)
            else -> result.notImplemented()
        }
    }

    private fun handleInit(call: MethodCall, result: MethodChannel.Result) {
        val token = call.argument<String>("token")
        if (token.isNullOrBlank()) {
            result.error("ARG", "token missing", null); return
        }
        val region = call.argument<String>("region")
        val options = call.argument<Map<String, Any>>("options") ?: emptyMap()

        val act = activity ?: run {
            result.error("STATE", "Activity is null", null); return
        }
        // 실제 SDK 초기화 예시
        // sdk = FoodLensSdk.initialize(
        //     context = act,
        //     token = token,
        //     region = region,
        //     options = options
        // )

        result.success(null)
    }

    private fun handleAnalyze(call: MethodCall, result: MethodChannel.Result) {
        val path = call.argument<String>("path")
        if (path.isNullOrBlank()) {
            result.error("ARG", "path missing", null); return
        }
        val options = call.argument<Map<String, Any>>("options") ?: emptyMap()

        // val s = sdk ?: run { result.error("STATE", "SDK not initialized", null); return }
        // 비동기 호출 예시 (SDK 콜백에 맞춰 구현)
        // s.analyze(path, options, onSuccess = { analysis ->
        //     result.success(analysis.toMap()) // Map<String, Any>
        // }, onError = { e ->
        //     result.error("SDK", e.message, null)
        // })

        // 샘플 스텁 응답
        val sample: Map<String, Any> = mapOf(
            "items" to emptyList<Any>(),
            "meta" to mapOf("path" to path, "options" to options)
        )
        result.success(sample)
    }
}
