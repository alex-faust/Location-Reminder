package com.udacity.project4.locationreminders.data

import androidx.lifecycle.MutableLiveData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersRepository
import kotlinx.coroutines.runBlocking

class FakeAndroidTestRepository: RemindersRepository {

    private var shouldReturnError = false
    var remindersServiceData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()
    private val observableReminders = MutableLiveData<Result<List<ReminderDTO>>>()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Test exception")
        }
        return Result.Success(remindersServiceData.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersServiceData[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error("Test exception")
        }
        remindersServiceData[id]?.let {
            return Result.Success(it)
        }
        return Result.Error("Could not find that id")
    }

    override suspend fun deleteAllReminders() {
        remindersServiceData.clear()
        refreshReminders()
    }

    private suspend fun refreshReminders() {
        observableReminders.value = getReminders()
    }

    fun addReminders(vararg reminders: ReminderDTO) {
        for (reminder in reminders) {
            remindersServiceData[reminder.id] = reminder
        }
        runBlocking { refreshReminders() }
    }
}