package io.iskaldvind.pictureoftheday.ui.picture

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import coil.load
import io.iskaldvind.pictureoftheday.R
import io.iskaldvind.pictureoftheday.util.EquilateralImageView
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.picture_fragment.*

class PictureFragment: Fragment() {

    private var shift: Int = 0
    private var enlarged = false

    private val spaceWords = listOf("Sun", "Moon", "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto",
    "Triton", "Fobs", "Deimos", "space", "nebula", "gas", "clouds", "light", "star", "planet", "system", "galaxy", "comet", "asteroid", "atmosphere")

    private val viewModel: PictureOfTheDayViewModel by lazy {
        ViewModelProviders.of(this).get(PictureOfTheDayViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.picture_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shift = arguments?.getInt(PICTURE_FOR) ?: 0
        viewModel.getData(shift)
            .observe(viewLifecycleOwner, Observer<PictureOfTheDayData> { renderData(it) })
        view.findViewById<EquilateralImageView>(R.id.image_view).setOnClickListener {
            if (enlarged) makeImageDefault() else enlargeImage()
        }
    }


    private fun makeImageDefault() {
        enlarged = false
        applyAnimation(R.layout.picture_fragment)
    }


    private fun enlargeImage() {
        enlarged = true
        applyAnimation(R.layout.picture_fragment_maxed)
    }


    private fun applyAnimation(@LayoutRes layout: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(requireContext(), layout)
        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator(1.0f)
        transition.duration = 1200

        TransitionManager.beginDelayedTransition(constraint_container, transition)
        constraintSet.applyTo(constraint_container)
    }



    private fun renderData(data: PictureOfTheDayData) {
        when (data) {
            is PictureOfTheDayData.Success -> {
                val serverResponseData = data.serverResponseData
                val url = serverResponseData.url
                if (url.isNullOrEmpty()) {
                    //showError("Сообщение, что ссылка пустая")
                    toast("Link is empty")
                } else {
                    //showSuccess()
                    image_view.load(url) {
                        lifecycle(this@PictureFragment)
                        error(R.drawable.ic_load_error_vector)
                        placeholder(R.drawable.ic_no_photo_vector)
                    }

                    serverResponseData.explanation?.let { explanation ->
                        val spannable = SpannableString(explanation)

                        spaceWords.forEach { word ->
                            var startIndex = 0
                            while (true) {
                                val index = explanation.indexOf(word, startIndex)
                                if (index == -1) break
                                startIndex = index + 1

                                spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorAccent)),
                                index, index + word.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                        }
                        image_description.text = spannable
                    }
                }
            }
            is PictureOfTheDayData.Loading -> {
                //showLoading()
            }
            is PictureOfTheDayData.Error -> {
                //showError(data.error.message)
                toast(data.error.message)
            }
        }
    }

    private fun Fragment.toast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.BOTTOM, 0, 250)
            show()
        }
    }

    companion object {
        private const val PICTURE_FOR = "PictureFragment.PictureFor"

        fun newInstance(shift: Int): PictureFragment {
            val args = Bundle()
            val fragment = PictureFragment()
            args.putInt(PICTURE_FOR, shift)
            fragment.arguments = args
            return fragment
        }
    }
}