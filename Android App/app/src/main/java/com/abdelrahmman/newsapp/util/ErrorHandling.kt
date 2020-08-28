package com.abdelrahmman.newsapp.util

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class ErrorHandling{

    companion object{

        private val TAG: String = "AppDebug"

        const val UNABLE_TO_RESOLVE_HOST = "Unable to resolve host"
        const val UNABLE_TODO_OPERATION_WO_INTERNET = "Can't do that operation without an internet connection"

        const val PAGINATION_DONE_ERROR = "Invalid page."
        const val ERROR_CHECK_NETWORK_CONNECTION = "Check network connection."
        const val ERROR_UNKNOWN = "Unknown error"

        fun isNetworkError(msg: String): Boolean{
            when{
                msg.contains(UNABLE_TO_RESOLVE_HOST) -> return true
                else-> return false
            }
        }

        fun parseDetailJsonResponse(rawJson: String?): String{
            Log.d(TAG, "parseDetailJsonResponse: ${rawJson}")
            try{
                if(!rawJson.isNullOrBlank()){
                    if(rawJson.equals(ERROR_CHECK_NETWORK_CONNECTION)){
                        return PAGINATION_DONE_ERROR
                    }
                    return JSONObject(rawJson).get("detail") as String
                }
            }catch (e: JSONException){
                Log.e(TAG, "parseDetailJsonResponse: ${e.message}")
            }
            return ""
        }

        fun isPaginationDone(errorResponse: String?): Boolean{
            return PAGINATION_DONE_ERROR.equals(parseDetailJsonResponse(errorResponse))
        }
    }

}