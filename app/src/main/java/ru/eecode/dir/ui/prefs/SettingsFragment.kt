package ru.eecode.dir.ui.prefs

import android.os.Bundle
import android.view.*
import androidx.preference.PreferenceFragmentCompat
import ru.eecode.dir.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

}