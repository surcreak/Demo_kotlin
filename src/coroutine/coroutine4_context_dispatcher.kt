package coroutine

import kotlinx.coroutines.*

/**
 * 协程上下文与调度器
 * 协程总是运行在一些以 CoroutineContext 类型为代表的上下文中，
 * 它们被定义在了 Kotlin 的标准库里.
 * 协程上下文是各种不同元素的集合。其中主元素是协程中的 Job.
 */

/**
 *  调度器与线程
 *  协程上下文包含一个 协程调度器 （参见 CoroutineDispatcher）
 *  它确定了哪些线程或与线程相对应的协程执行。
 *  协程调度器可以将协程限制在一个特定的线程执行，或将它分派到一个线程池，
 *  亦或是让它不受限地运行。
 *
 *  所有的协程构建器诸如 launch 和 async 接收一个可选的 CoroutineContext 参数，
 *  它可以被用来显式的为一个新协程或其它上下文元素指定一个调度器。
 */
//fun main() = runBlocking<Unit> {
//    launch { // 运行在父协程的上下文中，即 runBlocking 主协程
//        println("main runBlocking      " +
//                ": I'm working in thread ${Thread.currentThread().name}")
//    }
//    launch(Dispatchers.Unconfined) { // 不受限的——将工作在主线程中
//        println("Unconfined            " +
//                ": I'm working in thread ${Thread.currentThread().name}")
//    }
//    launch(Dispatchers.Default) { // 将会获取默认调度器
//        println("Default               " +
//                ": I'm working in thread ${Thread.currentThread().name}")
//    }
//    launch(newSingleThreadContext("MyOwnThread")) { // 将使它获得一个新的线程
//        println("newSingleThreadContext: " +
//                "I'm working in thread ${Thread.currentThread().name}")
//    }
//}

/**
 *  非受限调度器 vs 受限调度器
 *
 *  该协程的上下文继承自 runBlocking {...} 协程并在 main 线程中运行，
 *  当 delay 函数调用的时候，非受限的那个协程在默认的执行者线程中恢复执行
 */
//fun main() = runBlocking<Unit> {
//    launch(Dispatchers.Unconfined) { // 非受限的——将和主线程一起工作
//        println("Unconfined      " +
//                ": I'm working in thread ${Thread.currentThread().name}")
//        delay(500)
//        println("Unconfined      " +
//                ": After delay in thread ${Thread.currentThread().name}")
//    }
//    launch { // 父协程的上下文，主 runBlocking 协程
//        println("main runBlocking: " +
//                "I'm working in thread ${Thread.currentThread().name}")
//        delay(1000)
//        println("main runBlocking:" +
//                " After delay in thread ${Thread.currentThread().name}")
//    }
//}

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 *  调试协程与线程
 *  协程可以在一个线程上挂起并在其它线程上恢复。
 *  甚至一个单线程的调度器也是难以弄清楚协程在何时何地正在做什么事情。
 *  使用 -Dkotlinx.coroutines.debug JVM 参数
 */
//fun main() = runBlocking<Unit> {
//    val a = async {
//        log("I'm computing a piece of the answer")
//        6
//    }
//    val b = async {
//        log("I'm computing another piece of the answer")
//        7
//    }
//    log("The answer is ${a.await() * b.await()}")
//}

/**
 *  在不同线程间跳转
 *  使用 runBlocking 来显式指定了一个上下文，
 *  使用 withContext 函数来改变协程的上下文，而仍然驻留在相同的协程中
 *
 *  当我们不再需要某个在 newSingleThreadContext 中创建的线程的时候，
 *  它使用了 Kotlin 标准库中的 use 函数来释放该线程
 */
//fun main() = runBlocking<Unit> {
//    newSingleThreadContext("Ctx1").use { ctx1 ->
//        newSingleThreadContext("Ctx2").use { ctx2 ->
//            runBlocking(ctx1) {
//                log("Started in ctx1")
//                withContext(ctx2) {
//                    log("Working in ctx2")
//                }
//                log("Back to ctx1")
//            }
//        }
//    }
//}


/**
 *  协程的 Job 是上下文的一部分，并且可以
 *  使用 coroutineContext [Job] 表达式在上下文中检索它
 */
//fun main() = runBlocking<Unit> {
//    println("My job is ${coroutineContext[Job]}")
//}

/**
 *  子协程
 *  当一个协程被其它协程在 CoroutineScope 中启动的时候，
 *  它将通过 CoroutineScope.coroutineContext 来承袭上下文，
 *  并且这个新协程的 Job 将会成为父协程作业的 子 作业。
 *  当一个父协程被取消的时候，所有它的子协程也会被递归的取消
 *
 *  当使用 GlobalScope 来启动一个协程时，
 *  则新协程的作业没有父作业。 因此它与这个启动的作用域无关且独立运作
 */
