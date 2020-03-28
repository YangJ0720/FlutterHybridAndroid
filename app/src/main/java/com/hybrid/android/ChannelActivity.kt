package com.hybrid.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hybrid.android.channel.FlutterEventChannel
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StringCodec
import kotlinx.android.synthetic.main.activity_channel.*
import java.util.*

private const val METHOD_NAME_LOGIN = "login"

/**
 * @author YangJ
 */
class ChannelActivity : AppCompatActivity() {

    companion object {
        fun launchBasicMessage(context: Context) {
            val intent = Intent(context, ChannelActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var mEngine: FlutterEngine
    // BasicMessageChannel
    private var mBasicMessageChannel: BasicMessageChannel<String>? = null
    // MethodChannel
    private var mMethodChannel: MethodChannel? = null
    // EventChannel
    private lateinit var mEventChannel: FlutterEventChannel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)
        initData()
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mEventChannel.endOfStream()
    }

    private fun initData() {
        // 初始化FlutterEngine
        val engine = FlutterEngine(this)
        // 初始化路由
        engine.navigationChannel.setInitialRoute("initView")
        // 开始执行Dart代码以预热FlutterEngine
        engine.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
        FlutterEngineCache.getInstance().put("initView", engine)
        //
        this.mEngine = engine
        //
        mEventChannel = FlutterEventChannel.create(engine.dartExecutor)
    }

    private fun initView() {
        btnBasicMessageChanner.setOnClickListener {
            executeBasicMessageChannel()
        }
        btnMethodChanner.setOnClickListener {
            executeMethodChannel()
        }
        btnEventChanner.setOnClickListener {
            executeEventChannel()
        }
        // 初始化FlutterView
        val view = FlutterView(this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        view.attachToFlutterEngine(mEngine)
        frameLayout.addView(view, params)
    }

    private fun login(text: String): String {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        return "Native method invoke success"
    }

    private fun executeBasicMessageChannel() {
        if (mBasicMessageChannel == null) {
            val engine = mEngine
            val name = "basic_message_channel"
            val channel = BasicMessageChannel(engine.dartExecutor, name, StringCodec.INSTANCE)
            channel.setMessageHandler { message, reply ->
                val text = "Flutter传过来的消息: $message"
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                // 接收到Flutter的消息后，可以立即回复一个消息
                reply.reply("Native收到消息: $message")
            }
            mBasicMessageChannel = channel
        }
        // 主动向Flutter发送消息
        mBasicMessageChannel?.send("Native Message: ${System.currentTimeMillis()}")
    }

    private fun executeMethodChannel() {
        if (mMethodChannel == null) {
            val engine = mEngine
            val name = "method_channel"
            val channel = MethodChannel(engine.dartExecutor, name)
            channel.setMethodCallHandler { call, result ->
                when (val method = call.method) {
                    METHOD_NAME_LOGIN -> {
                        val text = "call.method = $method, arguments = ${call.arguments}"
                        val msg = login(text)
                        result.success(msg)
                    }
                    else -> {
                        result.error("404", "Can't find method", "Can't find method: $method")
                    }
                }

            }
            mMethodChannel = channel
        }
        // 调用Flutter的方法
        mMethodChannel?.invokeMethod("method", Random().nextInt())
    }

    /**
     * 将消息推送到Flutter
     */
    private fun executeEventChannel() {
        mEventChannel.sendEvent(System.currentTimeMillis())
    }

}