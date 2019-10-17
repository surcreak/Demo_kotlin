package coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

/**
 *  共享的可变状态与并发
 *
 *  协程可用多线程调度器（比如默认的 Dispatchers.Default）并发执行。
 *  这样就可以提出所有常见的并发问题。主要的问题是同步访问共享的可变状态。
 *  协程领域对这个问题的一些解决方案类似于多线程领域中的解决方案，
 *  但其它解决方案则是独一无二的。
 */

/**
 *  启动一百个协程，它们都做一千次相同的操作。我们同时会测量它们的完成时间以便进一步的比较
 */
suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100  // 启动的协程数量
    val k = 1000 // 每个协程重复执行同一动作的次数
    val time = measureTimeMillis {
        coroutineScope { // 协程的作用域
            repeat(n) {
                launch {
                    repeat(k) { action() }
                }
            }
        }
    }
    println("Completed ${n * k} actions in $time ms")
}

/**
 *  使用多线程的 Dispatchers.Default 来递增一个共享的可变变量。
 *
 *  volatile 无济于事，因为 volatile 变量保证可线性化（这是“原子”的技术术语）读取和写入变量，
 *  但在大量动作（在我们的示例中即“递增”操作）发生时并不提供原子性。
 */

//@Volatile
//var counter = 0
//
//fun main() = runBlocking<Unit> {
//    withContext(Dispatchers.Default) {
//        massiveRun {
//            counter++
//        }
//    }
//    println("Counter = $counter")
//}

/**
 *  使用多线程的 Dispatchers.Default 来递增一个共享的可变变量。
 *
 *  线程安全的数据结构
 *  一种对线程、协程都有效的常规解决方法，
 *  就是使用线程安全（也称为同步的、 可线性化、原子）的数据结构.
 *
 *  然而，它并不容易被扩展来应对复杂状态、或一些没有现成的线程安全实现的复杂操作
 */

//@Volatile
//var counter = AtomicInteger(0)
//
//fun main() = runBlocking<Unit> {
//    withContext(Dispatchers.Default) {
//        massiveRun {
//            counter.incrementAndGet()
//        }
//    }
//    println("Counter = $counter")
//}

/**
 *  以细粒度限制线程
 *
 *  限制线程 是解决共享可变状态问题的一种方案：
 *  对特定共享状态的所有访问权都限制在单个线程中。
 *  它通常应用于 UI 程序中：所有 UI 状态都局限于单个事件分发线程或应用主线程中。
 *  这在协程中很容易实现，通过使用一个单线程上下文：
 *
 *  这段代码运行非常缓慢，因为它进行了 细粒度 的线程限制。
 *  每个增量操作都得使用 [withContext(counterContext)] 块
 *  从多线程 Dispatchers.Default 上下文切换到单线程上下文。
 */

//val counterContext = newSingleThreadContext("CounterContext")
//var counter = 0
//
//fun main() = runBlocking<Unit> {
//    withContext(Dispatchers.Default) {
//        massiveRun {
//            // 将每次自增限制在单线程上下文中
//            withContext(counterContext) {
//                counter++
//            }
//        }
//    }
//    println("Counter = $counter")
//}

/**
 *  以粗粒度限制线程
 *
 *  在实践中，线程限制是在大段代码中执行的，
 *  例如：状态更新类业务逻辑中大部分都是限于单线程中。
 *  下面的示例演示了这种情况， 在单线程上下文中运行每个协程
 */

//val counterContext = newSingleThreadContext("CounterContext")
//var counter = 0
//
//fun main() = runBlocking<Unit> {
//    // 将一切都限制在单线程上下文中
//    withContext(counterContext) {
//        massiveRun {
//            counter++
//        }
//    }
//    println("Counter = $counter")
//}

/**
 *  互斥
 *
 *  该问题的互斥解决方案：使用永远不会同时执行的 关键代码块 来保护共享状态的所有修改。
 *  在阻塞的世界中，你通常会为此目的使用 synchronized 或者 ReentrantLock。
 *
 *  在协程中的替代品叫做 Mutex 。它具有 lock 和 unlock 方法， 可以隔离关键的部分。
 *  关键的区别在于 Mutex.lock() 是一个挂起函数，它不会阻塞线程。
 *
 *  锁是细粒度的，因此会付出一些代价。
 */

//val mutex = Mutex()
//var counter = 0
//
//fun main() = runBlocking<Unit> {
//    withContext(Dispatchers.Default) {
//        massiveRun {
//            // 用锁保护每次自增
//            mutex.withLock {
//                counter++
//            }
//        }
//    }
//    println("Counter = $counter")
//}

/**
 *  Actors
 *
 *  一个 actor 是由协程、 被限制并封装到该协程中的状态以及
 *  一个与其它协程通信的 通道 组合而成的一个实体。
 *  一个简单的 actor 可以简单的写成一个函数，
 *  但是一个拥有复杂状态的 actor 更适合由类来表示.
 */

// 计数器 Actor 的各种类型
sealed class CounterMsg
object IncCounter : CounterMsg() // 递增计数器的单向消息
class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg() // 携带回复的请求

// 这个函数启动一个新的计数器 actor
fun CoroutineScope.counterActor() = actor<CounterMsg> {
    var counter = 0 // actor 状态
    for (msg in channel) { // 即将到来消息的迭代器
        when (msg) {
            is IncCounter -> counter++
            is GetCounter -> msg.response.complete(counter)
        }
    }
}

fun main() = runBlocking<Unit> {
    val counter = counterActor() // 创建该 actor
    withContext(Dispatchers.Default) {
        massiveRun {
            counter.send(IncCounter)
        }
    }
    // 发送一条消息以用来从一个 actor 中获取计数值
    val response = CompletableDeferred<Int>()
    counter.send(GetCounter(response))
    println("Counter = ${response.await()}")
    counter.close() // 关闭该actor
}