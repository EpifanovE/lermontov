package ru.eecode.lermontov.ui.articles

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import ru.eecode.lermontov.R
import ru.eecode.lermontov.databinding.FragmentArticleBinding
import ru.eecode.lermontov.domain.ArticleViewModel

@AndroidEntryPoint
class ArticleFragment: Fragment() {

    private val viewModel: ArticleViewModel by activityViewModels()

    private var binding: FragmentArticleBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater.inflate(R.layout.fragment_article, container, false)
        setHasOptionsMenu(true)


        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentArticleBinding.bind(view)
        binding!!.lifecycleOwner = viewLifecycleOwner
        binding!!.viewmodel = viewModel

        binding!!.articleContent.textSize = getTextSize() ?: 18f
        binding!!.articleTitle.textSize = getTextSize() ?: 18f

        viewModel.articleId.value = arguments?.getInt("articleId")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.article, menu)

        val favoritesItem = menu.findItem(R.id.action_add_to_favorites)

        viewModel.isFavorite.observe(viewLifecycleOwner, {
            favoritesItem.isChecked = it != null
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