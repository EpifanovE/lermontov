package ru.eecode.lermontov.ui.prefs

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ru.eecode.lermontov.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

}