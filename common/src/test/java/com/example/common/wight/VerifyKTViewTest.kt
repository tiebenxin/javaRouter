package com.example.common.wight

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Liszt on 2019/6/5.
 */
class VerifyKTViewTest {
    @Test
    fun onMeasure() {
        //二维int数组
        var linePosition = Array(3, { Array(4, { it -> 0 }) })
        linePosition[0][0] = 32
        println(linePosition.size)
        println(linePosition[0].size)

        //二维String数组
        var saa = Array(3, { Array(4, { it -> "" }) })
        saa[0][0] = "A"
        println("" + linePosition[0][0] + "====" + saa[0][0])


        //二维String数组
        var a = Array(3, { arrayOfNulls<String>(4) })
        a[0][0] = "哈哈哈"
        a[2][3] = "0000"
//        a[2][4] = "1111"//索引越界
        println(a[0][0] + "----" + a[2][3])
        println(a.size)
        println(a[0].size)

//        var size:Int = 5
//        for(i in 0 until size){
//            println("当前位置：：" + i)
//        }
    }

}