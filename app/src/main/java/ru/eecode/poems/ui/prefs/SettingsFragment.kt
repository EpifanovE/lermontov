package ru.eecode.poems.ui.prefs

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ru.eecode.poems.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

}