package ipvc.estg.streetreport.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ipvc.estg.streetreport.entities.Note

@Dao
interface NotesDao{
    @Query("SELECT * FROM note_table")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("DELETE FROM note_table")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("DELETE FROM note_table WHERE name = :name")
    fun deleteByName(name: String)
}