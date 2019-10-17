package coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import java.io.IOException

/**
 *  异常处理
 *  我们已经知道当协程被取消的时候会在挂起点抛出
 *  CancellationException，并且它在协程机制中被忽略了
 *
 *  协程构建器有两种风格
 *  1.自动的传播异常（launch 以及 actor）,对待异常是不处理的
 *  2.将它们暴露给用户（async 以及 produce）,依赖用户来最终消耗异常
 */

/**
 *  异常的传播
 */
//fun main() = runBlocking<Unit> {
//    val job = GlobalScope.launch {
//        println("Throwing exception from launch")
//        throw IndexOutOfBoundsException() // 我们将在控制台打印 Thread.defaultUncaughtExceptionHandler
//    }
//    job.join()
//    println("Joined failed job")
//    val deferred = GlobalScope.async {
//        println("Throwing exception from async")
//        throw ArithmeticException() // 没有打印任何东西，依赖用户去调用等待
//    }
//    try {
//        deferred.await()
//        println("Unreached")
//    } catch (e: ArithmeticException) {
//        println("Caught ArithmeticException")
//    }
//}

/**
 *  CoroutineExceptionHandler
 *   CoroutineExceptionHandler 上下文元素被用来将通用的 catch 代码块
 *   用于在协程中自定义日志记录或异常处理。
 *   它和使用 Thread.uncaughtExceptionHandler 很相似
 *
 *
 */
//fun main() = runBlocking<Unit> {
//    val handler = CoroutineExceptionHandler { _, exception ->
//        println("Caught $exception")
//    }
//    val job = GlobalScope.launch(handler) {
//        throw AssertionError()
//    }
//    val deferred = GlobalScope.async(handler) {
//        throw ArithmeticException() // 没有打印任何东西，依赖用户去调用 deferred.await()
//    }
//    joinAll(job, deferred)
//}

/**
 *  取消与异常
 *  当一个协程在没有任何理由的情况下使用 Job.cancel 取消的时候，
 *  它会被终止，但是它不会取消它的父协程。
 */
//fun main() = runBlocking<Unit> {
//    val job = launch {
//        val child = launch {
//            try {
//                delay(Long.MAX_VALUE)
//            } finally {
//                println("Child is cancelled")
//            }
//        }
//        yield()
//        println("Cancelling child")
//        child.cancel()
//        child.join()
//        yield()
//        println("Parent is not cancelled")
//    }
//    job.join()
//}

/**
 *  取消与异常
 *  如果协程遇到除 CancellationException 以外的异常，
 *  它将取消具有该异常的父协程。 这种行为不能被覆盖
 */
//fun main() = runBlocking<Unit> {
//    val handler = CoroutineExceptionHandler { _, exception ->
//        println("Caught $exception")
//    }
//    val job = GlobalScope.launch(handler) {
//        launch { // 第一个子协程
//            try {
//                delay(Long.MAX_VALUE)
//            } finally {
//                withContext(NonCancellable) {
//                    println("Children are cancelled, but exception is not handled until all children terminate")
//                    delay(100)
//                    println("The first child finished its non cancellable block")
//                }
//            }
//        }
//        launch { // 第二个子协程
//            delay(10)
//            println("Second child throws an exception")
//            throw ArithmeticException()
//        }
//    }
//    job.join()
//}

/**
 *  异常聚合
 *  如果一个协程的多个子协程抛出异常将会发生什么？
 *  通常的规则是“第一个异常赢得了胜利”
 *
 *  协程在 finally 块中抛出了一个异常。 这时，多余的异常将会被压制
 */
//fun main() = runBlocking<Unit> {
//    val handler = CoroutineExceptionHandler { _, exception ->
//        println("Caught $exception with suppressed ${exception.suppressed.contentToString()}")
//    }
//    val job = GlobalScope.launch(handler) {
//        launch {
//            try {
//                delay(Long.MAX_VALUE)
//            } finally {
//                throw ArithmeticException()
//            }
//        }
//        launch {
//            delay(100)
//            throw IOException()
//        }
//        delay(Long.MAX_VALUE)
//    }
//    job.join()
//}

