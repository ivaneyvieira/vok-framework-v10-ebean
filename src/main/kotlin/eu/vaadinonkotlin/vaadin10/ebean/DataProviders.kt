package eu.vaadinonkotlin.vaadin10.ebean

import com.github.mvysny.vokdataloader.DataLoader
import com.vaadin.flow.component.grid.Grid
import eu.vaadinonkotlin.vaadin10.DataLoaderAdapter
import eu.vaadinonkotlin.vaadin10.VokDataProvider
import eu.vaadinonkotlin.vaadin10.asDataProvider
import eu.vaadinonkotlin.vaadin10.withConfigurableFilter2
import io.ebean.Finder
import io.ebean.Model

inline val <reified T: BaseModel> Finder<Long,  T>.dataProvider: VokDataProvider<T>
  get() = dataLoader.asDataProvider()


inline val <reified T: BaseModel> Finder<Long,  T>.dataLoader: DataLoader<T>
  get() = EntityDataLoader(T::class.java)


fun <T: Any> Grid<T>.setDataLoader(dataLoader: DataLoader<T>, idResolver: (T)->Any) {
  dataProvider = dataLoader.asDataProvider(idResolver)
}

fun <T: BaseModel> Grid<T>.setDataLoader(dataLoader: DataLoader<T>) {
  dataProvider = dataLoader.asDataProvider()
}

fun <T: BaseModel> DataLoader<T>.asDataProvider(): VokDataProvider<T> = DataLoaderAdapter(this) { it.id }
  .withConfigurableFilter2()