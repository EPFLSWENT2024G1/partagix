package com.android.partagix.model

import android.R.attr.text
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Typeface
import androidx.lifecycle.ViewModel
import com.android.partagix.model.stampDimension.StampDimension
import qrcode.QRCode
import qrcode.QRCodeBuilder


class StampViewModel(itemId: String, label : String, dim : StampDimension) : ViewModel() {

    init {
        val squares = QRCode.ofRoundedSquares()
        setSize(squares, itemId, label, dim)
        val qrCode = squares.build(itemId).render()
    }

    private fun setSize(squares: QRCodeBuilder, itemId : String, label: String, dim: StampDimension) {
        when(dim) {
            StampDimension.SMALL -> squares.withSize(5)
            StampDimension.MEDIUM -> squares.withSize(10)
            StampDimension.BIG -> squares.withSize(15)
            StampDimension.FULL_PAGE -> squares.withSize(20)
        }
    }
    private fun addLabel(pngBytes: ByteArray, label: String) {

    }
}
