package sandbox

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

sealed interface ChangeVariable

@JsonDeserialize(using = NullableDeserializer::class)
sealed interface Nullable<T : Any> : ChangeVariable {
    val value: T?
        get() = null
}

data class ValContainer<T : Any>(override val value: T) : Nullable<T>
class SetNull<T : Any>() : Nullable<T> {
    override val value: T?
        @JsonIgnore get() = super.value
}

class NullableDeserializer : JsonDeserializer<Nullable<*>>(), ContextualDeserializer {
    private var nullableType: JavaType? = null
    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> {
        property ?: return this
        nullableType =
            ctxt.typeFactory.constructParametricType(NullableContainer::class.java, property.type.containedType(0))
        return this
    }

    class NullableContainer<T>(val value: T?)

    /** The only thing I'm really trying to do here is: When you fill in value with a value, it must be a ValContainer, if value = null or not set it must be of type SetNull*/
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Nullable<*> {
        val value = ctxt.readValue<NullableContainer<*>>(p, nullableType)
        return if (value.value == null) return SetNull<Nothing>() else ValContainer(value.value)
    }
}


data class Foo(
    val a: Nullable<String>?,
    val b: Nullable<String>?,
    val c: Nullable<String>?,
)

fun main() {
    val mapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    val foo = Foo(
        a = ValContainer("foo"),
        b = SetNull(),
        c = null
    )

    val json = """
        {
            "a": { "value": "foo"},
            "b": null
        }
    """.trimIndent()

    val parsedFoo: Foo = mapper.readValue(json)
    println(parsedFoo)
    println(mapper.writeValueAsString(foo))
}
