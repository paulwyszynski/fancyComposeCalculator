@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pawys

import com.pawys.fancycomposecalculator.data.repository.SavedHistoryRepository
import com.pawys.fancycomposecalculator.model.Action
import com.pawys.fancycomposecalculator.model.Operand
import com.pawys.fancycomposecalculator.ui.CalculatorViewModel
import com.pawys.fancycomposecalculator.utils.CalculationUtil
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/*
INFO: Set the main dispatcher to a test dispatcher since UIThread is not available on a local JVM.
 This is not required on instrumented tests tho where the real UIThread is available.
 If not doing so, viewModelScope.launch will throw an exception.
 */
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

class CalculationTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun testIntegerOperations() {
        val calculationUtil = CalculationUtil()

        var result = calculationUtil.calculate("2+2")
        assertEquals("4", result)
        assertFalse(result == "4.0")

        result = calculationUtil.calculate("(-2)+10")
        assertEquals("8", result)
        assertFalse(result == "8.0")

        result = calculationUtil.calculate("(-2)*30+10")
        assertEquals("-50", result)
        assertFalse(result == "-50.0")

        result = calculationUtil.calculate("(-2)*(-30)+10")
        assertEquals("70", result)
        assertFalse(result == "70.0")

        result = calculationUtil.calculate("5*3-2*3+123.5+555.55+105000/3")
        assertEquals("35688.05", result)

        result = calculationUtil.calculate("1000/5+200-50*3")
        assertEquals("250", result)

        result = calculationUtil.calculate("(-10)*(-10)+50/2-5")
        assertEquals("120", result)

        result = calculationUtil.calculate("12345+67890-54321*2/3")
        assertEquals("44021", result)

        result = calculationUtil.calculate("10*10-10/10+10")
        assertEquals("109", result)

        result = calculationUtil.calculate("1000*2-500/2+250*3-125+62*2-31+100*3-75+200/4")
        assertEquals("2743", result)

        result = calculationUtil.calculate("5000-2500+1250-625+312-156+100*3-75+200/4")
        assertEquals("3556", result)

        result = calculationUtil.calculate("10000/5+2000-500*3+1000*2-250+500/2")
        assertEquals("4500", result)

        result = calculationUtil.calculate("(-100)*(-10)+500/2-50+1000*3-750+2000/4")
        assertEquals("3950", result)

        result = calculationUtil.calculate("12345+67890-54321*2/3+1000-500*2+250+10000-5000+2500-1250")
        assertEquals("50521", result)

        result = calculationUtil.calculate("1000-500+250-125+62-31+100*3-75+200/4+5000-2500+1250-625")
        assertEquals("4056", result)

        result = calculationUtil.calculate("2000*2-1000/2+500*3-250+125*2-62+200*3-150+400/4")
        assertEquals("5488", result)

        result = calculationUtil.calculate("10000-5000+2500-1250+625-312+200*3-150+400/4")
        assertEquals("7113", result)

        result = calculationUtil.calculate("20000/5+4000-1000*3+2000*2-500+1000/2")
        assertEquals("9000", result)

        result = calculationUtil.calculate("(-200)*(-10)+1000/2-100+2000*3-1500+4000/4")
        assertEquals("7900", result)

        result = calculationUtil.calculate("24690+135780-108642*2/3+2000-1000*2+500+20000-10000+5000-2500")
        assertEquals("101042", result)

        result = calculationUtil.calculate("2000-1000+500-250+125-62+200*3-150+400/4+10000-5000+2500-1250")
        assertEquals("8113", result)

        result = calculationUtil.calculate("20*20-20/20+20+200*2-100+50*3-150+2000*2-1000/2+500*3-250")
        assertEquals("5469", result)

