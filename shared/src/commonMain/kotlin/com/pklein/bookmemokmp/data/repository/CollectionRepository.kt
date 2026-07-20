package com.pklein.bookmemokmp.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.pklein.bookmemokmp.data.mapper.toDomain
import com.pklein.bookmemokmp.database.BookDatabase
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.repository.ICollectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CollectionRepository(
    db: BookDatabase,
) : ICollectionRepository {
    private val queries = db.bookQueries

    override fun getAll(): Flow<List<CollectionItem>> =
        queries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }

    override fun getByType(type: ItemType): Flow<List<CollectionItem>> =
        queries
            .selectByType(type.name)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }

    override fun getFavorites(): Flow<List<CollectionItem>> =
        queries
            .selectFavorites()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): CollectionItem? =
        withContext(Dispatchers.IO) {
            queries.selectById(id).executeAsOneOrNull()?.toDomain()
        }

    override suspend fun existsByTitleAndType(
        title: String,
        type: ItemType,
        excludeId: Long,
    ): Boolean =
        withContext(Dispatchers.IO) {
            queries.existsByTitleAndType(title, type.name, excludeId).executeAsOne() > 0
        }

    override suspend fun add(item: CollectionItem): Long =
        withContext(Dispatchers.IO) {
            queries.insert(
                type = item.type.name,
                title = item.title,
                author = item.author,
                illustrator = item.illustrator,
                year = item.year?.toLong(),
                bought = if (item.bought) 1L else 0L,
                wishlist = if (item.wishlist) 1L else 0L,
                finish = if (item.finished) 1L else 0L,
                tome = item.tome?.toLong(),
                chapter = item.chapter?.toLong(),
                episode = item.episode?.toLong(),
                season = item.season?.toLong(),
                desc = item.description,
                favorite = if (item.favorite) 1L else 0L,
                image_url = item.imageUrl,
                is_borrowed = if (item.isBorrowed) 1L else 0L,
                borrowed_since = item.borrowedSince,
                borrowed_by = item.borrowedBy,
                jikan_id = item.mangaApiId,
                jikan_author_id = item.mangaApiAuthorId,
                jikan_type = item.mangaApiType?.value,
                tot_tome = item.totTome?.toLong(),
                tot_chapter = item.totChapter?.toLong(),
                tot_episode = item.totEpisode?.toLong(),
                checked_tomes = item.checkedTomes,
                notes = item.notes,
                format = item.format?.name,
                is_digital = if (item.isDigital) 1L else 0L,
            )
            queries.lastInsertRowId().executeAsOne()
        }

    override suspend fun update(item: CollectionItem) =
        withContext(Dispatchers.IO) {
            queries.update(
                type = item.type.name,
                title = item.title,
                author = item.author,
                illustrator = item.illustrator,
                year = item.year?.toLong(),
                bought = if (item.bought) 1L else 0L,
                wishlist = if (item.wishlist) 1L else 0L,
                finish = if (item.finished) 1L else 0L,
                tome = item.tome?.toLong(),
                chapter = item.chapter?.toLong(),
                episode = item.episode?.toLong(),
                season = item.season?.toLong(),
                desc = item.description,
                favorite = if (item.favorite) 1L else 0L,
                image_url = item.imageUrl,
                is_borrowed = if (item.isBorrowed) 1L else 0L,
                borrowed_since = item.borrowedSince,
                borrowed_by = item.borrowedBy,
                jikan_id = item.mangaApiId,
                jikan_author_id = item.mangaApiAuthorId,
                jikan_type = item.mangaApiType?.value,
                tot_tome = item.totTome?.toLong(),
                tot_chapter = item.totChapter?.toLong(),
                tot_episode = item.totEpisode?.toLong(),
                checked_tomes = item.checkedTomes,
                notes = item.notes,
                format = item.format?.name,
                is_digital = if (item.isDigital) 1L else 0L,
                id = item.id,
            )
        }

    override suspend fun updateTotals(
        id: Long,
        totTome: Int?,
        totChapter: Int?,
        totEpisode: Int?,
    ) = withContext(Dispatchers.IO) {
        queries.updateTotals(
            tot_tome = totTome?.toLong(),
            tot_chapter = totChapter?.toLong(),
            tot_episode = totEpisode?.toLong(),
            id = id,
        )
    }

    override suspend fun updateLoan(
        id: Long,
        isBorrowed: Boolean,
        borrowedSince: Long?,
        borrowedBy: String?,
    ) = withContext(Dispatchers.IO) {
        queries.updateLoan(
            is_borrowed = if (isBorrowed) 1L else 0L,
            borrowed_since = borrowedSince,
            borrowed_by = borrowedBy,
            id = id,
        )
    }

    override suspend fun delete(id: Long) =
        withContext(Dispatchers.IO) {
            queries.delete(id)
        }
}
