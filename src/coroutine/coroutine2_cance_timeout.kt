package coroutine

import kotlinx.coroutines.*


/**
 *  取消与超时
 */

/**
 *  取消协程的执行
 */
//fun main() = runBlocking<Unit> {
//    val job = launch {
//        repeat(1000) { i ->
//            println("job: I'm sleeping $i ...")
//            delay(500L)
//        }
//    }
//    delay(1300L) // 延迟一段时间
//    println("main: I'm tired of waiting!")
//    job.cancel() // 取消该作业
//    job.join() // 等待作业执行结束
//    println("main: Now I can quit.")
//}

/**
 *  取消是协作的
 *  一段协程代码必须协作才能被取消
 *  如果协程正在执行计算任务，并且没有检查取消的话，那么它是不能被取消的
 */
//fun main() = runBlocking<Unit> {
//    val startTime = System.currentTimeMillis()
//    val job = launch(Dispatchers.Default) {
//        var nextPrintTime = startTime
//        var i = 0
//        while (i < 5) { // 一个执行计算的循环，只是为了占用 CPU
//            // 每秒打印消息两次
//            if (System.currentTimeMillis() >= nextPrintTime) {
//                println("job: I'm sleeping ${i++} ...")
//                nextPrintTime += 500L
//            }
//        }
//    }
//    delay(1300L) // 等待一段时间
//    println("main: I'm tired of waiting!")
//    job.cancelAndJoin() // 取消一个作业并且等待它结束
//    println("main: Now I can quit.")
//}


/**
 *  使计算代码可取消，有两种办法
 *  1.定期调用挂起函数来检查取消，用yield
 *  2.显式的检查取消状态
 *  此处为方法2. isActive 是一个可以被使用在 CoroutineScope 中的扩展属性
 */
//fun main() = runBlocking<Unit> {
//    val startTime = System.currentTimeMillis()
//    val job = launch(Dispatchers.Default) {
//        var nextPrintTime = startTime
//        var i = 0
//        while (isActive) { // 可以被取消的计算循环
//            // 每秒打印消息两次
//            if (System.currentTimeMillis() >= nextPrintTime) {
//                println("job: I'm sleeping ${i++} ...")
//                nextPrintTime += 500L
//            }
//        }
//    }
//    delay(1300L) // 等待一段时间
//    println("main: I'm tired of waiting!")
//    job.cancelAndJoin() // 取消该作业并等待它结束
//    println("main: Now I can quit.")
//}


/**
 *  在finally中释放资源
 *  处理在被取消时抛出 CancellationException 的可被取消的挂起函数
 */
//fun main() = runBlocking<Unit> {
//    val job = launch {
//        try {
//            repeat(1000) { i ->
//                println("job: I'm sleeping $i ...")
//                delay(500L)
//            }
//        } finally {
//            println("job: I'm running finally")
//        }
//    }
//    delay(1300L) // 延迟一段时间
//    println("main: I'm tired of waiting!")
//    job.cancelAndJoin() // 取消该作业并且等待它结束
//    println("main: Now I can quit.")
//}

/**
 *  运行不能取消的代码块
 *  如果去掉withContext，那么delay是会被取消的
 */
//fun main() = runBlocking<Unit> {
//    val job = launch {
//        try {
//            repeat(1000) { i ->
//                println("job: I'm sleeping $i ...")
//                delay(500L)
//            }
//        } finally {
//            withContext(NonCancellable) {
//                println("job: I'm running finally")
//                delay(1000L)
//                println("job: And I've just delayed for 1 sec because I'm non-cancellable")
//            }
//        }
//    }
//    delay(1300L) // 延迟一段时间
//    println("main: I'm tired of waiting!")
//    job.cancelAndJoin() // 取消该作业并等待它结束
//    println("main: Now I can quit.")
//}

/**
 *  超时，抛出异常TimeoutCancellationException
 *  之前没抛出异常是因为：
 *  被取消的协程中 CancellationException 被认为是协程执行结束的正常原因
 */
//fun main() = runBlocking<Unit> {
//    withTimeout(1300L) {
//        repeat(1000) { i ->
//            println("I'm sleeping $i ...")
//            delay(500L)
//        }
//    }
//}

/**
 *   withTimeoutOrNull 通过返回 null 来进行超时操作，从而替代抛出一个异常
 */
fun main() = runBlocking<Unit> {
    val result = withTimeoutOrNull(1300L) {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
        "Done" // 在它运行得到结果之前取消它
    }
    println("Result is $result")
}

