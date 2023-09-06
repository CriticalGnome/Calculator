package com.revotechs.calculator.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.revotechs.calculator.R
import com.revotechs.calculator.databinding.ViewCalculatorButtonBinding

class CalculatorButton(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val binding: ViewCalculatorButtonBinding

    var text: String = ""
        get() = binding.text.text.toString()
        set(value) {
            binding.text.text = value
            field = value
        }

    init {
        binding = ViewCalculatorButtonBinding.inflate(LayoutInflater.from(context), this, true)
        if (isInEditMode) LayoutInflater.from(context).inflate(R.layout.view_calculator_button, this)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CalculatorButton,
            0,
            0
        ).apply {
            try {
                getString(R.styleable.CalculatorButton_text)?.let { text = it }
            } finally {
                recycle()
            }
        }
    }
}
