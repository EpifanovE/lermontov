package ru.eecode.dir.ui.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.eecode.dir.R
import ru.eecode.dir.databinding.FragmentFavoritesBinding
import ru.eecode.dir.domain.FavoritesViewModel
import ru.eecode.dir.repository.db.articles.ArticleListItem
import ru.eecode.dir.ui.articles.ArticleAdapter

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private val adapter: ArticleAdapter = ArticleAdapter()

    private val viewModel: FavoritesViewModel by activityViewModels()

    private var binding: FragmentFavoritesBinding? = null

    private var articlesLayoutManager: LinearLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_favorites, container, false)
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFavoritesBinding.bind(view)
        binding!!.lifecycleOwner = viewLifecycleOwner
        binding!!.viewmodel = viewModel

        articlesLayoutManager = LinearLayoutManager(context)

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        adapter.onItemClickListener = object : ArticleAdapter.OnItemClickListener {
            override fun onItemClick(item: ArticleListItem) {
                val bundle = bundleOf("articleId" to item.id)
                Navigation.findNavController(view).navigate(R.id.action_nav_favorites_to_articleFragment, bundle)
            }
        }

        adapter.onFavoriteClickListener = object : ArticleAdapter.OnFavoriteClickListener {
            override fun onClick(articleId: Int, isFavorite: Boolean) {
                if (isFavorite) {
                    viewModel.removeFromFavorites(articleId)
                } else {
                    viewModel.addToFavorites(articleId)
                }
            }
        }

        val articlesIndex = binding!!.favoritesIndex

        articlesIndex.layoutManager = articlesLayoutManager
        articlesIndex.adapter = adapter

        viewModel.favorites.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }
}