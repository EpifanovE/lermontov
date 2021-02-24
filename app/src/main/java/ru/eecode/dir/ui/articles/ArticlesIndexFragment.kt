package ru.eecode.dir.ui.articles

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.eecode.dir.R
import ru.eecode.dir.databinding.FragmentArticlesIndexBinding
import ru.eecode.dir.domain.ArticleIndexViewModel
import ru.eecode.dir.repository.db.articles.ArticleListItem
import ru.eecode.dir.utils.hideKeyboard


@AndroidEntryPoint
class ArticlesIndexFragment : Fragment() {

    private val adapter: ArticleAdapter = ArticleAdapter()

    private val viewModel: ArticleIndexViewModel by activityViewModels()

    private var binding: FragmentArticlesIndexBinding? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        val rootView = inflater.inflate(R.layout.fragment_articles_index, container, false)
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticlesIndexBinding.bind(view)
        binding!!.lifecycleOwner = this
        binding!!.viewmodel = viewModel

        binding!!.articlesIndex.layoutManager = LinearLayoutManager(context)
        binding!!.articlesIndex.adapter = adapter

        setSearchInput()

        adapter.onItemClickListener = object : ArticleAdapter.OnItemClickListener {
            override fun onItemClick(item: ArticleListItem) {
                val bundle = bundleOf("articleId" to item.id)
                Navigation.findNavController(view).navigate(R.id.action_nav_articles_to_articleFragment, bundle)
            }
        }

        adapter.onDataChangedListener = object : ArticleAdapter.OnDataChangedListener {
            override fun onDataChanged() {
                if (viewModel.destroyed.value != true) {
                    binding!!.articlesIndex.layoutManager!!.scrollToPosition(0)
                }
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

        viewModel.articles.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding!!.articlesIndex.adapter = null
        binding = null
        viewModel.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun setSearchInput() {
        binding!!.searchInput.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus -> if (!hasFocus) hideKeyboard() }

        binding!!.searchResetButton.setOnClickListener {
            viewModel.filter.value = null
            binding!!.searchInput.text.clear()
        }

        viewModel.filter.observe(viewLifecycleOwner, {
            if (it != null && it.isNotEmpty()) {
                binding!!.searchResetButton.visibility = View.VISIBLE
            } else {
                binding!!.searchResetButton.visibility = View.GONE
            }
        })
    }

}

