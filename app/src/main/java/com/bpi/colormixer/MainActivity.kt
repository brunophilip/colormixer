package com.bpi.colormixer

import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import android.view.View
import android.widget.ImageView
import kotlin.math.max

class MainActivity : AppCompatActivity() {
    // Variables pour stocker les états des curseurs pour Zone 1
    private var zone1Red = 0
    private var zone1Yellow = 0
    private var zone1Blue = 0
    private var zone1White = 0
    private var zone1Black = 0

    // Variables pour stocker les états des curseurs pour Zone 2
    private var zone2Red = 0
    private var zone2Yellow = 0
    private var zone2Blue = 0
    private var zone2White = 0
    private var zone2Black = 0

    // Variables pour les SeekBars, TextViews, et les zones de prévisualisation
    private lateinit var colorPreviewLayout1: ConstraintLayout
    private lateinit var colorPreviewLayout2: ConstraintLayout
    private lateinit var colorMixLayout: ConstraintLayout
    private lateinit var resetButton: Button
    private lateinit var complementaryButton: Button

    // Déclaration des sliders pour les 5 couleurs de base
    private lateinit var redSeekBar: SeekBar
    private lateinit var yellowSeekBar: SeekBar
    private lateinit var blueSeekBar: SeekBar
    private lateinit var whiteSeekBar: SeekBar
    private lateinit var blackSeekBar: SeekBar

    // Déclaration des pourcentages à afficher
    private lateinit var redPercentage: TextView
    private lateinit var yellowPercentage: TextView
    private lateinit var bluePercentage: TextView
    private lateinit var whitePercentage: TextView
    private lateinit var blackPercentage: TextView

    // Vue qui affichera la couleur mélangée
    private lateinit var colorPreview: ConstraintLayout

    // Variable pour savoir quelle zone est sélectionnée
    private var selectedZone = 1 // 1 pour zone 1, 2 pour zone 2

    private var mixProgress = 50 // Initialisé à 50 pour un mélange égal
    private lateinit var seekBarMixProportion: SeekBar

    private lateinit var selectionIcon1: ImageView
    private lateinit var selectionIcon2: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialisation des composants
        initComponents()

        // Configuration des listeners pour les sliders
        setupListeners()