//fun main() = runBlocking<Unit> {
//    // 启动一个协程来处理某种传入请求（request）
//    val request = launch {
//        // 孵化了两个子作业, 其中一个通过 GlobalScope 启动
//        GlobalScope.launch {
//            println("job1: I run in GlobalScope and execute independently!")
//            delay(1000)
//            println("job1: I am not affected by cancellation of the request")
//        }
//        // 另一个则承袭了父协程的上下文
//        launch {
//            delay(100)
//            println("job2: I am a child of the request coroutine")
//            delay(1000)
//            println("job2: I will not execute this line if my parent request is cancelled")
//        }
//    }
//    delay(500)
//    request.cancel() // 取消请求（request）的执行
//    delay(1000) // 延迟一秒钟来看看发生了什么
//    println("main: Who has survived request cancellation?")
//}


/**
 *  父协程的职责
 *  一个父协程总是等待所有的子协程执行结束
 */
//fun main() = runBlocking<Unit> {
//    // 启动一个协程来处理某种传入请求（request）
//    val request = launch {
//        repeat(3) { i -> // 启动少量的子作业
//            launch  {
//                delay((i + 1) * 200L) // 延迟 200 毫秒、400 毫秒、600 毫秒的时间
//                println("Coroutine $i is done")
//            }
//        }
//        println("request: I'm done and I don't explicitly join my children that are still active")
//    }
//    request.join() // 等待请求的完成，包括其所有子协程
//    println("Now processing of the request is complete")
//}

/**
 *  命名协程以用于调试
 */
//fun main() = runBlocking<Unit> {
//    log("Started main coroutine")
//    // 运行两个后台值计算
//    val v1 = async(CoroutineName("v1coroutine")) {
//        delay(500)
//        log("Computing v1")
//        252
//    }
//    val v2 = async(CoroutineName("v2coroutine")) {
//        delay(1000)
//        log("Computing v2")
//        6
//    }
//    log("The answer for v1 / v2 = ${v1.await() / v2.await()}")
//}

/**
 *  组合上下文中的元素
 */
//fun main() = runBlocking<Unit> {
//    launch(Dispatchers.Default + CoroutineName("test")) {
//        println("I'm working in thread ${Thread.currentThread().name}")
//    }
//}

/**
 *  协程作用域
 *  可以通过 CoroutineScope() 创建或者通过MainScope() 工厂函数。
 *  前者创建了一个通用作用域，
 *  而后者为使用 Dispatchers.Main 作为默认调度器的 UI 应用程序 创建作用域：
 *
 *  Activity 类中实现 CoroutineScope 接口。
 *  最好的方法是使用具有默认工厂函数的委托。 我们也可以将所需的调度器与作用域合并
 */
//private val mainScope = MainScope() //android
//fun main() = runBlocking<Unit> {
//    val activity = Activity()
//    activity.doSomething() // 运行测试函数
//    println("Launched coroutines")
//    delay(500L) // 延迟半秒钟
//    println("Destroying activity!")
//    activity.destroy() // 取消所有的协程
//    delay(1000) // 为了在视觉上确认它们没有工作
//}
//
//class Activity : CoroutineScope by CoroutineScope(Dispatchers.Default) {
//    fun doSomething() {
//        // 在示例中启动了 10 个协程，且每个都工作了不同的时长
//        repeat(10) { i ->
//            launch {
//                delay((i + 1) * 200L) // 延迟 200 毫秒、400 毫秒、600 毫秒等等不同的时间
//                println("Coroutine $i is done")
//            }
//        }
//    }
//
//    fun destroy() {
//        cancel()
//    }
//}

/**
 *  线程局部数据
 *  在这个例子中我们使用 Dispatchers.Default 在后台线程池中启动了一个新的协程，
 *  所以它工作在线程池中的不同线程中，但它仍然具有线程局部变量的值，
 *  我们指定使用 threadLocal.asContextElement(value = "launch")，
 *  无论协程执行在什么线程中都是没有问题的
 */
//TODO ****没搞懂****
val threadLocal = ThreadLocal<String?>() // declare thread-local variable
fun main() = runBlocking<Unit> {
    threadLocal.set("main")
    println("Pre-main, current thread: ${Thread.currentThread()}, " +
            "thread local value: '${threadLocal.get()}'")
    val job = launch(Dispatchers.Default +
            threadLocal.asContextElement(value = "launch")) {
        println("Launch start, current thread: ${Thread.currentThread()}, " +
                "thread local value: '${threadLocal.get()}'")
        yield()
        println("After yield, current thread: ${Thread.currentThread()}, " +
                "thread local value: '${threadLocal.get()}'")
    }
    job.join()
    println("Post-main, current thread: ${Thread.currentThread()}, " +
            "thread local value: '${threadLocal.get()}'")
}
