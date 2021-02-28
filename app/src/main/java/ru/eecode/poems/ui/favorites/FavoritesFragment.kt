package ru.eecode.poems.ui.favorites

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
import ru.eecode.poems.R
import ru.eecode.poems.databinding.FragmentArticlesIndexBinding
import ru.eecode.poems.databinding.FragmentFavoritesBinding
import ru.eecode.poems.domain.FavoritesViewModel
import ru.eecode.poems.repository.db.articles.ArticleListItem
import ru.eecode.poems.ui.articles.ArticleAdapter

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var adapter: ArticleAdapter? = ArticleAdapter()

    private val viewModel: FavoritesViewModel by activityViewModels()

    private var _binding: FragmentFavoritesBinding? = null

    private val binding get() = _binding!!

    private var articlesLayoutManager: LinearLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        val rootView = binding.root
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = viewModel


        articlesLayoutManager = LinearLayoutManager(context)

        adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        adapter?.onItemClickListener = object : ArticleAdapter.OnItemClickListener {
            override fun onItemClick(item: ArticleListItem) {
                val bundle = bundleOf("articleId" to item.id)
                Navigation.findNavController(view).navigate(R.id.action_nav_favorites_to_articleFragment, bundle)
            }
        }

        adapter?.onFavoriteClickListener = object : ArticleAdapter.OnFavoriteClickListener {
            override fun onClick(articleId: Int, isFavorite: Boolean) {
                if (isFavorite) {
                    viewModel.removeFromFavorites(articleId)
                } else {
                    viewModel.addToFavorites(articleId)
                }
            }
        }

        binding.favoritesIndex.layoutManager = articlesLayoutManager
        binding.favoritesIndex.adapter = adapter

        viewModel.favorites.observe(viewLifecycleOwner, {
            adapter?.submitList(it)
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.favoritesIndex.adapter = null
        articlesLayoutManager = null
        _binding = null
    }
}