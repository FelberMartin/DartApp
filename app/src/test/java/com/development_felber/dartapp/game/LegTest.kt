//package com.development_felber.dartapp.game
//
//import com.development_felber.dartapp.game.gameaction.AddServeGameAction
//import com.google.common.truth.Truth.assertThat
//import org.junit.Test
//
//class LegTest {
//
//    @Test
//    fun getAverage() {
//        val leg = Leg()
//        leg.applyAction(AddServeGameAction(180))
//        leg.applyAction(AddServeGameAction(180))
//        leg.applyAction(AddServeGameAction(139))
//        leg.applyAction(AddServeGameAction(2))
//        leg.unusedDartCount = 2
//
//        val dartCount = 10.0
//
//        val expected = 501.0 / (dartCount / 3.0)
//        assertThat(leg.getAverage()).isEqualTo(expected)
//    }
//
//    @Test
//    fun `try to finish outside of finish range (without double), invalid serve`() {
//        val leg = Leg()
//        leg.applyAction(AddServeGameAction(180))
//        leg.applyAction(AddServeGameAction(141))
//        val valid = leg.isNumberValid(180, singleDart = false)
//        assertThat(valid).isFalse()
//    }
//
//}