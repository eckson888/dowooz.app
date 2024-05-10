package com.example.dowooz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrdersAdapter(
    private val orders: List<Order>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
        holder.deleteButton.setOnClickListener {
            onDeleteClick(order.id.orEmpty())
        }
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orderIdText: TextView = itemView.findViewById(R.id.orderItemId)
        private val orderItemAddress: TextView = itemView.findViewById(R.id.orderItemAddress)
        private val orderItemPhone: TextView = itemView.findViewById(R.id.orderItemPhone)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        fun bind(order: Order) {
            orderIdText.text = "ID: ${order.id}"
            orderItemAddress.text = "Adres: ${order.address}"
            orderItemPhone.text = "Telefon: ${order.phone}"
            deleteButton.setOnClickListener {
                onDeleteClick(order.id.orEmpty())
            }
        }
    }
}

