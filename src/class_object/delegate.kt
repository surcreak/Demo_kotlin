package class_object

/**
 *  委托
 *  委托模式已经证明是实现继承的一个很好的替代方式，
 *  而 Kotlin 可以零样板代码地原生支持它。
 */

//interface Base {
//    fun print()
//}
//
//class BaseImpl(val x: Int) : Base {
//    override fun print() { print(x) }
//}
//
////  by-子句表示 b 将会在 Derived 中内部存储, 可以认为作为Derived的成员函数。
////  并且编译器将生成Base的所有方法转发给b，认为是生成Base所有方法，并调用对应的b的方法。
////  the compiler will generate all the methods of Base that forward to b.
//class Derived(b: Base) : Base by b
//
//fun main() {
//    val b = BaseImpl(10)
//    Derived(b).print()
//}


/**
 *  覆盖由委托实现的接口成员
 *  printMessage 时程序会输出“abc”而不是“10”
 */
//interface Base {
//    fun printMessage()
//    fun printMessageLine()
//}
//
//class BaseImpl(val x: Int) : Base {
//    override fun printMessage() { print(x) }
//    override fun printMessageLine() { println(x) }
//}
//
//class Derived(b: Base) : Base by b {
//    override fun printMessage() { print("abc") }
//}
//
//fun main() {
//    val b = BaseImpl(10)
//    Derived(b).printMessage()
//    Derived(b).printMessageLine()
//}

/**
 *  重写的成员(message) 不会在委托对象(BaseImpl)的成员中调用 ，
 *  委托对象的成员只能访问其自身对接口成员实现：
 */
//interface Base {
//    val message: String
//    fun print()
//}
//
//class BaseImpl(val x: Int) : Base {
//    override val message = "BaseImpl: x = $x"
//    override fun print() { println(message) }
//}
//
//class Derived(b: Base) : Base by b {
//    // 在 b 的 `print` 实现中不会访问到这个属性
//    override val message = "Message of Derived"
//}
//
//fun main() {
//    val b = BaseImpl(10)
//    val derived = Derived(b)
//    derived.print()
//    println(derived.message)
//}