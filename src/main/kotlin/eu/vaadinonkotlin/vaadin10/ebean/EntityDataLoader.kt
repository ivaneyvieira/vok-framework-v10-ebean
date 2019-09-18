package eu.vaadinonkotlin.vaadin10.ebean

import com.github.mvysny.vokdataloader.AndFilter
import com.github.mvysny.vokdataloader.CompareOperator
import com.github.mvysny.vokdataloader.DataLoader
import com.github.mvysny.vokdataloader.EqFilter
import com.github.mvysny.vokdataloader.Filter
import com.github.mvysny.vokdataloader.ILikeFilter
import com.github.mvysny.vokdataloader.InFilter
import com.github.mvysny.vokdataloader.IsNotNullFilter
import com.github.mvysny.vokdataloader.IsNullFilter
import com.github.mvysny.vokdataloader.LikeFilter
import com.github.mvysny.vokdataloader.NotFilter
import com.github.mvysny.vokdataloader.OpFilter
import com.github.mvysny.vokdataloader.OrFilter
import com.github.mvysny.vokdataloader.SortClause
import io.ebean.DB
import io.ebean.ExpressionList
import io.ebean.Junction
import io.ebean.Query

class EntityDataLoader<T: BaseModel>(val clazz: Class<T>): DataLoader<T> {
  override fun fetch(filter: Filter<T>?, sortBy: List<SortClause>, range: LongRange): List<T> {
    return DB.find(clazz)
      .where()
      .makeEBeanFilter(filter)
      .makeEBeanSort(sortBy)
      .setFirstRow(range.first.toInt())
      .setMaxRows(range.last.toInt())
      .findList()
  }

  override fun getCount(filter: Filter<T>?): Long {
    filter ?: return 0
    return DB.find(clazz)
      .where()
      .makeEBeanFilter(filter)
      .findCount()
      .toLong()
  }
}

private fun <T: BaseModel> ExpressionList<T>.makeEBeanSort(sortBy: List<SortClause>): ExpressionList<T> {
  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

private fun <T: BaseModel> ExpressionList<T>.makeEBeanFilter(filter: Filter<in T>?): ExpressionList<T> {
  filter ?: return this
  return when(filter) {
    is EqFilter        -> this.eq(filter.propertyName, filter.value)
    is OpFilter        -> when(filter.operator) {
      CompareOperator.eq -> this.eq(filter.propertyName, filter.value)
      CompareOperator.lt -> this.lt(filter.propertyName, filter.value)
      CompareOperator.le -> this.le(filter.propertyName, filter.value)
      CompareOperator.gt -> this.gt(filter.propertyName, filter.value)
      CompareOperator.ge -> this.ge(filter.propertyName, filter.value)
    }
    is LikeFilter      -> this.like(filter.propertyName, filter.value)
    is ILikeFilter     -> this.ilike(filter.propertyName, filter.value)
    is IsNullFilter    -> this.isNull(filter.propertyName)
    is IsNotNullFilter -> this.isNotNull(filter.propertyName)
    is AndFilter       -> {
      var andStart: Junction<T> = this.and()
      val andChain = filter.children.fold(andStart) {ands, filterAnd ->
        ands.makeEBeanFilter(filterAnd) as Junction<T>
      }
      andChain.endAnd()
    }
    is OrFilter        -> {
      val orStart: Junction<T> = this.or()
      val orChain = filter.children.fold(orStart) {ors, filterOr ->
        ors.makeEBeanFilter(filterOr) as Junction<T>
      }
      orChain.endOr()
    }
    is InFilter        -> this.`in`(filter.propertyName, filter.value)
    is NotFilter       -> this.not().makeEBeanFilter(filter.child)
    else               -> this
  }
}
