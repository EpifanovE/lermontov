package ru.eecode.poems.ui.articles

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import ru.eecode.poems.App
import ru.eecode.poems.R
import ru.eecode.poems.databinding.FragmentArticleBinding
import ru.eecode.poems.databinding.FragmentArticlesIndexBinding
import ru.eecode.poems.domain.AdsViewModel
import ru.eecode.poems.domain.ArticleViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ArticleFragment : Fragment() {

    private val viewModel: ArticleViewModel by activityViewModels()

    private var _binding: FragmentArticleBinding? = null

    private val binding get() = _binding!!

    val adsViewModel: AdsViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentArticleBinding.inflate(inflater, container, false)

        val rootView = binding.root
        setHasOptionsMenu(true)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = viewModel

        binding.articleContent.textSize = getTextSize() ?: 18f
        binding.articleTitle.textSize = getTextSize() ?: 18f

        viewModel.articleId.value = arguments?.getInt("articleId")

        adsViewModel.emitLoadInterstitialEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.article, menu)

        val favoritesItem = menu.findItem(R.id.action_add_to_favorites)


        viewModel.isFavorite.observe(viewLifecycleOwner, {
            favoritesItem.isChecked = it != null
            val favoriteIcon = favoritesItem.icon

            if (it != null) {
                favoriteIcon.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    ContextCompat.getColor(requireContext(), R.color.favoriteButtonActive), BlendModeCompat.SRC_ATOP
                )
                favoritesItem.icon = favoriteIcon
            } else {
                favoriteIcon.colorFilter = null
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_to_favorites -> {
                viewModel.toggleFavorites()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getTextSize(): Float? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString("font_size", "18")?.toFloat()
    }
}