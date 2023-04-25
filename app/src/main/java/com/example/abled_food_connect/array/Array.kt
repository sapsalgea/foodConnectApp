package com.example.abled_food_connect.array

interface Array {
   fun numArray(num:Int): ArrayList<Int>



}

class Age  {
     fun numArray(): ArrayList<Int> {
        val list = ArrayList<Int>()

        for (i in 20..100) {
            list.add(i)

        }
        return list
    }

}
class MinimumAge : Array {
    override fun numArray(num:Int): ArrayList<Int> {
        val list = ArrayList<Int>()

        for (i in 20..num) {
            list.add(i)

        }
        return list
    }

}

class MaximumAge : Array {
    override fun numArray(num:Int): ArrayList<Int> {
        val list = ArrayList<Int>()

        for (i in num..100) {
            list.add(i)

        }
        return list
    }

}

class NumOfPeople : Array {
    override fun numArray(num:Int): ArrayList<Int> {
        val list = ArrayList<Int>()

        for (i in 1..num) {
            list.add(i)

        }
        return list
    }
}