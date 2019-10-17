package coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 *  异步流
 *  挂起函数可以异步的返回单个值，
 *  但是该如何异步返回多个计算好的值呢？这正是 Kotlin 流（Flow）的用武之地。
 */

/**
 *  如果使用一些消耗 CPU 资源的阻塞代码计算数字（每次计算需要 100 毫秒）
 *  那么我们可以使用 Sequence 来表示数字
 *
 *  然而，计算过程阻塞运行该代码的主线程
 */
//fun foo(): Sequence<Int> = sequence { // 序列构建器
//    for (i in 1..3) {
//        Thread.sleep(100) // 假装我们正在计算
//        yield(i) // 产生下一个值
//    }
//}
//
//fun main() {
//    foo().forEach { value -> println(value) }
//}

/**
 *  使用 suspend 修饰符标记函数 foo，
 *  这样它就可以在不阻塞的情况下执行其工作并将结果作为列表返回
 */
//suspend fun foo(): List<Int> {
//    delay(1000) // 假装我们在这里做了一些异步的事情
//    return listOf(1, 2, 3)
//}
//
//fun main() = runBlocking<Unit> {
//    foo().forEach { value -> println(value) }
//}

/**
 *  使用 List 结果类型，意味着我们只能一次返回所有值。
 *  为了表示异步计算的值流（stream），我们可以使用 Flow 类型
 *
 *  -名为 flow 的 Flow 类型构建器函数。
 *  -flow { ... } 构建块中的代码可以挂起。
 *  -函数 foo() 不再标有 suspend 修饰符。
 *  -流使用 emit 函数 发射 值。
 *  -流使用 collect 函数 收集 值。
 */
//fun foo(): Flow<Int> = flow { // 流构建器
//    for (i in 1..3) {
//        delay(100) // 假装我们在这里做了一些有用的事情
//        emit(i) // 发送下一个值
//    }
//}
//
//fun main() = runBlocking<Unit> {
//    // 启动并发的协程以验证主线程并未阻塞
//    launch {
//        for (k in 1..3) {
//            println("I'm not blocked $k")
//            delay(100)
//        }
//    }
//    // 收集这个流
//    foo().collect { value -> println(value) }
//}

/**
 *  流是冷的
 *  Flow 是一种类似于序列的冷流
 *
 *  这是返回一个流的 foo() 函数没有标记 suspend 修饰符的主要原因。
 *  通过它自己，foo() 会尽快返回且不会进行任何等待
 */
//fun foo(): Flow<Int> = flow {
//    println("Flow started")
//    for (i in 1..3) {
//        delay(100)
//        emit(i)
//    }
//}
//
//fun main() = runBlocking<Unit> {
//    println("Calling foo...")
//    val flow = foo()
//    println("Calling collect...")
//    flow.collect { value -> println(value) }
//    println("Calling collect again...")
//    flow.collect { value -> println(value) }
//}

/**
 *  流取消
 *  流采用与协程同样的协作取消,然而，流的基础设施未引入其他取消点。
 *  取消完全透明。像往常一样，流的收集可以在当流在一个可取消的挂起函数
 *  （例如 delay）中挂起的时候取消，否则不能取消。
 *
 */
//fun foo(): Flow<Int> = flow {
//    for (i in 1..3) {
//        delay(100)
//        println("Emitting $i")
//        emit(i)
//    }
//}
//
//fun main() = runBlocking<Unit> {
//    withTimeoutOrNull(250) { // 在 250 毫秒后超时
//        foo().collect { value -> println(value) }
//    }
//    println("Done")
//}

/**
 *  流构建器
 *  -flowOf 构建器定义了一个发射固定值集的流。
 *  -使用 .asFlow() 扩展函数，可以将各种集合与序列转换为流。
 */


/**
 *  操作符
 *
 *  过渡流操作符
 *  可以使用操作符转换流
 *  这些操作符也是冷操作符，就像流一样。这类操作符本身不是挂起函数
 *  流与序列的主要区别在于这些操作符中的代码可以调用挂起函数。
 */
suspend fun performRequest(request: Int): String {
    delay(1000) // 模仿长时间运行的异步工作
    return "response $request"
}

fun numbers(): Flow<Int> = flow {
    try {
        emit(1)
        emit(2)
        println("This line will not execute")
        emit(3)
    } finally {
        println("Finally in numbers")
    }
}

//fun main() = runBlocking<Unit> {
//    //  过渡流操作符
////    (1..3).asFlow() // 一个请求流
////            .map { request -> performRequest(request) }
////            .collect { response -> println(response) }
//
//    //  转换操作符
//    // 使用 transform 操作符，我们可以 发射 任意值任意次
////    (1..3).asFlow() // 一个请求流
////            .transform { request ->
////                emit("Making request $request")
////                emit(performRequest(request))
////            }
////            .collect { response -> println(response) }
//
//    //  限长操作符
////    numbers()
////            .take(2) // 只获取前两个
////            .collect { value -> println(value) }
//
//    //  末端流操作符
////    val sum = (1..5).asFlow()
////            .map { it * it } // 数字 1 至 5 的平方
////            .reduce { a, b -> a + b } // 求和（末端操作符）
////    println(sum)
//}

/**
 *  流是连续的
 */
// TODO 下面都是英文的，还没翻译
//fun main() = runBlocking<Unit> {
//    (1..5).asFlow()
//            .filter {
//                println("Filter $it")
//                it % 2 == 0
//            }
//            .map {
//                println("Map $it")
//                "string $it"
//            }.collect {
//                println("Collect $it")
//            }
//}

/**
 *  流上下文
 */
 fun foo(): Flow<Int> = flow {
     log("Started foo flow")
     for (i in 1..3) {
         emit(i)
     }
 }

 fun main() = runBlocking<Unit> {
     foo().collect { value -> log("Collected $value") }
 }