        // Initialisation de la couleur de prévisualisation
        updateColorPreview()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_colorWheel -> {
                val intent = Intent(this, ColorWheelActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initComponents() {
        // Initialisation des vues
        colorPreviewLayout1 = findViewById(R.id.colorPreviewLayout1)
        colorPreviewLayout2 = findViewById(R.id.colorPreviewLayout2)
        selectionIcon1 = findViewById(R.id.selectionIcon1)
        selectionIcon2 = findViewById(R.id.selectionIcon2)
        colorMixLayout = findViewById(R.id.colorMixLayout)
        resetButton = findViewById(R.id.resetButton)
        complementaryButton = findViewById(R.id.complementaryButton)

        // Association des sliders avec le layout
        redSeekBar = findViewById(R.id.seekBarRed)
        yellowSeekBar = findViewById(R.id.seekBarYellow)
        blueSeekBar = findViewById(R.id.seekBarBlue)
        whiteSeekBar = findViewById(R.id.seekBarWhite)
        blackSeekBar = findViewById(R.id.seekBarBlack)

        // Association des affichages de pourcentages
        redPercentage = findViewById(R.id.textViewRedPercentage)
        yellowPercentage = findViewById(R.id.textViewYellowPercentage)
        bluePercentage = findViewById(R.id.textViewBluePercentage)
        whitePercentage = findViewById(R.id.textViewWhitePercentage)
        blackPercentage = findViewById(R.id.textViewBlackPercentage)

        // Association de la vue de prévisualisation
        colorPreview = findViewById(R.id.colorPreviewLayout)

        seekBarMixProportion = findViewById(R.id.seekBarMixProportion)
        seekBarMixProportion.progress = mixProgress

        seekBarMixProportion.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mixProgress = progress
                updateMixedColor()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Listener pour le bouton de réinitialisation
        resetButton.setOnClickListener {
            resetAll()
        }

        // Listener pour le bouton de couleur complémentaire
        complementaryButton.setOnClickListener {
            calculateAndShowComplementaryColor()
        }

        // Clic sur la première zone de prévisualisation
        colorPreviewLayout1.setOnClickListener {
            selectedZone = 1
            selectionIcon1.visibility = View.VISIBLE
            selectionIcon2.visibility = View.GONE
            updateSeekBarsForSelectedZone()
        }

        // Clic sur la deuxième zone de prévisualisation
        colorPreviewLayout2.setOnClickListener {
            selectedZone = 2
            selectionIcon2.visibility = View.VISIBLE
            selectionIcon1.visibility = View.GONE
            updateSeekBarsForSelectedZone()
        }

        selectionIcon1.visibility = View.VISIBLE
        selectionIcon2.visibility = View.GONE
    }

    private fun setupListeners() {
        val seekBars = listOf(redSeekBar, yellowSeekBar, blueSeekBar, whiteSeekBar, blackSeekBar)

        for (seekBar in seekBars) {
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    updatePercentages()

                    // Mettre à jour les variables de la zone sélectionnée
                    if (selectedZone == 1) {
                        when (seekBar?.id) {
                            R.id.seekBarRed -> zone1Red = progress
                            R.id.seekBarYellow -> zone1Yellow = progress
                            R.id.seekBarBlue -> zone1Blue = progress
                            R.id.seekBarWhite -> zone1White = progress
                            R.id.seekBarBlack -> zone1Black = progress
                        }
                    } else if (selectedZone == 2) {
                        when (seekBar?.id) {
                            R.id.seekBarRed -> zone2Red = progress
                            R.id.seekBarYellow -> zone2Yellow = progress
                            R.id.seekBarBlue -> zone2Blue = progress
                            R.id.seekBarWhite -> zone2White = progress
                            R.id.seekBarBlack -> zone2Black = progress
                        }
                    }

                    // Mettre à jour la couleur de prévisualisation
                    updateColorPreview()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun updateMixedColor() {
        val color1 = calculateColor(zone1Red, zone1Yellow, zone1Blue, zone1White, zone1Black)
        val color2 = calculateColor(zone2Red, zone2Yellow, zone2Blue, zone2White, zone2Black)

        val mixedColor = mixColorsWithProportion(color1, color2, mixProgress / 100.0f)
        colorMixLayout.setBackgroundColor(mixedColor)
    }

    private fun mixColorsWithProportion(color1: Int, color2: Int, proportion: Float): Int {
        val r1 = Color.red(color1)
        val g1 = Color.green(color1)
        val b1 = Color.blue(color1)

        val r2 = Color.red(color2)
        val g2 = Color.green(color2)
        val b2 = Color.blue(color2)

        val r = (r1 * (1 - proportion) + r2 * proportion).toInt()
        val g = (g1 * (1 - proportion) + g2 * proportion).toInt()
        val b = (b1 * (1 - proportion) + b2 * proportion).toInt()

        return Color.rgb(r, g, b)
    }

    private fun updatePercentages() {
        // Calcul du total pour calculer les pourcentages
        val total = redSeekBar.progress + yellowSeekBar.progress +
                blueSeekBar.progress + whiteSeekBar.progress + blackSeekBar.progress

        // Éviter la division par zéro
        if (total > 0) {
            redPercentage.text = "${calculatePercentage(redSeekBar.progress, total)}%"
            yellowPercentage.text = "${calculatePercentage(yellowSeekBar.progress, total)}%"
            bluePercentage.text = "${calculatePercentage(blueSeekBar.progress, total)}%"
            whitePercentage.text = "${calculatePercentage(whiteSeekBar.progress, total)}%"
            blackPercentage.text = "${calculatePercentage(blackSeekBar.progress, total)}%"
        } else {
            // Si tout est à zéro
            redPercentage.text = "0%"
            yellowPercentage.text = "0%"
            bluePercentage.text = "0%"
            whitePercentage.text = "0%"
            blackPercentage.text = "0%"
        }
    }

    private fun calculatePercentage(value: Int, total: Int): Int {
        return (value * 100 / total)
    }

    private fun updateColorPreview() {
        // Calculer la couleur basée sur la zone sélectionnée
        val color1 = calculateColor(zone1Red, zone1Yellow, zone1Blue, zone1White, zone1Black)
        val color2 = calculateColor(zone2Red, zone2Yellow, zone2Blue, zone2White, zone2Black)

        colorPreviewLayout1.setBackgroundColor(color1)

        colorPreviewLayout2.setBackgroundColor(color2)

        updateMixedColor()
    }

    private fun updateSeekBarsForSelectedZone() {
        // Mettre à jour les SeekBars en fonction de la zone sélectionnée
        if (selectedZone == 1) {
            redSeekBar.progress = zone1Red
            yellowSeekBar.progress = zone1Yellow
            blueSeekBar.progress = zone1Blue
            whiteSeekBar.progress = zone1White
            blackSeekBar.progress = zone1Black
        } else {
            redSeekBar.progress = zone2Red
            yellowSeekBar.progress = zone2Yellow
            blueSeekBar.progress = zone2Blue
            whiteSeekBar.progress = zone2White
            blackSeekBar.progress = zone2Black
        }
    }

    private fun getColorFromRYBName(colorName: String?): Int {
        return when (colorName) {
            "RED" -> Color.RED
            "YELLOW" -> Color.YELLOW
            "BLUE" -> Color.BLUE
            "GREEN" -> Color.GREEN
            "PURPLE" -> Color.rgb(128, 0, 128) // Violet
            "ORANGE" -> Color.rgb(255, 165, 0)  // Orange
            else -> Color.WHITE // Par défaut
        }
    }

    private fun getDominantColors(red: Int, yellow: Int, blue: Int): List<Pair<String, Int>> {
        val colors = listOf(
            "RED" to red,
            "YELLOW" to yellow,
            "BLUE" to blue
        )
        // Trier les couleurs par ordre décroissant de leur valeur
        return colors.sortedByDescending { it.second }
    }

    private fun calculateAndShowComplementaryColor() {
        // Calculer la couleur complémentaire en fonction de la zone sélectionnée
        val complementaryColor = calculateComplementaryColorRYB(zone1Red, zone1Yellow, zone1Blue)

        selectedZone = 2
        selectionIcon2.visibility = View.VISIBLE
        selectionIcon1.visibility = View.GONE

        val complementaryValues = getRYBValuesFromColor(complementaryColor)

        zone2Red = complementaryValues.first
        zone2Yellow = complementaryValues.second
        zone2Blue = complementaryValues.third
        zone2White = zone1White
        zone2Black = zone1Black

        // Mettre à jour les curseurs dans l'interface utilisateur
        redSeekBar.progress = zone2Red
        yellowSeekBar.progress = zone2Yellow
        blueSeekBar.progress = zone2Blue

        // Mettre à jour les pourcentages
        updatePercentages()

        // Mettre à jour la zone 2 avec la couleur complémentaire
        //colorPreviewLayout2.setBackgroundColor(complementaryColor)
        updateColorPreview()
    }

    // Définir une map pour reconnaître les couleurs standard
    private val standardRYBColors = mapOf(
        Color.RED to "RED",
        Color.YELLOW to "YELLOW",
        Color.BLUE to "BLUE",
        Color.GREEN to "GREEN",
        Color.rgb(128, 0, 128) to "PURPLE", // Violet
        Color.rgb(255, 165, 0) to "ORANGE"  // Orange
    )

    private val rybComplementaryMap = mapOf(
        "RED" to "GREEN",    // Rouge ↔ Vert
        "YELLOW" to "PURPLE", // Jaune ↔ Violet
        "BLUE" to "ORANGE",   // Bleu ↔ Orange
        "GREEN" to "RED",    // Vert ↔ Rouge
        "PURPLE" to "YELLOW", // Violet ↔ Jaune
        "ORANGE" to "BLUE"   // Orange ↔ Bleu
    )

    // Fonction pour identifier si une couleur est proche d'une couleur standard RYB
    private fun identifyStandardRYBColor(color: Int): String? {
        // Calculer la distance entre la couleur donnée et chaque couleur standard
        val colorDistances = standardRYBColors.map { (stdColor, name) ->
            val r1 = Color.red(color)
            val g1 = Color.green(color)
            val b1 = Color.blue(color)

            val r2 = Color.red(stdColor)
            val g2 = Color.green(stdColor)
            val b2 = Color.blue(stdColor)

            // Distance euclidienne dans l'espace RGB
            val distance = Math.sqrt(
                Math.pow((r1 - r2).toDouble(), 2.0) +
                        Math.pow((g1 - g2).toDouble(), 2.0) +
                        Math.pow((b1 - b2).toDouble(), 2.0)
            )

            Pair(name, distance)
        }

        // Trouver la couleur standard la plus proche
        val closestColor = colorDistances.minByOrNull { it.second }

        // Si la distance est inférieure à un seuil, considérer cette couleur comme une couleur standard
        return if (closestColor != null && closestColor.second < 50.0) {
            closestColor.first
        } else {
            null
        }
    }

    private fun mixRYBColors(color1: String, color2: String, color3: String? = null): Int {
        val rgb1 = getColorFromRYBName(color1)
        val rgb2 = getColorFromRYBName(color2)
        val rgb3 = color3?.let { getColorFromRYBName(it) } ?: Color.TRANSPARENT

        // Mélanger les couleurs en moyenne
        val r = (Color.red(rgb1) + Color.red(rgb2) + if (rgb3 != Color.TRANSPARENT) Color.red(rgb3) else 0) /
                if (rgb3 != Color.TRANSPARENT) 3 else 2
        val g = (Color.green(rgb1) + Color.green(rgb2) + if (rgb3 != Color.TRANSPARENT) Color.green(rgb3) else 0) /
                if (rgb3 != Color.TRANSPARENT) 3 else 2
        val b = (Color.blue(rgb1) + Color.blue(rgb2) + if (rgb3 != Color.TRANSPARENT) Color.blue(rgb3) else 0) /
                if (rgb3 != Color.TRANSPARENT) 3 else 2

        return Color.rgb(r, g, b)
    }

    private fun calculateComplementaryColorRYB(red: Int, yellow: Int, blue: Int): Int {
        // Calculer la couleur actuelle en RGB
        val currentColor = calculateColor(red, yellow, blue, 0, 0)

        // Vérifier si c'est une couleur standard
        val standardColorName = identifyStandardRYBColor(currentColor)

        if (standardColorName != null) {
            // Si c'est une couleur standard, utiliser directement la map des complémentaires
            val complementaryName = rybComplementaryMap[standardColorName]
            if (complementaryName != null) {
                return getColorFromRYBName(complementaryName)
            }
        }

        // Si ce n'est pas une couleur standard, utiliser l'ancienne méthode
        val dominantColors = getDominantColors(red, yellow, blue)

        return when {
            // Une seule couleur dominante
            dominantColors[0].second > 0 && dominantColors[1].second == 0 && dominantColors[2].second == 0 -> {
                val complementaryColorName = rybComplementaryMap[dominantColors[0].first]
                getColorFromRYBName(complementaryColorName)
            }
            // Deux couleurs dominantes
            dominantColors[0].second > 0 && dominantColors[1].second > 0 && dominantColors[2].second == 0 -> {
                val complementaryColorName1 = rybComplementaryMap[dominantColors[0].first] ?: "WHITE"
                val complementaryColorName2 = rybComplementaryMap[dominantColors[1].first] ?: "WHITE"
                mixRYBColors(complementaryColorName1, complementaryColorName2)
            }
            // Trois couleurs dominantes
            else -> {
                val complementaryColorName1 = rybComplementaryMap[dominantColors[0].first] ?: "WHITE"
                val complementaryColorName2 = rybComplementaryMap[dominantColors[1].first] ?: "WHITE"
                val minorColorName = dominantColors[2].first
                mixRYBColors(complementaryColorName1, complementaryColorName2, minorColorName)
            }
        }
    }

    private fun getRYBValuesFromColor(color: Int): Triple<Int, Int, Int> {
        // Vérifier d'abord si c'est une couleur standard RYB
        val standardColorName = identifyStandardRYBColor(color)

        if (standardColorName != null) {
            // Pour les couleurs standard, définir des valeurs RYB représentatives
            return when (standardColorName) {
                "RED" -> Triple(255, 0, 0)
                "YELLOW" -> Triple(0, 255, 0)
                "BLUE" -> Triple(0, 0, 255)
                "GREEN" -> Triple(0, 200, 100) // Mélange de jaune et bleu
                "PURPLE" -> Triple(200, 0, 200) // Mélange de rouge et bleu
                "ORANGE" -> Triple(230, 150, 0) // Mélange de rouge et jaune
                else -> Triple(0, 0, 0)
            }
        }

        // Pour les autres couleurs, approximer les valeurs RYB
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        // Extraction approximative des composantes RYB
        // Ajustement des valeurs pour une meilleure répartition
        val maxValue = 255

        // Algorithme amélioré pour l'extraction RYB à partir de RGB
        val white = Math.min(r, Math.min(g, b))

        val red = Math.max(0, r - white)
        val green = Math.max(0, g - white)
        val blue = Math.max(0, b - white)

        val yellow = Math.min(red, green)

        val pureRed = Math.max(0, red - yellow)
        val pureBlue = blue
        val pureYellow = yellow

        // Normaliser les valeurs pour qu'elles restent dans la plage 0-255
        val maxComponent = Math.max(pureRed, Math.max(pureYellow, pureBlue))
        val scale = if (maxComponent > 0) maxValue.toFloat() / maxComponent else 1f

        return Triple(
            (pureRed * scale).toInt().coerceIn(0, maxValue),
            (pureYellow * scale).toInt().coerceIn(0, maxValue),
            (pureBlue * scale).toInt().coerceIn(0, maxValue)
        )
    }

    private fun calculateColor(red: Int, yellow: Int, blue: Int, white: Int, black: Int): Int {
        // Vérifier si toutes les valeurs sont à zéro
        if (red == 0 && yellow == 0 && blue == 0 && white == 0 && black == 0) {
            return Color.WHITE // Retourner du blanc si tout est à zéro
        }

        val redValue = red.toFloat()
        val yellowValue = yellow.toFloat()
        val blueValue = blue.toFloat()
        val whiteValue = white.toFloat()
        val blackValue = black.toFloat()

        val total = redValue + yellowValue + blueValue + whiteValue + blackValue

        // Normalisation des couleurs (RYB -> RGB conversion)
        val r = if (total > 0) redValue / total else 0f
        val y = if (total > 0) yellowValue / total else 0f
        val b = if (total > 0) blueValue / total else 0f
        val w = if (total > 0) whiteValue / total else 0f
        val k = if (total > 0) blackValue / total else 0f

        var finalR = (r + y - b * 0.5f) * 255
        var finalG = (y + b * 0.5f) * 255
        var finalB = (b + r * 0.2f) * 255

        // Gestion du blanc (interpolation linéaire)
        finalR = (finalR * (1 - w) + 255 * w).coerceIn(0f, 255f)
        finalG = (finalG * (1 - w) + 255 * w).coerceIn(0f, 255f)
        finalB = (finalB * (1 - w) + 255 * w).coerceIn(0f, 255f)

        // Gestion du noir (effet exponentiel)
        val blackEffect = k * k
        finalR = (finalR * (1 - blackEffect)).coerceIn(0f, 255f)
        finalG = (finalG * (1 - blackEffect)).coerceIn(0f, 255f)
        finalB = (finalB * (1 - blackEffect)).coerceIn(0f, 255f)

        return Color.rgb(
            finalR.toInt(),
            finalG.toInt(),
            finalB.toInt()
        )
    }

    private fun resetAll() {
        // Réinitialiser les sliders et les prévisualisations
        redSeekBar.progress = 0
        yellowSeekBar.progress = 0
        blueSeekBar.progress = 0
        whiteSeekBar.progress = 0
        blackSeekBar.progress = 0
        selectedZone = 1
        selectionIcon1.visibility = View.VISIBLE
        selectionIcon2.visibility = View.GONE
        updateSeekBarsForSelectedZone()
        updatePercentages()
        updateColorPreview()
    }

}
