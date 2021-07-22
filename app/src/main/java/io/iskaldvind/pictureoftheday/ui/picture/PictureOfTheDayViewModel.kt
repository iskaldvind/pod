package io.iskaldvind.pictureoftheday.ui.picture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.iskaldvind.pictureoftheday.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PictureOfTheDayViewModel(
    private val liveDataForViewToObserve: MutableLiveData<PictureOfTheDayData> = MutableLiveData(),
    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl()
) :
    ViewModel() {

    fun getData(shift: Int): LiveData<PictureOfTheDayData> {
        sendServerRequest(shift)
        return liveDataForViewToObserve
    }

    private fun sendServerRequest(shift: Int) {
        liveDataForViewToObserve.value = PictureOfTheDayData.Loading(null)
        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            PictureOfTheDayData.Error(Throwable("You need API key"))
        } else {
            val cal = Calendar.getInstance()
            cal.timeZone = TimeZone.getTimeZone("UTC")
            cal.add(Calendar.DATE, shift - 1)
            val mon = cal.get(Calendar.MONTH) + 1
            val monS = if (mon > 9) "$mon" else "0$mon"
            val date = cal.get(Calendar.DATE)
            val dateS = if (date > 9) "$date" else "0$date"
            val formattedDate = "${cal.get(Calendar.YEAR)}-$monS-$dateS"
            retrofitImpl.getRetrofitImpl().getPictureOfTheDay(apiKey, formattedDate).enqueue(object :
                Callback<PODServerResponseData> {
                override fun onResponse(
                    call: Call<PODServerResponseData>,
                    response: Response<PODServerResponseData>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        liveDataForViewToObserve.value =
                            PictureOfTheDayData.Success(response.body()!!)
                    } else {
                        val message = response.message()
                        if (message.isNullOrEmpty()) {
                            liveDataForViewToObserve.value =
                                PictureOfTheDayData.Error(Throwable("Unidentified error"))
                        } else {
                            liveDataForViewToObserve.value =
                                PictureOfTheDayData.Error(Throwable(message))
                        }
                    }
                }

                override fun onFailure(call: Call<PODServerResponseData>, t: Throwable) {
                    liveDataForViewToObserve.value = PictureOfTheDayData.Error(t)
                }
            })
        }
    }
}
