package com.revotechs.calculator.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.revotechs.calculator.R
import com.revotechs.calculator.dao.HistoryDao
import com.revotechs.calculator.entities.HistoryItem
import com.revotechs.calculator.tools.Calculator
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Project Calculator
 * Created on 26.05.2017
 * @author CriticalGnome
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val calculator = Calculator()
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

        result_view.text = calculator.calc(expression).toString()
        current_vew.text = expression

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (data != null) {
            expression = data.getStringExtra("expression")
        }
        result_view.text = calculator.calc(expression).toString()
        current_vew.text = expression
    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.button_reset -> {
                expression = NULL_VALUE
                comma = false
            }
            R.id.button_back -> {
                if (!expression.isEmpty()) {
                    expression = expression.substring(0, expression.length - 1)
                }
                if (expression.isEmpty()) {
                    expression = NULL_VALUE
                }
            }
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
            R.id.button_left_brace -> expression = (if (expression == NULL_VALUE) "(" else expression + "(")
            R.id.button_right_brace -> expression = (if (expression == NULL_VALUE) ")" else expression + ")")
            R.id.button_comma -> if (!comma) {
                expression += "."
                comma = true
            }
            R.id.button_add -> { comma = false; expression += "+" }
            R.id.button_sub -> { comma = false; expression += "-" }
            R.id.button_mul -> { comma = false; expression += "*" }
            R.id.button_div -> { comma = false; expression += "/" }
            R.id.button_equal -> {
                comma = false
                var result = calculator.calc(expression)
                if (result == "Wrong format") {
                    result = R.string.wrong_format.toString()
                }
                if (result == "Division by zero") {
                    result = R.string.division_by_zero.toString()
                }
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
                if (expression.contains(".")) {
                    comma = true
                }
            }
            R.id.current_vew -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivityForResult(intent, RESULT_CODE_FROM_HISTORY)
            }
            else -> {
            }
        }

        result_view.text = calculator.calc(expression).toString()
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
        private var comma: Boolean = false
        val RESULT_CODE_FROM_HISTORY = 404
    }
}
