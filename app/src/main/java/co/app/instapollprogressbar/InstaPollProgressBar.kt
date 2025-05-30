package co.app.instapollprogressbar

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class InstaPollProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val foregroundBar: View
    private val backgroundBar: View
    private val textView: TextView
    private val percentageText: TextView

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.poll_loader, this, true)
        backgroundBar = root.findViewById(R.id.backgroundBar)
        foregroundBar = root.findViewById(R.id.foregroundBar)
        textView = root.findViewById(R.id.textView)
        percentageText = root.findViewById(R.id.percentageText)
    }

    fun setLabel(text: String) {
        textView.text = text
    }

    fun setProgress(progress: Int) {
        // Ensure layout has been measured
        post {
            val totalWidth = backgroundBar.width
            val targetWidth = totalWidth * progress / 100

            val animator = ValueAnimator.ofInt(foregroundBar.width, targetWidth).apply {
                duration = 500
                addUpdateListener {
                    val value = it.animatedValue as Int
                    foregroundBar.layoutParams.width = value
                    foregroundBar.requestLayout()
                }
            }
            animator.start()

            // Optional: animate text coloring based on filled width
            animateTextColor(progress)

            // Animate percentage text
            percentageText.text = "$progress%"
            percentageText.visibility = View.VISIBLE
        }
    }

    private fun animateTextColor(progress: Int) {
        val text = textView.text.toString()
        val paint = textView.paint
        val totalWidth = backgroundBar.width
        val fillWidth = totalWidth * progress / 100

        var cutoffIndex = text.length
        var measuredWidth = 0f

        for (i in text.indices) {
            measuredWidth += paint.measureText(text[i].toString())
            if (measuredWidth > fillWidth) {
                cutoffIndex = i
                break
            }
        }

        val spannable = SpannableString(text)
        spannable.setSpan(ForegroundColorSpan(Color.WHITE), 0, cutoffIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(Color.BLACK), cutoffIndex, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannable
    }

}
