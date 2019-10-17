package functions_lambdas


/**
 *  高阶函数与 lambda 表达式
 */
//
//fun <T, R> Collection<T>.fold(
//        initial: R,
//        combine: (acc: R, nextElement: T) -> R
//): R {
//    var accumulator: R = initial
//    for (element: T in this) {
//        accumulator = combine(accumulator, element)
//    }
//    return accumulator
//}
//
//fun main() {
//    val items = listOf(1, 2, 3, 4, 5)
//
//// Lambdas 表达式是花括号括起来的代码块。
//    items.fold(0, {
//        // 如果一个 lambda 表达式有参数，前面是参数，后跟“->”
//        acc: Int, i: Int ->
//        print("acc = $acc, i = $i, ")
//        val result = acc + i
//        println("result = $result")
//        // lambda 表达式中的最后一个表达式是返回值：
//        result
//    })
//
//// lambda 表达式的参数类型是可选的，如果能够推断出来的话：
//    val joinedToString = items.fold("Elements:", { acc, i -> acc + " " + i })
//    println(joinedToString)
//
//// 函数引用也可以用于高阶函数调用：
//// 双冒号操作符 表示把一个方法当做一个参数，传递到另一个方法中进行使用
//    println(2.times(3))
//    val product = items.fold(1, Int::times)
//    println(product)
//}

/**
 *  函数类型
 *
 *  -所有函数类型都有一个圆括号括起来的参数类型列表以及一个返回类型：
 *      (A, B) -> C 表示接受类型分别为 A 与 B 两个参数并返回一个 C 类型值的函数类型。
 *      参数类型列表可以为空，如 () -> A。Unit 返回类型不可省略。
 *  -函数类型可以有一个额外的接收者类型，它在表示法中的点之前指定：
 *      类型 A.(B) -> C 表示可以在 A 的接收者对象上以一个 B 类型参数来调用并返回一个 C 类型值的函数。
 *      带有接收者的函数字面值通常与这些类型一起使用。
 *  -挂起函数属于特殊种类的函数类型，它的表示法中有一个 suspend 修饰符 ，
 *      例如 suspend () -> Unit 或者 suspend A.(B) -> C。
 */


/**
 *  类型 A.(B) -> C
 */
//fun main() {
//    val function : (Int.(String) -> String) = {
//        it+this.toString()
//    }
//    println(function.invoke(12, "3333"))
//}

/**
 *  typealias
 */
//typealias aliasTest = (Int, Int) -> String
//
//fun main() {
//    val test : aliasTest
//    test = object : aliasTest {
//        override fun invoke(p1: Int, p2: Int): String {
//            return "$p1 and $p2"
//        }
//    }
//    println(test.invoke(1,2))
//}

/**
 *  (Int) -> ((Int) -> String)
 */
//fun main() {
//    val function: ((Int) -> ((Int) -> String)) = {
//        val innerFunction : ((Int) -> String) = {inner ->
//            "${it+inner}"
//        }
//        innerFunction
//    }
//    println(function.invoke(12).invoke(33))
//}

/**
 *  函数类型实例化
 */
fun main() {
    List<Int>::size
}


