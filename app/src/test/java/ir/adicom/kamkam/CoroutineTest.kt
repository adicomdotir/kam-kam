package ir.adicom.kamkam

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import org.junit.Test

class CoroutineTest {

    @Test
    fun testCancellation() = runBlocking {

        println("1")
        val job = launch {
            println("3")
            try {
                println("4")
                delay(5000)
                println("Done")
            } finally {
                println("8")
                println("Finally")
            }
        }

        println("2")
        delay(500)
        println("5")
        job.cancel()
        println("6")

        try {
            println("7")
            job.join()
            println("Joined")
        } catch (e: CancellationException) {
            println("Join cancelled")
        }

        println("After")
    }

    @Test
    fun testCancellation02() = runBlocking {
        val job = launch {
            try {
                repeat(5) { i ->
                    println("Work $i")
                    delay(1000)
                }
            } finally {
                println("Cleanup")
            }
        }

        delay(1500)
        job.cancelAndJoin()

        println("After")
    }

    @Test
    fun testCancellation03() = runBlocking {
        val job = launch {
            try {
                repeat(5) { i ->
                    println("Work $i")
                    delay(1000)
                }
            } finally {
                println("Cleanup")
            }
        }

        delay(1500)
        job.cancel()

        println("After cancel")

        job.join()

        println("After join")
    }

    @Test
    fun testCancellation04() = runBlocking {
        val job = launch {
            try {
                delay(5000)
                println("Done")
            } finally {
                println("Finally")
                delay(1000)
                println("Cleanup done")
            }
        }

        delay(500)
        job.cancelAndJoin()

        println("After")
    }

    @Test
    fun testCancellation05() = runBlocking {
        val job = launch {
            repeat(5) { i ->
                println("Before $i")

                ensureActive()

                println("After $i")

                delay(1000)
            }
        }

        delay(1500)
        job.cancelAndJoin()

        println("Done")
    }

    @Test
    fun testCancellation06() = runBlocking {
        val job = launch {
            repeat(5) { i ->
                println("Before $i")

                if (isActive) {
                    println("Working $i")
                }

                delay(1000)
            }
        }

        delay(1900)
        job.cancelAndJoin()

        println("Done")
    }

    @Test
    fun testCancellation07() = runBlocking {
        try {
            val job = launch {
                delay(1000)
                throw RuntimeException("Boom")
            }

            job.join()

            println("After")
        } catch (e: Exception) {
            println("Error: " + e.message)
        }
    }

    @Test
    fun testCancellation08() = runBlocking {
        val job = launch {
            delay(5000)
            println("Done")
        }

        job.cancel()
        job.join()

        println("Finished")
    }

    @Test
    fun testCancellation09() = runBlocking {
        coroutineScope {
            launch {
                delay(1000)

                println("A")
            }

            launch {
                delay(3000)
                println("B")
            }
        }

        println("C")
    }

    @Test
    fun testCancellation10() = runBlocking {
        supervisorScope {
            val user = async {
                delay(5000)
                "User"
            }

            val transactions = async {
                delay(1000)
                "Transactions"
            }

            delay(500)

            user.cancel()

            println("After cancel")

            val result = transactions.await()
            println(result)
        }
    }

    @Test
    fun testCancellation11() = runBlocking {
        supervisorScope {
            val user = async {
                throw Exception("User failed")
            }

            val transactions = async {
                delay(3000)
                "Transactions loaded"
            }

            try {
                user.await()
            } catch (e: Exception) {
                println("User error")
            }

            delay(1000)

            val result = transactions.await()
            println(result)
        }
    }

    @Test
    fun testCancellation12() = runBlocking {

        launch {
            try {
                delay(5_000)
            } catch (e: Exception) {
                println("Caught ${e.message}")
            }

            println("Finished")
        }.also {
            delay(200)
            it.cancelAndJoin()
        }

        println("end".uppercase())

    }

    @Test
    fun `test zip & combine`() = runBlocking {
        val flowA = flowOf(1, 2, 3)
        val flowB = flowOf("A", "B")

        println("zip".uppercase())
        flowA.zip(flowB) { a, b ->
            "$a$b"
        }.collect {
            println(it)
        }


        println("combine".uppercase())
        combine(flowA, flowB) {a, b ->
            "$a$b"
        }.collect {
            println(it)
        }
    }

    @Test
    fun testZipWithDelay() = runBlocking {
        val numbers = flowOf(1, 2, 3)
            .onEach { delay(100) }
        println("1")

        val letters = flowOf("A", "B", "C")
            .onEach { delay(300) }

        println("2")
        numbers.zip(letters) { n, l -> "$n$l" }
            .collect { println(it) }
    }
}