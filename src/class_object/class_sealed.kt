package class_object

/**
 *  密封类用来表示受限的类继承结构：
 *  当一个值为有限几种的类型、而不能有任何其他类型时。
 *  在某种意义上，他们是枚举类的扩展：枚举类型的值集合也是受限的，
 *  但每个枚举常量只存在一个实例，而密封类的一个子类可以有可包含状态的多个实例。
 *
 *  密封类不允许有非-private 构造函数（其构造函数默认为 private）。
 *  一个密封类是自身抽象的，它不能直接实例化并可以有抽象（abstract）成员。
 *  密封类也可以有子类，但是所有子类都必须在与密封类自身相同的文件中声明。
 *  扩展密封类子类的类（间接继承者）可以放在任何位置，而无需在同一个文件中.
 */

sealed class Expr
data class Const(val number: Double) : Expr()
data class Sum(val e1: Expr, val e2: Expr) : Expr()
object NotANumber : Expr()

fun eval(expr: Expr): Double = when(expr) {
    is Const -> expr.number
    is Sum -> eval(expr.e1) + eval(expr.e2)
    NotANumber -> Double.NaN
    // 不再需要 `else` 子句，因为我们已经覆盖了所有的情况
}

fun main() {
    println(eval(Sum(Const(1.0), Const(2.0))))
}