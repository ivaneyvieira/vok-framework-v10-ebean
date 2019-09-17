package eu.vaadinonkotlin.vaadin10.ebean

import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.binder.Result
import com.vaadin.flow.data.binder.ValueContext
import com.vaadin.flow.data.converter.Converter
import io.ebean.DB
import io.ebean.EbeanServer

class EntityToIdConverter2<T: BaseModel>(val clazz: Class<T>): Converter<T?, Long?> {
  override fun convertToModel(value: T?, context: ValueContext?): Result<Long?> =
    Result.ok(value?.id)

  override fun convertToPresentation(value: Long?, context: ValueContext?): T? {
    if(value == null) return null
    return DB.find(clazz, value)
  }
}

inline fun <BEAN, reified ENTITY: BaseModel> Binder.BindingBuilder<BEAN, ENTITY?>.toId():
  Binder.BindingBuilder<BEAN, Long?> =
  withConverter(EntityToIdConverter2(ENTITY::class.java))