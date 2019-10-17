package coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

/**
 *  通道
 *  延期的值提供了一种便捷的方法使单个值在多个协程之间进行相互传输。
 *  通道提供了一种在流中传输值的方法。
 */

/**
 *  一个 Channel 是一个和 BlockingQueue 非常相似的概念。
 *  其中一个不同是它代替了阻塞的 put 操作并提供了挂起的 send，
 *  还替代了阻塞的 take 操作并提供了挂起的 receive。
 */
//fun main() = runBlocking{
//    val channel = Channel<Int>()
//    launch {
//        // 这里可能是消耗大量 CPU 运算的异步逻辑，我们将仅仅做 5 次整数的平方并发送
//        for (x in 1..5) channel.send(x * x)
//    }
//// 这里我们打印了 5 次被接收的整数：
//    repeat(5) { println(channel.receive()) }
//    println("Done!")
//}

/**
 *  关闭与迭代通道
 *  和队列不同，一个通道可以通过被关闭来表明没有更多的元素将会进入通道
 */
//fun main() = runBlocking{
//    val channel = Channel<Int>()
//    launch {
//        for (x in 1..5) channel.send(x * x)
//        channel.close() // 我们结束发送
//    }
//// 这里我们使用 `for` 循环来打印所有被接收到的元素（直到通道被关闭）
//    for (y in channel) println(y)
//    println("Done!")
//}

/**
 *  构建通道生产者
 *
 */
fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
    for (x in 1..5) send(x * x)
}


//fun main() = runBlocking{
//    val squares = produceSquares()
//    squares.consumeEach { println(it) }
//    println("Done!")
//}

fun CoroutineScope.produceNumbers() = produce<Int> {
    var x = 1
    while (true) send(x++) // 在流中开始从 1 生产无穷多个整数
}

fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
    for (x in numbers) send(x * x)
}

fun CoroutineScope.numbersFrom(start: Int) = produce<Int> {
    var x = start
    while (true) send(x++) // 开启了一个无限的整数流
}

fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce<Int> {
    for (x in numbers) if (x % prime != 0) send(x)
}

/**
 *  打印了前十个素数
 *  现在我们开启了一个从 2 开始的数字流管道，从当前的通道中取一个素数，
 *  并为每一个我们发现的素数启动一个流水线阶段：
 *  numbersFrom(2) -> filter(2) -> filter(3) -> filter(5) -> filter(7) ……
 *
 *  使用 cancelChildren 扩展函数，取消所有的子协程
 */
//fun main() = runBlocking{
//    var cur = numbersFrom(2)
//    for (i in 1..10) {
//        val prime = cur.receive()
//        println(prime)
//        cur = filter(cur, prime)
//    }
//    coroutineContext.cancelChildren() // 取消所有的子协程来让主协程结束
//}

/**
 *  连接整个管道
 */
//fun main() = runBlocking{
//    val numbers = produceNumbers() // 从 1 开始生产整数
//    val squares = square(numbers) // 对整数做平方
//    for (i in 1..5) println(squares.receive()) // 打印前 5 个数字
//    println("Done!") // 我们的操作已经结束了
//    coroutineContext.cancelChildren() // 取消子协程
//}

/**
 *  扇出
 *  多个协程也许会接收相同的管道，在它们之间进行分布式工作。
 */

//fun CoroutineScope.produceNumbers1() = produce<Int> {
//    var x = 1 // 从 1 开始
//    while (true) {
//        send(x++) // 产生下一个数字
//        delay(100) // 等待 0.1 秒
//    }
//}
//
//fun CoroutineScope.launchProcessor(id: Int, channel: ReceiveChannel<Int>) = launch {
//    for (msg in channel) {
//        println("Processor #$id received $msg")
//    }
//}
//
//fun main() = runBlocking{
//    val producer = produceNumbers1()
//    repeat(5) { launchProcessor(it, producer) } //多个协程接受同一个管道
//    delay(950)
//    producer.cancel() // 取消协程生产者从而将它们全部杀死
//}

/**
 *  扇入
 *  多个协程可以发送到同一个通道
 */
//suspend fun sendString(channel: SendChannel<String>, s: String, time: Long) {
//    while (true) {
//        delay(time)
//        channel.send(s)
//    }
//}
//
//fun main() = runBlocking{
//    val channel = Channel<String>()
//    launch { sendString(channel, "foo", 200L) }
//    launch { sendString(channel, "BAR!", 500L) }
//    repeat(6) { // 接收前六个
//        println(channel.receive())
//    }
//    coroutineContext.cancelChildren() // 取消所有子协程来让主协程结束
//}

/**
 *  带缓冲的通道
 *  缓冲允许发送者在被挂起前发送多个元素
 */
//fun main() = runBlocking{
//    val channel = Channel<Int>(4) // 启动带缓冲的通道
//    val sender = launch { // 启动发送者协程
//        repeat(10) {
//            println("Sending $it") // 在每一个元素发送前打印它们
//            channel.send(it) // 将在缓冲区被占满时挂起
//        }
//    }
//// 没有接收到东西……只是等待……
//    delay(1000)
//    sender.cancel() // 取消发送者协程
//}


/**
 *  通道是公平的
 *  发送和接收操作是 公平的 并且尊重调用它们的多个协程。它们遵守先进先出原则
 */
//data class Ball(var hits: Int)
//
//fun main() = runBlocking {
//    val table = Channel<Ball>() // 一个共享的 table（桌子）
//    launch { player("ping", table) }
//    launch { player("pong", table) }
//    table.send(Ball(0)) // 乒乓球
//    delay(2000) // 延迟 1 秒钟
//    coroutineContext.cancelChildren() // 游戏结束，取消它们
//}
//
//suspend fun player(name: String, table: Channel<Ball>) {
//    for (ball in table) { // 在循环中接收球
//        ball.hits++
//        println("$name $ball")
//        delay(300) // 等待一段时间
//        table.send(ball) // 将球发送回去
//    }
//}

/**
 *  计时器通道是一种特别的会合通道，每次经过特定的延迟都会从该通道进行消费并产生 Unit
 */
fun main() = runBlocking<Unit> {
    val tickerChannel = ticker(delayMillis = 100, initialDelayMillis = 0) //创建计时器通道
    var nextElement = withTimeoutOrNull(1) { tickerChannel.receive() }
    println("Initial element is available immediately: $nextElement") // 初始尚未经过的延迟

    nextElement = withTimeoutOrNull(50) { tickerChannel.receive() } // 所有随后到来的元素都经过了 100 毫秒的延迟
    println("Next element is not ready in 50 ms: $nextElement")

    nextElement = withTimeoutOrNull(60) { tickerChannel.receive() }
    println("Next element is ready in 100 ms: $nextElement")

    // 模拟大量消费延迟
    println("Consumer pauses for 150ms")
    delay(150)
    // 下一个元素立即可用
    nextElement = withTimeoutOrNull(1) { tickerChannel.receive() }
    println("Next element is available immediately after large consumer delay: $nextElement")
    // 请注意，`receive` 调用之间的暂停被考虑在内，下一个元素的到达速度更快
    nextElement = withTimeoutOrNull(60) { tickerChannel.receive() }
    println("Next element is ready in 50ms after consumer pause in 150ms: $nextElement")

    tickerChannel.cancel() // 表明不再需要更多的元素
}