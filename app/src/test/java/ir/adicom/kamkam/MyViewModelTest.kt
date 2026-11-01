package ir.adicom.kamkam

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class FakeUserRepository(
    private val result: Result<User> = Result.success(User("Test User"))
) : UserRepository {
    override suspend fun getUser(): User = result.getOrThrow()
}

@OptIn(ExperimentalCoroutinesApi::class)
class MyViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)   // <-- fixes "Main dispatcher" crash
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadData emits Success state`() = runTest {
        val viewModel = MyViewModel(FakeUserRepository())

        viewModel.loadData()

        // Let viewModelScope.launch run
        advanceUntilIdle()

        // Replace Turbine with a direct .value read on StateFlow
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals("Test User", (state as UiState.Success).user.name)
    }

    @Test
    fun `loadData emits Error state on failure`() = runTest {
        val fake = FakeUserRepository(Result.failure(RuntimeException("boom")))
        val viewModel = MyViewModel(fake)

        viewModel.loadData()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals("boom", (state as UiState.Error).message)
    }
}