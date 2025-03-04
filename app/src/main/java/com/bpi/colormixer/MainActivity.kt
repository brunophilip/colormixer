// MainActivity.kt
package com.bpi.colormixer

import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Button
import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import android.util.Log

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
    private lateinit var resetButton: Button
    private lateinit var colorSelectorGroup: RadioGroup
    private lateinit var radioButtonColor1: RadioButton
    private lateinit var radioButtonColor2: RadioButton
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
        resetButton = findViewById(R.id.resetButton)
        colorSelectorGroup = findViewById(R.id.colorSelectorGroup)
        radioButtonColor1 = findViewById(R.id.radioButtonColor1)
        radioButtonColor2 = findViewById(R.id.radioButtonColor2)
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

        // Écouteur de changement pour le RadioGroup
        colorSelectorGroup.setOnCheckedChangeListener { _, checkedId ->
            updateSeekBarsFromZone()
        }

        // Listener pour le bouton de réinitialisation
        resetButton.setOnClickListener {
            resetAll()
        }

        // Listener pour le bouton de couleur complémentaire
        complementaryButton.setOnClickListener {
            calculateAndShowComplementaryColor()
        }

    }

    private fun setupListeners() {
        val seekBars = listOf(redSeekBar, yellowSeekBar, blueSeekBar, whiteSeekBar, blackSeekBar)

        for (seekBar in seekBars) {
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    updatePercentages()

                    // Mettre à jour les variables de zone
                    when (seekBar?.id) {
                        R.id.seekBarRed -> if (radioButtonColor1.isChecked) zone1Red = progress else zone2Red = progress
                        R.id.seekBarYellow -> if (radioButtonColor1.isChecked) zone1Yellow = progress else zone2Yellow = progress
                        R.id.seekBarBlue -> if (radioButtonColor1.isChecked) zone1Blue = progress else zone2Blue = progress
                        R.id.seekBarWhite -> if (radioButtonColor1.isChecked) zone1White = progress else zone2White = progress
                        R.id.seekBarBlack -> if (radioButtonColor1.isChecked) zone1Black = progress else zone2Black = progress
                    }
                    updateColorPreview()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
        resetButton.setOnClickListener {
            resetSeekBars()
        }
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

    private fun updateSeekBarsFromZone() {
        if (radioButtonColor1.isChecked) {
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
        updatePercentages()
        updateColorPreview()
    }

    // Fonction pour mettre à jour la couleur de la prévisualisation
    private fun updateColorPreview() {
        val selectedColor = if (radioButtonColor1.isChecked) colorPreviewLayout1 else colorPreviewLayout2
        val red = if (radioButtonColor1.isChecked) zone1Red else zone2Red
        val yellow = if (radioButtonColor1.isChecked) zone1Yellow else zone2Yellow
        val blue = if (radioButtonColor1.isChecked) zone1Blue else zone2Blue
        val white = if (radioButtonColor1.isChecked) zone1White else zone2White
        val black = if (radioButtonColor1.isChecked) zone1Black else zone2Black

        val color = calculateColor(red, yellow, blue, white, black)
        Log.v("updateColorPreview", "red : $red, yellow : $yellow, blue : $blue, white : $white, black : $black")
        selectedColor.setBackgroundColor(color)
    }

    private fun calculateAndShowComplementaryColor() {
        // Calculer la couleur complémentaire de la zone 1
        val color = calculateColor(zone1Red, zone1Yellow, zone1Blue, zone1White, zone1Black)
        val complementaryColor = calculateComplementaryColor(color)

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

        // Sélectionner la zone 2
        radioButtonColor2.isChecked = true

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

    private fun calculateComplementaryColor(color: Int): Int {
        // Convertir la couleur RGB en HSV
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)

        // Inverser la teinte pour obtenir la couleur complémentaire
        hsv[0] = (hsv[0] + 180) % 360

        // Convertir la couleur HSV en RGB
        return Color.HSVToColor(hsv)
    }
/*
    // Exemple de méthode pour calculer la couleur (simple addition des couleurs)
    private fun calculateColor(red: Int, yellow: Int, blue: Int, white: Int, black: Int): Int {
        // Vérifier si toutes les valeurs sont à zéro
        if (red == 0 && yellow == 0 && blue == 0 && black == 0) {
            return Color.WHITE // Retourner du blanc si tout est à zéro
        }

        val redValue = red.toFloat()
        val yellowValue = yellow.toFloat()
        val blueValue = blue.toFloat()
        val whiteValue = white.toFloat()
        val blackValue = black.toFloat()

        val total = redValue + yellowValue + blueValue

        // Normalisation des couleurs (RYB -> RGB conversion)
        val r = redValue / (if (total == 0f) 1f else total)
        val y = yellowValue / (if (total == 0f) 1f else total)
        val b = blueValue / (if (total == 0f) 1f else total)

        val finalR = (r + y - b * 0.5f) * 255
        val finalG = (y + b * 0.5f) * 255
        val finalB = (b + r * 0.2f) * 255

        // Gestion du blanc et du noir
        val whiteEffect = whiteValue / 200  // Réduit l'impact du blanc (divisé par 200)
        val blackEffect = blackValue / 200  // Réduit l'impact du noir (divisé par 200)

        val adjustedR = ((1 - whiteEffect) * finalR + whiteEffect * 255).toInt()
        val adjustedG = ((1 - whiteEffect) * finalG + whiteEffect * 255).toInt()
        val adjustedB = ((1 - whiteEffect) * finalB + whiteEffect * 255).toInt()

        val finalAdjustedR = (adjustedR * (1 - blackEffect)).toInt()
        val finalAdjustedG = (adjustedG * (1 - blackEffect)).toInt()
        val finalAdjustedB = (adjustedB * (1 - blackEffect)).toInt()

        return Color.rgb(
            finalAdjustedR.coerceIn(0, 255),
            finalAdjustedG.coerceIn(0, 255),
            finalAdjustedB.coerceIn(0, 255)
        )
    }
*/
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
    /*
private fun updateColorPreview() {
    val redValue = redSeekBar.progress.toFloat()
    val yellowValue = yellowSeekBar.progress.toFloat()
    val blueValue = blueSeekBar.progress.toFloat()
    val whiteValue = whiteSeekBar.progress.toFloat()
    val blackValue = blackSeekBar.progress.toFloat()

    val total = redValue + yellowValue + blueValue
    if (total == 0f) {
        colorPreview.setBackgroundColor(Color.WHITE)
        return
    }

    // Normalisation des couleurs (RYB -> RGB conversion)
    val r = redValue / total
    val y = yellowValue / total
    val b = blueValue / total

    val finalR = (r + y - b * 0.5f) * 255
    val finalG = (y + b * 0.5f) * 255
    val finalB = (b + r * 0.2f) * 255

    // 💡 Nouvelle gestion du blanc et du noir
    val whiteEffect = whiteValue / 200  // Réduit l'impact du blanc (divisé par 200)
    val blackEffect = blackValue / 200  // Réduit l'impact du noir (divisé par 200)

    val adjustedR = ((1 - whiteEffect) * finalR + whiteEffect * 255).toInt()
    val adjustedG = ((1 - whiteEffect) * finalG + whiteEffect * 255).toInt()
    val adjustedB = ((1 - whiteEffect) * finalB + whiteEffect * 255).toInt()

    val finalAdjustedR = (adjustedR * (1 - blackEffect)).toInt()
    val finalAdjustedG = (adjustedG * (1 - blackEffect)).toInt()
    val finalAdjustedB = (adjustedB * (1 - blackEffect)).toInt()

    val color = Color.rgb(
        finalAdjustedR.coerceIn(0, 255),
        finalAdjustedG.coerceIn(0, 255),
        finalAdjustedB.coerceIn(0, 255)
    )
    colorPreview.setBackgroundColor(color)
} */

    private fun resetAll() {
        // Réinitialiser les SeekBars
        val seekBars = listOf(redSeekBar, yellowSeekBar, blueSeekBar, whiteSeekBar, blackSeekBar)
        for (seekBar in seekBars) {
            seekBar.progress = 0
        }

        // Réinitialiser les variables de zone
        zone1Red = 0
        zone1Yellow = 0
        zone1Blue = 0
        zone1White = 0
        zone1Black = 0
        zone2Red = 0
        zone2Yellow = 0
        zone2Blue = 0
        zone2White = 0
        zone2Black = 0

        // Réinitialiser les zones de prévisualisation
        colorPreviewLayout1.setBackgroundColor(Color.WHITE)
        colorPreviewLayout2.setBackgroundColor(Color.WHITE)

        // Mettre à jour les pourcentages et la couleur de prévisualisation
        updatePercentages()
        updateColorPreview()
    }

    private fun resetSeekBars() {
        // Réinitialiser les curseurs
        val seekBars = listOf(redSeekBar, yellowSeekBar, blueSeekBar, whiteSeekBar, blackSeekBar)
        for (seekBar in seekBars) {
            seekBar.progress = 0
        }

        // Réinitialiser les variables de zone
        zone1Red = 0
        zone1Yellow = 0
        zone1Blue = 0
        zone1White = 0
        zone1Black = 0
        zone2Red = 0
        zone2Yellow = 0
        zone2Blue = 0
        zone2White = 0
        zone2Black = 0

        // Mettre à jour les pourcentages
        updatePercentages()

        // Mettre à jour la couleur de prévisualisation
        updateColorPreview()
    }

}
