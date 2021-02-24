package ru.eecode.dir.ui.articles

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.eecode.dir.R
import ru.eecode.dir.databinding.FragmentArticleBinding
import ru.eecode.dir.domain.ArticleIndexViewModel
import ru.eecode.dir.domain.ArticleViewModel

@AndroidEntryPoint
class ArticleFragment: Fragment() {

    private val viewModel: ArticleViewModel by activityViewModels()

    private var binding: FragmentArticleBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater.inflate(R.layout.fragment_article, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentArticleBinding.bind(view)
        binding!!.lifecycleOwner = viewLifecycleOwner
        binding!!.viewmodel = viewModel

        viewModel.articleId.value = arguments?.getInt("articleId")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}