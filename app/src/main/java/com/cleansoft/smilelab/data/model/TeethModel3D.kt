package com.cleansoft.smilelab.data.model

/**
 * Modelos 3D disponíveis
 */
enum class TeethModelType(
    val displayName: String,
    val description: String,
    val icon: String,
    val fileName: String
) {
    COMPLETE_DENTITION(
        displayName = "Dentição Completa",
        description = "Modelo completo com todos os dentes",
        icon = "🦷",
        fileName = "complete_teeth.glb"
    ),
    MOLAR(
        displayName = "Molar",
        description = "Dente molar para mastigação",
        icon = "🔷",
        fileName = "molar.glb"
    ),
    INCISOR(
        displayName = "Incisivo",
        description = "Dente frontal para cortar",
        icon = "🔶",
        fileName = "incisor.glb"
    ),
    CANINE(
        displayName = "Canino",
        description = "Dente pontiagudo ao lado dos incisivos",
        icon = "⚡",
        fileName = "canine.glb"
    ),
    PREMOLAR(
        displayName = "Pré-Molar",
        description = "Dente entre canino e molar",
        icon = "💎",
        fileName = "premolar.glb"
    ),
    TOOTH_SECTION(
        displayName = "Corte Transversal",
        description = "Anatomia interna do dente",
        icon = "🔬",
        fileName = "tooth_section.glb"
    ),
    HEALTHY_TOOTH(
        displayName = "Dente Saudável",
        description = "Dente em perfeito estado",
        icon = "✨",
        fileName = "healthy_tooth.glb"
    ),
    CAVITY_TOOTH(
        displayName = "Dente com Cárie",
        description = "Demonstração de cárie dentária",
        icon = "⚠️",
        fileName = "cavity_tooth.glb"
    )
}

/**
 * Modelo para item da galeria 3D
 */
data class TeethModel3D(
    val type: TeethModelType,
    val isFavorite: Boolean = false,
    val lastViewed: Long? = null,
    val viewCount: Int = 0
) {
    val displayName: String get() = type.displayName
    val description: String get() = type.description
    val icon: String get() = type.icon
    val fileName: String get() = type.fileName
}

