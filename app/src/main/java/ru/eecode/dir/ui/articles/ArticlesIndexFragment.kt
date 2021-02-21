package ru.eecode.dir.ui.articles

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.eecode.dir.R
import ru.eecode.dir.databinding.FragmentArticlesIndexBinding
import ru.eecode.dir.domain.ArticleIndexViewModel
import ru.eecode.dir.utils.hideKeyboard
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import ru.eecode.dir.repository.db.articles.ArticleListItem

@AndroidEntryPoint
class ArticlesIndexFragment : Fragment() {

    private var adapter: ArticleAdapter? = null

    private val viewModel: ArticleIndexViewModel by activityViewModels()

    private var binding: FragmentArticlesIndexBinding? = null;

    private var articlesLayoutManager: LinearLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater.inflate(R.layout.fragment_articles_index, container, false)
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentArticlesIndexBinding.bind(view)
        binding!!.lifecycleOwner = viewLifecycleOwner
        binding!!.viewmodel = viewModel

        articlesLayoutManager = LinearLayoutManager(context)

        adapter = ArticleAdapter()

        adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        adapter!!.onItemClickListener = object : ArticleAdapter.OnItemClickListener {
            override fun onItemClick(item: ArticleListItem) {
                viewModel.loadArticle(item.id)
                Navigation.findNavController(view).navigate(R.id.action_nav_articles_to_articleFragment)
            }
        }

        adapter!!.onDataChangedListener = object : ArticleAdapter.OnDataChangedListener {
            override fun onDataChanged() {
                if (viewModel.destroyed.value != true) {
                    articlesLayoutManager!!.scrollToPosition(0)
                }
            }
        }

        val articlesIndex = binding!!.articlesIndex

        articlesIndex.layoutManager = articlesLayoutManager
        articlesIndex.adapter = adapter

        setSearchInput()

        viewModel.articles.observe(viewLifecycleOwner, {
            adapter!!.submitList(it)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onDestroyView() {

        binding!!.articlesIndex.adapter = null
        binding = null
        if (articlesLayoutManager != null) articlesLayoutManager = null
        viewModel.onDestroy()
        super.onDestroyView()
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

