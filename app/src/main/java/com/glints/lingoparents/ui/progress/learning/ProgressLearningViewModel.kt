package com.glints.lingoparents.ui.progress.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.ui.progress.ProgressViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class ProgressLearningViewModel : ViewModel() {
    sealed class ProgressLearningEvent {
        object Loading: ProgressLearningEvent()
    }
}