package com.revotechs.calculator.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.revotechs.calculator.R
import com.revotechs.calculator.dao.HistoryDao
import com.revotechs.calculator.entitiy.HistoryItem
import com.revotechs.calculator.tool.MathParser
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Project Calculator
 * Created on 26.05.2017
 * @author CriticalGnome
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val mathParser = MathParser()
    private val historyDao = HistoryDao()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFields()

        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            initAdditionalFields()
        }

        result_view.text = NULL_VALUE
        current_vew.text = expression

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (data != null) {
            expression = data.getStringExtra("expression")
        }
        result_view.text = mathParser.parse(expression).toString()
        current_vew.text = expression
    }

    override fun onClick(v: View) {

        when (v.id) {

            // Numbers
            R.id.button_0 -> expression = (if (expression == NULL_VALUE) "0" else expression + "0")
            R.id.button_1 -> expression = (if (expression == NULL_VALUE) "1" else expression + "1")
            R.id.button_2 -> expression = (if (expression == NULL_VALUE) "2" else expression + "2")
            R.id.button_3 -> expression = (if (expression == NULL_VALUE) "3" else expression + "3")
            R.id.button_4 -> expression = (if (expression == NULL_VALUE) "4" else expression + "4")
            R.id.button_5 -> expression = (if (expression == NULL_VALUE) "5" else expression + "5")
            R.id.button_6 -> expression = (if (expression == NULL_VALUE) "6" else expression + "6")
            R.id.button_7 -> expression = (if (expression == NULL_VALUE) "7" else expression + "7")
            R.id.button_8 -> expression = (if (expression == NULL_VALUE) "8" else expression + "8")
            R.id.button_9 -> expression = (if (expression == NULL_VALUE) "9" else expression + "9")

            // Constants
            R.id.button_e -> expression = (if (expression == NULL_VALUE) "e" else expression + "e")
            R.id.button_pi -> expression = (if (expression == NULL_VALUE) "pi" else expression + "pi")

            // Functions
            R.id.button_sin -> expression = (if (expression == NULL_VALUE) "sin(" else expression + "sin(")
            R.id.button_cos -> expression = (if (expression == NULL_VALUE) "cos(" else expression + "cos(")
            R.id.button_tan -> expression = (if (expression == NULL_VALUE) "tan(" else expression + "tan(")
            R.id.button_ctg -> expression = (if (expression == NULL_VALUE) "ctg(" else expression + "ctg(")
            R.id.button_ln -> expression = (if (expression == NULL_VALUE) "ln(" else expression + "ln(")
            R.id.button_lg -> expression = (if (expression == NULL_VALUE) "lg(" else expression + "lg(")
            R.id.button_sqrt -> expression = (if (expression == NULL_VALUE) "sqrt(" else expression + "sqrt(")

            // Braces
            R.id.button_left_brace -> expression = (if (expression == NULL_VALUE) "(" else expression + "(")
            R.id.button_right_brace -> expression = (if (expression == NULL_VALUE) ")" else expression + ")")

            // Symbols
            R.id.button_exponentiation -> expression += "^"
            R.id.button_comma -> expression += "."
            R.id.button_add -> expression += "+"
            R.id.button_sub -> expression += "-"
            R.id.button_mul -> expression += "*"
            R.id.button_div -> expression += "/"

            // Equal
            R.id.button_equal -> {
                val result = mathParser.parse(expression).toString()
                historyDao.create(
                        HistoryItem(
                            null,
                            SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date()),
                            expression,
                            result,
                            null,
                            false),
                        v.context)
                expression = result
            }

            //Reset
            R.id.button_reset -> {
                expression = NULL_VALUE
            }

            // Backspace
            R.id.button_back -> {
                if (!expression.isEmpty()) {
                    expression = expression.substring(0, expression.length - 1)
                }
                if (expression.isEmpty()) {
                    expression = NULL_VALUE
                }
            }

            // Switch to history
            R.id.current_vew -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivityForResult(intent, RESULT_CODE_FROM_HISTORY)
            }
            else -> {
            }
        }

        if (mathParser.parse(expression) != 0.0) {
            result_view.text = mathParser.parse(expression).toString()
        }
        current_vew.text = expression

    }

    private fun initFields() {

        current_vew.setOnClickListener(this)

        button_0.setOnClickListener(this)
        button_1.setOnClickListener(this)
        button_2.setOnClickListener(this)
        button_3.setOnClickListener(this)
        button_4.setOnClickListener(this)
        button_5.setOnClickListener(this)
        button_6.setOnClickListener(this)
        button_7.setOnClickListener(this)
        button_8.setOnClickListener(this)
        button_9.setOnClickListener(this)
        button_add.setOnClickListener(this)
        button_sub.setOnClickListener(this)
        button_mul.setOnClickListener(this)
        button_div.setOnClickListener(this)
        button_comma.setOnClickListener(this)
        button_reset.setOnClickListener(this)
        button_equal.setOnClickListener(this)
        button_back.setOnClickListener(this)
    }

    private fun initAdditionalFields() {

        button_left_brace.setOnClickListener(this)
        button_right_brace.setOnClickListener(this)
        button_sqrt.setOnClickListener(this)
        button_exponentiation.setOnClickListener(this)
        button_sin.setOnClickListener(this)
        button_cos.setOnClickListener(this)
        button_tan.setOnClickListener(this)
        button_ctg.setOnClickListener(this)
        button_ln.setOnClickListener(this)
        button_lg.setOnClickListener(this)
        button_pi.setOnClickListener(this)
        button_e.setOnClickListener(this)
    }

    fun onClickHistory(item: MenuItem) {
        val context = this@MainActivity
        val intent = Intent(context, HistoryActivity::class.java)
        startActivityForResult(intent, RESULT_CODE_FROM_HISTORY)
    }

    companion object {

        private val DATE_FORMAT = "EEEE, dd MMMM yyyy, H:mm:ss"
        private val NULL_VALUE = "0"
        private var expression = NULL_VALUE
        val RESULT_CODE_FROM_HISTORY = 404
    }
}
