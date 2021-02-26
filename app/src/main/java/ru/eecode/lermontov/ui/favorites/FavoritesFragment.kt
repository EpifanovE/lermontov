package ru.eecode.lermontov.ui.favorites

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.eecode.lermontov.R
import ru.eecode.lermontov.databinding.FragmentFavoritesBinding
import ru.eecode.lermontov.domain.FavoritesViewModel
import ru.eecode.lermontov.repository.db.articles.ArticleListItem
import ru.eecode.lermontov.ui.articles.ArticleAdapter

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorites, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_favorites -> {
                confirmDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun confirmDialog() {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }

        builder?.apply {
            setPositiveButton(R.string.yes) { _, _ ->
                viewModel.clearFavorites()
            }
            setNegativeButton(R.string.no) { dialog, _ ->
                dialog.cancel()
            }
            setMessage(R.string.clear_favorites_dialog_message)
                ?.setTitle(R.string.clear_favorites_dialog_title)
        }

        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
    }
}