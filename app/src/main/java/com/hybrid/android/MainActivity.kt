package com.hybrid.android

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author YangJ
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        btnFlutterView.setOnClickListener {
            createFlutterView()
            Toast.makeText(this, R.string.flutter_view, Toast.LENGTH_SHORT).show()
        }
        btnFlutterFragment.setOnClickListener {
            createFlutterFragment()
            Toast.makeText(this, R.string.flutter_fragment, Toast.LENGTH_SHORT).show()
        }
        btnChanner.setOnClickListener {
            ChannelActivity.launchBasicMessage(this)
        }
    }

    private fun removeAllView() {
        if (frameLayout.childCount > 0) {
            frameLayout.removeAllViews()
        }
    }

    private fun createFlutterView() {
        removeAllView()
        //
        val view = FlutterView(this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        frameLayout.addView(view, params)
        // 初始化FlutterEngine
        val engine = FlutterEngine(this)
        // 初始化路由
        engine.navigationChannel.setInitialRoute("view_route")
        // 开始执行Dart代码以预热FlutterEngine
        engine.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
        FlutterEngineCache.getInstance().put("initView", engine)
        view.attachToFlutterEngine(engine)
    }

    private fun createFlutterFragment() {
        removeAllView()
        //
        val initialRoute = "view_fragment"
        val builder = FlutterFragment.withNewEngine()
        val fragment = builder.initialRoute(initialRoute).build<FlutterFragment>()
        // 添加fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.frameLayout, fragment)
        transaction.commit()
    }
}
