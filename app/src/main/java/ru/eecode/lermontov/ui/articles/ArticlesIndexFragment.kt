package ru.eecode.lermontov.ui.articles

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.eecode.lermontov.R
import ru.eecode.lermontov.databinding.FragmentArticlesIndexBinding
import ru.eecode.lermontov.domain.ArticleIndexViewModel
import ru.eecode.lermontov.repository.db.articles.ArticleListItem
import ru.eecode.lermontov.utils.hideKeyboard


@AndroidEntryPoint
class ArticlesIndexFragment : Fragment() {

    private var adapter: ArticleAdapter? = ArticleAdapter()

    private val viewModel: ArticleIndexViewModel by activityViewModels()

    private var binding: FragmentArticlesIndexBinding? = null

    private var searchLayout: ConstraintLayout? = null

    private var searchInput: EditText? = null

    private var searchResetButton: Button? = null

    private lateinit var searchTextWatcher: TextWatcher

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

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

        searchLayout = activity?.findViewById(R.id.searchLayout);
        searchInput = searchLayout?.findViewById(R.id.searchInput)
        searchResetButton = searchLayout?.findViewById(R.id.searchResetButton)

        searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.filter.value = s.toString()
            }
        }

        setSearchInput()

        adapter?.onItemClickListener = object : ArticleAdapter.OnItemClickListener {
            override fun onItemClick(item: ArticleListItem) {
                val bundle = bundleOf("articleId" to item.id)
                Navigation.findNavController(view).navigate(R.id.action_nav_articles_to_articleFragment, bundle)
            }
        }

        adapter?.onDataChangedListener = object : ArticleAdapter.OnDataChangedListener {
            override fun onDataChanged() {
                if (viewModel.needToResetPosition()) {
                    binding!!.articlesIndex.layoutManager!!.scrollToPosition(0)
                }
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

        viewModel.articles.observe(viewLifecycleOwner, {
            adapter?.submitList(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val view = activity?.currentFocus
        if (view != null) {
            context?.hideKeyboard(view)
        }

        searchInput?.removeTextChangedListener(searchTextWatcher)
        searchLayout?.visibility = View.GONE
        binding!!.articlesIndex.adapter = null
        binding = null
        viewModel.onDestroy()
        searchLayout = null
        searchInput = null
        searchResetButton = null
    }

    private fun setSearchInput() {
        searchLayout?.visibility = View.VISIBLE

        searchInput?.addTextChangedListener(searchTextWatcher)

        searchInput?.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus -> if (!hasFocus) hideKeyboard() }

        searchResetButton?.setOnClickListener {
            viewModel.filter.value = null
            searchInput?.text?.clear()
        }

        viewModel.filter.observe(viewLifecycleOwner, {
            if (it != null && it.isNotEmpty()) {
                searchResetButton?.visibility = View.VISIBLE
            } else {
                searchResetButton?.visibility = View.GONE
            }
        })
    }

}

