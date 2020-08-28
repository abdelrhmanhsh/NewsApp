package com.abdelrahmman.newsapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.abdelrahmman.newsapp.ui.DataState
import com.abdelrahmman.newsapp.ui.Response
import com.abdelrahmman.newsapp.ui.ResponseType
import com.abdelrahmman.newsapp.util.Constants.Companion.NETWORK_TIMEOUT
import com.abdelrahmman.newsapp.util.Constants.Companion.TESTING_CACHE_DELAY
import com.abdelrahmman.newsapp.util.Constants.Companion.TESTING_NETWORK_DELAY
import com.abdelrahmman.newsapp.util.ErrorHandling
import com.abdelrahmman.newsapp.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.abdelrahmman.newsapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.abdelrahmman.newsapp.util.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.abdelrahmman.newsapp.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import com.abdelrahmman.newsapp.util.GenericApiResponse
import com.abdelrahmman.newsapp.util.GenericApiResponse.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

abstract class NetworkBoundResource<ResponseObject, CacheObject, ViewStateType>(
    isNetworkAvailable: Boolean,
    isNetworkRequest: Boolean,
    shouldCancelIfNoInternet: Boolean,
    shouldLoadFromCache: Boolean
){
    private val TAG: String = "AppDebug"

    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true, cachedData = null))

        if (shouldLoadFromCache){
            val dbSource = loadFromCache()
            result.addSource(dbSource){
                result.removeSource(dbSource)
                setValue(DataState.loading(isLoading = true, cachedData = it))
            }
        }

        if (isNetworkRequest){
            if (isNetworkAvailable){
                doNetworkRequest()
            } else {

                if (shouldCancelIfNoInternet){
                    onErrorReturn(UNABLE_TODO_OPERATION_WO_INTERNET, shouldUseDialog = true, shouldUseToast = false)
                } else {
                    doCachedRequest()
                }

            }
        } else {
            doCachedRequest()
        }
    }

    private fun doCachedRequest() {
        coroutineScope.launch {
            delay(TESTING_CACHE_DELAY)

            createCacheRequestAndReturn()
        }
    }

    private fun doNetworkRequest(){
        coroutineScope.launch {

            delay(TESTING_NETWORK_DELAY)

            withContext(Main){
                val apiResponse = createCall()
                result.addSource(apiResponse){ response ->
                    result.removeSource(apiResponse) // it's like attaching an observer, getting the result and removing the observer

                    coroutineScope.launch {
                        handleNetworkCall(response)
                    }

                }
            }
        }

        GlobalScope.launch(IO) {
            delay(NETWORK_TIMEOUT)

            if (!job.isCompleted){
                Log.e(TAG, "NetworkKBoundResource: JOB NETWORK TIMEOUT.")
                job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
            }

        }
    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when(response){

            is ApiSuccessResponse ->{
                handleApiSuccessResponse(response)
            }

            is ApiErrorResponse -> {
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}")
                onErrorReturn(response.errorMessage, true, false)
            }

            is ApiEmptyResponse -> {
                Log.e(TAG, "NetworkBoundResource: Request returned nothing (HTTP 204)")
                onErrorReturn("Request returned nothing (HTTP 204)", true, false)
            }
        }
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>){
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean){
        var message = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()

        if (message == null){
            message = ERROR_UNKNOWN
        } else if(ErrorHandling.isNetworkError(message)){
            message = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (shouldUseToast){
            responseType = ResponseType.Toast()
        }
        if (useDialog){
            responseType = ResponseType.Dialog()
        }

        onCompleteJob(DataState.error(
            response = Response(
                message = message,
                responseType = responseType
            )
        ))
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job{
        Log.d(TAG, "initNewJob: called...")
        job = Job()
        job.invokeOnCompletion(onCancelling = true, invokeImmediately = true, handler = object : CompletionHandler{
            override fun invoke(cause: Throwable?) {

                if (job.isCancelled){
                    Log.e(TAG, "NetworkBoundResource: Job has been cancelled.")
                    cause?.let {
                        onErrorReturn(it.message, false, true)
                    }?: onErrorReturn(ERROR_UNKNOWN, false, true)
                }else if (job.isCompleted){
                    Log.e(TAG, "NetworkBoundResource: Job has been cancelled.")
                    // Do nothing
                }
            }
        })
        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun createCacheRequestAndReturn()

    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun loadFromCache(): LiveData<ViewStateType>

    abstract suspend fun updateLocalDb(cacheObject: CacheObject?)

    abstract fun setJob(job: Job)

}