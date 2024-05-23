package com.android.partagix.utils

import java.util.Calendar
import java.util.Date

fun stripTime(date: Date): Date {
  val calendar = Calendar.getInstance()
  calendar.time = date
  calendar.set(Calendar.HOUR_OF_DAY, 0)
  calendar.set(Calendar.MINUTE, 0)
  calendar.set(Calendar.SECOND, 0)
  calendar.set(Calendar.MILLISECOND, 0)
  return calendar.time
}
