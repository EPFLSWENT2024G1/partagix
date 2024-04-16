package com.android.partagix.model

import androidx.lifecycle.ViewModel
import com.android.partagix.model.stampDimension.StampDimension

class StampViewModel(itemId: String) : ViewModel() {

  /**
   * Get the ordinal of the dimension, given the detailedDimension string.
   *
   * @param detailedDimension the detailed dimension string
   * @return the ordinal of the dimension, or -1 if the dimension is not found
   */
  fun getDetailedDimensionOrdinal(detailedDimension: String): Int {
    for (stampDimension in StampDimension.values()) {
      if (stampDimension.detailedDimension == detailedDimension) {
        return stampDimension.ordinal
      }
    }
    return -1
  }
}
