package dd.wan.thread

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import java.lang.Thread.currentThread
import java.util.*
import kotlin.math.abs
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    val MSG_PLUS: Int = 10
    var y1 = 0
    var y2 = 0
    var check = true
    val mHandler2 = Handler(Looper.getMainLooper())
    var mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_PLUS -> textView.text = msg.arg1.toString()
            }
        }
    }

    var thread = Thread()
    var runnable = Runnable {
        var count = textView.text.toString().toInt();
        while (check && count != 0) {
            if (count < 0)
                count++
            else count--
            val message = Message()
            message.what = MSG_PLUS
            message.arg1 = count
            try {
                Thread.sleep(100)
                mHandler.sendMessage(message)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ramdomColor()
        layout.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        stopThread()
                        y1 = event.y.toInt()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        y2 = event.y.toInt()
                        if (y2 < y1) {
                            textView.text =
                                (textView.text.toString().toInt() + 1).toString()
                        } else {
                            textView.text =
                                (textView.text.toString().toInt() - 1).toString()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        startThread()
                        return true
                    }
                }
                return true
            }
        })

        Plus.setOnClickListener {
            textView.text =
                (textView.text.toString().toInt() + 1).toString()
        }
        Plus.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Plus.setBackgroundColor(Color.parseColor("#FF36C538"))
                        stopThread()
                        mHandler2.postDelayed(runnable1, 100)
                    }
                    MotionEvent.ACTION_UP -> {
                        mHandler2.removeCallbacks(runnable1)
                        Plus.setBackgroundColor(Color.parseColor("#36BFC5"))
                        startThread()
                    }
                }
                return false
            }

            var runnable1: Runnable = object : Runnable {
                override fun run() {
                    textView.text =
                        (textView.text.toString().toInt() + 1).toString()
                    mHandler2.postDelayed(this, 100)
                }
            }
        })

        Minus.setOnClickListener {
            textView.text =
                (textView.text.toString().toInt() - 1).toString()
        }
        Minus.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Minus.setBackgroundColor(Color.parseColor("#FF36C538"))
                        stopThread()
                        mHandler2.postDelayed(runnable1, 100)
                    }
                    MotionEvent.ACTION_UP -> {
                        mHandler2.removeCallbacks(runnable1)
                        Minus.setBackgroundColor(Color.parseColor("#36BFC5"))
                        startThread()
                    }
                }
                return false
            }

            var runnable1: Runnable = object : Runnable {
                override fun run() {
                    textView.text =
                        (textView.text.toString().toInt() - 1).toString()
                    mHandler2.postDelayed(this, 100)
                }
            }
        })
    }

    fun startThread() {
        synchronized(this) { check = true }
        thread = Thread(runnable)
        mHandler.postDelayed({ thread.start() }, 2000)
    }

    fun stopThread() {
        synchronized(this) { check = false }
        mHandler.removeCallbacksAndMessages(null)
        if (!thread.isInterrupted)
            thread.interrupt()
    }

    fun ramdomColor() {
        var threadColor = Thread(Runnable {
            val rnd = Random()
            while (true) {
                var count = textView.text.toString().toInt()
                if (count != 0) {
                    val color: Int =
                        Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                    try {
                        Thread.sleep(4000)
                        textView.setTextColor(color)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        })
        threadColor.start()
    }
}


// CÁCH 2 SỬ DỤNG HÀM onLongClick

//        Plus.setOnClickListener {
//            textView.text =
//                (textView.text.toString().toInt() + 1).toString()
//        }
//        Plus.setOnLongClickListener(object : OnLongClickListener {
//            private val mHandler2 = Handler()
//            private val incrementRunnable: Runnable = object : Runnable {
//                override fun run() {
//                    mHandler2.removeCallbacks(this)
//                    if (Plus.isPressed) {
//                        Plus.setBackgroundColor(Color.parseColor("#FF36C538"))
//                        stopThread()
//                        textView.text =
//                            (textView.text.toString().toInt() + 1).toString()
//                        mHandler2.post(this)
//                    } else {
//                        Plus.setBackgroundColor(Color.parseColor("#36BFC5"))
//                        startThread()
//                    }
//                }
//            }
//
//            override fun onLongClick(view: View): Boolean {
//                mHandler2.postDelayed(incrementRunnable, 100)
//                return true
//            }
//        })
//        Minus.setOnLongClickListener(object : OnLongClickListener {
//            private val mHandler2 = Handler()
//            private val incrementRunnable: Runnable = object : Runnable {
//                override fun run() {
//                    mHandler2.removeCallbacks(this)
//                    if (Minus.isPressed) {
//                        Plus.setBackgroundColor(Color.parseColor("#FF36C538"))
//                        stopThread()
//                        textView.text =
//                            (textView.text.toString().toInt() - 1).toString()
//                        mHandler2.post(this)
//                    } else {
//                        Plus.setBackgroundColor(Color.parseColor("#36BFC5"))
//                        startThread()
//                    }
//                }
//            }
//
//            override fun onLongClick(view: View): Boolean {
//                mHandler2.postDelayed(incrementRunnable, 100)
//                return true
//            }
//        })
//        Minus.setOnClickListener {
//            textView.text =
//                (textView.text.toString().toInt() - 1).toString()
//        }





