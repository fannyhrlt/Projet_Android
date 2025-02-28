package fr.isen.herault.smartcompanion




data class Event(
    val id: String, // Store id as a String
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val category: String
) {
    fun getNumericId(): Int? {
        return id.toIntOrNull() // Returns null if conversion fails
    }
}




val fakeEvents = listOf(
    Event("1", "Soirée BDE", "Une super soirée organisée par le BDE.", "2025-03-10", "Salle des fêtes", "Fête"),
    Event("2", "Gala ISEN", "Le gala annuel de l'ISEN.", "2025-06-20", "Palais des Congrès", "Cérémonie"),
    Event("3", "Journée Cohésion", "Une journée pour souder les étudiants.", "2025-04-15", "Campus ISEN", "Activité")
)
