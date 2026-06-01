import kotlinx.serialization.descriptors.SerialDescriptor

data class Task (
    val id :Int = 1,
    val title : String = "",
    val description: String = "",
    val isComplete : Boolean = false
)
