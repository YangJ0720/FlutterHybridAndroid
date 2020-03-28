package com.hybrid.android.channel

import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel

/**
 * @author YangJ
 */
class FlutterEventChannel : EventChannel.StreamHandler {

    private var mEvents: EventChannel.EventSink? = null

    companion object {

        private const val FLUTTER_EVENT_CHANNEL = "event_channel"

        fun create(messenger: BinaryMessenger): FlutterEventChannel {
            return FlutterEventChannel(messenger)
        }

    }

    constructor(messenger: BinaryMessenger) {
        val channel = EventChannel(messenger, FLUTTER_EVENT_CHANNEL)
        channel.setStreamHandler(this)
    }

    fun sendEvent(data: Any) {
        mEvents?.success(data)
    }

    fun endOfStream() {
        mEvents?.endOfStream()
    }

    fun getEventSink(): EventChannel.EventSink? {
        return mEvents
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        mEvents = events
    }

    override fun onCancel(arguments: Any?) {
        mEvents = null
    }
}