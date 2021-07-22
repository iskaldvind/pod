package io.iskaldvind.pictureoftheday.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.iskaldvind.pictureoftheday.R
import io.iskaldvind.pictureoftheday.ui.picture.PictureOfTheDayFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sp = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val themeId = sp.getInt("theme", R.style.Light)
        val theme = if (themeId == R.style.Light) themeId else R.style.Dark
        setTheme(theme)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PictureOfTheDayFragment.newInstance())
                .commitNow()
        }
    }


    object ThemeHolder {
        var theme: Int = R.style.Light
    }


    fun changeTheme() {
        ThemeHolder.theme = if (ThemeHolder.theme == R.style.Light) R.style.Dark else R.style.Light
        val sp = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt("theme", ThemeHolder.theme)
        editor.apply()
        this.recreate()
    }


}
