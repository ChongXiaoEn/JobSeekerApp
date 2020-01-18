package com.example.jobseeker

import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

//    private lateinit var userDao: UserDatabaseDao
//    private lateinit var db: UserDatabase
//
//    @Before
//    fun createDb() {
//
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//
//        db = Room.inMemoryDatabaseBuilder(context, UserDatabase::class.java).allowMainThreadQueries().build()
//
//        userDao = db.userDatabaseDao
//
//    }
//
//    @After
//    @Throws(IOException::class)
//    fun closeDb() {
//
//        db.close()
//
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun insertAndGetUser() {
//
//        val user = Users()
//
//        userDao.insert(user)
//
//        val getUser = userDao.get(user.userID)
//        assertEquals(getUser?.contactNo, null)
//
//
//    }
}
