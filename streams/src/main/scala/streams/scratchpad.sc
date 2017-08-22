
val levelVector = Vector(Vector('a','a'),Vector('b','a'),Vector('a','a'))
val ys = levelVector map ( _ indexOf 'b' )
val x = ys indexWhere (_ >= 0)
