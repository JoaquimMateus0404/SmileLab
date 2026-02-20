package com.cleansoft.smilelab.data.model

/**
 * Modelos e imagens disponíveis na pasta assets/models
 */
enum class TeethModelType(
    val displayName: String,
    val description: String,
    val fileName: String,
    val is3D: Boolean
) {
    // Imagens
    CANINE_IMAGE(
        displayName = "Canino",
        description = "Imagem de um canino",
        fileName = "canine.jpg",
        is3D = false
    ),
    DENTE_CANINO_IMAGE(
        displayName = "Dente Canino",
        description = "Outra imagem de canino",
        fileName = "dente_canino.jpg",
        is3D = false
    ),
    INCISOR_IMAGE(
        displayName = "Incisivo",
        description = "Imagem de um incisivo",
        fileName = "incisor.jpg",
        is3D = false
    ),
    MOLAR_IMAGE(
        displayName = "Molar",
        description = "Imagem de um molar",
        fileName = "molar.jpg",
        is3D = false
    ),
    PREMOLAR_IMAGE(
        displayName = "Pré-molar",
        description = "Imagem de um pré-molar",
        fileName = "premolar.jpg",
        is3D = false
    ),
    DENTAL_ANATOMY_IMAGE(
        displayName = "Anatomia Dentária",
        description = "Ilustração da anatomia dentária",
        fileName = "dental_anatomy.png",
        is3D = false
    ),
    DENTAL_STRUCTURE_IMAGE(
        displayName = "Estrutura Dentária",
        description = "Ilustração da estrutura dentária",
        fileName = "dental_structure.jpg",
        is3D = false
    ),
    GINGIVITIS_IMAGE(
        displayName = "Gengivite",
        description = "Imagem ilustrativa de gengivite",
        fileName = "gingivitis.jpg",
        is3D = false
    ),
    PERIODONTITIS_IMAGE(
        displayName = "Periodontite",
        description = "Imagem ilustrativa de periodontite",
        fileName = "periodontitis.jpg",
        is3D = false
    ),
    ORAL_DENTITION_IMAGE(
        displayName = "Dentição Oral",
        description = "Imagem geral da dentição",
        fileName = "oral_dentition.jpg",
        is3D = false
    ),
    TOOTH_WITH_AND_WITHOUT_CAVITIES_IMAGE(
        displayName = "Dente com e sem cárie",
        description = "Comparação de dente com e sem cárie",
        fileName = "tooth_with_and_without_cavities.jpg",
        is3D = false
    ),
    TOOTH_WITH_DECAY_IMAGE(
        displayName = "Dente com cárie",
        description = "Imagem mostrando dente com cárie",
        fileName = "tooth_with_decay.jpg",
        is3D = false
    ),
    TONGUE_CLEANER_IMAGE(
        displayName = "Limpador de língua",
        description = "Imagem de um limpador de língua",
        fileName = "tongue_cleaner.jpg",
        is3D = false
    ),
    TOOTHBRUSH_IMAGE(
        displayName = "Escova de dentes",
        description = "Imagem de uma escova de dentes",
        fileName = "toothbrush.jpg",
        is3D = false
    ),

    // Modelos 3D (.glb)
    MANDIBULAR_FIRST_PREMOLAR(
        displayName = "Mandíbula: Primeiro Pré-Molar",
        description = "Modelo 3D do primeiro pré-molar mandibular",
        fileName = "mandibular_first_premolar.glb",
        is3D = true
    ),
    MANDIBULAR_LEFT_CANINE(
        displayName = "Mandíbula: Canino Esquerdo",
        description = "Modelo 3D do canino mandibular esquerdo",
        fileName = "mandibular_left_canine.glb",
        is3D = true
    ),
    MANDIBULAR_SECOND_MOLAR(
        displayName = "Mandíbula: Segundo Molar",
        description = "Modelo 3D do segundo molar mandibular",
        fileName = "mandibular_second_molar.glb",
        is3D = true
    ),
    MANDIBULAR_THIRD_MOLAR(
        displayName = "Mandíbula: Terceiro Molar",
        description = "Modelo 3D do terceiro molar mandibular",
        fileName = "mandibular_third_molar.glb",
        is3D = true
    ),
    MAXILLARY_CANINE(
        displayName = "Maxila: Canino",
        description = "Modelo 3D do canino maxilar",
        fileName = "maxillary_canine.glb",
        is3D = true
    ),
    MAXILLARY_FIRST_MOLAR(
        displayName = "Maxila: Primeiro Molar",
        description = "Modelo 3D do primeiro molar maxilar",
        fileName = "maxillary_first_molar.glb",
        is3D = true
    ),
    MAXILLARY_FIRST_MOLAR_WITH_CUSP_OF_CARABELLII(
        displayName = "Maxila: 1º Molar (com Cúspide de Carabelli)",
        description = "Modelo 3D do primeiro molar maxilar com a cúspide de Carabelli",
        fileName = "maxillary_first_molar_with_cusp_of_carabelli.glb",
        is3D = true
    ),
    MAXILLARY_FIRST_PREMOLAR(
        displayName = "Maxila: Primeiro Pré-Molar",
        description = "Modelo 3D do primeiro pré-molar maxilar",
        fileName = "maxillary_first_premolar.glb",
        is3D = true
    ),
    MAXILLARY_LEFT_CENTRAL_INCISOR(
        displayName = "Maxila: Incisivo Central Esquerdo",
        description = "Modelo 3D do incisivo central maxilar esquerdo",
        fileName = "maxillary_left_central_incisor.glb",
        is3D = true
    ),
    MAXILLARY_SECOND_MOLAR(
        displayName = "Maxila: Segundo Molar",
        description = "Modelo 3D do segundo molar maxilar",
        fileName = "maxillary_second_molar.glb",
        is3D = true
    ),
    PERMANENT_DENTITION(
        displayName = "Canais",
        description = "Modelo 3D da parte interior do dente",
        fileName = "inside_my_tooth.glb",
        is3D = true
    ),
}

/**
 * Modelo para item da galeria 3D/imagens
 */
data class TeethModel3D(
    val type: TeethModelType,
    val isFavorite: Boolean = false,
    val lastViewed: Long? = null,
    val viewCount: Int = 0
) {
    val displayName: String get() = type.displayName
    val description: String get() = type.description
    val fileName: String get() = type.fileName
    val is3D: Boolean get() = type.is3D

    // Representação curta para a paleta (emoji) - imagens ganham um ícone de quadro, 3D ganha dente
    val previewEmoji: String get() = if (is3D) "🦷" else "🖼️"
}
