package com.stockmaster.app.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stockmaster.app.R
import com.stockmaster.app.data.Product
import com.stockmaster.app.data.Session
import com.stockmaster.app.data.model.TopProduct
import com.stockmaster.app.databinding.FragmentDashboardBinding
import com.stockmaster.app.databinding.ItemLowStockBinding
import com.stockmaster.app.databinding.ItemTopProductBinding
import com.stockmaster.app.ui.LoginActivity
import com.stockmaster.app.ui.MainActivity
import com.stockmaster.app.ui.MainViewModel
import com.stockmaster.app.util.Money
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val vm: MainViewModel by activityViewModels()
    private var _b: FragmentDashboardBinding? = null
    private val b get() = _b!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentDashboardBinding.bind(view)
        b.greetingText.text = greeting()
        b.staffNameText.text = Session.currentStaff?.name ?: "Staff"
        b.staffRoleText.text = Session.currentStaff?.role ?: ""

        b.rvLowStock.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = LowStockAdapter()
            isNestedScrollingEnabled = false
        }
        b.rvTopProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = TopProductAdapter()
            isNestedScrollingEnabled = false
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.todayRevenue.collect { b.statRevenue.text = Money.naira(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            vm.todaySalesCount.collect { b.statSales.text = "$it sales today" }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            vm.stockValue.collect { b.statStockValue.text = Money.naira(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            vm.productCount.collect { b.statProducts.text = "$it items" }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            vm.lowStock.collect { list ->
                (b.rvLowStock.adapter as LowStockAdapter).submit(list)
                b.lowStockAlert.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                b.rvLowStock.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            vm.topProducts.collect { list ->
                (b.rvTopProducts.adapter as TopProductAdapter).submit(list)
                b.topProductsAlert.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                b.rvTopProducts.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        b.btnAddSale.setOnClickListener { (activity as? MainActivity)?.navigateTo(R.id.nav_sell) }
        b.btnAudit.setOnClickListener { (activity as? MainActivity)?.navigateTo(R.id.nav_more) }
        b.btnReports.setOnClickListener { (activity as? MainActivity)?.navigateTo(R.id.nav_more) }
        b.btnUpdatePrice.setOnClickListener { (activity as? MainActivity)?.navigateTo(R.id.nav_inventory) }
        b.btnManageCustomers.setOnClickListener { (activity as? MainActivity)?.navigateTo(R.id.nav_customers) }

        b.btnLogout.setOnClickListener {
            Session.currentStaff = null
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()
        }
    }

    private fun greeting(): String {
        val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            h < 12 -> "Good morning ☀️"
            h < 17 -> "Good afternoon 🌤️"
            else -> "Good evening 🌙"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }

    inner class LowStockAdapter : RecyclerView.Adapter<LowStockAdapter.VH>() {
        private val items = mutableListOf<Product>()
        fun submit(list: List<Product>) { items.clear(); items.addAll(list); notifyDataSetChanged() }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(ItemLowStockBinding.inflate(layoutInflater, parent, false))
        override fun getItemCount() = items.size
        override fun onBindViewHolder(holder: VH, position: Int) {
            val p = items[position]
            holder.b.name.text = p.name
            holder.b.meta.text = "Reorder at ${p.reorderLevel} ${p.unit}"
            holder.b.badgeText.text = "Only ${p.quantity} ${p.unit} left"
        }
        inner class VH(val b: ItemLowStockBinding) : RecyclerView.ViewHolder(b.root)
    }

    inner class TopProductAdapter : RecyclerView.Adapter<TopProductAdapter.VH>() {
        private val items = mutableListOf<TopProduct>()
        fun submit(list: List<TopProduct>) { items.clear(); items.addAll(list); notifyDataSetChanged() }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(ItemTopProductBinding.inflate(layoutInflater, parent, false))
        override fun getItemCount() = items.size
        override fun onBindViewHolder(holder: VH, position: Int) {
            val p = items[position]
            holder.b.name.text = "${position + 1}. ${p.name}"
            holder.b.meta.text = "₦${Money.naira(p.revenue)} sold"
            holder.b.revenue.text = Money.naira(p.revenue)
        }
        inner class VH(val b: ItemTopProductBinding) : RecyclerView.ViewHolder(b.root)
    }
}
