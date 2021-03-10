package ipvc.estg.streetreport.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import ipvc.estg.streetreport.dao.NotesDao
import ipvc.estg.streetreport.entities.Note

class NoteRepository(private val noteDao: NotesDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allNote: LiveData<List<Note>> = noteDao.getAllNotes()


    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun deleteAll(){
        noteDao.deleteAll()
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }
    fun deleteByName(name: String){
        noteDao.deleteByName(name)
    }

    fun deletebyId(id: Int?) {
        noteDao.deleteById(id)
    }
}