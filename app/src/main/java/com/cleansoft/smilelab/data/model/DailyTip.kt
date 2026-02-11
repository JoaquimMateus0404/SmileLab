package com.cleansoft.smilelab.data.model

/**
 * Dicas diárias sobre saúde bucal
 */
data class DailyTip(
    val id: Int,
    val title: String,
    val content: String,
    val category: TipCategory,
    val icon: String
)

enum class TipCategory {
    BRUSHING,
    FLOSSING,
    NUTRITION,
    PREVENTION,
    HABITS
}

object DailyTips {
    fun getAll() = listOf(
        DailyTip(
            1,
            "Escove por 2 minutos",
            "O tempo ideal de escovação é de 2 minutos, garantindo que todos os dentes sejam limpos adequadamente.",
            TipCategory.BRUSHING,
            "⏱️"
        ),
        DailyTip(
            2,
            "Troque sua escova regularmente",
            "Substitua sua escova de dentes a cada 3 meses ou quando as cerdas estiverem gastas.",
            TipCategory.BRUSHING,
            "🪥"
        ),
        DailyTip(
            3,
            "Use fio dental diariamente",
            "O fio dental remove até 80% da placa que a escova não alcança entre os dentes.",
            TipCategory.FLOSSING,
            "🧵"
        ),
        DailyTip(
            4,
            "Beba água após as refeições",
            "Água ajuda a remover resíduos de alimentos e neutraliza ácidos que causam cáries.",
            TipCategory.NUTRITION,
            "💧"
        ),
        DailyTip(
            5,
            "Evite açúcar antes de dormir",
            "Consumir açúcar antes de dormir aumenta o risco de cáries, pois a produção de saliva diminui durante o sono.",
            TipCategory.NUTRITION,
            "🍬"
        ),
        DailyTip(
            6,
            "Visite o dentista regularmente",
            "Consultas semestrais ajudam a detectar problemas precocemente e manter sua saúde bucal.",
            TipCategory.PREVENTION,
            "👨‍⚕️"
        ),
        DailyTip(
            7,
            "Não escovar imediatamente após ácidos",
            "Espere 30 minutos após consumir alimentos ácidos antes de escovar para proteger o esmalte.",
            TipCategory.HABITS,
            "🍋"
        ),
        DailyTip(
            8,
            "Use pasta com flúor",
            "O flúor fortalece o esmalte e ajuda a prevenir cáries.",
            TipCategory.BRUSHING,
            "✨"
        ),
        DailyTip(
            9,
            "Escove a língua",
            "A língua acumula bactérias que causam mau hálito. Escove-a suavemente todos os dias.",
            TipCategory.BRUSHING,
            "👅"
        ),
        DailyTip(
            10,
            "Mastigue alimentos fibrosos",
            "Frutas e vegetais fibrosos ajudam a limpar os dentes naturalmente.",
            TipCategory.NUTRITION,
            "🥕"
        ),
        DailyTip(
            11,
            "Técnica correta de escovação",
            "Use movimentos circulares suaves, inclinando a escova a 45° em relação à gengiva.",
            TipCategory.BRUSHING,
            "🔄"
        ),
        DailyTip(
            12,
            "Não compartilhe escova de dentes",
            "Compartilhar escovas pode transferir bactérias e vírus entre pessoas.",
            TipCategory.HABITS,
            "🚫"
        ),
        DailyTip(
            13,
            "Enxaguante bucal ajuda",
            "Use enxaguante bucal sem álcool para complementar a higiene, mas não substitui escovação.",
            TipCategory.BRUSHING,
            "🧪"
        ),
        DailyTip(
            14,
            "Cuidado com bebidas ácidas",
            "Refrigerantes e sucos cítricos podem erodir o esmalte. Use canudo para minimizar contato.",
            TipCategory.NUTRITION,
            "🥤"
        ),
        DailyTip(
            15,
            "Mascar chiclete sem açúcar",
            "Estimula a produção de saliva, que neutraliza ácidos e protege contra cáries.",
            TipCategory.HABITS,
            "🍃"
        )
    )

    fun getTipOfDay(): DailyTip {
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        val tips = getAll()
        return tips[dayOfYear % tips.size]
    }
}

