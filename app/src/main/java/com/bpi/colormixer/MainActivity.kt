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
        val color = if (selectedZone == 1) {
            calculateColor(zone1Red, zone1Yellow, zone1Blue, zone1White, zone1Black)
        } else {
            calculateColor(zone2Red, zone2Yellow, zone2Blue, zone2White, zone2Black)
        }

        // Mettre à jour la prévisualisation de la couleur
        if (selectedZone == 1) {
            colorPreviewLayout1.setBackgroundColor(color)
        } else {
            colorPreviewLayout2.setBackgroundColor(color)
        }

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

    private fun calculateAndShowComplementaryColor() {

        selectedZone = 2
        selectionIcon2.visibility = View.VISIBLE
        selectionIcon1.visibility = View.GONE

        // Calculer la couleur complémentaire de la zone 1
        val color = calculateColor(zone1Red, zone1Yellow, zone1Blue, zone1White, zone1Black)
        val complementaryColor = calculateComplementaryColorRYB(color)

        // Extraire les composantes RGB de la couleur complémentaire
        val complementaryRed = Color.red(complementaryColor)
        val complementaryGreen = Color.green(complementaryColor)
        val complementaryBlue = Color.blue(complementaryColor)

        // Mettre à jour les curseurs de la zone 2 avec les valeurs RGB de la couleur complémentaire
        zone2Red = complementaryRed
        zone2Yellow = complementaryGreen
        zone2Blue = complementaryBlue
        zone2White = 0 // Réinitialiser le blanc
        zone2Black = 0 // Réinitialiser le noir

        val colorzone2 = calculateColor(zone2Red, zone2Yellow, zone2Blue, zone2White, zone2Black)

        // Mettre à jour les curseurs dans l'interface utilisateur
        redSeekBar.progress = zone2Red
        yellowSeekBar.progress = zone2Yellow
        blueSeekBar.progress = zone2Blue
        whiteSeekBar.progress = zone2White
        blackSeekBar.progress = zone2Black

        // Mettre à jour la zone 2 avec la couleur complémentaire
        colorPreviewLayout2.setBackgroundColor(colorzone2)

        // Mettre à jour les pourcentages
        updatePercentages()
    }

    private fun calculateComplementaryColorRYB(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        //normalement dans le cercle chromatique RYB l'orange est entre le rouge et le jaune, donc entre 330 et 30 degré.
        hsv[0] = when {
            hsv[0] in 330.0..30.0 -> 210f // Orange -> Bleu
            hsv[0] in 30.0..90.0 -> 300f // Jaune -> Violet
            hsv[0] in 90.0..180.0 -> 330f // Vert -> Rouge-Violet
            hsv[0] in 180.0..270.0 -> 30f  // Bleu -> Orange-Rouge
            hsv[0] in 270.0..330.0 -> 90f // Violet -> Jaune-Vert
            else -> (hsv[0] + 180) % 360
        }
        return Color.HSVToColor(hsv)
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
