package coroutine

import kotlinx.coroutines.*


/**
 *  组合挂起函数
 */


suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // 假设我们在这里做了一些有用的事
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // 假设我们在这里也做了一些有用的事
    return 29
}

/**
 *  默认顺序调用
 */
//fun main() = runBlocking<Unit> {
//    val time = measureTimeMillis {
//        val one = doSomethingUsefulOne()
//        val two = doSomethingUsefulTwo()
//        println("The answer is ${one + two}")
//    }
//    println("Completed in $time ms")
//}

/**
 *  使用 async 并发
 *  概念上async 就类似于 launch，它启动了一个单独的协程，
 *  与其它所有的协程一起并发的工作，不同之处在
 *  launch返回一个 Job 并且不附带任何结果值,
 *  而 async 返回一个 Deferred
 *
 *  Deferred:一个轻量级的非阻塞 future
 *  可以使用 .await() 在一个延期的值上得到它的最终结果，
 *  Deferred 也是一个 Job，所以如果需要的话，你可以取消它。
 */
//fun main() = runBlocking<Unit> {
//    val time = measureTimeMillis {
//        val one = async { doSomethingUsefulOne() }
//        val two = async { doSomethingUsefulTwo() }
//        println("The answer is ${one.await() + two.await()}")
//    }
//    println("Completed in $time ms")
//}

/**
 *  惰性启动的 async
 *  如果不start，而是直接调用await()那么会顺序执行
 */
//fun main() = runBlocking<Unit> {
//    val time = measureTimeMillis {
//        val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
//        val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
//        // 执行一些计算
//        one.start() // 启动第一个
//        two.start() // 启动第二个
////        val oneValue = one.await()
////        val twoValue = two.await()
////        println("The answer is ${oneValue + twoValue}")
//        println("The answer is ${one.await() + two.await()}")
//    }
//    println("Completed in $time ms")
//}

/**
 *  async 风格的函数
 *  不是挂起函数哦，可以在任何地方使用。
 *  在调用它们的代码中意味着异步（并发）执行。
 */
// somethingUsefulOneAsync 函数的返回值类型是 Deferred<Int>
fun somethingUsefulOneAsync() = GlobalScope.async {
    doSomethingUsefulOne()
}

// somethingUsefulTwoAsync 函数的返回值类型是 Deferred<Int>
fun somethingUsefulTwoAsync() = GlobalScope.async {
    doSomethingUsefulTwo()
}

/**
 *  注意，在这个示例中我们在 `main` 函数的右边没有加上 `runBlocking`
 *  在 Kotlin 的协程中使用这种风格是强烈不推荐的
 */
//fun main() {
//    val time = measureTimeMillis {
//        // 我们可以在协程外面启动异步执行
//        val one = somethingUsefulOneAsync()
//        val two = somethingUsefulTwoAsync()
//        // 但是等待结果必须调用其它的挂起或者阻塞
//        // 当我们等待结果的时候，这里我们使用 `runBlocking { …… }` 来阻塞主线程
//        runBlocking {
//            println("The answer is ${one.await() + two.await()}")
//        }
//    }
//    println("Completed in $time ms")
//}


/**
 *  使用 async 的结构化并发
 *  这种情况下，如果在 concurrentSum 函数内部发生了错误，
 *  并且它抛出了一个异常， 所有在作用域中启动的协程都会被取消
 */
suspend fun concurrentSum(): Int = coroutineScope {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    one.await() + two.await()
}

//fun main() = runBlocking<Unit> {
//    val time = measureTimeMillis {
//        println("The answer is ${concurrentSum()}")
//    }
//    println("Completed in $time ms")
//}

/**
 *  取消始终通过协程的层次结构来进行传递
 *  如果其中一个子协程（即 two）失败，第一个 async 以及等待中的父协程都会被取消
 */
suspend fun failedConcurrentSum(): Int = coroutineScope {
    val one = async<Int> {
        try {
            delay(Long.MAX_VALUE) // 模拟一个长时间的运算
            42
        } finally {
            println("First child was cancelled")
        }
    }
    val two = async<Int> {
        println("Second child throws an exception")
        throw ArithmeticException()
    }
    one.await() + two.await()
}

fun main() = runBlocking<Unit> {
    try {
        failedConcurrentSum()
    } catch(e: ArithmeticException) {
        println("Computation failed with ArithmeticException")
    }
}

