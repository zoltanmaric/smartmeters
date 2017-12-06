package services

import javax.inject.{Inject, Singleton}

import dao.Dao

@Singleton
class ReadingService @Inject()(dao: Dao) {
}
