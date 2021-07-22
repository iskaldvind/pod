package io.iskaldvind.pictureoftheday.api

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import io.iskaldvind.pictureoftheday.ui.picture.PictureFragment

class ViewPagerAdapter(private val fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PictureFragment.newInstance(0)
            1 -> PictureFragment.newInstance(-1)
            2 -> PictureFragment.newInstance(-2)
            else -> PictureFragment.newInstance(0)
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Today"
            1 -> "Yesterday"
            2 -> "Earlier"
            else -> "Else"
        }
    }
}
