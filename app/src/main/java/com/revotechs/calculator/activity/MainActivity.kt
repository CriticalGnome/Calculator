package com.revotechs.calculator.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.revotechs.calculator.R
import com.revotechs.calculator.dao.HistoryDao
import com.revotechs.calculator.databinding.ActivityMainBinding
import com.revotechs.calculator.entitiy.HistoryItem
import com.revotechs.calculator.tool.MathParser
import java.text.SimpleDateFormat
import java.util.*

/**
 * Project Calculator
 * Created on 26.05.2017
 * @author CriticalGnome
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var expression: String = NULL_VALUE
        set(value) {
            field = value
            if (mathParser.parse(expression) != 0.0) {
                binding.resultView.text = mathParser.parse(expression).toString()
            }
            binding.currentView.text = expression

        }

    private val mathParser = MathParser()
    private val historyDao = HistoryDao()

    private val contract = ActivityResultContracts.StartActivityForResult()
    private val activityResultLauncher = registerForActivityResult(contract) { result ->
        if (result.resultCode == RESULT_OK) {
            expression = result.data?.getStringExtra("expression") ?: NULL_VALUE
            binding.resultView.text = mathParser.parse(expression).toString()
            binding.currentView.text = expression
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                activityResultLauncher.launch(Intent(this, HistoryActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        initFields()

        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            initAdditionalFields()
        }

        binding.resultView.text = NULL_VALUE
        binding.currentView.text = expression
    }

    private fun initFields() = with(binding) {
        // Numbers
        button0.setOnClickListener { addToNullValue("0") }
        button1.setOnClickListener { addToNullValue("1") }
        button2.setOnClickListener { addToNullValue("2") }
        button3.setOnClickListener { addToNullValue("3") }
        button4.setOnClickListener { addToNullValue("4") }
        button5.setOnClickListener { addToNullValue("5") }
        button6.setOnClickListener { addToNullValue("6") }
        button7.setOnClickListener { addToNullValue("7") }
        button8.setOnClickListener { addToNullValue("8") }
        button9.setOnClickListener { addToNullValue("9") }
        // Math
        buttonComma.setOnClickListener { expression += "." }
        buttonAdd.setOnClickListener { expression += "+" }
        buttonSub.setOnClickListener { expression += "-" }
        buttonMul.setOnClickListener { expression += "*" }
        buttonDiv.setOnClickListener { expression += "/" }
        //Reset
        buttonReset.setOnClickListener { expression = NULL_VALUE }
        // Backspace
        buttonBack.setOnClickListener { expression = if (expression.isNotEmpty()) expression.dropLast(1) else NULL_VALUE }
        // Equal
        buttonEqual.setOnClickListener {
                val result = mathParser.parse(expression).toString()
                historyDao.create(
                    HistoryItem(
                        null,
                        SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date()),
                        expression,
                        result,
                        null,
                        false
                    ),
                    it.context
                )
                expression = result
        }
        // Switch to history
        currentView.setOnClickListener {
            activityResultLauncher.launch(Intent(this@MainActivity, HistoryActivity::class.java))
        }
    }

    private fun initAdditionalFields() = with(binding) {
        // Braces
        buttonLeftBrace?.setOnClickListener { addToNullValue("(") }
        buttonRightBrace?.setOnClickListener { addToNullValue(")") }
        // Functions
        buttonSin?.setOnClickListener { addToNullValue("sin(") }
        buttonCos?.setOnClickListener { addToNullValue("cos(") }
        buttonTan?.setOnClickListener { addToNullValue("tan(") }
        buttonCtg?.setOnClickListener { addToNullValue("ctg(") }
        buttonLn?.setOnClickListener { addToNullValue("ln(") }
        buttonLg?.setOnClickListener { addToNullValue("lg(") }
        buttonSqrt?.setOnClickListener { addToNullValue("sqrt(") }
        // Constants
        buttonE?.setOnClickListener { addToNullValue("e") }
        buttonPi?.setOnClickListener { addToNullValue("pi") }
        // Symbols
        buttonExponentiation?.setOnClickListener { expression += "^" }
    }

    private fun addToNullValue(additional: String) {
        expression = if (expression == NULL_VALUE) additional else expression + additional
    }

    companion object {
        private const val DATE_FORMAT = "EEEE, dd MMMM yyyy, H:mm:ss"
        private const val NULL_VALUE = "0"
    }
}