        result = calculationUtil.calculate("200-100+50-25+12-6+200*3-150+400/4+2000-1000+500-250")
        assertEquals("1931", result)
    }

    @Test
    fun testFloatingPointOperations() {
        val calculationUtil = CalculationUtil()
        var result = calculationUtil.calculate("2.5+2.5")

        assertEquals("5", result)
        assertFalse(result == "5.0")

        result = calculationUtil.calculate("2.43+0.02")
        assertEquals("2.45", result)

        result = calculationUtil.calculate("1000-500+250-125+62.5-31.25")
        assertEquals("656.25", result)

        result = calculationUtil.calculate("100-50+25-12.5+6.25-3.125")
        assertEquals("65.625", result)

        result = calculationUtil.calculate("1+2-3*4/5+6-7*8/9+10")
        assertEquals("10.378", result)

        result = calculationUtil.calculate("2+4-6*8/10+12-14*16/18+20+200*2-100+50*3-150+2000*2-1000/2+500*3-250")
        assertEquals("5070.756", result)

        result = calculationUtil.calculate("1+2-3*4/5+6-7*8/9+10+100*2-50+25*3-75+1000*2-500/2+250*3-125")
        assertEquals("2535.378", result)

        result = calculationUtil.calculate("1234.567+8901.234-5678.901*2/3+456.789-123.456*2+789.012/3")
        assertEquals("6822.748", result)

        result = calculationUtil.calculate("9876.543+1234.567-8901.234*2/3+456.789-123.456*2+789.012/3")
        assertEquals("5649.835", result)

        result = calculationUtil.calculate("12345.678+98765.432-54321.098*2/3+4567.890-1234.567*2+7890.123/3")
        assertEquals("79625.842", result)

        result = calculationUtil.calculate("2345.678+8765.432-4321.098*2/3+345.678-123.456*2+678.901/3")
        assertEquals("8555.444", result)

        result = calculationUtil.calculate("3456.789+7654.321-3210.987*2/3+234.567-123.456*2+567.890/3")
        assertEquals("9147.404", result)
    }

    @Test
    fun testViewModel() {
        runTest {
            // Test some edge cases here...
            val calculationUtil = CalculationUtil()
            val repositoryMock = mockk<SavedHistoryRepository>()
            coEvery { repositoryMock.save(any()) } just Runs
            val viewModel = CalculatorViewModel(calculationUtil, repositoryMock)

            viewModel.onCalculatorAction(Action.Value("1"))
            viewModel.onCalculatorAction(Action.FloatPoint())
            viewModel.onCalculatorAction(Action.Value("2"))
            assertEquals("1.2", viewModel.uiState.value.outputString)

            viewModel.onCalculatorAction(Action.Clear)
            assertEquals("", viewModel.uiState.value.outputString)

            viewModel.onCalculatorAction(Action.Value("1111"))
            viewModel.onCalculatorAction(Action.Remove)
            assertEquals("111", viewModel.uiState.value.outputString)
            viewModel.onCalculatorAction(Action.Clear)

            viewModel.onCalculatorAction(Action.Value("10"))
            viewModel.onCalculatorAction(Action.Operation(Operand.Plus()))
            viewModel.onCalculatorAction(Action.Calculation)
            assertEquals("10+", viewModel.uiState.value.outputString)
            viewModel.onCalculatorAction(Action.Clear)

            viewModel.onCalculatorAction(Action.Value("10"))
            viewModel.onCalculatorAction(Action.FloatPoint())
            viewModel.onCalculatorAction(Action.Operation(Operand.Plus()))
            assertEquals("10+", viewModel.uiState.value.outputString)
            viewModel.onCalculatorAction(Action.Clear)

            viewModel.onCalculatorAction(Action.Value("1000"))
            viewModel.onCalculatorAction(Action.FloatPoint())
            viewModel.onCalculatorAction(Action.Invert)
            assertEquals("(-1000)", viewModel.uiState.value.outputString)
            viewModel.onCalculatorAction(Action.Clear)

            viewModel.onCalculatorAction(Action.Value("1000"))
            viewModel.onCalculatorAction(Action.Operation(Operand.Plus()))
            viewModel.onCalculatorAction(Action.Value("2.345"))
            viewModel.onCalculatorAction(Action.Invert)
            assertEquals("1000+(-2.345)", viewModel.uiState.value.outputString)
            viewModel.onCalculatorAction(Action.Invert)
            assertEquals("1000+2.345", viewModel.uiState.value.outputString)
            viewModel.onCalculatorAction(Action.Clear)

            viewModel.onCalculatorAction(Action.Value("1000"))
            viewModel.onCalculatorAction(Action.Invert)
            viewModel.onCalculatorAction(Action.Operation(Operand.Plus()))
            viewModel.onCalculatorAction(Action.Value("2.345"))
            viewModel.onCalculatorAction(Action.Invert)
            assertEquals("(-1000)+(-2.345)", viewModel.uiState.value.outputString)
            viewModel.onCalculatorAction(Action.Invert)
            assertEquals("(-1000)+2.345", viewModel.uiState.value.outputString)
            viewModel.onCalculatorAction(Action.Clear)

            viewModel.onCalculatorAction(Action.Value("1"))
            viewModel.onCalculatorAction(Action.Value("0"))
            viewModel.onCalculatorAction(Action.Value("0"))
            viewModel.onCalculatorAction(Action.Value("0"))
            viewModel.onCalculatorAction(Action.Invert)
            viewModel.onCalculatorAction(Action.Operation(Operand.Plus()))
            viewModel.onCalculatorAction(Action.Value("2"))
            viewModel.onCalculatorAction(Action.FloatPoint())
            viewModel.onCalculatorAction(Action.Value("3"))
            viewModel.onCalculatorAction(Action.Value("4"))
            viewModel.onCalculatorAction(Action.Value("5"))
            viewModel.onCalculatorAction(Action.Invert)
            assertEquals("(-1000)+(-2.345)", viewModel.uiState.value.outputString)
            viewModel.onCalculatorAction(Action.Invert)
            assertEquals("(-1000)+2.345", viewModel.uiState.value.outputString)
            viewModel.onCalculatorAction(Action.Clear)
        }
    }
}
