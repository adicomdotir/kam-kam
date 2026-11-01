package ir.adicom.kamkam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// --- Model ---
data class User(val name: String)

// --- Repository interface ---
interface UserRepository {
    suspend fun getUser(): User
}

// --- UI State ---
sealed interface UiState {
    object Loading : UiState
    data class Success(val user: User) : UiState
    data class Error(val message: String) : UiState
}

// --- ViewModel ---
class MyViewModel(private val repo: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


    fun loadData() {
        viewModelScope.launch {

            _uiState.value = UiState.Loading
            try {
                val user = repo.getUser()
                _uiState.value = UiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown")
            }
        }
    }
}