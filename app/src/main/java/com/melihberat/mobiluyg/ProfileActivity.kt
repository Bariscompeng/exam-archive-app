package com.melihberat.mobiluyg

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_PREV = "EXTRA_PREV"


        private const val PREFS = "app_prefs"
        private const val KEY_REMEMBER = "REMEMBER"


        private const val AVATAR_PREF_PREFIX = "avatar_uri_"
    }

    private lateinit var bottom: BottomNavigationView
    private lateinit var avatarImage: ImageView
    private var currentUser: String = "Melih"  // aktif kullanıcı adı

    private val prefs by lazy { getSharedPreferences(PREFS, MODE_PRIVATE) }
    private fun saveUsername(u: String) = prefs.edit().putString(LoginActivity.EXTRA_USERNAME, u).apply()
    private fun loadUsername(): String? = prefs.getString(LoginActivity.EXTRA_USERNAME, null)


    private val pickFromGallery = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {  }

            setAvatarFromUri(uri)
            saveAvatarUri(currentUser, uri.toString())
        }
    }


    private var pendingCameraUri: Uri? = null

    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            pendingCameraUri?.let { uri ->
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                setAvatarFromUri(uri)
                saveAvatarUri(currentUser, uri.toString())
            }
        } else {
            pendingCameraUri?.let { uri ->
                runCatching { contentResolver.delete(uri, null, null) }
            }
        }
        pendingCameraUri = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        var username = intent.getStringExtra(LoginActivity.EXTRA_USERNAME)?.trim()


        if (username.isNullOrBlank()) {
            username = loadUsername()
        }


        currentUser = if (username.isNullOrBlank()) "Melih" else username!!
        if (!username.isNullOrBlank()) saveUsername(currentUser)

        findViewById<TextView>(R.id.tvUserName).text = currentUser


        findViewById<MaterialToolbar>(R.id.topAppBar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        avatarImage = findViewById(R.id.avatarImage)
        findViewById<ImageView>(R.id.btnEditAvatar).setOnClickListener {
            showAvatarPicker()
        }


        loadSavedAvatar()


        bottom = findViewById(R.id.bottomNav)
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(
                        Intent(this, HomeActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            putExtra(LoginActivity.EXTRA_USERNAME, currentUser)
                        }
                    )
                    true
                }
                R.id.nav_fav -> {
                    startActivity(
                        Intent(this, FavoritesActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            putExtra(LoginActivity.EXTRA_USERNAME, currentUser)
                            putExtra(EXTRA_PREV, "Profile")
                        }
                    )
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
        bottom.menu.findItem(R.id.nav_profile).isChecked = true


        findViewById<MaterialButton>(R.id.btnEditProfile).setOnClickListener {
            Toast.makeText(this, "Profil düzenleme açılacak", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialButton>(R.id.btnDeleteAccount).setOnClickListener {
            confirmAndDeleteAccount()
        }


        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            logout()
        }

        findViewById<MaterialButton>(R.id.btnFeedback).setOnClickListener {
            Toast.makeText(this, "Geri bildirim ekranı açılacak", Toast.LENGTH_SHORT).show()
        }
    }


    private fun confirmAndDeleteAccount() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Hesabı Sil")
            .setMessage("“$currentUser” hesabını kalıcı olarak silmek istediğine emin misin? Bu işlem geri alınamaz.")
            .setNegativeButton("İptal", null)
            .setPositiveButton("Sil") { _, _ ->
                performAccountDeletion()
            }
            .show()
    }

    private fun performAccountDeletion() {

        val deleted = deleteAccountFromDatabase(currentUser)

        if (!deleted) {
            Toast.makeText(this, "Hesap silinemedi. Lütfen tekrar dene.", Toast.LENGTH_SHORT).show()
            return
        }

        prefs.edit()
            .putBoolean(KEY_REMEMBER, false)
            .remove(LoginActivity.EXTRA_USERNAME)
            .remove(LoginActivity.EXTRA_ROLE)
            .remove(AVATAR_PREF_PREFIX + currentUser)
            .apply()

        val i = Intent(this, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(i)
        finish()
    }


    private fun deleteAccountFromDatabase(username: String): Boolean {
        return try {
            val repo = (application as? AccountRepositoryProvider)?.accountRepository()
            if (repo != null) {
                repo.deleteByUsername(username)
            } else {
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun showAvatarPicker() {
        val items = arrayOf("Kamera", "Galeri")
        MaterialAlertDialogBuilder(this)
            .setTitle("Profil fotoğrafı")
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> openSystemCamera()
                    1 -> pickFromGallery.launch(arrayOf("image/*"))
                }
                dialog.dismiss()
            }
            .setNegativeButton("İptal", null)
            .show()
    }


    private fun openSystemCamera() {
        val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())
        val imagesDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        val photoFile = File(imagesDir, "IMG_$time.jpg")

        val authority = "$packageName.fileprovider"
        val uri = FileProvider.getUriForFile(this, authority, photoFile)
        pendingCameraUri = uri


        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        takePicture.launch(uri)
    }

    private fun saveAvatarUri(user: String, uriString: String) {
        prefs.edit().putString(AVATAR_PREF_PREFIX + user, uriString).apply()
    }

    private fun loadAvatarUri(user: String): String? {
        return prefs.getString(AVATAR_PREF_PREFIX + user, null)
    }

    private fun loadSavedAvatar() {
        val saved = loadAvatarUri(currentUser) ?: return
        val uri = Uri.parse(saved)
        if (saved.startsWith("file://")) {
            val path = saved.removePrefix("file://")
            val bm = BitmapFactory.decodeFile(path)
            if (bm != null) avatarImage.setImageBitmap(bm)
        } else {
            runCatching { avatarImage.setImageURI(uri) }
        }
    }

    private fun setAvatarFromUri(uri: Uri) {
        avatarImage.setImageURI(uri)
    }


    private fun logout() {

        prefs.edit()
            .putBoolean(KEY_REMEMBER, false)
            .remove(LoginActivity.EXTRA_USERNAME)
            .remove(LoginActivity.EXTRA_ROLE)
            .apply()

        val i = Intent(this, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(i)
        finish()
    }

    override fun onBackPressed() {
        when (intent.getStringExtra(EXTRA_PREV)) {
            "Favorites" -> {
                startActivity(
                    Intent(this, FavoritesActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        putExtra(LoginActivity.EXTRA_USERNAME, currentUser)
                    }
                )
            }
            else -> super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        bottom.menu.findItem(R.id.nav_profile).isChecked = true
    }
}


interface AccountRepository {
    fun deleteByUsername(username: String): Boolean
}

interface AccountRepositoryProvider {
    fun accountRepository(): AccountRepository
}
