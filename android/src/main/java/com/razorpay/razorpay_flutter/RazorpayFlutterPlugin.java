package com.razorpay.razorpay_flutter;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * RazorpayFlutterPlugin
 */
public class RazorpayFlutterPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

    private RazorpayDelegate razorpayDelegate;
    private ActivityPluginBinding pluginBinding;
    private static final String CHANNEL_NAME = "razorpay_flutter";
    private MethodChannel channel;
    private Map<String, Object> _arguments;
    private String customerMobile;
    private String color;

    public RazorpayFlutterPlugin() {
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL_NAME);
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        if (channel != null) {
            channel.setMethodCallHandler(null);
            channel = null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMethodCall(MethodCall call, Result result) {
        if (razorpayDelegate == null) {
            result.error("NO_ACTIVITY", "Razorpay plugin not attached to an activity", null);
            return;
        }

        switch (call.method) {
            case "open":
                razorpayDelegate.openCheckout((Map<String, Object>) call.arguments, result);
                break;

            case "setPackageName":
                razorpayDelegate.setPackageName((String) call.arguments);
                break;

            case "resync":
                razorpayDelegate.resync(result);
                break;

            case "setKeyID":
                String key = call.arguments.toString();
                razorpayDelegate.setKeyID(key, result);
                break;

            case "linkNewUpiAccount":
                _arguments = call.arguments();
                customerMobile = (String) _arguments.get("customerMobile");
                color = (String) _arguments.get("color");
                razorpayDelegate.linkNewUpiAccount(customerMobile, color, result);
                break;

            case "manageUpiAccounts":
                _arguments = call.arguments();
                customerMobile = (String) _arguments.get("customerMobile");
                color = (String) _arguments.get("color");
                razorpayDelegate.manageUpiAccounts(customerMobile, color, result);
                break;

            case "isTurboPluginAvailable":
                razorpayDelegate.isTurboPluginAvailable(result);
                break;

            default:
                result.notImplemented();
        }
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.razorpayDelegate = new RazorpayDelegate(binding.getActivity());
        this.pluginBinding = binding;
        binding.addActivityResultListener(razorpayDelegate);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onDetachedFromActivity() {
        if (pluginBinding != null) {
            pluginBinding.removeActivityResultListener(razorpayDelegate);
            pluginBinding = null;
        }
    }
}
