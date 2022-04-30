package com.example.dartapp.graphs

import com.example.dartapp.database.Leg

class StatTypeBase (
    val name: String,
    val legsToNumberReducer: (List<Leg>) -> Number
) {

}