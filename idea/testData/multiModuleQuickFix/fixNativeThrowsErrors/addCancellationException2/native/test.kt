// "Add 'CancellationException::class'" "false"
// ERROR: @Throws on suspend declaration must have CancellationException (or any of its superclasses) listed
// ACTION: Make internal
// ACTION: Make private

class MyException : Throwable()

<caret>@Throws(exceptionClasses = [MyException::class])
suspend fun addCE() {}