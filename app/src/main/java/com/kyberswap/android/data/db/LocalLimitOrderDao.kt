package com.kyberswap.android.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kyberswap.android.domain.model.LocalLimitOrder
import io.reactivex.Flowable

/**
 * Data Access Object for the current_order table.
 */
@Dao
interface LocalLimitOrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(localLimitOrder: LocalLimitOrder)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateOrder(localLimitOrder: LocalLimitOrder)

    @Query("SELECT * from current_orders where userAddr = :address AND type = :type LIMIT 1")
    fun findLocalLimitOrderByAddressFlowable(
        address: String,
        type: Int = LocalLimitOrder.TYPE_LIMIT_ORDER_V1
    ): Flowable<LocalLimitOrder>

    @Query("SELECT * from current_orders where userAddr = :address AND type = :type LIMIT 1")
    fun findLocalLimitOrderByAddress(
        address: String,
        type: Int = LocalLimitOrder.TYPE_LIMIT_ORDER_V1
    ): LocalLimitOrder?

    @Query("SELECT * from current_orders where userAddr = :address")
    fun findAllLimitOrderByAddress(
        address: String
    ): List<LocalLimitOrder>

    @Query("DELETE FROM current_orders")
    fun deleteAllLocalLimitOrders()

    @Delete
    fun delete(model: LocalLimitOrder)

    @get:Query("SELECT * FROM current_orders")
    val all: Flowable<List<LocalLimitOrder>>
}

