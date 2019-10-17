package coroutine

import kotlinx.coroutines.*

/**
 *  协程基础
 */

/**
 * 第一个协程实例
 */
//fun main() {
//    GlobalScope.launch { // 在后台启动一个新的协程并继续
//        delay(1000L) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
//        println("World!") // 在延迟后打印输出
//    }
//    println("Hello,") // 协程已在等待时主线程还在继续
//    Thread.sleep(2000L) // 阻塞主线程 2 秒钟来保证 JVM 存活
//}

/**
 *  本质上协程是轻量级的线程
 *  delay是一个特殊的挂起函数，不会造成线程阻塞，但会挂起协程，并只能在协程中使用。
 */
//fun main() {
//    thread {
//        Thread.sleep(1000L)
//        println("World!")
//    }
//    println("Hello,")
//    Thread.sleep(2000L)
//}

/**
 *  显示使用runBlocking协程构建器来阻塞主线程
 */
//fun main() {
//    GlobalScope.launch { // 在后台启动一个新的协程并继续
//        delay(1000L)
//        println("World!")
//    }
//    println("Hello,") // 主线程中的代码会立即执行
//    runBlocking {     // 但是这个表达式阻塞了主线程
//        delay(2000L)  // ……我们延迟 2 秒来保证 JVM 的存活
//    }
//}


/**
 *  使用 runBlocking 来包装 main 函数的执行
 */
//fun main() = runBlocking<Unit> { // 开始执行主协程
//    GlobalScope.launch { // 在后台启动一个新的协程并继续
//        delay(1000L)
//        println("World!")
//    }
//    println("Hello,") // 主协程在这里会立即执行
//    delay(2000L)      // 延迟 2 秒来保证 JVM 存活
//}

/**
 *  显式（以非阻塞方式）等待所启动的后台 Job 执行结束
 */
//fun main() = runBlocking<Unit> {
//    val job = GlobalScope.launch { // 启动一个新协程并保持对这个作业的引用
//        delay(1000L)
//        println("World!")
//    }
//    println("Hello,")
//    job.join() // 等待直到子协程执行结束
//    println("job end")
//}

/**
 *  结构化的并发
 *  在执行操作所在的指定作用域内启动协程，
 *  而不是像通常使用线程（线程总是全局的）那样在 GlobalScope 中启动
 */
//fun main() = runBlocking { // this: CoroutineScope
//    launch { // 在 runBlocking 作用域中启动一个新协程
//        delay(1000L)
//        println("World!")
//    }
//    println("Hello,")
//}

/**
 *  作用域构建器
 *  使用coroutineScope构建器声明自己的作用域，
 *  它会创建一个协程作用域，并且在所有已启动子协程完毕之前不会结束。
 *  并且coroutineScope不会阻塞当前线程
 */
//fun main() = runBlocking { // this: CoroutineScope
//    launch {
//        delay(200L)
//        println("Task from runBlocking")
//    }
//
//    coroutineScope { // 创建一个协程作用域
//        launch {
//            delay(500L)
//            println("Task from nested launch")
//        }
//
//        delay(100L)
//        println("Task from coroutine scope") // 这一行会在内嵌 launch 之前输出
//    }
//
//    println("Coroutine scope is over") // 这一行在内嵌 launch 执行完毕后才输出
//}

/**
 *  作用域构建器
 *  coroutineScope不会阻塞当前线程
 */
//fun main()  {
//
//    GlobalScope.launch {
//        coroutineScope { // 创建一个协程作用域
//            launch {
//                delay(500L)
//                println("Task from nested launch") // 不输出
//            }
//            delay(100L)
//            println("Task from coroutine scope") // 不输出
//        }
//    }
//
//    println("Coroutine scope is over") // 这一行直接输出，直接退出
//}

/**
 *  提取函数重构
 */
//fun main() = runBlocking {
//    launch { doWorld() }
//    println("Hello,")
//}
//
//// 这是你的第一个挂起函数
//suspend fun doWorld() {
//    delay(1000L)
//    println("World!")
//}

/**
 *  协程很轻量
 *  用线程卡死
 */
//fun main() = runBlocking {
//    repeat(100_000) { // 启动大量的协程
//        launch {
//            delay(1000L)
//            print(".")
//        }
//    }
//}

/**
 *  全局协程像守护线程
 *  在 GlobalScope 中启动的活动协程并不会使进程保活。它们就像守护线程。
 */
fun main() = runBlocking {
    GlobalScope.launch {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }
    delay(1300L) // 在延迟后退出
}