/**
 *  取消异常是透明的并且会在默认情况下解包
 */
//fun main() = runBlocking<Unit> {
//    val handler = CoroutineExceptionHandler { _, exception ->
//        println("Caught original $exception")
//    }
//    val job = GlobalScope.launch(handler) {
//        val inner = launch {
//            launch {
//                launch {
//                    throw IOException()
//                }
//            }
//        }
//        try {
//            inner.join()
//        } catch (e: CancellationException) {
//            println("Rethrowing CancellationException with original cause")
//            throw e
//        }
//    }
//    job.join()
//}

/**
 *  监督
 *
 *  作用域内定义作业的 UI 组件。如果任何一个 UI 的子作业执行失败了，
 *  它并不总是有必要取消（有效地杀死）整个 UI 组件，
 *  但是如果 UI 组件被销毁了（并且它的作业也被取消了），
 *  由于它的结果不再被需要了，它有必要使所有的子作业执行失败。
 *
 *  另一个例子是服务进程孵化了一些子作业并且需要 监督 它们的执行，
 *  追踪它们的故障并在这些子作业执行失败的时候重启。
 */

/**
 *  监督作业
 *  SupervisorJob 可以被用于这些目的。它类似于常规的 Job，
 *  唯一的不同是：SupervisorJob 的取消只会向下传播
 */
//fun main() = runBlocking<Unit> {
//    val supervisor = SupervisorJob()
//    with(CoroutineScope(coroutineContext + supervisor)) {
//        // 启动第一个子作业——这个示例将会忽略它的异常（不要在实践中这么做！）
//        val firstChild = launch(CoroutineExceptionHandler { _, _ ->  }) {
//            println("First child is failing")
//            throw AssertionError("First child is cancelled")
//        }
//        // 启动第两个子作业
//        val secondChild = launch {
//            firstChild.join()
//            // 取消了第一个子作业且没有传播给第二个子作业
//            println("First child is cancelled: ${firstChild.isCancelled}, but second one is still active")
//            try {
//                delay(Long.MAX_VALUE)
//            } finally {
//                // 但是取消了监督的传播
//                println("Second child is cancelled because supervisor is cancelled")
//            }
//        }
//        // 等待直到第一个子作业失败且执行完成
//        firstChild.join()
//        println("Cancelling supervisor")
//        supervisor.cancel()
//        secondChild.join()
//    }
//}

/**
 *  监督作用域
 *  作用域的并发，supervisorScope 可以被用来替代 coroutineScope 来实现相同的目的。
 *  它只会单向的传播并且当子作业自身执行失败的时候将它们全部取消。它也会在所有的子作业执行结束前等待
 */
//fun main() = runBlocking<Unit> {
//    try {
//        supervisorScope {
//            val child = launch {
//                try {
//                    println("Child is sleeping")
//                    delay(Long.MAX_VALUE)
//                } finally {
//                    println("Child is cancelled")
//                }
//            }
//            // 使用 yield 来给我们的子作业一个机会来执行打印
//            yield()
//            println("Throwing exception from scope")
//            throw AssertionError()
//        }
//    } catch(e: AssertionError) {
//        println("Caught assertion error")
//    }
//}

/**
 *  监督协程中的异常
 *  常规的作业和监督作业之间的另一个重要区别是异常处理。
 *  每一个子作业应该通过异常处理机制处理自身的异常。
 *  这种差异来自于子作业的执行失败不会传播给它的父作业的事实。
 */
fun main() = runBlocking<Unit> {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }
    supervisorScope {
        val child = launch(handler) {
            println("Child throws an exception")
            throw AssertionError()
        }
        println("Scope is completing")
    }
    println("Scope is completed")
}