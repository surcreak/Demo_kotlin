package class_object

import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 *  属性委托
 *  有一些常见的属性类型，虽然我们可以在每次需要的时候手动实现它们，
 *  但是如果能够为大家把他们只实现一次并放入一个库会更好
 *
 *  -延迟属性（lazy properties）: 其值只在首次访问时计算；
 *  -可观察属性（observable properties）: 监听器会收到有关此属性变更的通知；
 *  -把多个属性储存在一个映射（map）中，而不是每个存在单独的字段中。
 */
//class Delegate {
//    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
//        return "$thisRef, thank you for delegating '${property.name}' to me!"
//    }
//
//    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
//        println("$value has been assigned to '${property.name}' in $thisRef.")
//    }
//}
//
//class Example {
//    var p: String by Delegate()
//}
//
//fun main() {
//    val e = Example()
//    e.p = "NEW"
//    println(e.p)
//}

/**
 *  标准委托
 *  Kotlin 标准库为几种有用的委托提供了工厂方法
 */

/**
 *  延迟属性 Lazy
 *  Lazy 属性的求值是同步锁的（synchronized）：
 *  该值只在一个线程中计算，并且所有线程会看到相同的值
 *
 *  LazyThreadSafetyMode.SYNCHRONIZED   默认，同步锁
 *  LazyThreadSafetyMode.PUBLICATION    初始化委托的同步锁不是必需的，这样多个线程可以同时执行
 *  LazyThreadSafetyMode.NONE           不会有任何线程安全的保证以及相关的开销
 */
//val lazyValue: String by lazy(LazyThreadSafetyMode.PUBLICATION) {
//    println("computed!")
//    "Hello"
//}
//
//fun main() {
//    println(lazyValue)
//    println(lazyValue)
//}


/**
 *  可观察属性 Observable
 *  否决 vetoable
 */
//class User {
//    var name: String by Delegates.observable("<no name>") {
//        prop, old, new ->
//        println("$old -> $new")
//    }
//
//    var age: Int by Delegates.vetoable(0) {
//        prop, old, new ->
//        println("$old -> $new")
//        new >= old
//    }
//}
//
//fun main() {
//    val user = User()
//    user.name = "first"
//    user.name = "second"
//
//    user.age = 18
//    println("change to 18, real is ${user.age}")
//    user.age = 30
//    println("change to 30, real is ${user.age}")
//    user.age = 10
//    println("change to 10, real is ${user.age}")
//}

/**
 *  把属性储存在映射中
 */
//class User(val map: Map<String, Any?>) {
//    val name: String by map
//    val age: Int     by map
//}
//fun main() {
//    val user = User(mapOf(
//            "name" to "John Doe",
//            "age"  to 25
//    ))
//    println(user.name) // Prints "John Doe"
//    println(user.age) // Prints 25
//}