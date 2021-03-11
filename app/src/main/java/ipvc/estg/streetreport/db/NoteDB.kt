package ipvc.estg.streetreport.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ipvc.estg.streetreport.dao.NotesDao
import ipvc.estg.streetreport.entities.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

@Database(entities = arrayOf(Note::class), version = 1, exportSchema = false)
public abstract class NoteDB : RoomDatabase() {
// Annotates class to be a Room Database with a table (entity) of the Word class

    abstract fun notesDao(): NotesDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var noteDao = database.notesDao()
                    //teste commit


                    // Add sample notes.
                    var note = Note(1, "Nota 1", "Lombas", DateFormat.getDateInstance().format(Date()))
                    noteDao.insert(note)
                    note = Note(2, "Note 2", "Buracos", DateFormat.getDateInstance().format(Date()))
                    noteDao.insert(note)
                    note = Note(3, "Note 3", "Pouca Luz", DateFormat.getDateInstance().format(Date()))
                    noteDao.insert(note)

                }
            }
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NoteDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NoteDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDB::class.java,
                    "notes_database"
                )
                    //estratégia de destruição
                    .fallbackToDestructiveMigration()
                    .addCallback(WordDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}