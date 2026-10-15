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
import com.stockmaster.app.data.TopProduct
import com.stockmaster.app.databinding.FragmentDashboardBinding
import com.stockmaster.app.databinding.ItemLowStockBinding
import com.stockmaster.app.databinding.ItemTopProductBinding
import com.stockmaster.app.ui.MainActivity
import com.stockmaster.app.ui.MainViewModel
import com.stockmaster.app.util.Money
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val vm: MainViewModel by activityViewModels()
    private var _b: FragmentDashboardBinding? = null
    private val b get() = _b!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentDashboardBinding.bind(view)

        b.greeting.text = greeting()
        b.staffName.text = Session.staffName
        b.staffRole.text = Session.role

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
                b.lowStockCount.text = "${list.size}"
                b.statLowStock.text = "${list.size} alerts"
                b.lowStockEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                b.rvLowStock.visibility  = if (list.isEmpty()) View.GONE else View.VISIBLE
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            vm.topProducts.collect { list ->
                (b.rvTopProducts.adapter as TopProductAdapter).submit(list)
                b.topEmpty.visibility  = if (list.isEmpty()) View.VISIBLE else View.GONE
                b.rvTopProducts.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        b.qaNewSale.setOnClickListener   { (activity as? MainActivity)?.navigateTo(R.id.nav_sell) }
        b.qaInventory.setOnClickListener { (activity as? MainActivity)?.navigateTo(R.id.nav_inventory) }
        b.qaAudit.setOnClickListener     { (activity as? MainActivity)?.navigateTo(R.id.nav_more) }
        b.qaReports.setOnClickListener  { (activity as? MainActivity)?.navigateTo(R.id.nav_more) }
        b.qaStaff.setOnClickListener     { (activity as? MainActivity)?.navigateTo(R.id.nav_more) }
        b.qaCustomers.setOnClickListener{ (activity as? MainActivity)?.navigateTo(R.id.nav_customers) }

        b.btnLogout.setOnClickListener {
            Session.clear()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()
        }
    }

    private fun greeting(): String {
        val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            h < 12 -> "Good morning ☀️"
            h < 17 -> "Good afternoon 🌤"
            else   -> "Good evening 🌙"
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }

    inner class LowStockAdapter : RecyclerView.Adapter<LowStockAdapter.VH>() {
        private val items = mutableListOf<Product>()
        fun submit(list: List<Product>) { items.clear(); items.addAll(list); notifyDataSetChanged() }
        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int) =
            VH(ItemLowStockBinding.inflate(layoutInflater, parent, false))
        override fun getItemCount() = items.size
        override fun onBindViewHolder(holder: VH, position: Int) {
            val p = items[position]
            holder.b.name.text = p.name
            holder.b.meta.text = "reorder at ${Money.qty(p.reorderLevel)} ${p.unit}"
            holder.b.badge.text = "${Money.qty(p.quantity)} ${p.unit}"
        }
        inner class VH(val b: ItemLowStockBinding) : RecyclerView.ViewHolder(b.root)
    }

    inner class TopProductAdapter : RecyclerView.Adapter<TopProductAdapter.VH>() {
        private val items = mutableListOf<TopProduct>()
        fun submit(list: List<TopProduct>) { items.clear(); items.addAll(list); notifyDataSetChanged() }
        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int) =
            VH(ItemTopProductBinding.inflate(layoutInflater, parent, false))
        override fun getItemCount() = items.size
        override fun onBindViewHolder(holder: VH, position: Int) {
            val p = items[position]
            holder.b.rank.text = "${position + 1}"
            holder.b.name.text = p.name
            holder.b.meta.text = "${Money.qty(p.qty)} sold"
            holder.b.revenue.text = Money.naira(p.revenue)
        }
        inner class VH(val b: ItemTopProductBinding) : RecyclerView.ViewHolder(b.root)
    }
}
