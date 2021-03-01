package ru.eecode.poems.ui.store

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import dagger.hilt.android.AndroidEntryPoint
import ru.eecode.poems.databinding.FragmentStoreBinding
import ru.eecode.poems.domain.StoreViewModel
import ru.eecode.poems.domain.store.StoreProduct
import ru.eecode.poems.ui.MainActivity

@AndroidEntryPoint
class StoreFragment: Fragment() {

    private val storeViewModel: StoreViewModel by activityViewModels()

    private var _binding: FragmentStoreBinding? = null

    private val binding get() = _binding!!

    private var adapter: ProductAdapter? = ProductAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        val rootView = binding.root
        setHasOptionsMenu(true)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.storeViewModel = storeViewModel

        binding.productsList.layoutManager = LinearLayoutManager(context)

        adapter?.onItemClickListener = object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(item: StoreProduct) {

                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(item.skuDetails)
                    .build()

                val responseCode = (requireActivity() as MainActivity).billingClient.launchBillingFlow(requireActivity(), flowParams).responseCode
            }
        }

        binding.productsList.adapter = adapter

        storeViewModel.products.observe(viewLifecycleOwner, {
            if (it != null) {
                adapter?.setItems(it)
            }
        })
    }

}