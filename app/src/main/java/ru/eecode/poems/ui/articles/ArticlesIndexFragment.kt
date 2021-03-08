package ru.eecode.poems.ui.articles

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import ru.eecode.poems.R
import ru.eecode.poems.databinding.FragmentArticlesIndexBinding
import ru.eecode.poems.domain.AdsViewModel
import ru.eecode.poems.domain.ArticleIndexViewModel
import ru.eecode.poems.db.model.ArticleListItem
import ru.eecode.poems.utils.hideKeyboard


@AndroidEntryPoint
class ArticlesIndexFragment : Fragment() {

    private var adapter: ArticleAdapter? = null

    private val viewModel: ArticleIndexViewModel by activityViewModels()

    private var _binding: FragmentArticlesIndexBinding? = null

    private val binding get() = _binding!!

    private var searchLayout: ConstraintLayout? = null

    private var searchInput: EditText? = null

    private var searchResetButton: Button? = null

    private var searchTextWatcher: TextWatcher? = null

    val adsViewModel: AdsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentArticlesIndexBinding.inflate(inflater, container, false)

        adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        val rootView = binding.root
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        adapter = ArticleAdapter()

        binding.articlesIndex.layoutManager = LinearLayoutManager(context)
        binding.articlesIndex.adapter = adapter

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
                    binding.articlesIndex.layoutManager?.scrollToPosition(0)
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

        adsViewModel.emitShowInterstitialEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val view = activity?.currentFocus
        if (view != null) {
            context?.hideKeyboard(view)
        }

        adapter = null
        searchTextWatcher = null
        searchInput?.removeTextChangedListener(searchTextWatcher)
        searchLayout?.visibility = View.GONE
        binding.articlesIndex.adapter = null
        _binding = null
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

